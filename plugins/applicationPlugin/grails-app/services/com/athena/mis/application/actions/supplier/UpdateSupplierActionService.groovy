package com.athena.mis.application.actions.supplier

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SupplierService
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update supplier object and to show on grid list
 *  For details go through Use-Case doc named 'UpdateSupplierActionService'
 */
class UpdateSupplierActionService extends BaseService implements ActionIntf {

    private static final String UPDATE_FAILURE_MESSAGE = "Supplier could not be updated"
    private static final String UPDATE_SUCCESS_MESSAGE = "Supplier has been updated successfully"
    private static final String SUPPLIER_NOT_FOUND = "Supplier not found"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String NAME_ALREADY_EXISTS = "Same supplier name already exists"
    private static final String SUPPLIER = "supplier"

    private final Logger log = Logger.getLogger(getClass())

    SupplierService supplierService
    SystemEntityService systemEntityService
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * Check different criteria to update supplier object
     *      1) Check access permission to update supplier object
     *      2) Check existence of selected supplier object
     *      3) Validate supplier object to update
     *      4) Check duplicate supplier name
     * @param params -N/A
     * @param obj -Supplier object send from controller
     * @return -a map containing supplier object for execute method
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            //Check parameters
            if ((!params.id) || (!params.version) || (!params.name)) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            long id = Long.parseLong(params.id.toString())
            int version = Integer.parseInt(params.version.toString())

            //Check existing of Obj and version matching
            Supplier oldSupplier = (Supplier) supplierCacheUtility.read(id)
            if ((!oldSupplier) || (oldSupplier.version != version)) {
                result.put(Tools.MESSAGE, SUPPLIER_NOT_FOUND)
                return result
            }
            // Check existing of same vehicle name
            String name = params.name.toString()
            int duplicateCount = supplierCacheUtility.countByNameIlikeAndIdNotEqual(name, oldSupplier.id)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, NAME_ALREADY_EXISTS)
                return result
            }
            Supplier supplier = buildObject(params, oldSupplier)

            result.put(SUPPLIER, supplier)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * update supplier object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -supplierObject send from controller
     * @return -updated supplier object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Supplier supplierInstance = (Supplier) preResult.get(SUPPLIER)
            supplierService.update(supplierInstance)//update in DB
            //update in cache and keep the data sorted
            supplierCacheUtility.update(supplierInstance, supplierCacheUtility.SORT_ON_NAME, supplierCacheUtility.SORT_ORDER_ASCENDING)
            result.put(SUPPLIER, supplierInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap updated supplier object to show on grid
     * @param obj -updated supplier object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Supplier supplier = (Supplier) executeResult.get(SUPPLIER)
            SystemEntity supplierType = systemEntityService.read(supplier.supplierTypeId)
            GridEntity object = new GridEntity()
            object.id = supplier.id
            object.cell = [
                    Tools.LABEL_NEW,
                    supplier.id,
                    supplierType.key,
                    supplier.name,
                    supplier.accountName,
                    supplier.address ? supplier.address : Tools.EMPTY_SPACE,
                    supplier.bankAccount ? supplier.bankAccount : Tools.EMPTY_SPACE,
                    supplier.itemCount
            ]
            Map resultMap = [entity: object, version: supplier.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(SUPPLIER, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build Supplier object
     * @param parameterMap -serialized parameters from UI
     * @param oldSupplier -object of Supplier
     * @return -new Supplier object
     */
    private Supplier buildObject(GrailsParameterMap params, Supplier oldSupplier) {
        Supplier supplier = new Supplier(params)
        oldSupplier.name = supplier.name
        oldSupplier.address = supplier.address
        oldSupplier.accountName = supplier.accountName
        oldSupplier.bankAccount = supplier.bankAccount
        oldSupplier.bankName = supplier.bankName
        oldSupplier.supplierTypeId = supplier.supplierTypeId
        oldSupplier.companyId = appSessionUtil.getCompanyId()
        oldSupplier.updatedBy = appSessionUtil.getAppUser().id
        oldSupplier.updatedOn = new Date()
        return oldSupplier
    }
}
