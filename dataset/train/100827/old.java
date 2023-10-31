public class old{
    public Optional<BlockMeta> addBlockMeta(BlockMeta block) {
        long blockId = block.getBlockId();
        long blockSize = block.getBlockSize();
        if (getAvailableBytes() < blockSize) {
          LOG.error("Fail to create blockId {} in dir {}: {} bytes required, but {} bytes available",
              blockId, toString(), blockSize, getAvailableBytes());
          return Optional.absent();
        }
        if (hasBlockMeta(blockId)) {
          LOG.error("Fail to create blockId {} in dir {}: blockId exists", blockId, toString());
          return Optional.absent();
        }
        mBlockIdToBlockMap.put(blockId, block);
        mAvailableBytes -= blockSize;
        Preconditions.checkState(mAvailableBytes >= 0, "Available bytes should always be non-negative");
        return Optional.of(block);
    }
}
