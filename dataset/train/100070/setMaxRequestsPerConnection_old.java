public class old{
    public PoolingOptions setMaxSimultaneousRequestsPerHostThreshold(HostDistance distance, int newMaxRequests) {
        if (newMaxRequests <= 0 || newMaxRequests > StreamIdGenerator.MAX_STREAM_PER_CONNECTION_V3)
            throw new IllegalArgumentException(String.format("Max requests must be in the range (1, %d)",
                                               StreamIdGenerator.MAX_STREAM_PER_CONNECTION_V3));

        switch (distance) {
            case LOCAL:
                maxSimultaneousRequestsPerHostLocal = newMaxRequests;
                break;
            case REMOTE:
                maxSimultaneousRequestsPerHostRemote = newMaxRequests;
                break;
            default:
                throw new IllegalArgumentException("Cannot set max requests per host for " + distance + " hosts");
        }
        return this;
    }
}
