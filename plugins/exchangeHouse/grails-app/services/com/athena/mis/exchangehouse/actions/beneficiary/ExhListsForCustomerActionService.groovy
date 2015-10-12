package com.athena.mis.exchangehouse.actions.beneficiary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of beneficiary for grid
 *  For details go through Use-Case doc named 'ExhListsForCustomerActionService'
 */
class ExhListsForCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass());

    private static final String EXCEPTION_MESSAGE = "Internal Server Error"
    private static final String DEFAULT_ERROR_MESSAGE = "Can not load beneficiary list"
    private static final String LST_BENEFICIARY = "beneficiaryListJSON"

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get beneficiary list for grid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initSearch(params)                  // initialize params for flexGrid

            Long customerId = exhSessionUtil.getUserCustomerId()
            boolean isApproved = Boolean.parseBoolean(params.isApproved)

            if (isApproved.booleanValue()) {
                isApproved = Boolean.parseBoolean(params.isApproved)
            }

            List<ExhBeneficiary> lstBeneficiary = findAllByCustomerId(customerId, isApproved)        // get list of beneficiary by customerId
            int count = countByCustomerId(customerId, isApproved)

            result.put(LST_BENEFICIARY, lstBeneficiary)
            result.put(Tools.COUNT, Integer.valueOf(count))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
            List<ExhBeneficiary> lstBeneficiary = (List<ExhBeneficiary>) executeResult.get(LST_BENEFICIARY)
            int count = ((Integer) executeResult.get(Tools.COUNT)).intValue()
            List warpBeneficiaries = wrapBeneficiaryList(lstBeneficiary, start)
            Map output = [page: pageNumber, total: count, rows: warpBeneficiaries]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }
    }

    /**
     * Wrap list of beneficiaries in grid entity
     * @param lstBeneficiary -list of employee object(s)
     * @param start -starting index of the page
     * @return -list of wrapped beneficiaries
     */
    private List wrapBeneficiaryList(List<ExhBeneficiary> lstBeneficiary, int start) {

        List beneficiaries = []
        int counter = start + 1
        ExhBeneficiary beneficiary
        GridEntity obj
        for (int i = 0; i < lstBeneficiary.size(); i++) {
            beneficiary = lstBeneficiary[i]
            String updatedOn = DateUtility.getLongDateForUI(beneficiary.updatedOn)
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(beneficiary.updatedBy)
            obj = new GridEntity()          // build grid object
            obj.id = beneficiary.id
            obj.cell = [
                    counter,
                    beneficiary.id,
                    beneficiary.fullName,
                    beneficiary.bank,
                    beneficiary.accountNo,
                    beneficiary.photoIdType,
                    updatedBy?updatedBy.username:Tools.EMPTY_SPACE,
                    updatedOn
            ]
            beneficiaries << obj
            counter++
        }
        return beneficiaries
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
     * Get list of beneficiary by customer id
     */
    private List<ExhBeneficiary> findAllByCustomerId(long customerId, boolean isApproved) {
        String sortColumnStr = sortColumn.replaceAll(/\B[A-Z]/) { '_' + it }.toLowerCase()
        String approvedOrNotSql = 'exh_beneficiary.approved_by = 0'
        if (isApproved) {
            approvedOrNotSql = 'exh_beneficiary.approved_by > 0'
        }
        String sql = """
            SELECT *
            FROM exh_beneficiary
            JOIN exh_customer_beneficiary_mapping ON exh_customer_beneficiary_mapping.beneficiary_id=exh_beneficiary.id
            WHERE exh_customer_beneficiary_mapping.customer_id=${customerId}
            AND ${approvedOrNotSql}
            ORDER BY ${sortColumnStr} ${sortOrder} OFFSET ${start} LIMIT ${resultPerPage}
        """
        return getEntityList(sql, ExhBeneficiary.class)
    }

    private int countByCustomerId(long customerId, boolean isApproved) {
        String approvedOrNotSql = 'exh_beneficiary.approved_by = 0'
        if (isApproved) {
            approvedOrNotSql = 'exh_beneficiary.approved_by > 0'
        }
        String sql = """
            SELECT COUNT(exh_beneficiary.id)
            FROM exh_beneficiary
            JOIN exh_customer_beneficiary_mapping ON exh_customer_beneficiary_mapping.beneficiary_id=exh_beneficiary.id
            WHERE exh_customer_beneficiary_mapping.customer_id=${customerId}
            AND ${approvedOrNotSql}
        """
        int count = (int) executeSelectSql(sql).first().count
        return count
    }
}