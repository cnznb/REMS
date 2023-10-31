public class old{
    private void retry(final boolean retryCurrent, ConsistencyLevel newConsistencyLevel) {
        final Host h = current;
        this.retryConsistencyLevel = newConsistencyLevel;

        // We should not retry on the current thread as this will be an IO thread.
        manager.executor().execute(new Runnable() {
            @Override
            public void run() {
                if (queryStateRef.get().isCancelled())
                    return;
                try {
                    if (retryCurrent) {
                        if (query(h))
                            return;
                    }
                    sendRequest();
                } catch (Exception e) {
                    setFinalException(null, new DriverInternalError("Unexpected exception while retrying query", e));
                }
            }
        });
    }
}
