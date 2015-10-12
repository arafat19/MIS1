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
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for supplier CRUD and list of supplier for grid
 *  For details go through Use-Case doc named 'ShowSupplierActionService'
 */
class ShowSupplierActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load supplier page"
    private static final String SUPPLIER_LIST = "supplierList"

    SystemEntityService systemEntityService
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * check access permission to show supplier CRUD page
     * @param parameters -N/A
     * @param obj -N/A
     * @return -boolean value of access permission(true/false)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            //Only admin has right to show supplier page
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * get list of supplierObjects
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map contains supplierList and count
     */
    public Object execute(Object params, Object obj) {
        try {
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
     * @param obj -a map contains supplierObjectList and count receives from execute method
     * @return -wrapped supplierObjectList to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            List<Supplier> supplierList = (List<Supplier>) executeResult.supplierList
            int count = (int) executeResult.count
            List resultSupplierList = wrapListInGridEntityList(supplierList, start)
            Map output = [page: pageNumber, total: count, rows: resultSupplierList]
            result.put(SUPPLIER_LIST, output)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
     * @param supplierList - list of supplier objects
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
