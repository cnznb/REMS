public class old{
    public void testSphereFnOptHelper( OptimizationAlgorithm oa, int numLineSearchIter, int nDimensions ){
		
		if( PRINT_OPT_RESULTS ) System.out.println("---------\n Alg=" + oa
				+ ", nIter=" + numLineSearchIter + ", nDimensions=" + nDimensions );
		
		NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder()
		.numLineSearchIterations(numLineSearchIter)
		.iterations(1000)
		.learningRate(0.01)
		.layer(new RBM()).batchSize(1).build();
		conf.addVariable("x");	//Normally done by ParamInitializers, but obviously that isn't done here 
		
		Random rng = new DefaultRandom(12345L);
		org.nd4j.linalg.api.rng.distribution.Distribution dist
			= new org.nd4j.linalg.api.rng.distribution.impl.UniformDistribution(rng,-10, 10);
		Model m = new SphereFunctionModel(nDimensions,dist,conf);
		
		double scoreBefore = m.score();
		assertTrue(!Double.isNaN(scoreBefore) && !Double.isInfinite(scoreBefore));
		if( PRINT_OPT_RESULTS ){
			System.out.println("Before:");
			System.out.println(scoreBefore);
			System.out.println(m.params());
		}

		ConvexOptimizer opt;
		switch(oa){
		case STOCHASTIC_GRADIENT_DESCENT:
			opt = new StochasticGradientDescent(conf,new DefaultStepFunction(),null,m);
			break;
		case LINE_GRADIENT_DESCENT:
			opt = new LineGradientDescent(conf,new DefaultStepFunction(),null,m);
			break;
		case CONJUGATE_GRADIENT:
			opt = new ConjugateGradient(conf,new DefaultStepFunction(),null,m);
			break;
		case LBFGS:
			opt = new LBFGS(conf,new DefaultStepFunction(),null,m);
			break;
		default:
			fail("Not supported: " + oa);	//Hessian free is NN-specific.
			opt = null;
			break;
		}

		opt.setupSearchState(m.gradientAndScore());
		opt.optimize();
		
		double scoreAfter = m.score();
		assertTrue(!Double.isNaN(scoreAfter) && !Double.isInfinite(scoreAfter));
		if( PRINT_OPT_RESULTS ){
			System.out.println("After:");
			System.out.println(scoreAfter);
			System.out.println(m.params());
		}
		
		//Expected behaviour after optimization:
		//(a) score is better (lower) after optimization.
		//(b) Parameters are closer to minimum after optimization (TODO)
		assertTrue("Score did not improve after optimization (b="+scoreBefore+",a="+scoreAfter+")",scoreAfter < scoreBefore);

	}
}
