public class old{
    public void run() {
        SimpleConsumer consumer = null;
        info("Starting partition fetcher for " + m_topicAndPartition);
        long submitCount = 0;
        AtomicLong cbcnt = new AtomicLong(0);
        try {
            //Startwith the starting leader.
            HostAndPort leaderBroker = m_leader;
            int sleepCounter = 1;
            while (!m_shutdown) {
                if (consumer == null) {
                    consumer = new SimpleConsumer(leaderBroker.getHost(), leaderBroker.getPort(), m_consumerSocketTimeout, m_fetchSize, CLIENT_ID);
                }
                //If we dont know the offset get it backoff if we fail.
                if (m_currentOffset.get() < 0) {
                    getOffsetCoordinator();
                    m_currentOffset.set(getLastOffset(kafka.api.OffsetRequest.LatestTime()));
                    if (m_currentOffset.get() < 0) {
                        sleepCounter = backoffSleep(sleepCounter);
                        info("Latest offset not found for " + m_topicAndPartition + " using earliest offset.");
                        //No latest time available so get earliest known for this consumer group.
                        m_currentOffset.set(getLastOffset(kafka.api.OffsetRequest.EarliestTime()));
                    }
                    sleepCounter = backoffSleep(sleepCounter);
                    info("Starting offset for " + m_topicAndPartition + " is set to: " + m_currentOffset.get());
                    continue;
                }
                long currentFetchCount = 0;
                //Build fetch request of we have a valid offset and not too many are pending.
                FetchRequest req = new FetchRequestBuilder().clientId(CLIENT_ID)
                        .addFetch(m_topicAndPartition.topic(),
                                m_topicAndPartition.partition(), m_currentOffset.get(), m_fetchSize)
                        .build();
                FetchResponse fetchResponse = null;
                try {
                    fetchResponse = consumer.fetch(req);
                    if (fetchResponse == null) {
                        sleepCounter = backoffSleep(sleepCounter);
                        continue;
                    }
                } catch (Exception ex) {
                    error(ex, "Failed to fetch from %s", m_topicAndPartition);
                    //See if its network error and find new leader for this partition.
                    if (ex instanceof ClosedChannelException) {
                        closeConsumer(consumer);
                        consumer = null;
                        leaderBroker = findNewLeader();
                        if (leaderBroker == null) {
                            //point to original leader which will fail and we fall back again here.
                            error(null, "Fetch Failed to find leader continue with old leader: %s", m_leader);
                            leaderBroker = m_leader;
                        } else {
                            if (!leaderBroker.equals(m_leader)) {
                                info("Fetch Found new leader for " + m_topicAndPartition + " New Leader: " + leaderBroker);
                            }
                        }
                        //find leader would sleep and backoff
                        continue;
                    }
                    sleepCounter = backoffSleep(sleepCounter);
                    continue;
                }
                if (fetchResponse.hasError()) {
                    // Something went wrong!
                    short code = fetchResponse.errorCode(m_topicAndPartition.topic(), m_topicAndPartition.partition());
                    warn(null, "Failed to fetch messages for %s Code: %d", m_topicAndPartition, code);
                    sleepCounter = backoffSleep(sleepCounter);
                    if (code == ErrorMapping.OffsetOutOfRangeCode()) {
                        // We asked for an invalid offset. For simple case ask for the last element to reset
                        info("Invalid offset requested for " + m_topicAndPartition);
                        getOffsetCoordinator();
                        m_currentOffset.set(getLastOffset(kafka.api.OffsetRequest.LatestTime()));
                        continue;
                    }
                    closeConsumer(consumer);
                    consumer = null;
                    leaderBroker = findNewLeader();
                    if (leaderBroker == null) {
                        //point to original leader which will fail and we fall back again here.
                        error(null, "Failed to find leader continue with old leader: %s", m_leader);
                        leaderBroker = m_leader;
                    } else {
                        if (!leaderBroker.equals(m_leader)) {
                            info("Found new leader for " + m_topicAndPartition + " New Leader: " + leaderBroker);
                        }
                    }
                    continue;
                }
                sleepCounter = 1;
                for (MessageAndOffset messageAndOffset : fetchResponse.messageSet(m_topicAndPartition.topic(), m_topicAndPartition.partition())) {
                    //You may be catchin up so dont sleep.
                    currentFetchCount++;
                    long currentOffset = messageAndOffset.offset();
                    //if currentOffset is less means we have already pushed it and also check pending queue.
                    if (currentOffset < m_currentOffset.get()) {
                        continue;
                    }
                    ByteBuffer payload = messageAndOffset.message().payload();

                    String line = new String(payload.array(),payload.arrayOffset(),payload.limit(),"UTF-8");
                    CSVInvocation invocation = new CSVInvocation(m_procedure, line);
                    TopicPartitionInvocationCallback cb = new TopicPartitionInvocationCallback(currentOffset, messageAndOffset.nextOffset(), cbcnt);
                    m_pendingOffsets.add(currentOffset);
                    if (!callProcedure(cb, invocation)) {
                        if (isDebugEnabled()) {
                            debug("Failed to process Invocation possibly bad data: " + line);
                        }
                        synchronized(m_seenOffset) {
                            //Make this failed offset known to seen offsets so committer can push ahead.
                            m_seenOffset.add(messageAndOffset.nextOffset());
                        }
                        m_pendingOffsets.remove(currentOffset);
                    }
                    submitCount++;
                    m_currentOffset.set(messageAndOffset.nextOffset());
                    if (m_shutdown) {
                        break;
                    }
                }
                if (m_shutdown) {
                    break;
                }
                //wait to fetch more if we are in backpressure or read nothing last time.
                if (currentFetchCount == 0 || m_hasBackPressure) {
                    try {
                        Thread.sleep(m_backpressureSleepMs);
                    } catch (InterruptedException ie) {
                    }
                }
                commitOffset();
            }
            //Drain will make sure there is nothing in pending.
            info("Partition fecher stopped for " + m_topicAndPartition
                    + " Last commit point is: " + m_currentOffset.get()
                    + " Callback Rcvd: " + cbcnt.get()
                    + " Submitted: " + submitCount);
        } catch (Exception ex) {
            error("Failed to start topic partition fetcher for " + m_topicAndPartition, ex);
        } finally {
            commitOffset();
            closeConsumer(consumer);
            consumer = null;
            closeConsumer(m_offsetManager.getAndSet(null));
        }
    }
}
