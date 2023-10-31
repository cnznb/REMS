public class old{
    public synchronized PoolingOptions setMaxSimultaneousRequestsPerConnectionThreshold(HostDistance distance, int newMaxSimultaneousRequests) {
        if (distance == HostDistance.IGNORED)
            throw new IllegalArgumentException("Cannot set max simultaneous requests per connection threshold for " + distance + " hosts");

        checkRequestsPerConnectionRange(newMaxSimultaneousRequests, "Max simultaneous requests per connection", distance);
        maxSimultaneousRequestsPerConnection[distance.ordinal()] = newMaxSimultaneousRequests;
        return this;
    }
}
