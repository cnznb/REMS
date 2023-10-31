public class old{
    public DeploymentGroup getDeploymentGroup(final String name)
        throws DeploymentGroupDoesNotExistException {
        log.debug("getting deployment-group: {}", name);
        final ZooKeeperClient client = provider.get("getDeploymentGroup");
        try {
        final byte[] data = client.getData(Paths.configDeploymentGroups(name));
        return Json.read(data, DeploymentGroup.class);
        } catch (NoNodeException e) {
        throw new DeploymentGroupDoesNotExistException(name);
        } catch (KeeperException | IOException e) {
        throw new HeliosRuntimeException("getting deployment-group " + name + " failed", e);
        }
    }
}
