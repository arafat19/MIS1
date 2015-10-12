package com.athena.mis.accounting.actions.acccustomgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccCustomGroup
import com.athena.mis.accounting.service.AccCustomGroupService
import com.athena.mis.accounting.utility.AccCustomGroupCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
/**
 *  Delete custom group object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAccCustomGroupActionService'
 */
class DeleteAccCustomGroupActionService extends BaseService implements ActionIntf {

    AccCustomGroupService accCustomGroupService
    @Autowired
    AccCustomGroupCacheUtility accCustomGroupCacheUtility

    private static final String ACC_CUSTOM_GROUP_DELETE_SUCCESS_MSG = "Custom Group has been successfully deleted"
    private static final String ACC_CUSTOM_GROUP_DELETE_FAILURE_MSG = "Custom Group has not been deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete Custom Group"
    private static final String DELETED = "deleted"
    private static final String HAS_ASSOCIATION_MESSAGE_CHART_OF_ACCOUNT = " chart of account is associated with this Custom Group"


    private final Logger log = Logger.getLogger(getClass())
    /**
     * Checking pre condition and association before deleting the custom group object
     * 1.
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            Long accCustomGroupId = Long.parseLong(params.id.toString())
            AccCustomGroup accCustomGroup = (AccCustomGroup) accCustomGroupCacheUtility.read(accCustomGroupId)
            if (!accCustomGroup) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            Map preResult = (Map) checkAssociation(accCustomGroup)
            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)

            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Delete coa object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long accCustomGroupId = Long.parseLong(params.id)
            AccCustomGroup accCustomGroup = AccCustomGroup.get(accCustomGroupId)
            boolean success = accCustomGroupService.delete(accCustomGroup)
            accCustomGroupCacheUtility.delete(accCustomGroupId)
            return new Boolean(true)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            return Boolean.FALSE
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_DELETE_SUCCESS_MSG)
        return result
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
            result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_DELETE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_DELETE_FAILURE_MSG)
            return result
        }
    }

    private LinkedHashMap checkAssociation(AccCustomGroup accCustomGroup) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        long customGroupId = accCustomGroup.id
        int count = 0
        count = AccChartOfAccount.countByAccCustomGroupId(customGroupId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_CHART_OF_ACCOUNT)
            return result
        }
        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }
}
