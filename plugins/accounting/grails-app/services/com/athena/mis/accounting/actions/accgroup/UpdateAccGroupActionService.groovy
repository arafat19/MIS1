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
 *  Class to update accGroup object and to show on grid list
 *  For details go through Use-Case doc named 'UpdateAccGroupActionService'
 */
class UpdateAccGroupActionService extends BaseService implements ActionIntf {

    private static final String UPDATE_FAILURE_MESSAGE = "Fail to update account group"
    private static final String UPDATE_SUCCESS_MESSAGE = "Account group has been updated successfully"
    private static final String NAME_EXISTS = "Group Name already exists"
    private static final String RESERVED_ERROR = "Reserved account group can't be updated"
    private static final String OBJ_NOT_FOUND = "Selected account group not exists"
    private static final String ACC_GROUP = "accGroup"

    private final Logger log = Logger.getLogger(getClass())

    AccGroupService accGroupService
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    /**
     * Check different criteria to update accGroup object
     *      1) Check existence of selected(old) accGroup object
     *      2) Check duplicate accGroup name
     *      3) Check if Account-Group is reserved or not
     *      4) Validate accGroup object to update
     * @param params -N/A
     * @param obj -AccGroup object send from controller
     * @return -a map containing accGroup object for execute method
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            AccGroup accGroup = (AccGroup) obj
            AccGroup oldAccGroup =  accGroupService.read(accGroup.id)
            if (!oldAccGroup) {  //check existence of accGroup object
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            oldAccGroup.name = accGroup.name
            oldAccGroup.description = accGroup.description
            oldAccGroup.isActive = accGroup.isActive

            AccGroup existingAccGroup = accGroupCacheUtility.readByNameForUpdate(accGroup.name, accGroup.id)
            if (existingAccGroup) {//Check duplicate accGroup name
                result.put(Tools.MESSAGE, NAME_EXISTS)
                return result
            }

            if (oldAccGroup.isReserved) {//reserve Account-Group could not be updated
                result.put(Tools.MESSAGE, RESERVED_ERROR)
                return result
            }

            //validate accGroup object
            oldAccGroup.validate()
            if (accGroup.hasErrors()) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                return result
            }
            result.put(ACC_GROUP, oldAccGroup)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * update accGroup object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -accGroupObject send from executePreCondition
     * @return -updated accGroup object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccGroup accGroup = (AccGroup) preResult.get(ACC_GROUP)
            AccGroup newAccGroup = accGroupService.update(accGroup)//update in DB
            //update in cache and keep the data sorted
            accGroupCacheUtility.update(newAccGroup, accGroupCacheUtility.SORT_BY_NAME, accGroupCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_GROUP, newAccGroup)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to update AccGroup')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
     * Wrap updated accGroup object to show on grid
     * @param obj -updated accGroup object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccGroup group = (AccGroup) executeResult.get(ACC_GROUP)
            GridEntity object = new GridEntity()
            object.id = group.id
            object.cell = [
                    Tools.LABEL_NEW,
                    group.id,
                    group.name ? group.name : Tools.EMPTY_SPACE,
                    group.description ? group.description : Tools.EMPTY_SPACE,
                    group.isActive ? Tools.YES : Tools.NO,
                    group.isReserved ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: group.version]
            result.put(ACC_GROUP, resultMap)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
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
     * @return -a map containing isError(true) & relevant error message
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

}

