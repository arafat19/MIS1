package com.athena.mis.exchangehouse.actions.beneficiary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.exchangehouse.config.ExhSysConfigurationCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.entity.ExhPhotoIdType
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for beneficiary CRUD and list of beneficiary for grid
 *  For details go through Use-Case doc named 'ExhShowBeneficiaryActionService'
 */
class ExhShowBeneficiaryActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load beneficiary information page"
    private static final String FIRST_NAME = 'firstName'
    private static final String CUSTOMER_ID = 'customerId'
    private static final String CUSTOMER_CODE = 'customerCode'
    private static final String CUSTOMER_NAME = 'customerName'
    private static final String PHOTO_ID_TYPE_LIST = 'photoIdTypeList'
    private static final String BENEFICIARY_LIST_JSON = 'beneficiaryListJSON'
    private final String USER_NAME = "userName"
    private static final String CUSTOMER = "customer"
    private static final String VALUE_1 = "1"
    private static final String IS_SANCTION_STATUS = "isSanctionStatus"


    ExhCustomerService exhCustomerService

    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhSysConfigurationCacheUtility exhSysConfigurationCacheUtility

    /**
     * Get parameters from UI and check beneficiary object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)            // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.customerId) {           // check required params
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long customerId = Tools.parseLongInput(params.customerId)
            if (customerId == 0) {                                   // check parse exception
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            AppUser appUser = exhSessionUtil.appSessionUtil.getAppUser()
            result.put(USER_NAME, appUser.username)
            ExhCustomer customer = exhCustomerService.read(customerId)
            if (!customer) {                                      // check whether get customer object exists or not
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            if (customer.companyId != appUser.companyId) {         // check company's customer
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(CUSTOMER, customer)
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
     * Get list of photo ID type for dropDown and beneficiary list for grid
     * @param parameter -serialized parameters from UI
     * @param obj -map returned from preCondition method
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameter, Object obj) {
        LinkedHashMap result = new LinkedHashMap()

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)                // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameter
            LinkedHashMap preResult = (LinkedHashMap) obj          // cast map returned from execute method
            result.put(USER_NAME, preResult.get(USER_NAME))
            ExhCustomer customer = (ExhCustomer) preResult.get(CUSTOMER)
            if (!params.rp) {
                params.rp = DEFAULT_RESULT_PER_PAGE
            }
            initPager(params)              // initialize parameters for flexGrid
            sortColumn = FIRST_NAME
            sortOrder = ASCENDING_SORT_ORDER

            List<GroovyRowResult> beneficiaryList = []

            LinkedHashMap serviceReturn = listByCustomer(customer.id)
            result.put(CUSTOMER_ID, customer.id)
            result.put(CUSTOMER_CODE, customer.code)
            result.put(CUSTOMER_NAME, customer.fullName)

            beneficiaryList = (List<GroovyRowResult>) serviceReturn.beneficiaryList
            int count = (int) serviceReturn.count
            List beneficiaries = wrapBeneficiary(beneficiaryList, start)                    // warp beneficiary for grid
            Map gridOutput = [page: pageNumber, total: count, rows: beneficiaries]

            List<ExhPhotoIdType> photoIdTypeList = exhPhotoIdTypeCacheUtility.list()    // get photo ID type list from cache for dropDown
            boolean isSanctionStatus = getIsSanctionExceptionStatus()
            result.put(PHOTO_ID_TYPE_LIST, photoIdTypeList)
            result.put(BENEFICIARY_LIST_JSON, gridOutput)
            result.put(IS_SANCTION_STATUS, isSanctionStatus)
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
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for build Success Result For UI
     */
    public Object buildSuccessResultForUI(Object beneficiaryResult) {
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
     * @return -list of wrapped beneficiarys
     */
    private List wrapBeneficiary(List<GroovyRowResult> lstBeneficiary, int start) {
        List beneficiarys = []
        int counter = start + 1
        GridEntity obj
        for (int i = 0; i < lstBeneficiary.size(); i++) {
            GroovyRowResult eachRow = lstBeneficiary[i]
            String updatedOn = DateUtility.getLongDateForUI(eachRow.updated_on)

            obj = new GridEntity()
            obj.id = eachRow.id
            obj.cell = [counter, eachRow.id, eachRow.full_name, eachRow.approved,
                    eachRow.bank, eachRow.account_no, eachRow.photo_id_type,eachRow.updated_by,updatedOn]
            beneficiarys << obj
            counter++
        }
        return beneficiarys
    }

    /**
     * get customer specific beneficiary list
     */
    private LinkedHashMap listByCustomer(long customerId) {
        List<GroovyRowResult> beneficiaryList = listBeneficiary(customerId)
        int total = countByCustomerId(customerId)

        return [beneficiaryList: beneficiaryList, count: total]
    }

    private static final String SQL_QUERY = """
        SELECT ben.id, ARRAY_TO_STRING(ARRAY[first_name,middle_name,last_name],' ') full_name,
                CASE WHEN approved_by >0 THEN 'YES'
                     ELSE 'NO'
                END AS approved,
        bank,account_no, photo_id_type, ben.updated_on, au.username AS updated_by
        FROM exh_beneficiary ben
        LEFT JOIN exh_customer_beneficiary_mapping ON exh_customer_beneficiary_mapping.beneficiary_id=ben.id
        LEFT JOIN app_user au ON ben.updated_by = au.id
        WHERE exh_customer_beneficiary_mapping.customer_id=:customerId
        ORDER BY approved asc OFFSET :start LIMIT :limit
    """

    private List<GroovyRowResult> listBeneficiary(long customerId) {
        Map queryParams = [customerId: customerId, start: start, limit: resultPerPage]
        List<GroovyRowResult> result = executeSelectSql(SQL_QUERY, queryParams)
        return result
    }

    private static final String SQL_QUERY_COUNT = """
        SELECT COUNT(*)
        FROM exh_customer_beneficiary_mapping
        WHERE customer_id=:customerId
    """

    private int countByCustomerId(long customerId) {
        Map queryParams = [customerId: customerId]
        int count = (int) executeSelectSql(SQL_QUERY_COUNT, queryParams).first().count
        return count
    }

    private boolean getIsSanctionExceptionStatus() {
        boolean defaultStatus = true
        SysConfiguration sysConfig = exhSysConfigurationCacheUtility.readByKeyAndCompanyId(exhSysConfigurationCacheUtility.EXH_VERIFY_BENEFICIARY_SANCTION, exhSessionUtil.appSessionUtil.appUser.companyId)
        if (sysConfig) {
            defaultStatus = sysConfig.value.equals(VALUE_1)
        }
        return defaultStatus
    }
}
