public class old{
    private static void fillTable(Client client, String tbl) throws Exception {
        Random r = new Random(777);
        for (int i = 0; i < 1000; ++i) {

            double d;
            do {
                d = r.nextGaussian() * 1000;
            } while (d > Long.MAX_VALUE || d <= Long.MIN_VALUE);

            long val = (long) d;
            client.callProcedure(tbl + ".Insert", i, val);
        }
    }
}
