package com.athena.mis.document.actions.dbinstance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.document.entity.DocDbInstance
import com.athena.mis.document.utility.DocDBVendorCacheUtility
import com.athena.mis.document.utility.DocDbInstanceCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ShowDocDbInstanceActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load DB Instance list"
    private static final String LST_DB_INSTANCE = "lstDbInstance"
    private static final String GRID_OBJ = "gridObj"

    @Autowired
    DocDbInstanceCacheUtility docDbInstanceCacheUtility
    @Autowired
    DocDBVendorCacheUtility docDBVendorCacheUtility

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get the list of DB Instance
     * @param params - serialize parameters from UI
     * @param obj - N/A
     * @return result - A map of containing all object necessary for buildSuccessResultForUI
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {

            GrailsParameterMap parameters = (GrailsParameterMap) params
            initPager(parameters)
            List<DocDbInstance> lstDbInstance = docDbInstanceCacheUtility.list(this)
            int count = docDbInstanceCacheUtility.count()

            result.put(LST_DB_INSTANCE, lstDbInstance)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap list of DB Instance for grid
     * @param obj - a map returned from execute method
     * @return result - a map containing necessary information for show page
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            List<DocDbInstance> lstDbInstance = (List<DocDbInstance>) executeResult.get(LST_DB_INSTANCE)
            int total = (int) executeResult.get(Tools.COUNT)
            List<DocDbInstance> lstWrapDbInstance = wrapDbInstanceList(lstDbInstance, start)
            Map gridObject = [page: pageNumber, total: total, rows: lstWrapDbInstance]
            result.put(GRID_OBJ, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
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
     * Wrap list in grid entity list
     * @param lstDbInstance - list of DB Instance
     * @param start - starting index of the page
     * @return lstWrapDbInstance - list of wrap DB Instance
     */
    private List wrapDbInstanceList(List<DocDbInstance> lstDbInstance, int start) {
        List lstWrapDbInstance = []
        int counter = start + 1
        for (int i = 0; i < lstDbInstance.size(); i++) {
            DocDbInstance dbInstance = lstDbInstance[i]
            SystemEntity systemEntity = (SystemEntity) docDBVendorCacheUtility.read(dbInstance.vendorId)
            GridEntity obj = new GridEntity()
            obj.id = dbInstance.id
            obj.cell = [
                    counter,
                    dbInstance.id,
                    systemEntity.key,
                    dbInstance.instanceName,
                    dbInstance.driver,
                    dbInstance.connectionString
            ]
            lstWrapDbInstance << obj
            counter++
        }
        return lstWrapDbInstance
    }


}
