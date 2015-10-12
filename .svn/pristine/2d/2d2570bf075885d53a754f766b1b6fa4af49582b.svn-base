package com.athena.mis.accounting.actions.accbankstatement

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for bank statement upload
 *  For details go through Use-Case doc named 'ShowAccBankStatementActionService'
 */
class ShowAccBankStatementActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load bank statement page"
    private static final String BANK_LIST = "bankList"

    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get bank list for dropDown
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)  // initialize parameters for flexGrid
            List<AccChartOfAccount> accChartOfAccountList = (List<AccChartOfAccount>) accChartOfAccountCacheUtility.list() // get list of chart of account
            int countCOA = accChartOfAccountList.size() // get total count of chart of account
            List bankList = []
            AccChartOfAccount accChartOfAccount
            AccGroup accGroup = accGroupCacheUtility.readBySystemAccGroup(accGroupCacheUtility.ACC_GROUP_BANK)

            for (int i = 0; i < countCOA; i++) {
                accChartOfAccount = accChartOfAccountList[i]
                if (accChartOfAccount.accGroupId == accGroup.id) {  // if accChartOfAccount.accGroupId == accGroupCacheUtility.ACC_GROUP_BANK_ID then the description/head name of chart of account becomes  bank name
                    Map rowCOA = [id: accChartOfAccount.id, name: accChartOfAccount.description]
                    bankList << rowCOA
                }
            }
            result.put(BANK_LIST, bankList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
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
     * It gives the bank list for dropDown in the UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj    // cast map returned from execute method
            result.put(BANK_LIST, Tools.listForKendoDropdown(executeResult.get(BANK_LIST),null,null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
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
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }
}
