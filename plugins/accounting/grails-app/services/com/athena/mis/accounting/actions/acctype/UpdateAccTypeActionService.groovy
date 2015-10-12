package com.athena.mis.accounting.actions.acctype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.service.AccTypeService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update account type (accType) object and grid data
 *  For details go through Use-Case doc named 'UpdateAccTypeActionService'
 */
class UpdateAccTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    AccTypeService accTypeService
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String OBJ_NOT_FOUND_MASSAGE = "Selected account type is not found"
    private static final String NAME_ALREADY_EXISTS = "Same account type name already exists"
    private static final String ACC_TYPE_PREFIX_ALREADY_EXISTS = "Same account type prefix already exists"
    private static final String ACC_TYPE_ORDER_ID_ALREADY_EXISTS = "Same account type order already exists"
    private static final String HAS_NO_ACCESS_MESSAGE = "Only development user can update account type"
    private static final String ACC_TYPE_OBJECT = "accType"
    private static final String ACC_TYPE_UPDATE_FAILURE_MESSAGE = "Account type could not be updated"
    private static final String ACC_TYPE_UPDATE_SUCCESS_MESSAGE = "Account type has been updated successfully"

    /**
     * Get parameters from UI and build account type (accType) object for update
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            // Access check for update, only development user has authority to update account type (accType)
            if (!accSessionUtil.appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.MESSAGE, HAS_NO_ACCESS_MESSAGE)
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // check required parameters
            if ((!parameterMap.name) || (!parameterMap.prefix) || (!parameterMap.orderId) || (!parameterMap.id)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long accTypeId = Long.parseLong(parameterMap.id.toString())
            AccType oldAccType = accTypeService.read(accTypeId) // get account type (accType) object
            if (!oldAccType) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
                return result
            }

            AccType accType = buildAccTypeObject(parameterMap, oldAccType) // build new object of account type (accType)

            // check unique account type (accType) By Name
            int duplicateNameCount = AccType.countByNameIlikeAndCompanyIdAndIdNotEqual(accType.name, accType.companyId, accType.id)
            if (duplicateNameCount > 0) {
                result.put(Tools.MESSAGE, NAME_ALREADY_EXISTS)
                return result
            }
            // check unique account type (accType) By Prefix
            int duplicateCountPrefix = AccType.countByPrefixIlikeAndCompanyIdAndIdNotEqual(accType.prefix, accType.companyId, accType.id)
            if (duplicateCountPrefix > 0) {
                result.put(Tools.MESSAGE, ACC_TYPE_PREFIX_ALREADY_EXISTS)
                return result
            }
            // check unique account type (accType) By OrderId
            int duplicateCountOrderId = AccType.countByOrderIdAndCompanyIdAndIdNotEqual(accType.orderId, accType.companyId, accType.id)
            if (duplicateCountOrderId > 0) {
                result.put(Tools.MESSAGE, ACC_TYPE_ORDER_ID_ALREADY_EXISTS)
                return result
            }
            result.put(ACC_TYPE_OBJECT, accType)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Update account type(accType) object in DB & update cache utility
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            AccType accType = (AccType) preResult.get(ACC_TYPE_OBJECT)
            accTypeService.update(accType)    // update account type(accType) object in DB
            // update accType cache utility and keep the data sorted
            accTypeCacheUtility.update(accType, accTypeCacheUtility.SORT_ON_NAME, accTypeCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_TYPE_OBJECT, accType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ACC_TYPE_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TYPE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Show updated account type(accType) object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj    // cast map returned from execute method
            AccType accType = (AccType) receiveResult.get(ACC_TYPE_OBJECT)
            String description = Tools.makeDetailsShort(accType.description, Tools.DEFAULT_LENGTH_DETAILS_OF_SYS_CONFIG)
            GridEntity object = new GridEntity()    // build grid entity object
            object.id = accType.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accType.id,
                    accType.systemAccountType,
                    accType.name,
                    accType.orderId,
                    accType.prefix,
                    description
            ]
            result.put(Tools.MESSAGE, ACC_TYPE_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TYPE_UPDATE_FAILURE_MESSAGE)
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
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, ACC_TYPE_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TYPE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build account type(accType) object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldAccTypeObject -old accType object
     * @return -updated accType object
     */
    private AccType buildAccTypeObject(GrailsParameterMap parameterMap, AccType oldAccTypeObject) {
        AccType accType = new AccType(parameterMap)
        oldAccTypeObject.name = accType.name
        oldAccTypeObject.orderId = accType.orderId
        oldAccTypeObject.prefix = accType.prefix
        oldAccTypeObject.description = accType.description
        return oldAccTypeObject
    }
}
