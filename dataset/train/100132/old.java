public class old{
    public static Word2Vec loadGoogleModel(File modelFile, boolean binary)
        throws IOException
    {
        return binary ? readBinaryModel(modelFile) : readTextModel(modelFile);
    }
}
