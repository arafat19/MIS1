package com.athena.mis.accounting.actions.accvouchertypecoa

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
 *  Class to get search result list of accVoucherTypeCoa object(s) to show on grid
 *  For details go through Use-Case doc named 'SearchAccVoucherTypeCoaActionService'
 */
class SearchAccVoucherTypeCoaActionService extends BaseService implements ActionIntf {

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to search voucher type grid list"
    private static final String ACC_VOUCHER_TYPE_COA_LIST = "accVoucherTypeCoaList"
    private static final String SORT_COLUMN = "acc_voucher_type_coa.id"

    private Logger log = Logger.getLogger(getClass())

    @Autowired
    AccSessionUtil accSessionUtil

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get search result list of accVoucherTypeCoa object(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains accVoucherTypeCoa and count
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameter, Object obj = null) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameter
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = SORT_COLUMN
                params.sortorder = DESCENDING_SORT_ORDER
            }

            initSearch(params)

            Map resultMap = searchVoucherTypeCoa()
            List<GroovyRowResult> voucherTypeCoaList = resultMap.lstAccVoucherTypeCoa
            int count = resultMap.count

            result.put(ACC_VOUCHER_TYPE_COA_LIST, voucherTypeCoaList)
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
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap accVoucherTypeCoa object list to show on grid
     * @param obj -a map contains accVoucherTypeCoa object list and count
     * @return -wrapped accVoucherTypeCoa object list to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            List<GroovyRowResult> voucherTypeCoaList = (List<GroovyRowResult>) executeResult.get(ACC_VOUCHER_TYPE_COA_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List accVoucherTypeCoa = wrapListInGridEntityList(voucherTypeCoaList, start)
            Map output = [page: pageNumber, total: count, rows: accVoucherTypeCoa]
            result.put(ACC_VOUCHER_TYPE_COA_LIST, output)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [page: pageNumber, total: 0, rows: null,
                    isError: Boolean.TRUE, message: DEFAULT_ERROR_MESSAGE]
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
     * wrappedAccVoucherTypeCoa object list for grid
     * @param voucherTypeCoaList -list of accVoucherTypeCoa objects
     * @param start -start index
     * @return -wrappedAccVoucherTypeCoa object list
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> voucherTypeCoaList, int start) {
        List lstVoucherTypeCoa = [] as List
        int counter = start + 1
        for (int i = 0; i < voucherTypeCoaList.size(); i++) {
            GroovyRowResult singleRow = voucherTypeCoaList[i]
            GridEntity obj = new GridEntity()
            obj.id = singleRow.id
            obj.cell = [
                    counter,
                    singleRow.id,
                    singleRow.name,
                    singleRow.code
            ]
            lstVoucherTypeCoa << obj
            counter++
        }
        return lstVoucherTypeCoa
    }

    private static final String COUNT_QUERY = """
               SELECT COUNT(acc_voucher_type_coa.id) AS count
               FROM acc_voucher_type_coa
               LEFT JOIN acc_chart_of_account ON acc_chart_of_account.id = acc_voucher_type_coa.coa_id
               LEFT JOIN system_entity ON system_entity.id = acc_voucher_type_coa.acc_voucher_type_id
               WHERE acc_chart_of_account.company_id =:companyId AND
                     system_entity.key ilike :name
        """
    /**
     * get search result list of accVoucherTypeCoa object(s) for grid
     * @return -map contains list of groovyRowResult and count
     */
    private Map searchVoucherTypeCoa() {
        String strQuery = """
               SELECT acc_voucher_type_coa.id, system_entity.key as name,acc_chart_of_account.code as code
               FROM acc_voucher_type_coa
               LEFT JOIN acc_chart_of_account ON acc_chart_of_account.id = acc_voucher_type_coa.coa_id
               LEFT JOIN system_entity ON system_entity.id = acc_voucher_type_coa.acc_voucher_type_id
               WHERE acc_chart_of_account.company_id =:companyId AND
                     system_entity.key ilike :name
               ORDER BY ${sortColumn} ${sortOrder}
               LIMIT ${resultPerPage} OFFSET ${start}
        """
        Map queryParams = [
                name: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                companyId: accSessionUtil.appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstAccVoucherTypeCoa = executeSelectSql(strQuery, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(COUNT_QUERY, queryParams)
        int count = (int) resultCount[0][0]

        return [lstAccVoucherTypeCoa: lstAccVoucherTypeCoa, count: count]
    }
}
