public class old{
    public void setMpDRGateway(final PartitionDRGateway mpGateway)
    {
        m_drGatewayMP = mpGateway;
        if (m_cl != null) {
            m_durabilityListener.createFirstCompletionCheck(m_cl.isSynchronous(), m_drGatewayMP != null);
        }
    }
}
