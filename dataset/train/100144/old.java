public class old{
    public static NetParameter readCaffeModel(String caffeModelPath, int sizeLimitMb) throws IOException {
        InputStream is = new FileInputStream(caffeModelPath);
        CodedInputStream codeStream = CodedInputStream.newInstance(is);
        // Increase the limit when loading bigger caffemodels size
        int oldLimit = codeStream.setSizeLimit(sizeLimitMb * 1024 * 1024);
        return NetParameter.parseFrom(codeStream);
    }
}
