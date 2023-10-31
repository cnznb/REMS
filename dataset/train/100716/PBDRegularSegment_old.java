public class old{
    public void open(boolean forWrite) throws IOException
    {
        if (!m_closed) {
            throw new IOException("Segment is already opened");
        }
        if (!m_file.exists()) {
            if (!forWrite) {
                throw new IOException("File " + m_file + " does not exist");
            }
            m_syncedSinceLastEdit = false;
        }
        assert(m_ras == null);
        m_ras = new RandomAccessFile( m_file, forWrite ? "rw" : "r");
        m_fc = m_ras.getChannel();
        m_tmpHeaderBuf = DBBPool.allocateDirect(SEGMENT_HEADER_BYTES);

        if (forWrite) {
            initNumEntries();
        }
        m_fc.position(SEGMENT_HEADER_BYTES);

        m_closed = false;
    }
}
