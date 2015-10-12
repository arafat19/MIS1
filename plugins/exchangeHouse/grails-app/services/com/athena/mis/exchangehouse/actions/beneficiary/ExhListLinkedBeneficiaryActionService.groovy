package com.athena.mis.exchangehouse.actions.beneficiary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of linked beneficiary for grid
 *  For details go through Use-Case doc named 'ExhListLinkedBeneficiaryActionService'
 */
class ExhListLinkedBeneficiaryActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String FIRST_NAME = 'first_name'
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load linked beneficiary list"
    private static final String LST_BENEFICIARY = "lstBeneficiary"

    @Autowired
    ExhSessionUtil exhSessionUtil

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
            if (!params.sortname) {
                params.sortname = FIRST_NAME
                params.sortorder = ASCENDING_SORT_ORDER
            }

            long beneficiaryId = params.beneficiaryId ? Long.parseLong(params.beneficiaryId) : 0L
            String email = params.email
            String phone = params.phone
            String accountNo = params.accountNo

            initSearch(params)                  // initialize params for flexGrid

            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            List<ExhBeneficiary> lstBeneficiary = listLinkedBeneficiary(companyId, email, accountNo, phone, beneficiaryId)   // get list of linked beneficiary
            int count = countLinkedBeneficiary(companyId, email, accountNo, phone, beneficiaryId)        // count of list
            result.put(LST_BENEFICIARY, lstBeneficiary)
            result.put(Tools.COUNT, Integer.valueOf(count))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Wrap beneficiary list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<ExhBeneficiary> lstBeneficiary = (List<ExhBeneficiary>) executeResult.get(LST_BENEFICIARY)
            int count = ((Integer) executeResult.get(Tools.COUNT)).intValue()
            List wrapBeneficiaries = wrapLinkedBeneficiary(lstBeneficiary, start)
            Map output = [page: pageNumber, total: count, rows: wrapBeneficiaries]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }

    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Wrap list of beneficiaries in grid entity
     * @param lstBeneficiary -list of employee object(s)
     * @param start -starting index of the page
     * @return -list of wrapped beneficiaries
     */
    private List wrapLinkedBeneficiary(List<ExhBeneficiary> lstBeneficiary, int start) {
        List beneficiaries = []
        if (lstBeneficiary == null) return beneficiaries

        int counter = start + 1
        ExhBeneficiary beneficiary
        GridEntity obj
        for (int i = 0; i < lstBeneficiary.size(); i++) {
            beneficiary = lstBeneficiary[i]
            obj = new GridEntity()       // build grid entity object
            obj.id = beneficiary.id
            obj.cell = [
                    counter,
                    beneficiary.id,
                    beneficiary.fullName,
                    beneficiary.email,
                    beneficiary.phone,
                    beneficiary.accountNo,
                    beneficiary.bank
            ]
            beneficiaries << obj
            counter++
        }
        return beneficiaries

    }

    /**
     * Get list of beneficiary by params
     */
    private List<ExhBeneficiary> listLinkedBeneficiary(long companyId, String email, String accountNo,
                                                       String phone, long beneficiaryId) {

        String sql = """
            SELECT * FROM exh_beneficiary
            WHERE
                exh_beneficiary.company_id=${companyId} AND
                exh_beneficiary.id <> ${beneficiaryId} AND
                (exh_beneficiary.email ilike '${email}' OR
                exh_beneficiary.account_no ilike '${accountNo}' OR
                exh_beneficiary.phone ilike '${phone}')
            ORDER BY ${sortColumn} ${sortOrder}
        """
        return getEntityList(sql, ExhBeneficiary.class)
    }

    private int countLinkedBeneficiary(long companyId, String email,
                                       String accountNo, String phone, long beneficiaryId) {
        String sql = """
            SELECT COUNT(id) FROM exh_beneficiary
            WHERE
                exh_beneficiary.company_id=${companyId} AND
                exh_beneficiary.id <> ${beneficiaryId} AND
                (exh_beneficiary.email ilike '${email}' OR
                exh_beneficiary.account_no ilike '${accountNo}' OR
                exh_beneficiary.phone ilike '${phone}')
        """
        int count = (int) executeSelectSql(sql).first().count
        return count
    }
}
