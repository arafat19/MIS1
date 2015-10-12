package com.athena.mis.accounting.actions.acclc

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get list of accLc object(s) to show on grid
 *  For details go through Use-Case doc named 'ListAccLcActionService'
 */
class ListAccLcActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AccSessionUtil accSessionUtil

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load LC list"
    private static final String ACC_LC_LIST = "accLcList"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get list of accLc object(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains accLcList and count
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            initPager(parameterMap)

            //get list of accLc objects
            LinkedHashMap accLcReturn = listForAccLc()
            List<GroovyRowResult> accLcList = (List<GroovyRowResult>) accLcReturn.accLcList
            int totalCount = (int) accLcReturn.count

            result.put(Tools.COUNT, totalCount)
            result.put(ACC_LC_LIST, accLcList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap accLcObject list to show on grid
     * @param obj -a map contains accLcObject list and count
     * @return -wrapped accLcObject list to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            List<GroovyRowResult> accLcList = (List) executeResult.get(ACC_LC_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List accLcListWrap = wrapListInGridEntityList(accLcList, start)
            Map output = [page: pageNumber, total: count, rows: accLcListWrap]
            result.put(ACC_LC_LIST, output)
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
     * wrappedAccLcObject list for grid
     * @param accLcList -list of GroovyRowResult
     * @param start -start index
     * @return -wrappedAccLcObject list
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> lstAccLc, int start) {
        List accLcList = []
        int counter = start + 1
        GroovyRowResult singleRow
        for (int i = 0; i < lstAccLc.size(); i++) {
            GridEntity obj = new GridEntity()
            singleRow = lstAccLc[i]
            obj.id = singleRow.id
            obj.cell = [
                    counter,
                    singleRow.id,
                    singleRow.lcNo,
                    Tools.formatAmountWithoutCurrency(singleRow.amount),
                    singleRow.bank_name,
                    singleRow.item_name,
                    singleRow.supplier_name
            ]
            accLcList << obj
            counter++
        }
        return accLcList
    }

    private static final String SELECT_ACC_LC_QUERY = """
        SELECT alc.id,alc.lc_no AS lcNo ,alc.amount AS amount,
               alc.bank AS bank_name,sup.name AS supplier_name,
               it.name AS item_name
        FROM acc_lc alc
        LEFT JOIN item it ON it.id = alc.item_id
        LEFT JOIN supplier sup ON sup.id = alc.supplier_id
        WHERE alc.company_id=:companyId
        ORDER BY alc.lc_no LIMIT :resultPerPage OFFSET :start
        """

    private static final String SELECT_ACC_LC_COUNT_QUERY = """
        SELECT COUNT(alc.id) count
        FROM acc_lc alc
        WHERE alc.company_id=:companyId
        """
    /**
     * get list of accLc object
     * @return -a map contains accLc objects and count
     */
    private Map listForAccLc() {
        Map queryParams = [
                companyId: accSessionUtil.appSessionUtil.getCompanyId(),
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(SELECT_ACC_LC_QUERY, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(SELECT_ACC_LC_COUNT_QUERY, queryParams)
        int total = countResult[0].count
        return [accLcList: result, count: total]
    }

}
