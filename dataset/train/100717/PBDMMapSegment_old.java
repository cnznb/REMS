public class old{
    public void open(boolean forWrite) throws IOException {
        if (!m_closed) {
            throw new IOException("Segment is already opened");
        }
        if (!m_file.exists()) {
            m_syncedSinceLastEdit = false;
        }
        assert(m_ras == null);
        m_ras = new RandomAccessFile(m_file, "rw");
        m_fc = m_ras.getChannel();
        if (forWrite) {
            //If this is for writing, map the chunk size RW and put the buf positions at the start
            m_buf = DBBPool.wrapMBB(m_fc.map(MapMode.READ_WRITE, 0, CHUNK_SIZE));
            m_buf.b().position(SIZE_OFFSET + 4);
            m_readBuf = m_buf.b().duplicate();
            initNumEntries();
        } else {
            //If it isn't for write, map read only to the actual size and put the write buf position at the end
            //so size is reported correctly
            final long size = m_fc.size();
            m_buf = DBBPool.wrapMBB(m_fc.map(MapMode.READ_ONLY, 0, size));
            m_readBuf = m_buf.b().duplicate();
            m_buf.b().position((int) size);
            m_readBuf.position(SIZE_OFFSET + 4);
        }
        m_closed = false;
    }
}
