package com.athena.mis.exchangehouse.actions.beneficiary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for beneficiary CRUD and list of beneficiary for grid
 *  For details go through Use-Case doc named 'ExhShowNewBeneficiariesForCustomerActionService'
 */
class ExhShowNewBeneficiariesForCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String CUSTOMER_ID = 'customerId'
    private static final String CUSTOMER_NAME = 'customerName'
    private static final String SHOW_FAILURE_MESSAGE = "Failed to load beneficiary information page"
    private static final String BENEFICIARY_LIST_JSON = 'beneficiaryListJSON'
    private static final String FIRST_NAME = 'firstName'

    ExhCustomerService exhCustomerService
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get list of new beneficiary for grid
     * @param parameters -serialized parameters from UI
     * @param obj -map returned from preCondition method
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {

        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)          // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if(!params.rp ){
                params.rp = 20                  // DEFAULT_RESULT_PER_PAGE
            }
            initPager(params)                   // initialize parameters for flexGrid
            sortColumn = FIRST_NAME
            sortOrder = ASCENDING_SORT_ORDER

            List<ExhBeneficiary> beneficiaryList = []
            int count = 0
            List beneficiaries = []
            Long customerId = exhSessionUtil.getUserCustomerId()

            LinkedHashMap serviceReturn = null
            if (customerId) {
                serviceReturn = listForCustomer(customerId)
                beneficiaryList = (List<ExhBeneficiary>) serviceReturn.beneficiaryList
                count = (int) serviceReturn.count
                beneficiaries = wrapBeneficiaryList(beneficiaryList, start)          // get wrapped beneficiary
            }

            Map gridOutput = [page: pageNumber, total: count, rows: beneficiaries]
            result.put(BENEFICIARY_LIST_JSON, gridOutput)
            ExhCustomer customer = exhCustomerService.read(customerId)
            result.put(CUSTOMER_ID, customer.id)
            result.put(CUSTOMER_NAME, customer.name)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }

    }

    /**
     * do nothing for post condition
     */
    public  Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for build Success Result For UI
     */
    public Object buildSuccessResultForUI(Object obj) {
       return null
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
                LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from execute method
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of beneficiaries in grid entity
     * @param lstBeneficiary -list of beneficiary object(s)
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
            obj = new GridEntity()
            obj.id = beneficiary.id
            obj.cell = [counter,
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
     * get customer specific beneficiary list
     */
    private LinkedHashMap listForCustomer(Long customerId) {
        List<ExhBeneficiary> beneficiaryList = findAllByCustomerId(customerId)
        int count = countByCustomerId(customerId)
        return [beneficiaryList: beneficiaryList, count: count]
    }

    private List<ExhBeneficiary> findAllByCustomerId(customerId){
        String sortColumnStr  = sortColumn.replaceAll(/\B[A-Z]/) { '_' + it }.toLowerCase()
        String sql = """
        SELECT *
        FROM exh_beneficiary
        LEFT JOIN exh_customer_beneficiary_mapping ON exh_customer_beneficiary_mapping.beneficiary_id=exh_beneficiary.id
        WHERE exh_customer_beneficiary_mapping.customer_id=${customerId}
        AND exh_beneficiary.approved_by = 0
        ORDER BY ${sortColumnStr} ${sortOrder} OFFSET ${start} LIMIT ${resultPerPage}
        """
        // max:resultPerPage, sort:sortColumn, order:sortOrder
        return getEntityList(sql, ExhBeneficiary.class)
    }

    private static final String QUERY_COUNT =
        """
            SELECT COUNT(exh_beneficiary.id)
            FROM exh_beneficiary
            JOIN exh_customer_beneficiary_mapping ON exh_customer_beneficiary_mapping.beneficiary_id=exh_beneficiary.id
            WHERE exh_customer_beneficiary_mapping.customer_id=:customerId
            AND exh_beneficiary.approved_by = 0
        """
    private int countByCustomerId(customerId){
        Map queryParams = [customerId:customerId]
        int count = (int) executeSelectSql(QUERY_COUNT, queryParams).first().count
        return count
    }

}
