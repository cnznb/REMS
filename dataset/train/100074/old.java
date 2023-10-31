public class old{
    public void configureScanner ()
    throws MojoExecutionException
    {
        // start the scanner thread (if necessary) on the main webapp
        scanList = new ArrayList<File>();
        if (webApp.getDescriptor() != null)
        {
            try (Resource r = Resource.newResource(webApp.getDescriptor());)
            {
                scanList.add(r.getFile());
            }
            catch (IOException e)
            {
                throw new MojoExecutionException("Problem configuring scanner for web.xml", e);
            }
        }

        if (webApp.getJettyEnvXml() != null)
        {
            try (Resource r = Resource.newResource(webApp.getJettyEnvXml());)
            {
                scanList.add(r.getFile());
            }
            catch (IOException e)
            {
                throw new MojoExecutionException("Problem configuring scanner for jetty-env.xml", e);
            }
        }

        if (webApp.getDefaultsDescriptor() != null)
        {
            try (Resource r = Resource.newResource(webApp.getDefaultsDescriptor());)
            {
                if (!WebAppContext.WEB_DEFAULTS_XML.equals(webApp.getDefaultsDescriptor()))
                    scanList.add(r.getFile());
            }
            catch (IOException e)
            {
                throw new MojoExecutionException("Problem configuring scanner for webdefaults.xml", e);
            }
        }

        if (webApp.getOverrideDescriptor() != null)
        {
            try (Resource r = Resource.newResource(webApp.getOverrideDescriptor());)
            {
                scanList.add(r.getFile());
            }
            catch (IOException e)
            {
                throw new MojoExecutionException("Problem configuring scanner for webdefaults.xml", e);
            }
        }


        File jettyWebXmlFile = findJettyWebXmlFile(new File(webAppSourceDirectory,"WEB-INF"));
        if (jettyWebXmlFile != null)
            scanList.add(jettyWebXmlFile);
        scanList.addAll(extraScanTargets);
        scanList.add(project.getFile());
        if (webApp.getTestClasses() != null)
            scanList.add(webApp.getTestClasses());
        if (webApp.getClasses() != null)
        scanList.add(webApp.getClasses());
        scanList.addAll(webApp.getWebInfLib());

        scannerListeners = new ArrayList<Scanner.BulkListener>();
        scannerListeners.add(new Scanner.BulkListener()
        {
            public void filesChanged (List changes)
            {
                try
                {
                    boolean reconfigure = changes.contains(project.getFile().getCanonicalPath());
                    restartWebApp(reconfigure);
                }
                catch (Exception e)
                {
                    getLog().error("Error reconfiguring/restarting webapp after change in watched files",e);
                }
            }
        });
    }
}
