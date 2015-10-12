package com.athena.mis.accounting.actions.acccustomgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccCustomGroup
import com.athena.mis.accounting.service.AccCustomGroupService
import com.athena.mis.accounting.utility.AccCustomGroupCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.AppUser
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create custom group & display in the grid
 *  For details go through Use-Case doc named 'CreateAccCustomGroupActionService'
 */
class CreateAccCustomGroupActionService extends BaseService implements ActionIntf {

    AccCustomGroupService accCustomGroupService
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccCustomGroupCacheUtility accCustomGroupCacheUtility

    private static final String ACC_CUSTOM_GROUP_CREATE_FAILURE_MSG = "Custom Group has not been saved"
    private static final String ACC_CUSTOM_GROUP_CREATE_SUCCESS_MSG = "Custom Group has been successfully saved"
    private static final String INPUT_VALIDATION_ERROR_MSG = "Given inputs are not valid"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to create Custom Group"
    private static final String ACC_CUSTOM_GROUP_NAME_EXISTS = "Custom Group Name already exists"
    private static final String ACC_CUSTOM_GROUP = "accCustomGroup"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * 1. pull custom group object from cache utility
     * 2. check custom group existence
     * @param parameters -N/A
     * @param obj -object receive from controller
     * @return -Map containing isError(true/false)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            AccCustomGroup accCustomGroup = (AccCustomGroup) obj

            AppUser user = accSessionUtil.appSessionUtil.getAppUser()
            accCustomGroup.companyId = user.companyId

            AccCustomGroup existingAccCustomGroup = accCustomGroupCacheUtility.readByName(accCustomGroup.name)
            if (existingAccCustomGroup) {
                result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_NAME_EXISTS)
                return result
            }
            accCustomGroup.validate()
            if (accCustomGroup.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR_MSG)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Save custom group object in DB and update cache utility accordingly
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return - saved accCustomGroup object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            AccCustomGroup accCustomGroup = (AccCustomGroup) obj
            accCustomGroup.companyId = accSessionUtil.appSessionUtil.getAppUser().companyId
            AccCustomGroup savedAccCustomGroup = accCustomGroupService.create(accCustomGroup)
            accCustomGroupCacheUtility.add(accCustomGroup, accCustomGroupCacheUtility.SORT_BY_NAME, accCustomGroupCacheUtility.SORT_ORDER_ASCENDING)
            return savedAccCustomGroup
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            return null
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. Show newly created custom group object in grid
     * 2. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            AccCustomGroup accCustomGroup = (AccCustomGroup) obj
            GridEntity object = new GridEntity()
            object.id = accCustomGroup.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accCustomGroup.id,
                    accCustomGroup.name,
                    accCustomGroup.description,
                    accCustomGroup.isActive ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: accCustomGroup.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_CREATE_SUCCESS_MSG)
            result.put(ACC_CUSTOM_GROUP, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_CREATE_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_CREATE_FAILURE_MSG)
            return result
        }
    }
}