package com.athena.mis.arms.actions.rmsexchangehousecurrencyposting

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search specific list of ExchangeHouseCurrencyPosting
 *  For details go through Use-Case doc named 'SearchRmsExchangeHouseCurrencyPostingActionService'
 */
class SearchRmsExchangeHouseCurrencyPostingActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsSessionUtil rmsSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load exchange house currency posting List"
    private static final String LST_EXH_HOUSE_CUR_POSTING = "lstExhHouseCurPosting"

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
     * Get ExchangeHouseCurrencyPosting list through specific search
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            initSearch(parameters)          // initialize parameters
            // Search ExchangeHouseCurrencyPosting from DB
            Map searchResult = search(this)
            List<GroovyRowResult> lstExhHouseCurPosting = (List<GroovyRowResult>) searchResult.lstExhHouseCurPosting
            Integer count = (Integer) searchResult.count
            result.put(LST_EXH_HOUSE_CUR_POSTING, lstExhHouseCurPosting)
            result.put(Tools.COUNT, count)
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
     * Wrap ExchangeHouseCurrencyPosting list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     */
    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> lstExhHouseCurPosting = (List<GroovyRowResult>) executeResult.get(LST_EXH_HOUSE_CUR_POSTING)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedExhHouseCurPosting = wrapExhHouseCurPosting(lstExhHouseCurPosting, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedExhHouseCurPosting]
            return output
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
    Object buildFailureResultForUI(Object obj) {
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
     * Wrap list of ExchangeHouseCurrencyPosting in grid entity
     * @param lstExhHouseCurPosting -list of ExchangeHouseCurrencyPosting object
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
     * Get ExchangeHouseCurrencyPosting list through specific search
     * @param baseService
     * @return -a map containing ExchangeHouseCurrencyPosting list and search count
     */
    private Map search(BaseService baseService) {
        String searchQuery = """
            SELECT ehcp.id, ehcp.version, eh.name exchange_house, ehcp.amount amount, created.username created_by,
                        ehcp.created_on created_on, updated.username updated_by, ehcp.updated_on updated_on
            FROM rms_exchange_house_currency_posting ehcp
                LEFT JOIN rms_exchange_house eh ON ehcp.exchange_house_id = eh.id
                LEFT JOIN app_user created ON ehcp.created_by = created.id
                LEFT JOIN app_user updated ON ehcp.updated_by = updated.id
            WHERE ${baseService.queryType} ilike :query
            AND amount >= 0
                AND ehcp.task_id = 0
                AND ehcp.company_id = :companyId
            ORDER BY exchange_house
            LIMIT :resultPerPage OFFSET :start
        """
        String countQuery = """
            SELECT COUNT(ehcp.id)
            FROM rms_exchange_house_currency_posting ehcp
                LEFT JOIN rms_exchange_house eh ON ehcp.exchange_house_id = eh.id
            WHERE ${baseService.queryType} ilike :query
            AND amount > 0
            AND ehcp.company_id = :companyId
        """
        Map queryParams = [
                companyId: rmsSessionUtil.appSessionUtil.getCompanyId(),
                query: Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE,
                resultPerPage: baseService.resultPerPage,
                start: baseService.start
        ]
        List<GroovyRowResult> lstExhHouseCurPosting = executeSelectSql(searchQuery, queryParams)
        int count = executeSelectSql(countQuery, queryParams).first().count

        return [lstExhHouseCurPosting : lstExhHouseCurPosting, count : count.toInteger()]
    }

}
