package com.athena.mis.fixedasset.actions.fxdmaintenancetype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import com.athena.mis.fixedasset.utility.FxdMaintenanceTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Search Maintenance Type.
 * For details go through Use-Case doc named 'FxdSearchFxdMaintenanceTypeActionService'
 */
class FxdSearchFxdMaintenanceTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    FxdMaintenanceTypeCacheUtility fxdMaintenanceTypeCacheUtility
    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to search maintenance type grid"
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
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = fxdMaintenanceTypeCacheUtility.SORT_ON_ID
                params.sortorder = fxdMaintenanceTypeCacheUtility.SORT_ORDER_DESCENDING
            }
            initSearch(params)
            Map searchResult = fxdMaintenanceTypeCacheUtility.search(queryType, query, this)
            List typeList = searchResult.list
            int total = searchResult.count
            result.put(Tools.COUNT, total)
            result.put(FXD_MAINTENANCE_TYPE_LIST, typeList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List<FxdMaintenanceType> typeList = (List<FxdMaintenanceType>) receiveResult.get(FXD_MAINTENANCE_TYPE_LIST)
            List maintenanceTypeList = wrapListInGridEntityList(typeList, start)
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
     * @param maintenanceTypeList - list of maintenance type
     * @param start - starting point of index
     * @return - wrapped maintenance type for grid entity
     */
    private List wrapListInGridEntityList(List<FxdMaintenanceType> maintenanceTypeList, int start) {
        List lstType = []
        int counter = start + 1
        for (int i = 0; i < maintenanceTypeList.size(); i++) {
            FxdMaintenanceType maintenanceType = maintenanceTypeList[i]
            GridEntity obj = new GridEntity()
            obj.id = maintenanceType.id
            obj.cell = [
                    counter,
                    maintenanceType.id,
                    maintenanceType.name
            ]
            lstType << obj
            counter++
        }
        return lstType
    }
}
