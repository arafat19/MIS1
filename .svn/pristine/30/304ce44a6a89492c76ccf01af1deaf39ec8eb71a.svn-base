package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.Country
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.exchangehouse.config.ExhSysConfigurationCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.integration.sarb.SarbPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for customer CRUD and list of customer for grid
 *  For details go through Use-Case doc named 'ExhShowCustomerActionService'
 */
class ExhShowCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass());

    private static final String SHOW_FAILURE_MESSAGE = "Fail to load create customer page"
    private static final String SORT_COLUMN_NAME = "name"
    private static final String LIST_PROVINCE = "lstProvince"
    private static final String CUSTOMER_LIST_JSON = "customerListJSON"
    private static final String LOGGED_IN_USER = "loggedInUser"
    private static final String COMPANY_COUNTRY_ID = "companyCountryId"
    private static final String PHONE_NO_PATTERN = "phoneNoPattern"
    private static final String ISD_CODE = "isdCode"
    private static final String VALUE_1 = "1"
    private static final String IS_SANCTION_STATUS = "isSanctionStatus"

    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    ExhSysConfigurationCacheUtility exhSysConfigurationCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
	@Autowired(required = false)
	SarbPluginConnector sarbImplService

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get list of country, photo ID type for dropDown and Customer list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()

        try {
            GrailsParameterMap parameterMap=(GrailsParameterMap) params
            parameterMap.rp = DEFAULT_RESULT_PER_PAGE              // set result per page '15'
            initPager(parameterMap)                               // initialize parameterMap for flexGrid
            sortColumn = SORT_COLUMN_NAME                   // set sort column by 'name'
            sortOrder = ASCENDING_SORT_ORDER                // set sort column by 'asc'

            List<ExhCustomer> customerList = []
            int count = 0

            LinkedHashMap serviceReturn = listExhCustomer()    // list of customer

            customerList = (List<ExhCustomer>) serviceReturn.customerList
            count = (int) serviceReturn.count
            List customers = wrapCustomerListGrid(customerList, start)
            Map gridOutput = [page: pageNumber, total: 0, rows: []]          // empty grid obj

            Company company = (Company) companyCacheUtility.read(exhSessionUtil.appSessionUtil.getCompanyId())
            Country country = (Country) countryCacheUtility.read(company.countryId)       // get country obj from cache utility
            String phonePattern = country.phoneNumberPattern.encodeAsURL()              // get country wise phone no pattern
            boolean isSanctionStatus = getIsSanctionExceptionStatus()
			//if SARB plugin is installed build province dropDown
			if(sarbImplService) {
				List lstProvince = sarbImplService.provinceList()
				List listProvinceForDropDown = Tools.listForKendoDropdown(lstProvince, null, null)
				result.put(LIST_PROVINCE, listProvinceForDropDown)
			}
            result.put(CUSTOMER_LIST_JSON, gridOutput)
            result.put(LOGGED_IN_USER, exhSessionUtil.appSessionUtil.getAppUser())
            result.put(PHONE_NO_PATTERN, phonePattern)
            result.put(ISD_CODE, country.isdCode)
            result.put(IS_SANCTION_STATUS, isSanctionStatus)
            result.put(COMPANY_COUNTRY_ID, company.countryId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
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
     * do nothing for buildSuccessResultForUI operation
     */
    public Object buildSuccessResultForUI(Object customerResult) {
        return null
    }

    /**
     * do nothing for buildFailureResultForUI operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Wrap list of Customer in grid entity
     * @param lstCustomer -list of Customer object(s)
     * @param start -starting index of the page
     * @return -list of wrapped customers
     */
    private List wrapCustomerListGrid(List<ExhCustomer> lstCustomer, int start) {
        List customers = []
        int counter = start + 1
        for (int i = 0; i < lstCustomer.size(); i++) {
            ExhCustomer customer = lstCustomer[i]
            GridEntity obj = new GridEntity()    // build grid object
            obj.id = customer.id
            String dateOfBirth = DateUtility.getDateFormatAsString(customer.dateOfBirth)
            obj.cell = [counter, customer.code, customer.fullName,
                    dateOfBirth,
                    customer.photoIdTypeId ? exhPhotoIdTypeCacheUtility.read(customer.photoIdTypeId).name : Tools.NONE,
                    customer.photoIdNo ? customer.photoIdNo : Tools.NONE, customer.phone ? customer.phone : Tools.NONE]
            customers << obj
            counter++
        }
        return customers
    }

    /**
     * Get country list for dropDown
     */
    /*private List<Country> getCountryListForDropDown() {
        List<Country> countryList = []
        List<Country> tempCountryList = countryCacheUtility.list()

        for (int i = 0; i < tempCountryList.size(); i++) {
            Country country = new Country()
            country.properties = tempCountryList[i].properties
            country.id = tempCountryList[i].id
            countryList << country
        }
        return countryList
    }*/

    /**
     * Get photo ID type list for dropDown
     */
   /* private List<ExhPhotoIdType> getPhotoIdTypeListForDropDown() {
        List<ExhPhotoIdType> photoIdTypeList = []
        List<ExhPhotoIdType> tempPhotoIdTypeList = exhPhotoIdTypeCacheUtility.list()

        for (int i = 0; i < tempPhotoIdTypeList.size(); i++) {
            ExhPhotoIdType photoIdType = new ExhPhotoIdType()
            photoIdType.properties = tempPhotoIdTypeList[i].properties
            photoIdType.id = tempPhotoIdTypeList[i].id
            photoIdTypeList << photoIdType
        }

        return photoIdTypeList

    }*/

    /**
     * Get list of customer and count by company
     */
    private LinkedHashMap listExhCustomer() {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        List<ExhCustomer> customerList = ExhCustomer.findAllByCompanyId(companyId, [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        int count = ExhCustomer.countByCompanyId(companyId)
        return [customerList: customerList, count: count]
    }

    private boolean getIsSanctionExceptionStatus() {
        boolean defaultStatus = true
        SysConfiguration sysConfig = exhSysConfigurationCacheUtility.readByKeyAndCompanyId(exhSysConfigurationCacheUtility.EXH_VERIFY_CUSTOMER_SANCTION, exhSessionUtil.appSessionUtil.getCompanyId())
        if (sysConfig) {
            defaultStatus = sysConfig.value.equals(VALUE_1)
        }
        return defaultStatus
    }
}
