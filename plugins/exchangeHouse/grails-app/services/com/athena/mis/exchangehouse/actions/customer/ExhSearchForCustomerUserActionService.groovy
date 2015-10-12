package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhUserCustomerCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show specific customer user details
 *  For details go through Use-Case doc named 'ExhSearchForCustomerUserActionService'
 */
class ExhSearchForCustomerUserActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private final String CUSTOMER_NOT_FOUND = "Customer not found."
    private final String FAILURE_MSG = "Fail to get customer's information."
    private final String CUSTOMER_OBJ = "customer"
    private final String CUSTOMER_INFO_MAP = "customerInfoMap"

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired
    ExhUserCustomerCacheUtility exhUserCustomerCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    /**
     * Get parameters from UI and check customer object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)             // set default
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.customerCode) {     // check required params
                result.put(Tools.MESSAGE, CUSTOMER_NOT_FOUND)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Get customer for UI through specific search
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            String customerId = params.customerCode.toString()
            ExhCustomer customer = findByCodeAndCompanyId(customerId)    // get customer
            if (!customer) {           // check whether get customer object exists or not
                result.put(Tools.MESSAGE, CUSTOMER_NOT_FOUND)
                return result
            }
            result.put(CUSTOMER_OBJ, customer)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
     * Build customerInfoMap map for UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    @Transactional(readOnly = true)
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap serviceReturn = (LinkedHashMap) obj
            ExhCustomer customer = (ExhCustomer) serviceReturn.get(CUSTOMER_OBJ)

            //get User Account of the customer
            AppUserEntity appUserEntity = (AppUserEntity) exhUserCustomerCacheUtility.readByCustomerId(customer.id)
            String loginId = Tools.EMPTY_SPACE
            String password = Tools.EMPTY_SPACE
            if (appUserEntity) {
                AppUser appUser = (AppUser) appUserCacheUtility.read(appUserEntity.appUserId)
                loginId = appUser.loginId
                password = appUser.password
            }

            Map customerInfoMap = [            // a map build for UI information
                    id: customer.id,
                    version: customer.version,
                    role: roleTypeCacheUtility.ROLE_TYPE_EXH_CUSTOMER,    // get role type of customer
                    loginId: loginId,
                    password: password,
                    code: customer.code,
                    name: customer.fullName,
                    country: countryCacheUtility.read(customer.countryId).nationality,     // get nationality from country
                    phone: customer.phone ? customer.phone : Tools.EMPTY_SPACE,
                    photoIdType: customer.photoIdTypeId ? exhPhotoIdTypeCacheUtility.read(customer.photoIdTypeId).name : Tools.EMPTY_SPACE,
                    photoIdNo: customer.photoIdNo ? customer.photoIdNo : Tools.EMPTY_SPACE,
                    dateOfBirth: DateUtility.getDateFormatAsString(customer.dateOfBirth),    // get date format 'dd-MMMM-yyyy'
                    dateOfBirthForUI: DateUtility.getDateForUI(customer.dateOfBirth),
                    email: customer.email ? customer.email : Tools.EMPTY_SPACE,
                    address: customer.address ? customer.address : Tools.EMPTY_SPACE,
                    postCode: customer.postCode ? customer.postCode : Tools.EMPTY_SPACE,
                    //exchangeXHouseId: customer.companyId,
                    userName: appUserCacheUtility.read(customer.userId).username,
                    userId: customer.userId,
                    photoIdExpiryDate: customer.photoIdExpiryDate ? DateUtility.getDateFormatAsString(customer.photoIdExpiryDate) : Tools.EMPTY_SPACE,
                    photoIdExpiryDateForUI: customer.photoIdExpiryDate ? DateUtility.getDateForUI(customer.photoIdExpiryDate) : Tools.EMPTY_SPACE,
                    addressVerifiedStatus: customer.addressVerifiedStatus == Tools.CUSTOMER_ADDRESS_VERIFIED ? Boolean.TRUE : Boolean.FALSE,
                    sourceOfFund: customer.sourceOfFund ? customer.sourceOfFund : Tools.EMPTY_SPACE
            ]

            result.put(CUSTOMER_INFO_MAP, customerInfoMap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
                Map preResult = (Map) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Get customer object by customer code
     * */
    private ExhCustomer readByCodeAndExchangeHouse(String code) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        ExhCustomer customer = ExhCustomer.findByCodeAndCompanyId(code, companyId, [readOnly: true])
        return customer
    }

    private ExhCustomer findByCodeAndCompanyId(String customerCode) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        ExhCustomer customer = ExhCustomer.findByCodeAndCompanyId(customerCode, companyId, [readOnly: true])
        return customer
    }

}
