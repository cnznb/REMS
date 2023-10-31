public class old{
    public PigStats launchPig(PhysicalPlan physicalPlan, String grpName,
			PigContext pigContext) throws Exception {
		if (LOG.isDebugEnabled())
		    LOG.debug(physicalPlan);
        this.pigContext = pigContext;
        saveUdfImportList(pigContext);
		JobConf jobConf = SparkUtil.newJobConf(pigContext);
		jobConf.set(PigConstants.LOCAL_CODE_DIR,
				System.getProperty("java.io.tmpdir"));

		SchemaTupleBackend.initialize(jobConf, pigContext);
		SparkOperPlan sparkplan = compile(physicalPlan, pigContext);
		if (LOG.isDebugEnabled())
			  explain(sparkplan, System.out, "text", true);
		SparkPigStats sparkStats = (SparkPigStats) pigContext
				.getExecutionEngine().instantiatePigStats();
		PigStats.start(sparkStats);
		startSparkIfNeeded(pigContext);
		// Set a unique group id for this query, so we can lookup all Spark job
		// ids
		// related to this query.
		jobGroupID = UUID.randomUUID().toString();
		sparkContext.setJobGroup(jobGroupID, "Pig query to Spark cluster",
				false);
		jobMetricsListener.reset();
		this.currentDirectoryPath = Paths.get(".").toAbsolutePath()
				.normalize().toString()
				+ "/";
		startSparkJob();
		LinkedList<POStore> stores = PlanHelper.getPhysicalOperators(
				physicalPlan, POStore.class);
		POStore firstStore = stores.getFirst();
		if (firstStore != null) {
			MapRedUtil.setupStreamingDirsConfSingle(firstStore, pigContext,
					jobConf);
		}
		new ParallelismSetter(sparkplan, jobConf).visit();
		byte[] confBytes = KryoSerializer.serializeJobConf(jobConf);
		// Create conversion map, mapping between pig operator and spark convertor
		Map<Class<? extends PhysicalOperator>, RDDConverter> convertMap
				= new HashMap<Class<? extends PhysicalOperator>, RDDConverter>();
		convertMap.put(POLoad.class, new LoadConverter(pigContext,
				physicalPlan, sparkContext.sc()));
		convertMap.put(POStore.class, new StoreConverter(pigContext));
		convertMap.put(POForEach.class, new ForEachConverter(confBytes));
		convertMap.put(POFilter.class, new FilterConverter());
		convertMap.put(POPackage.class, new PackageConverter(confBytes));
		convertMap.put(POLocalRearrange.class, new LocalRearrangeConverter());
        convertMap.put(POGlobalRearrangeSpark.class, new GlobalRearrangeConverter());
        convertMap.put(POLimit.class, new LimitConverter());
        convertMap.put(PODistinct.class, new DistinctConverter());
		convertMap.put(POUnion.class, new UnionConverter(sparkContext.sc()));
		convertMap.put(POSort.class, new SortConverter());
		convertMap.put(POSplit.class, new SplitConverter());
		convertMap.put(POSkewedJoin.class, new SkewedJoinConverter());
		convertMap.put(POMergeJoin.class, new MergeJoinConverter());
		convertMap.put(POCollectedGroup.class, new CollectedGroupConverter());
		convertMap.put(POCounter.class, new CounterConverter());
		convertMap.put(PORank.class, new RankConverter());
		convertMap.put(POStream.class, new StreamConverter(confBytes));
                convertMap.put(POFRJoin.class, new FRJoinConverter());
		sparkPlanToRDD(sparkplan, convertMap, sparkStats, jobConf);
		cleanUpSparkJob();
		sparkStats.finish();
		return sparkStats;
	}
}
