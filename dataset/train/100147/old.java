public class old{
    private RollingUpdateTaskResult rollingUpdateAwaitRunning(final DeploymentGroup deploymentGroup,
    final String host) {
        final ZooKeeperClient client = provider.get("rollingUpdateAwaitRunning");
        final Map<JobId, TaskStatus> taskStatuses = getTaskStatuses(client, host);

        if (!taskStatuses.containsKey(deploymentGroup.getJobId())) {
        // job hasn't shown up yet, probably still being written
        return RollingUpdateTaskResult.TASK_IN_PROGRESS;
        } else if (!taskStatuses.get(deploymentGroup.getJobId()).getState()
        .equals(TaskStatus.State.RUNNING)) {
        // job isn't running yet

        try {
        final String statusPath = Paths.statusDeploymentGroup(deploymentGroup.getName());
        final long secondsSinceDeploy = MILLISECONDS.toSeconds(
        System.currentTimeMillis() - client.getNode(statusPath).getStat().getMtime());
        if (secondsSinceDeploy > deploymentGroup.getRolloutOptions().getTimeout()) {
        // time exceeding the configured deploy timeout has passed, and this job is still not
        // running
        return RollingUpdateTaskResult.error("timed out waiting for job to reach RUNNING");
        }
        } catch (KeeperException e) {
        // statusPath doesn't exist or some other ZK issue. probably this deployment group
        // was removed.
        log.warn("error determining deployment group modification time: {} - {}",
        deploymentGroup.getName(), e);
        }

        return RollingUpdateTaskResult.TASK_IN_PROGRESS;
        } else {
        // the job is running on the host. last thing we have to ensure is that it was
        // deployed by this deployment group. otherwise some weird conflict has occurred and we
        // won't be able to undeploy the job on the next update.
        final Deployment deployment = getDeployment(host, deploymentGroup.getJobId());
        if (deployment == null) {
        return RollingUpdateTaskResult.error("deployment for this job is very broken in ZK");
        } else if (!Objects.equals(deployment.getDeploymentGroupName(), deploymentGroup.getName())) {
        return RollingUpdateTaskResult.error("job was already deployed, either manually or by a " +
            "different deployment group");
        }
        return RollingUpdateTaskResult.TASK_COMPLETE;
        }
    }
}
