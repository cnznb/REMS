public class old{
    public static void main(String[] args) {
        int port = 9990;
        String host = "localhost";
        String protocol = "http-remoting";

        try {
            CommandLine line = parser.parse(options, args, false);

            if (line.hasOption("help")) {
                formatter.printHelp(usage, options);
                return;
            }
            if (line.hasOption("host")) {
                host = line.getOptionValue("host");
            }
            if (line.hasOption("port")) {
                port = Integer.parseInt(line.getOptionValue("port"));
            }
            if (line.hasOption("protocol")) {
                protocol = line.getOptionValue("protocol");
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp(usage, options);
            return;
        } catch (NumberFormatException nfe) {
            System.out.println(nfe.getMessage());
            formatter.printHelp(usage, options);
            return;
        }
        System.out.println("Initializing JBoss Diagnostic Reporter...");

        // Try to run JDR on the Wildfly JVM
        CLI cli = null;
        try {
            cli = CLI.newInstance();
            cli.connect(host, port, null, null);
            Result cmdResult = cli.cmd("/subsystem=jdr:generate-jdr-report()");
            ModelNode response = cmdResult.getResponse();
            reportFailure(response);
            ModelNode result = response.get(ClientConstants.RESULT);
            String startTime = result.get("start-time").asString();
            String endTime = result.get("end-time").asString();
            String reportLocation = result.get("report-location").asString();
            System.out.println("JDR started: " + startTime);
            System.out.println("JDR ended: " + endTime);
            System.out.println("JDR location: " + reportLocation);
        } catch(IllegalStateException ise) {
            System.out.println(ise.getMessage());

            // Unable to connect to a running server, so proceed without it
            JdrReportService reportService = new JdrReportService();

            JdrReport response = null;
            try {
                response = reportService.standaloneCollect(protocol, host, String.valueOf(port));
                System.out.println("JDR started: " + response.getStartTime().toString());
                System.out.println("JDR ended: " + response.getEndTime().toString());
                System.out.println("JDR location: " + response.getLocation());
            } catch (OperationFailedException e) {
                System.out.println("Failed to complete the JDR report: " + e.getMessage());
            }
        } finally {
            if(cli != null) {
                try {
                    cli.disconnect();
                } catch(Exception e) {
                    System.out.println("Caught exception while disconnecting: " + e.getMessage());
                }
            }
        }
        System.exit(0);
    }
}
