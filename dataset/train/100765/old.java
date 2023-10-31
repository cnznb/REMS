public class old{
    PreparedStatement getPreparedQuery(QueryType type, Option... options) {
        String queryString;
        queryString = type.makePreparedQueryString(tableMetadata, mapper, options);
        PreparedStatement stmt = preparedQueries.get(queryString);
        if (stmt == null) {
            synchronized (preparedQueries) {
                stmt = preparedQueries.get(queryString);
                if (stmt == null) {
                    logger.debug("Preparing query {}", queryString);
                    stmt = session().prepare(queryString);
                    Map<String, PreparedStatement> newQueries = new HashMap<String, PreparedStatement>(preparedQueries);
                    newQueries.put(queryString, stmt);
                    preparedQueries = newQueries;
                }
            }
        }
        return stmt;
    }
}
