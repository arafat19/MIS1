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

// update custom group
class UpdateAccCustomGroupActionService extends BaseService implements ActionIntf {

    // constants
    private static final String ACC_CUSTOM_GROUP_UPDATE_FAILURE_MESSAGE = "Custom group could not be updated"
    private static final String ACC_CUSTOM_GROUP_UPDATE_SUCCESS_MESSAGE = "Custom group has been updated successfully"
    private static final String ACC_CUSTOM_GROUP = "accCustomGroup"
    private static final String ACC_CUSTOM_GROUP_NAME_EXISTS = "Custom Group Name already exists"
    private static final String ERROR_INVALID_INPUT = "Error occurred due to invalid input"

    private final Logger log = Logger.getLogger(getClass())

    AccCustomGroupService accCustomGroupService
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccCustomGroupCacheUtility accCustomGroupCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            AccCustomGroup accCustomGroup = (AccCustomGroup) obj
            AccCustomGroup existingAccCustomGroup = accCustomGroupCacheUtility.readByNameForUpdate(accCustomGroup.name, accCustomGroup.id)
            if (existingAccCustomGroup) {
                result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_NAME_EXISTS)
                return result
            }
            AppUser user = accSessionUtil.appSessionUtil.getAppUser()
            accCustomGroup.companyId = user.companyId

            accCustomGroup.validate()
            if (accCustomGroup.hasErrors()) {
                result.put(Tools.MESSAGE, ERROR_INVALID_INPUT)
                return result
            }
            result.put(ACC_CUSTOM_GROUP, accCustomGroup)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccCustomGroup accCustomGroup = (AccCustomGroup) preResult.get(ACC_CUSTOM_GROUP)
            AccCustomGroup updatedCustomGroup = accCustomGroupService.update(accCustomGroup)
            accCustomGroupCacheUtility.update(updatedCustomGroup, accCustomGroupCacheUtility.SORT_BY_NAME, accCustomGroupCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_CUSTOM_GROUP, updatedCustomGroup)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException(ACC_CUSTOM_GROUP_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccCustomGroup accCustomGroup = (AccCustomGroup) executeResult.get(ACC_CUSTOM_GROUP)

            GridEntity object = new GridEntity()
            object.id = accCustomGroup.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accCustomGroup.id,
                    accCustomGroup.name ? accCustomGroup.name : Tools.EMPTY_SPACE,
                    accCustomGroup.description ? accCustomGroup.description : Tools.EMPTY_SPACE,
                    accCustomGroup.isActive ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: accCustomGroup.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_UPDATE_SUCCESS_MESSAGE)
            result.put(ACC_CUSTOM_GROUP, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

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
            result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

}

