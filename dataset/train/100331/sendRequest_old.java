public class old{
    void sendRequest() {
        try {
            Host host;
            while (!isDone.get() && (host = queryPlan.next()) != null && !queryStateRef.get().isCancelled()) {
                if(logger.isTraceEnabled())
                    logger.trace("[{}] Querying node {}", id, host);
                if (query(host))
                    return;
            }
            reportNoMoreHosts(this);
        } catch (Exception e) {
            // Shouldn't happen really, but if ever the loadbalancing policy returned iterator throws, we don't want to block.
            setFinalException(null, new DriverInternalError("An unexpected error happened while sending requests", e));
        }
    }
}
