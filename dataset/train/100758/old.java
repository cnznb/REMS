public class old{
    public boolean callProcedure(ImportContext ic, String proc, Object... fieldList) {
        if (!m_stopped) {
            return VoltDB.instance().getClientInterface().getInternalConnectionHandler()
                    .callProcedure(ic.getBackpressureTimeout(), proc, fieldList);
        } else {
            m_logger.warn("Importer is in stopped state. Cannot execute procedures");
            return false;
        }
    }
}
