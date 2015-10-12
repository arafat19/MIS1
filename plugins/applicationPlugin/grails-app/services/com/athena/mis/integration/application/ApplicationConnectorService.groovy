package com.athena.mis.integration.application

import com.athena.mis.PluginConnector

class ApplicationConnectorService extends PluginConnector{

    static transactional = false
    static lazyInit = false

    // registering plugin in servletContext
    @Override
    public boolean initialize() {
        setPlugin(APPLICATION, this);
        return true
    }
    @Override
    public String getName() {
        return PluginConnector.APPLICATION;
    }
    @Override
    public int getId() {
        return PluginConnector.APPLICATION_ID;
    }
}
