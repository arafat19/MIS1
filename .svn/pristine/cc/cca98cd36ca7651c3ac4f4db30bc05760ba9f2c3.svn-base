package com.athena.mis.accounting.actions.accchartofaccount

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
 *  Show list of chart of account by accGroup id
 *  For details go through Use-Case doc named 'ListAccChartOfAccountByAccGroupIdActionService'
 */
class ListAccChartOfAccountByAccGroupIdActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    private static final String COA_LIST = "coaList"
    private static final String ACC_GROUP = "accGroup"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to get Chart Of Account List"
    private static final String INVALID_ERROR_MESSAGE = "Error occurred due to invalid input"
    private static final String GROUP_NOT_FOUND = "Group not found"
    /**
     * Get chart of account list by acc group id for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success & group object
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long accGroupId = 0d
            try {
                accGroupId = Long.parseLong(params.accGroupId.toString())
            } catch (Exception e) {
                result.put(Tools.MESSAGE, INVALID_ERROR_MESSAGE)
                return result
            }

            AccGroup accGroup = (AccGroup) accGroupCacheUtility.read(accGroupId)
            if (!accGroup) {
                result.put(Tools.MESSAGE, GROUP_NOT_FOUND)
                return result
            }

            result.put(ACC_GROUP, accGroup)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Get chart of account list for grid
     * @param params -N/A
     * @param obj -received for preExecute method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success & chart of account list(id,name)
     */
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            AccGroup accGroup = (AccGroup) receivedResult.get(ACC_GROUP)

            // Get COA Information in right grid
            List<AccChartOfAccount> lstCOA = accChartOfAccountCacheUtility.listByAccGroupId(accGroup.id)

            result.put(COA_LIST, Tools.listForKendoDropdown(buildCoaListForDropDown(lstCOA),null,"ALL"))      // coa list[id,name]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * @param obj -map returned from execute method
     * @return -a map containing coa list
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(COA_LIST, executeResult.get(COA_LIST))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     *
     * @param accChartOfAccountList- coa list
     * @return - coa list[id, name]
     */
    private List buildCoaListForDropDown(List<AccChartOfAccount> accChartOfAccountList) {
        List coaList = []
        int size = accChartOfAccountList.size()
        AccChartOfAccount accChartOfAccount
        for (int i = 0; i < size; i++) {
            accChartOfAccount = accChartOfAccountList[i]
            coaList << [id: accChartOfAccount.id,
                    name: accChartOfAccount.description + Tools.PARENTHESIS_START + accChartOfAccount.code + Tools.PARENTHESIS_END]
        }
        return coaList
    }
}

