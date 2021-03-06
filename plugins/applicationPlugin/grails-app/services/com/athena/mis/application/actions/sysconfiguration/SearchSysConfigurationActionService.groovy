package com.athena.mis.application.actions.sysconfiguration

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.SysConfigurationService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get search result list of SysConfiguration object(s) to show on grid
 *  For details go through Use-Case doc named 'SearchSysConfigurationActionService'
 */
class SearchSysConfigurationActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    SysConfigurationService sysConfigurationService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to search System-Configuration list"
    private static final String SYS_CONFIGURATION_LIST = "sysConfigurationList"
    private static final String SORT_BY_KEY = "key"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initSearch(params)
            // if url found no param of plugin then set application plugin as default
            String plugin = params.plugin ? params.plugin : 1
            int pluginId = Integer.parseInt(plugin)

            // if plugin id is not desired then redirect to application plugin as default
            if (pluginId >= 14) {
                pluginId = 1
            }
            // considering search with only key
            String queryOnName = Tools.PERCENTAGE + query + Tools.PERCENTAGE
            long companyId = appSessionUtil.getCompanyId()
            List sysConList = sysConfigurationService.findAllByKeyAndCompanyIdAndPluginId(queryOnName, companyId, pluginId, this)
            int count = sysConfigurationService.countByKeyAndCompanyIdAndPluginId(queryOnName, companyId, pluginId)
            result.put(SYS_CONFIGURATION_LIST, sysConList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }
    }

    /**
     * do nothing for pre condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrapped sysConfiguration list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<SysConfiguration> sysConList = (List<SysConfiguration>) executeResult.get(SYS_CONFIGURATION_LIST)
            int count = (int) executeResult.get(Tools.COUNT)

            List resultList = wrapListInGridEntityList(sysConList, start)
            return [page: pageNumber, total: count, rows: resultList]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null]
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * wrapped SysConfiguration object list to show on grid
     * @param sysConList -list of SysConfiguration object(s)
     * @param start -starting index
     * @return -list of wrapped SysConfiguration object(s)
     */
    private List wrapListInGridEntityList(List<SysConfiguration> sysConList, int start) {
        List lstSysConfig = [] as List
        int counter = start + 1
        for (int i = 0; i < sysConList.size(); i++) {
            GridEntity obj = new GridEntity()
            obj.id = sysConList[i].id
            String description = Tools.makeDetailsShort(sysConList[i].description, Tools.DEFAULT_LENGTH_DETAILS_OF_SYS_CONFIG)
            obj.cell = [
                    counter, sysConList[i].id,
                    sysConList[i].key,
                    sysConList[i].value,
                    description
            ]
            lstSysConfig << obj
            counter++
        }
        return lstSysConfig
    }

}
