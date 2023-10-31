public class old{
    public  INDArray output(INDArray x) {
        if(x == null)
            throw new IllegalArgumentException("No null input allowed");

        INDArray preOutput = preOutput(x);
        if(conf.getActivationFunction().equals("softmax")) {
            INDArray ret = Nd4j.getExecutioner().execAndReturn(Nd4j.getOpFactory().createTransform("softmax", preOutput), 1);
            return ret;
        }

        this.input = x;
        return super.activate();

    }
}
