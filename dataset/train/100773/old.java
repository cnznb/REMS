public class old{
    int run(final Namespace options, final HeliosClient client, final PrintStream out,
          final boolean json, final BufferedReader stdin)
      throws ExecutionException, InterruptedException {
    final String name = options.getString(nameArg.getDest());
    final boolean full = options.getBoolean(fullArg.getDest());

    final DeploymentGroupStatusResponse status = client.deploymentGroupStatus(name).get();

    if (status == null) {
      if (json) {
        final Map<String, Object> output = Maps.newHashMap();
        output.put("status", "DEPLOYMENT_GROUP_NOT_FOUND");
        out.print(Json.asStringUnchecked(output));
      } else {
        out.printf("Unknown deployment group: %s%n", name);
      }
      return 1;
    }
    if (json) {
      out.println(Json.asPrettyStringUnchecked(status));
    } else {
      final JobId jobId = status.getJobId();
      final String error = status.getError();
      out.printf("Name: %s%n", name);
      out.printf("Job Id: %s%n", full ? jobId : jobId.toShortString());
      out.printf("Status: %s%n", status.getStatus());
      if (!Strings.isNullOrEmpty(error)) {
        out.printf("Error: %s%n", error);
      }
      out.printf("%n");
      printTable(out, jobId, status.getHostStatuses(), full);
    }
    return 0;
  }
}
