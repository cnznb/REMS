public class old{
    public int getMaxSimultaneousRequestsPerConnectionThreshold(HostDistance distance) {
        return maxSimultaneousRequestsPerConnection[distance.ordinal()];
    }
}
