public class old{
    public static Path depthFirstSearchForFile(final FileStatus[] statusArray,
        final FileSystem fileSystem) throws IOException {

      // Most recent files first
      Arrays.sort(statusArray,
          new Comparator<FileStatus>() {
            @Override
            public int compare(final FileStatus fs1, final FileStatus fs2) {
                return Longs.compare(fs2.getModificationTime(),fs1.getModificationTime());
              }
            }
      );

      for (FileStatus f : statusArray) {
        Path p = depthFirstSearchForFile(f, fileSystem);
        if (p != null) {
          return p;
        }
      }

      return null;
    }
}
