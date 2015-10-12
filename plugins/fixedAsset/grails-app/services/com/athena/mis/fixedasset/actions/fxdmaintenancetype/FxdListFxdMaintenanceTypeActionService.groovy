package com.athena.mis.fixedasset.actions.fxdmaintenancetype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import com.athena.mis.fixedasset.utility.FxdMaintenanceTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * List of Maintenance Type.
 * For details go through Use-Case doc named 'FxdListFxdMaintenanceTypeActionService'
 */
class FxdListFxdMaintenanceTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    @Autowired
    FxdMaintenanceTypeCacheUtility fxdMaintenanceTypeCacheUtility

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load maintenance type grid"
    private static final String FXD_MAINTENANCE_TYPE_LIST = "fxdMaintenanceTypeList"
    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. set sort name & sort order for pagination
     * 2. initialize pagination
     * 3. pull maintenance type from cache utility
     * @param params - serialized parameters from UI
     * @param obj -N/A
     * @return - list of maintenance type
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object ob) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            if ((!parameters.sortname) || (parameters.sortname.toString().equals(ID))) {
                parameters.sortname = fxdMaintenanceTypeCacheUtility.SORT_ON_ID
                parameters.sortorder = DESCENDING_SORT_ORDER
            }
            initPager(parameters)
            int count = fxdMaintenanceTypeCacheUtility.count()
            List fxdMaintenanceTypeList = fxdMaintenanceTypeCacheUtility.list(this)
            result.put(Tools.COUNT, count)
            result.put(FXD_MAINTENANCE_TYPE_LIST, fxdMaintenanceTypeList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return null
        }
    }
    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. receive maintenance type from execute method
     * @param obj- object returned from execute method
     * @return - a map containing wrapped maintenance type for grid show
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<FxdMaintenanceType> fxdMaintenanceTypeList = (List<FxdMaintenanceType>) executeResult.get(FXD_MAINTENANCE_TYPE_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List maintenanceTypeList = wrapListInGridEntityList(fxdMaintenanceTypeList, start)
            result = [page: pageNumber, total: count, rows: maintenanceTypeList]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * wrap maintenance type for grid entity
     * @param fxdMaintenanceTypeList - list of maintenance type
     * @param start - starting point of index
     * @return - wrapped maintenance type for grid entity
     */
    private List wrapListInGridEntityList(List<FxdMaintenanceType> fxdMaintenanceTypeList, int start) {
        List lstMaintenanceType = []
        int counter = start + 1
        int len = 0
        if (fxdMaintenanceTypeList != null) {
            len = fxdMaintenanceTypeList.size()
        }
        for (int i = 0; i < len; i++) {
            FxdMaintenanceType fxdMaintenanceType = fxdMaintenanceTypeList[i]
            GridEntity obj = new GridEntity()
            obj.id = fxdMaintenanceType.id
            obj.cell = [
                    counter,
                    fxdMaintenanceType.id,
                    fxdMaintenanceType.name
            ]
            lstMaintenanceType << obj
            counter++
        }
        return lstMaintenanceType
    }
}
