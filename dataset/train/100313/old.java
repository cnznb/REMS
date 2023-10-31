public class old{
    public double optimize (double initialStep,INDArray parameters,INDArray gradients) throws InvalidStepException {
        double slope, test, alamin, alam, alam2, tmplam;
        double rhs1, rhs2, a, b, disc, oldAlam;double f, fold, f2;
        INDArray oldParameters = parameters.dup();
        INDArray gDup = gradients.dup();

        alam2 = 0.0;
        f2 = fold = optimizer.score();

        if (logger.isDebugEnabled()) {
            logger.trace ("ENTERING BACKTRACK\n");
            logger.trace("Entering BackTrackLinnSearch, value = " + fold + ",\ndirection.oneNorm:"
                    +	gDup.norm1(Integer.MAX_VALUE) + "  direction.infNorm:"+ FastMath.max(Float.NEGATIVE_INFINITY,abs(gDup).max(Integer.MAX_VALUE).getDouble(0)));
        }

        double sum = gradients.norm2(Integer.MAX_VALUE).getDouble(0);
        if(sum > stpmax) {
            logger.warn("attempted step too big. scaling: sum= " + sum +
                    ", stpmax= "+ stpmax);
            gradients.muli(stpmax / sum);
        }

        //dot product
        slope = Nd4j.getBlasWrapper().dot(gDup, gradients);
        logger.debug("slope = " + slope);

        if (slope < 0)
            throw new InvalidStepException("Slope = " + slope + " is negative");

        if (slope == 0)
            throw new InvalidStepException ("Slope = " + slope + " is zero");

        // find maximum lambda
        // converge when (delta x) / x < REL_TOLX for all coordinates.
        //  the largest step size that triggers this threshold is
        //  precomputed and saved in alamin
        INDArray maxOldParams = abs(oldParameters);
        Nd4j.getExecutioner().exec(new ScalarSetValue(maxOldParams,1));



        INDArray testMatrix = abs(gradients).divi(maxOldParams);
        test = testMatrix.max(Integer.MAX_VALUE).getDouble(0);

        alamin = relTolx / test;

        alam  = 1.0;
        oldAlam = 0.0;
        int iteration;
        // look for step size in direction given by "line"
        for(iteration = 0; iteration < maxIterations; iteration++) {
            // initially, alam = 1.0, i.e. take full Newton step
            logger.trace("BackTrack loop iteration " + iteration +" : alam=" + alam +" oldAlam=" + oldAlam);
            logger.trace ("before step, x.1norm: " + parameters.norm1(Integer.MAX_VALUE) +  "\nalam: " + alam + "\noldAlam: " + oldAlam);
            assert(alam != oldAlam) : "alam == oldAlam";
            if(stepFunction == null)
                stepFunction =  new DefaultStepFunction();
            //scale wrt updates
            stepFunction.step(parameters, gradients, new Object[]{alam,oldAlam}); //step

            if(logger.isDebugEnabled())  {
                double norm1 = parameters.norm1(Integer.MAX_VALUE).getDouble(0);
                logger.debug ("after step, x.1norm: " + norm1);
            }

            // check for convergence
            //convergence on delta x
            //if all of the parameters are < 1e-12

            if ((alam < alamin) || Nd4j.getExecutioner().execAndReturn(new Eps(oldParameters, parameters,
                    parameters.dup(), parameters.length())).sum(Integer.MAX_VALUE).getDouble(0) == parameters.length()) {
                function.setParams(oldParameters);
                function.setScore();
                f = function.score();
                logger.trace("EXITING BACKTRACK: Jump too small (alamin = "+ alamin + "). Exiting and using xold. Value = " + f);
                return 0.0;
            }

            function.setParams(parameters);
            oldAlam = alam;
            function.setScore();
            f = function.score();

            logger.debug("value = " + f);

            // sufficient function increase (Wolf condition)
            if(f >= fold + ALF * alam * slope) {

                logger.debug("EXITING BACKTRACK: value=" + f);

                if (f < fold)
                    throw new IllegalStateException
                            ("Function did not increase: f = " + f + " < " + fold + " = fold");
                return alam;
            }
            alam2 = alam;
            f2 = f;
            logger.debug("tmplam:" + tmplam);
            alam = Math.max(tmplam, .1f * alam);  // lambda >= .1*Lambda_1
        }
        return 0.0;
    }
}
