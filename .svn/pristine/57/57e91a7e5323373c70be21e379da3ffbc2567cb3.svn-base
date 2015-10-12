package com.athena.mis.arms.actions.rmsexchangehousecurrencyposting

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.arms.service.RmsExchangeHouseCurrencyPostingService
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Get list of ExchangeHouseCurrencyPosting
 * For details go through Use-Case doc named 'ShowRmsExchangeHouseCurrencyPostingActionService'
 */
class ShowRmsExchangeHouseCurrencyPostingActionService extends BaseService implements ActionIntf{

    RmsExchangeHouseCurrencyPostingService rmsExchangeHouseCurrencyPostingService
    @Autowired
    RmsSessionUtil rmsSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String LST_EXH_HOUSE_CUR_POSTING = "lstExhHouseCurPosting"
    private static final String DEFAULT_FAILURE_MSG_SHOW_EXH_HOUSE_CUR_POSTING = "Failed to load exchange house currency posting page"
    private static final String GRID_OBJ = "gridObj"

    /**
     * Do nothing for pre operation
     */
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * 1. Get ExchangeHouseCurrencyPosting list for grid
     * 2. Get count of total ExchangeHouseCurrencyPosting
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap();
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameterMap)                // initialize parameters
            List<GroovyRowResult> lstExhHouseCurPosting = list(this)        // get list of ExchangeHouseCurrencyPosting from DB
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            // get count of total ExchangeHouseCurrencyPosting from DB
            int count = rmsExchangeHouseCurrencyPostingService.countByCompanyIdAndAmountGreaterThanAndTaskId(companyId)
            result.put(LST_EXH_HOUSE_CUR_POSTING, lstExhHouseCurPosting)
            result.put(Tools.COUNT, count.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex){
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_EXH_HOUSE_CUR_POSTING)
            return result
        }
    }

    /**
     * Wrap RmsExchangeHouseCurrencyPosting list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj                   // cast map returned from execute method
            List<GroovyRowResult> lstExhHouseCurPosting = (List<GroovyRowResult>) executeResult.get(LST_EXH_HOUSE_CUR_POSTING)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            // Wrap list of ExchangeHouseCurrencyPosting in grid entity
            List lstWrappedExhHouseCurPosting = wrapExhHouseCurPosting(lstExhHouseCurPosting, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedExhHouseCurPosting]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_EXH_HOUSE_CUR_POSTING)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj                   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_EXH_HOUSE_CUR_POSTING)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_EXH_HOUSE_CUR_POSTING)
            return result
        }
    }

    /**
     * Wrap list of ExchangeHouseCurrencyPosting in grid entity
     * @param lstExhHouseCurPosting -list of ExchangeHouseCurrencyPosting object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ExchangeHouseCurrencyPosting
     */
    private List wrapExhHouseCurPosting(List<GroovyRowResult> lstExhHouseCurPosting, int start) {
        List lstWrappedExhHouseCurPosting = []
        int counter = start + 1
        for (int i = 0; i < lstExhHouseCurPosting.size(); i++) {
            GroovyRowResult groovyRowResult = lstExhHouseCurPosting[i]
            GridEntity obj = new GridEntity()
            obj.id = groovyRowResult.id
            obj.cell = [
                    counter,
                    groovyRowResult.id,
                    groovyRowResult.exchange_house,
                    groovyRowResult.amount,
                    groovyRowResult.created_by,
                    DateUtility.getLongDateForUI(groovyRowResult.created_on),
                    groovyRowResult.updated_by,
                    DateUtility.getLongDateForUI(groovyRowResult.updated_on)
            ]
            lstWrappedExhHouseCurPosting << obj
            counter++
        }
        return lstWrappedExhHouseCurPosting
    }

    /**
     * Get list of ExchangeHouseCurrencyPosting object
     * @param baseService
     * @return -list of ExchangeHouseCurrencyPosting list
     */
    private List<GroovyRowResult> list(BaseService baseService) {
        String queryStr = """
            SELECT ehcp.id, ehcp.version, eh.name exchange_house, ehcp.amount amount, created.username created_by,
                        ehcp.created_on created_on, updated.username updated_by, ehcp.updated_on updated_on
            FROM rms_exchange_house_currency_posting ehcp
                LEFT JOIN rms_exchange_house eh ON ehcp.exchange_house_id = eh.id
                LEFT JOIN app_user created ON ehcp.created_by = created.id
                LEFT JOIN app_user updated ON ehcp.updated_by = updated.id
            WHERE amount >= 0
                AND ehcp.task_id = 0
                AND ehcp.company_id = :companyId
            ORDER BY exchange_house
            LIMIT :resultPerPage OFFSET :start
        """
        Map queryParams = [
                companyId: rmsSessionUtil.appSessionUtil.getCompanyId(),
                resultPerPage: baseService.resultPerPage,
                start: baseService.start
        ]
        List<GroovyRowResult> lstRmsExhHouseCurPosting = executeSelectSql(queryStr, queryParams)
        return lstRmsExhHouseCurPosting
    }
}
