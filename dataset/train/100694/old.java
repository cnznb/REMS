public class old{
    public void testBasicCreateIndex() throws Exception
    {
        String pathToCatalog = Configuration.getPathToCatalogForTest("adhocddl.jar");
        String pathToDeployment = Configuration.getPathToCatalogForTest("adhocddl.xml");
    
        VoltProjectBuilder builder = new VoltProjectBuilder();
        builder.addLiteralSchema(
                "create table FOO (" +
                "ID integer not null," +
                "VAL bigint, " +
                "constraint PK_TREE primary key (ID)" +
                ");\n" +
                "create table FOO_R (" +
                "ID integer not null," +
                "VAL bigint, " +
                "constraint PK_TREE_R primary key (ID)" +
                ");\n"
                );
        builder.addPartitionInfo("FOO", "ID");
        builder.setUseDDLSchema(true);
        boolean success = builder.compile(pathToCatalog, 2, 1, 0);
        assertTrue("Schema compilation failed", success);
        MiscUtils.copyFile(builder.getPathToDeployment(), pathToDeployment);
    
        VoltDB.Configuration config = new VoltDB.Configuration();
        config.m_pathToCatalog = pathToCatalog;
        config.m_pathToDeployment = pathToDeployment;
    
        try {
            startSystem(config);
    
            // Create index on partitioned tables
            assertFalse(findIndexInSystemCatalogResults("FOODEX"));
            try {
                m_client.callProcedure("@AdHoc",
                        "create index FOODEX on FOO (VAL);");
            }
            catch (ProcCallException pce) {
                pce.printStackTrace();
                fail("Should be able to create an index on a partitioned table");
            }
            assertTrue(findIndexInSystemCatalogResults("FOODEX"));
            // Create index on replicated tables
            assertFalse(findIndexInSystemCatalogResults("FOODEX_R"));
            try {
                m_client.callProcedure("@AdHoc",
                        "create index FOODEX_R on FOO_R (VAL);");
            }
            catch (ProcCallException pce) {
                pce.printStackTrace();
                fail("Should be able to create an index on a replicated table");
            }
            assertTrue(findIndexInSystemCatalogResults("FOODEX_R"));
            // Create unique index on partitioned tables
            assertFalse(findIndexInSystemCatalogResults("UNIQFOODEX"));
            boolean threw = false;
            try {
                m_client.callProcedure("@AdHoc",
                        "create assumeunique index UNIQFOODEX on FOO (VAL);");
            }
            catch (ProcCallException pce) {
                pce.printStackTrace();
                fail("Should be able to create a unique index on a partitioned table");
            }
            assertTrue(findIndexInSystemCatalogResults("UNIQFOODEX"));
            // Can create redundant unique index on a table
            try {
                m_client.callProcedure("@AdHoc",
                        "create unique index UNIQFOODEX2 on FOO (ID);");
            }
            catch (ProcCallException pce) {
                pce.printStackTrace();
                fail("Should be able to create redundant unique index");
            }
            // It's going to get dropped because it's redundant, so don't expect to see it here
            assertFalse(findIndexInSystemCatalogResults("UNIQFOODEX2"));
            // drop an index we added
            try {
                m_client.callProcedure("@AdHoc",
                        "drop index FOODEX;");
            }
            catch (ProcCallException pce) {
                pce.printStackTrace();
                fail("Should be able to drop an index");
            }
            assertFalse(findIndexInSystemCatalogResults("FOODEX"));
            // can't drop it twice
            threw = false;
            try {
                m_client.callProcedure("@AdHoc",
                        "drop index FOODEX;");
            }
            catch (ProcCallException pce) {
                threw = true;
            }
            assertTrue("Shouldn't be able to drop bad index without if exists", threw);
            assertFalse(findIndexInSystemCatalogResults("FOODEX"));
            // unless we use if exists
            try {
                m_client.callProcedure("@AdHoc",
                        "drop index FOODEX if exists;");
            }
            catch (ProcCallException pce) {
                pce.printStackTrace();
                fail("Should be able to drop a bad index with if exists");
            }
            assertFalse(findIndexInSystemCatalogResults("FOODEX"));
        }
        finally {
            teardownSystem();
        }
    }
}