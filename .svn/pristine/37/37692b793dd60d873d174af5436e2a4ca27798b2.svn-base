package com.athena.mis.exchangehouse.actions.beneficiary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional


/**
 *  Show list of beneficiary for grid
 *  For details go through Use-Case doc named 'ExhListBeneficiaryActionService'
 */
class ExhListBeneficiaryActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String APPROVED_BY = 'approved_by'
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load Beneficiary List"
    private static final String LST_BENEFICIARY = "lstBeneficiary"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get beneficiary list for grid
     * @param parameter -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object execute(Object parameter, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameter

            if (!params.rp) {
                params.rp = DEFAULT_RESULT_PER_PAGE       // set result per page param
            }

            if (!params.sortname) {
                params.sortname = APPROVED_BY
                params.sortorder = ASCENDING_SORT_ORDER
            }

            initSearch(params)              // initialize params for flexGrid

            Long customerId = null
            if (params.customerId) {      // check required params
                customerId = Long.parseLong(params.customerId)
            }

            List<GroovyRowResult> lstBeneficiary = listBeneficiary(customerId)     // get list of beneficiary by customerId
            int count = countByCustomerId(customerId)

            result.put(LST_BENEFICIARY, lstBeneficiary)
            result.put(Tools.COUNT, Integer.valueOf(count))
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap beneficiary list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<GroovyRowResult> lstBeneficiary = (List<GroovyRowResult>) executeResult.get(LST_BENEFICIARY)
            int count = ((Integer) executeResult.get(Tools.COUNT)).intValue()
            List warpBeneficiaries = wrapBeneficiary(lstBeneficiary, start)
            Map output = [page: pageNumber, total: count, rows: warpBeneficiaries]
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
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
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
     * Wrap list of beneficiaries in grid entity
     * @param lstBeneficiary -list of employee object(s)
     * @param start -starting index of the page
     * @return -list of wrapped beneficiaries
     */
    private List wrapBeneficiary(List<GroovyRowResult> lstBeneficiary, int start) {
        List beneficiaries = []
        int counter = start + 1
        GridEntity obj
        for (int i = 0; i < lstBeneficiary.size(); i++) {
            GroovyRowResult eachRow = lstBeneficiary[i]
            String updatedOn = DateUtility.getLongDateForUI(eachRow.updated_on)
            obj = new GridEntity()      // build grid object
            obj.id = eachRow.id
            obj.cell = [
                    counter,
                    eachRow.id,
                    eachRow.full_name,
                    eachRow.approved,
                    eachRow.bank,
                    eachRow.account_no,
                    eachRow.photo_id_type,
                    eachRow.updatedBy?eachRow.updatedBy:Tools.EMPTY_SPACE,
                    updatedOn
            ]
            beneficiaries << obj
            counter++
        };
        return beneficiaries
    }

    /**
     * Get list of beneficiary by customer id
     */
    private List<GroovyRowResult> listBeneficiary(long customerId) {
        String QUERY_LIST_OF_BENEFICIARY =
            """
            SELECT eb.id, ARRAY_TO_STRING(ARRAY[first_name,middle_name,last_name],' ') full_name,
                    CASE WHEN approved_by >0 THEN 'YES'
                         ELSE 'NO'
                    END AS approved,
            bank,account_no, photo_id_type, photo_id_no,eb.updated_on,au.username as updatedBy
            FROM exh_beneficiary eb
            LEFT JOIN exh_customer_beneficiary_mapping ON exh_customer_beneficiary_mapping.beneficiary_id=eb.id
            LEFT JOIN app_user au ON eb.updated_by = au.id
            WHERE exh_customer_beneficiary_mapping.customer_id=:customerId
            ORDER BY ${sortColumn} ${sortOrder} OFFSET :start LIMIT :resultPerPage
        """
        Map queryParams = [customerId:customerId, start: start, resultPerPage: resultPerPage]
        List<GroovyRowResult> result = executeSelectSql(QUERY_LIST_OF_BENEFICIARY, queryParams)
        return result
    }

    private static final String QUERY_COUNT =
        """
            SELECT COUNT(*)
            FROM exh_customer_beneficiary_mapping
            WHERE customer_id=:customerId
        """

    private int countByCustomerId(long customerId) {
        Map queryParams = [customerId: customerId]
        int count = (int) executeSelectSql(QUERY_COUNT, queryParams).first().count
        return count
    }

}
