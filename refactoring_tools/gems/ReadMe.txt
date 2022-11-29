1. Install the plugin in Ecliplse Luna using the following steps:
    a. Copy the UI_1.0.0.201706122202.jar file to ecliplse/plugins folder
    b. Edit the bundles.info file in folder eclipse./configuration/org.eclipse.equinox.simpleconfigurator/bundles.info
       and add the following line UI,1.0.0.201706122202,plugins/UI_1.0.0.201706122202.jar,4,false
    c. Create a UI.prefs file in folder workspace/.metadata/.plugins/org.eclipse.core.runtime/.settings and add 
       PYTHON_PATH=<your path to python> 
       e.g. /usr/local/bin/python
       eclipse.preferences.version=1

2. Copy the gems.pickle.dat, gems.py and normailize.out files to the folder workspace/gems/