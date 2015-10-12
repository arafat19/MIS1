package com.athena.mis.accounting.actions.accsubaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccSubAccount
import com.athena.mis.accounting.entity.AccVoucherDetails
import com.athena.mis.accounting.service.AccSubAccountService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.accounting.utility.AccSubAccountCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete accSubAccount object from DB as well as from cache
 *  For details go through Use-Case doc named 'DeleteAccSubAccountActionService'
 */
class DeleteAccSubAccountActionService extends BaseService implements ActionIntf {

    AccSubAccountService accSubAccountService
    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String INVALID_INPUT_MESSAGE = "Could not delete sub account due to invalid input"
    private static final String DELETE_SUCCESS_MESSAGE = "Sub Account has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Sub Account could not be deleted, please refresh the accSubAccount list"
    private static final String HAS_ASSOCIATION_MESSAGE = " voucher(s) are associated with selected sub-account"
    private static final String DEFAULT_ERROR_MESSAGE = "Could not delete sub account"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to delete accSubAccount object
     *      1) Check existence of required parameter
     *      2) Check existence of accSubAccount object
     *      3) check association with accVoucher
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) { //Check existence of required parameter
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }

            long accSubAccountId = Long.parseLong(params.id.toString())
            AccSubAccount accSubAccount = (AccSubAccount) accSubAccountCacheUtility.read(accSubAccountId)
            if (!accSubAccount) {//check existence of accSubAccount object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            //check association
            Map hasAssociationMap = (Map) hasAssociation(accSubAccount)
            Boolean hasAssociation = (Boolean) hasAssociationMap.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, hasAssociationMap.get(Tools.MESSAGE))
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * delete accSubAccount object from DB & cache
     * @param parameters -parameter send from UI
     * @param obj -N/A
     * @return -Boolean value (true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long accSubAccountId = Long.parseLong(parameterMap.id.toString())
            accSubAccountService.delete(accSubAccountId)//delete from DB
            accSubAccountCacheUtility.delete(accSubAccountId)//delete from cache
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
     * contains delete success message to show on UI
     * @param obj -N/A
     * @return -a map contains isError(false) and delete success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Check association on accSubAccount with accVoucher
     * @param accSubAccount -AccSubAccount
     * @return -a map contains booleanValue(true/false) and association message
     *          based on existence of association
     */
    private LinkedHashMap hasAssociation(AccSubAccount accSubAccount) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        long accSubAccountId = accSubAccount.id
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeSubAccount = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT, companyId)
        int count = AccVoucherDetails.countBySourceTypeIdAndSourceId(accSourceTypeSubAccount.id, accSubAccountId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE)
            return result
        }
        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }
}
