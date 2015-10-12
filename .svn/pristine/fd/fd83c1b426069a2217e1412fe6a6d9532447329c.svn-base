package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppSysConfigurationCacheUtility
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.integration.document.DocumentPluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/*Renders the value if sysConfiguration object exists*/

class ShowSysConfigTagLibActionService extends BaseService implements ActionIntf {

    private static final String KEY = 'key'
    private static final String PLUGIN_ID = 'pluginId'

    private Logger log = Logger.getLogger(getClass())

    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    AppSysConfigurationCacheUtility appSysConfigurationCacheUtility

    /**
     * Do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Returns value if system configuration exists
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - value of sysConfig or null depending on existence of object
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Map attrs = (Map) parameters
            String key = attrs.get(KEY)
            String strPluginId = attrs.get(PLUGIN_ID)
            if ((!key) || (key.length() == 0) || (!strPluginId) || (strPluginId.length() == 0)) {
                return null
            }
            long pluginId = Long.parseLong(strPluginId)
            long companyId = appSessionUtil.getCompanyId()
            SysConfiguration sysConfiguration = getObjectOfSysConfig(key, pluginId, companyId)
            if (!sysConfiguration) {
                return null
            }
            return sysConfiguration.value
        } catch (Exception e) {
            log.error(e.message)
            return null
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success result
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * get object of sysConfiguration
     * @param key - key of sysConfiguration
     * @param pluginId - plugin id
     * @param companyId -id of company
     * @return - object of sysConfiguration
     */
    private SysConfiguration getObjectOfSysConfig(String key, long pluginId, long companyId) {
        SysConfiguration sysConfiguration
        switch (pluginId) {
            case PluginConnector.APPLICATION_ID:
                sysConfiguration = (SysConfiguration) appSysConfigurationCacheUtility.readByKeyAndCompanyId(key)
                break
            case PluginConnector.DOCUMENT_ID:
                sysConfiguration = (SysConfiguration) documentImplService.readSysConfig(key, companyId)
                break
            default:
                return null
        }
        return sysConfiguration
    }
}
