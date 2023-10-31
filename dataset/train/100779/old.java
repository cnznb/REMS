public class old{
    public static boolean isOrbotInRequiredState(int middleButton, final Runnable middleButtonRunnable,
        Preferences.ProxyPrefs proxyPrefs, FragmentActivity fragmentActivity) {
        Handler ignoreTorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        // every message received by this handler will mean  the middle button was pressed
        middleButtonRunnable.run();
        }
        };

        if (!proxyPrefs.torEnabled) {
            return true;
        }

        if (!OrbotHelper.isOrbotInstalled(fragmentActivity)) {

        OrbotHelper.getInstallDialogFragmentWithThirdButton(
        new Messenger(ignoreTorHandler),
        middleButton
        ).show(fragmentActivity.getSupportFragmentManager(), "OrbotHelperOrbotInstallDialog");
            return false;
        } else if (!OrbotHelper.isOrbotRunning()) {
        OrbotHelper.getOrbotStartDialogFragment(new Messenger(ignoreTorHandler),
        middleButton)
        .show(fragmentActivity.getSupportFragmentManager(), "OrbotHelperOrbotStartDialog");
            return false;
        } else {
            return true;
        }
    }
}
