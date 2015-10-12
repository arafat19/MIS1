package com.athena.mis.accounting.actions.accgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.service.AccGroupService
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create AccGroup object and to show on grid list
 *  For details go through Use-Case doc named 'CreateAccGroupActionService'
 */
class CreateAccGroupActionService extends BaseService implements ActionIntf {

    AccGroupService accGroupService
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    private static final String CREATE_FAILURE_MSG = "Account-Group has not been saved"
    private static final String CREATE_SUCCESS_MSG = "Account-Group has been successfully saved"
    private static final String INPUT_VALIDATION_ERROR_MSG = "Given inputs are not valid"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to create Account-Group"
    private static final String NAME_EXISTS = "Account-Group name already exists"
    private static final String ACC_GROUP = "accGroup"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria for creating new accGroup
     *      1) Check duplicate accGroup name
     *      2) Validate accGroup object
     * @param params -N/A
     * @param obj -AccGroup object send from controller
     * @return -a map containing accGroup object for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            AccGroup accGroup = (AccGroup) obj

            long accGroupId = accGroupService.getAccGroupId()
            accGroup.isReserved = false
            accGroup.id = accGroupId
            accGroup.systemAccGroup = accGroupId

            AccGroup existingAccGroup = accGroupCacheUtility.readByName(accGroup.name)
            if (existingAccGroup) { //Check duplicate accGroup name
                result.put(Tools.MESSAGE, NAME_EXISTS)
                return result
            }

            //Validate accGroup object
            accGroup.validate()
            if (accGroup.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR_MSG)
                return result
            }
            result.put(ACC_GROUP, accGroup)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Save accGroup object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -accGroupObject send from executePreCondition
     * @return -newly created accGroup object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccGroup accGroup = (AccGroup) preResult.get(ACC_GROUP)
            AccGroup newAccGroup = accGroupService.create(accGroup)// save in DB
            // save in cache and keep the data sorted
            accGroupCacheUtility.add(newAccGroup, accGroupCacheUtility.SORT_BY_NAME, accGroupCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_GROUP, newAccGroup)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
     * Wrap newly created accGroup to show on grid
     * @param obj -newly created accGroup object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccGroup accGroup = (AccGroup) executeResult.get(ACC_GROUP)
            GridEntity object = new GridEntity()
            object.id = accGroup.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accGroup.id,
                    accGroup.name ? accGroup.name : Tools.EMPTY_SPACE,
                    accGroup.description ? accGroup.description : Tools.EMPTY_SPACE,
                    accGroup.isActive ? Tools.YES : Tools.NO,
                    accGroup.isReserved ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: accGroup.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            result.put(ACC_GROUP, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
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
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }
}