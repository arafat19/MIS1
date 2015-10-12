package com.athena.mis.application.actions.supplier

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get list of supplierObject to show on grid
 *  For details go through Use-Case doc named 'ListSupplierActionService'
 */
class ListSupplierActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load supplier grid list"

    SystemEntityService systemEntityService
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * check access permission to get supplierList
     * @param parameters -N/A
     * @param obj -N/A
     * @return -boolean value of access permission(true/false)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            //Only admin has right to get supplierObjectList
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        }
    }

    /**
     * get list of supplierObjects
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains supplierList and count
     */
    public Object execute(Object parameters, Object obj = null) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = supplierCacheUtility.SORT_ON_NAME
                params.sortorder = supplierCacheUtility.SORT_ORDER_ASCENDING
            }
            initPager(params)
            int count = supplierCacheUtility.count()
            List supplierList = supplierCacheUtility.list(this)
            return [supplierList: supplierList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
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
     * wrap supplierObjectList to show on grid
     * @param obj -a map contains supplierObjectList and count
     * @return -wrapped supplierObjectList to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<Supplier> supplierList = (List<Supplier>) executeResult.supplierList
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * wrappedSupplierObjectList for grid
     * @param supplierList -list of supplier objects
     * @param start -start index
     * @return -wrappedSupplierObjectList
     */
    private List wrapListInGridEntityList(List<Supplier> supplierList, int start) {
        List suppliers = [] as List
        int counter = start + 1
        for (int i = 0; i < supplierList.size(); i++) {
            Supplier supplier = supplierList[i]
            SystemEntity supplierType = systemEntityService.read(supplier.supplierTypeId)
            GridEntity obj = new GridEntity()
            obj.id = supplier.id
            obj.cell = [
                    counter,
                    supplier.id,
                    supplierType.key,
                    supplier.name,
                    supplier.accountName,
                    supplier.address ? supplier.address : Tools.EMPTY_SPACE,
                    supplier.bankAccount ? supplier.bankAccount : Tools.EMPTY_SPACE,
                    supplier.itemCount
            ]
            suppliers << obj
            counter++
        }
        return suppliers
    }
}
