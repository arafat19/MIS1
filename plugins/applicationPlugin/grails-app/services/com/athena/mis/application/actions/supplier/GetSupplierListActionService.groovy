package com.athena.mis.application.actions.supplier

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get wrappedList of supplier object to show on right grid of : Procurement->SupplierWise PO report
 *  For details go through Use-Case doc named 'GetSupplierListActionService'
 */
class GetSupplierListActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to get supplier list"

    @Autowired
    SupplierCacheUtility supplierCacheUtility

    /**
     * do nothing at pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get list of supplier objects
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map contains supplierList & count for buildSuccessResultForUI
     */
    public Object execute(Object params, Object obj) {
        try {
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = supplierCacheUtility.SORT_ON_NAME
                params.sortorder = ASCENDING_SORT_ORDER
            }
            initSearch(params)
            List supplierList
            int count
            if (query) {
                Map lstResult = supplierCacheUtility.search(queryType, query, this)
                supplierList = lstResult.list
                count = lstResult.count
            } else {
                supplierList = supplierCacheUtility.list(this)
                count = supplierCacheUtility.count()
            }
            return [supplierList: supplierList, count: count]
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrapped supplier object list to show on grid
     * @param obj -a map contains supplierList and count
     * @return -a map contains wrapped supplierList to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<Object> supplierList = (List) executeResult.supplierList
            int count = (int) executeResult.count
            List wrapSupplierList = wrapListInGridEntityList(supplierList, start)
            return [page: pageNumber, total: count, rows: wrapSupplierList]
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
            if (receiveResult.message) {
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
     * wrappedSupplierObjectList for grid
     * @param supplierList -list of supplier objects
     * @param start -start index
     * @return -wrappedSupplierObjectList
     */
    private List wrapListInGridEntityList(List<Object> supplierList, int start) {
        List lstSupplier = [] as List
        int counter = start + 1
        for (int i = 0; i < supplierList.size(); i++) {
            Object supplier = supplierList[i]
            GridEntity obj = new GridEntity()
            obj.id = supplier.id
            obj.cell = [counter, supplier.name]
            lstSupplier << obj
            counter++
        }
        return lstSupplier
    }
}
