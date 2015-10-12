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
 *  Class to create supplier object and to show on grid list
 *  For details go through Use-Case doc named 'CreateSupplierActionService'
 */
class CreateSupplierActionService extends BaseService implements ActionIntf {

    private static final String SAVE_SUCCESS_MESSAGE = "Supplier has been saved successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Supplier could not be saved"
    private static final String NAME_ALREADY_EXISTS = "Same supplier name already exists"
    private static final String INVALID_INPUT_MSG = "Failed to create supplier due to invalid input"
    private static final String SUPPLIER = "supplier"

    private final Logger log = Logger.getLogger(getClass())

    SupplierService supplierService
    SystemEntityService systemEntityService
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * Check different criteria for creating new supplier
     *      1) Check access permission to create supplier
     *      2) Validate supplier object
     *      3) Check duplicate supplier name
     * @param params -N/A
     * @param obj -Supplier object send from controller
     * @return -a map containing supplier object for execute method
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameterMap, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameterMap

            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            //Check parameters
            if ((!params.name)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }
            int duplicateCount = supplierCacheUtility.countByNameIlike(params.name)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, NAME_ALREADY_EXISTS)
                return result
            }
            Supplier supplier = buildObject(params)

            result.put(SUPPLIER, supplier)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        }
    }

    /**
     * Save supplier object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -supplierObject send from controller
     * @return -newly created supplier object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Supplier supplier = (Supplier) preResult.get(SUPPLIER)
            Supplier newSupplier = supplierService.create(supplier)//save in DB
            //save in cache and keep the data sorted
            supplierCacheUtility.add(newSupplier, supplierCacheUtility.SORT_ON_NAME, supplierCacheUtility.SORT_ORDER_ASCENDING)
            result.put(SUPPLIER, newSupplier)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
     * Wrap newly created supplier to show on grid
     * @param obj -newly created supplier object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj   // cast map returned from execute method
            Supplier supplier = (Supplier) receiveResult.get(SUPPLIER)
            SystemEntity supplierType = systemEntityService.read(supplier.supplierTypeId)
            GridEntity object = new GridEntity()
            object.id = supplier.id
            object.cell = [
                    Tools.LABEL_NEW,
                    supplier.id,
                    supplierType.key,
                    supplier.name,
                    supplier.accountName,
                    supplier.address,
                    supplier.bankAccount,
                    supplier.itemCount
            ]
            Map resultMap = [entity: object, version: supplier.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(SUPPLIER, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build Supplier object
     * @param parameterMap -serialized parameters from UI
     * @return -new Supplier object
     */
    private Supplier buildObject(GrailsParameterMap params) {
        Supplier supplier = new Supplier(params)
        supplier.itemCount = 0
        supplier.companyId = appSessionUtil.getCompanyId()
        supplier.createdOn = new Date()
        supplier.createdBy = appSessionUtil.getAppUser().id
        supplier.updatedOn = null
        supplier.updatedBy = 0
        return supplier
    }
}
