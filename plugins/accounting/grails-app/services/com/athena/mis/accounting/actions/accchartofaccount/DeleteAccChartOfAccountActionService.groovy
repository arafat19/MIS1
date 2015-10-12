package com.athena.mis.accounting.actions.accchartofaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.*
import com.athena.mis.accounting.service.AccChartOfAccountService
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
/**
 *  Delete coa object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAccChartOfAccountActionService'
 */
class DeleteAccChartOfAccountActionService extends BaseService implements ActionIntf {

    AccChartOfAccountService accChartOfAccountService
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility

    private static final String ACC_CHART_OF_ACCOUNT_DELETE_SUCCESS_MSG = "Chart of account has been successfully deleted"
    private static final String ACC_CHART_OF_ACCOUNT_DELETE_FAILURE_MSG = "Chart of account has not been deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete chart of account"
    private static final String HAS_ASSOCIATION_MESSAGE_SUB_ACCOUNT = " sub account(s) associated with this chart of account"
    private static final String HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS = " voucher details(s) associated with this chart of account"
    private static final String HAS_ASSOCIATION_MESSAGE_VOUCHER_TYPE_COA = " voucher type(s) associated with this chart of account"
    private static final String HAS_ASSOCIATION_MESSAGE_BANK_STATEMENT = " Bank Statement(s) associated with this chart of account"
    private static final String DELETED = "deleted"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Checking pre condition and association before deleting the coa object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long accChartOfAccountId = Long.parseLong(params.id)
            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(accChartOfAccountId) // get coa object
            if (!accChartOfAccount) {    // check whether selected coa object exists or not
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            Map preResult = (Map) hasAssociation(accChartOfAccount)      // association check with relative domains
            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }
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
     * Delete coa object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap executeResult = new LinkedHashMap()
        try {
            executeResult.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long accChartOfAccountId = Long.parseLong(parameterMap.id)
            boolean success = accChartOfAccountService.delete(accChartOfAccountId)  // delete coa
            accChartOfAccountCacheUtility.delete(accChartOfAccountId)              // delete coa from cache utility
            executeResult.put(Tools.IS_ERROR, Boolean.FALSE)

            return executeResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            executeResult.put(Tools.IS_ERROR, Boolean.TRUE)
            executeResult.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_DELETE_FAILURE_MSG)
            return executeResult
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
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(DELETED, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_DELETE_SUCCESS_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_DELETE_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_DELETE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_DELETE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Check association of coa with relevant domains
     * @param accChartOfAccount -coa object
     * @return -a map containing isError(true/false) depending on association and relevant message
     */
    private LinkedHashMap hasAssociation(AccChartOfAccount accChartOfAccount) {

        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        long accChartOfAccountId = accChartOfAccount.id
        Integer count = 0

        count = AccSubAccount.countByCoaId(accChartOfAccountId)          // check whether this coa used in Sub Account or not
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_SUB_ACCOUNT)
            return result
        }

        count = AccVoucherDetails.countByCoaId(accChartOfAccountId)      // check whether this coa used in Voucher Details or not
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS)
            return result
        }

        count = AccVoucherTypeCoa.countByCoaId(accChartOfAccountId)     // check whether this coa used in Voucher Type or not
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_VOUCHER_TYPE_COA)
            return result
        }

        count = AccBankStatement.countByBankAccId(accChartOfAccountId)   // check whether this coa used in Bank Statement or not
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_BANK_STATEMENT)
            return result
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }
}
