public class old{
    private void notifyAccountWithDataLocked(Context context, Account account,
        LocalMessage message, NotificationData data) {
        boolean updateSilently = false;

        if (message == null) {
            /* this can happen if a message we previously notified for is read or deleted remotely */
            message = findNewestMessageForNotificationLocked(context, data);
            updateSilently = true;
            if (message == null) {
                // seemingly both the message list as well as the overflow list is empty;
                // it probably is a good idea to cancel the notification in that case
                notifyAccountCancel(context, account);
                return;
            }
        } else {
            data.addMessage(message);
        }
        final KeyguardManager keyguardService = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        final CharSequence sender = getMessageSender(context, account, message);
        final CharSequence subject = getMessageSubject(context, message);
        CharSequence summary = buildMessageSummary(context, sender, subject);
        boolean privacyModeEnabled =
                (K9.getNotificationHideSubject() == NotificationHideSubject.ALWAYS) ||
                (K9.getNotificationHideSubject() == NotificationHideSubject.WHEN_LOCKED &&
                keyguardService.inKeyguardRestrictedInputMode());
        if (privacyModeEnabled || summary.length() == 0) {
            summary = context.getString(R.string.notification_new_title);
        }
        NotificationManager notifMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_notify_new_mail);
        builder.setWhen(System.currentTimeMillis());
        if (!updateSilently) {
            builder.setTicker(summary);
        }
        final int newMessages = data.getNewMessageCount();
        final int unreadCount = data.unreadBeforeNotification + newMessages;
        builder.setNumber(unreadCount);
        String accountDescr = (account.getDescription() != null) ?
                account.getDescription() : account.getEmail();
        final ArrayList<MessageReference> allRefs = new ArrayList<MessageReference>();
        data.supplyAllMessageRefs(allRefs);

        if (platformSupportsExtendedNotifications() && !privacyModeEnabled) {
            if (newMessages > 1) {
                // multiple messages pending, show inbox style
                NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle(builder);
                for (Message m : data.messages) {
                    style.addLine(buildMessageSummary(context,
                            getMessageSender(context, account, m),
                            getMessageSubject(context, m)));
                }
                if (!data.droppedMessages.isEmpty()) {
                    style.setSummaryText(context.getString(R.string.notification_additional_messages,
                            data.droppedMessages.size(), accountDescr));
                }
                final String title = context.getResources().getQuantityString(
                    R.plurals.notification_new_messages_title, newMessages, newMessages);
                style.setBigContentTitle(title);
                builder.setContentTitle(title);
                builder.setSubText(accountDescr);
                builder.setStyle(style);
            } else {
                // single message pending, show big text
                NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(builder);
                CharSequence preview = getMessagePreview(context, message);
                if (preview != null) {
                    style.bigText(preview);
                }
                builder.setContentText(subject);
                builder.setSubText(accountDescr);
                builder.setContentTitle(sender);
                builder.setStyle(style);

                builder.addAction(
                    platformSupportsLockScreenNotifications()
                        ? R.drawable.ic_action_single_message_options_dark_vector
                        : R.drawable.ic_action_single_message_options_dark,
                    context.getString(R.string.notification_action_reply),
                    NotificationActionService.getReplyIntent(context, account, message.makeMessageReference()));
            }

            // Mark Read on phone
            builder.addAction(
                platformSupportsLockScreenNotifications()
                    ? R.drawable.ic_action_mark_as_read_dark_vector
                    : R.drawable.ic_action_mark_as_read_dark,
                context.getString(R.string.notification_action_mark_as_read),
                NotificationActionService.getReadAllMessagesIntent(context, account, allRefs));

            NotificationQuickDelete deleteOption = K9.getNotificationQuickDeleteBehaviour();
            boolean showDeleteAction = deleteOption == NotificationQuickDelete.ALWAYS ||
                    (deleteOption == NotificationQuickDelete.FOR_SINGLE_MSG && newMessages == 1);

            NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
            if (showDeleteAction) {
                // we need to pass the action directly to the activity, otherwise the
                // status bar won't be pulled up and we won't see the confirmation (if used)

                // Delete on phone
                builder.addAction(
                    platformSupportsLockScreenNotifications()
                        ? R.drawable.ic_action_delete_dark_vector
                        : R.drawable.ic_action_delete_dark,
                    context.getString(R.string.notification_action_delete),
                    NotificationDeleteConfirmation.getIntent(context, account, allRefs));

                // Delete on wear only if no confirmation is required
                if (!K9.confirmDeleteFromNotification()) {
                    NotificationCompat.Action wearActionDelete =
                            new NotificationCompat.Action.Builder(
                                    R.drawable.ic_action_delete_dark,
                                    context.getString(R.string.notification_action_delete),
                                    NotificationDeleteConfirmation.getIntent(context, account, allRefs))
                                    .build();
                    builder.extend(wearableExtender.addAction(wearActionDelete));
                }
            }
            if (NotificationActionService.isArchiveAllMessagesWearAvaliable(context, account, data.messages)) {

                // Archive on wear
                NotificationCompat.Action wearActionArchive =
                        new NotificationCompat.Action.Builder(
                                R.drawable.ic_action_delete_dark,
                                context.getString(R.string.notification_action_archive),
                                NotificationActionService.getArchiveAllMessagesIntent(context, account, allRefs))
                                .build();
                builder.extend(wearableExtender.addAction(wearActionArchive));
            }
            if (NotificationActionService.isSpamAllMessagesWearAvaliable(context, account, data.messages)) {

                // Archive on wear
                NotificationCompat.Action wearActionSpam =
                        new NotificationCompat.Action.Builder(
                                R.drawable.ic_action_delete_dark,
                                context.getString(R.string.notification_action_spam),
                                NotificationActionService.getSpamAllMessagesIntent(context, account, allRefs))
                                .build();
                builder.extend(wearableExtender.addAction(wearActionSpam));
            }
        } else {
            String accountNotice = context.getString(R.string.notification_new_one_account_fmt,
                    unreadCount, accountDescr);
            builder.setContentTitle(accountNotice);
            builder.setContentText(summary);
        }
        for (Message m : data.messages) {
            if (m.isSet(Flag.FLAGGED)) {
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                break;
            }
        }

        TaskStackBuilder stack;
        boolean treatAsSingleMessageNotification;

        if (platformSupportsExtendedNotifications()) {
            // in the new-style notifications, we focus on the new messages, not the unread ones
            treatAsSingleMessageNotification = newMessages == 1;
        } else {
            // in the old-style notifications, we focus on unread messages, as we don't have a
            // good way to express the new message count
            treatAsSingleMessageNotification = unreadCount == 1;
        }
        if (treatAsSingleMessageNotification) {
            stack = buildMessageViewBackStack(context, message.makeMessageReference());
        } else if (account.goToUnreadMessageSearch()) {
            stack = buildUnreadBackStack(context, account);
        } else {
            String initialFolder = message.getFolder().getName();
            /* only go to folder if all messages are in the same folder, else go to folder list */
            for (MessageReference ref : allRefs) {
                if (!TextUtils.equals(initialFolder, ref.getFolderName())) {
                    initialFolder = null;
                    break;
                }
            }

            stack = buildMessageListBackStack(context, account, initialFolder);
        }

        builder.setContentIntent(stack.getPendingIntent(
                account.getAccountNumber(),
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT));
        builder.setDeleteIntent(NotificationActionService.getAcknowledgeIntent(context, account));

        // Only ring or vibrate if we have not done so already on this account and fetch
        boolean ringAndVibrate = false;
        if (!updateSilently && !account.isRingNotified()) {
            account.setRingNotified(true);
            ringAndVibrate = true;
        }

        NotificationSetting n = account.getNotificationSetting();

        configureLockScreenNotification(builder, context, account, newMessages, unreadCount, accountDescr, sender, data.messages);

        configureNotification(
                builder,
                (n.shouldRing()) ?  n.getRingtone() : null,
                (n.shouldVibrate()) ? n.getVibration() : null,
                (n.isLed()) ? Integer.valueOf(n.getLedColor()) : null,
                K9.NOTIFICATION_LED_BLINK_SLOW,
                ringAndVibrate);

        notifMgr.notify(account.getAccountNumber(), builder.build());
    }
}
