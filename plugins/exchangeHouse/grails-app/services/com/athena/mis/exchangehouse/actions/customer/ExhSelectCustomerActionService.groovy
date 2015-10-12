package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.integration.sarb.SarbPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select customer object and show in UI for editing
 *  For details go through Use-Case doc named 'ExhSelectCustomerActionService'
 */
class ExhSelectCustomerActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String CUSTOMER_DATE_OF_BIRTH = "customerDateOfBirth"
    private static final String DECLARATION_START = "declarationStart"
    private static final String DECLARATION_END = "declarationEnd"
    private static final String VISA_EXPIRE_DATE = "visaExpireDate"
    private static final String PHOTO_ID_EXPIRY_DATE = "photoIdExpiryDate"
    private static final String ADDRESS_VERIFIED_STATUS = "addressVerifiedStatus"
    private static final String CUSTOMER_SELECT_ERROR = "Failed to find the customer"
    public static final String CUSTOMER_NOT_FOUND_ERROR = "Selected customer is not found"
    public static final String CUSTOMER_OBJECT = "customer"
    public static final String SARB_CUSTOMER_DETAILS = "sarbCustomerDetails"
    private static final String AGENT_ID = "agentId"

    ExhCustomerService exhCustomerService

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
     * Get customer object by id
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long customerId = Long.parseLong(parameterMap.id)

            ExhCustomer customer = exhCustomerService.read(customerId)  // get customer object from DB

            if (!customer) {
                result.put(Tools.MESSAGE, CUSTOMER_NOT_FOUND_ERROR)
                return result
            }

            result.put(CUSTOMER_OBJECT, customer)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_SELECT_ERROR)
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
     * Build a map with customer object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            ExhCustomer customer = (ExhCustomer) executeResult.get(CUSTOMER_OBJECT)
            String customerDateOfBirth = DateUtility.getDateForUI(customer.dateOfBirth)    // date format for UI ie 'dd/MM/yyyy'
            String photoIdExpiryDate = DateUtility.getDateForUI(customer.photoIdExpiryDate)
            String declarationStart = DateUtility.getDateForUI(customer.declarationStart)
            String declarationEnd = DateUtility.getDateForUI(customer.declarationEnd)
            String visaExpireDate = DateUtility.getDateForUI(customer.visaExpireDate)
            String addressVerifiedStatus = customer.addressVerifiedStatus == Tools.CUSTOMER_ADDRESS_VERIFIED ? Boolean.TRUE : Boolean.FALSE
			//if SARB plugin is installed, select sarb info from DB
			if(sarbImplService) {
				Object sarbCustomerDetails = sarbImplService.readSarbCustomerDetails(customer.id)
				result.put(SARB_CUSTOMER_DETAILS, sarbCustomerDetails)
			}
            result.put(Tools.ENTITY, customer)
            result.put(Tools.VERSION, customer.version)
            result.put(CUSTOMER_DATE_OF_BIRTH, customerDateOfBirth)
            result.put(DECLARATION_START, declarationStart)
            result.put(DECLARATION_END, declarationEnd)
            result.put(VISA_EXPIRE_DATE, visaExpireDate)
            result.put(AGENT_ID, exhSessionUtil.getUserAgentId())
            result.put(PHOTO_ID_EXPIRY_DATE, photoIdExpiryDate)
            result.put(ADDRESS_VERIFIED_STATUS, addressVerifiedStatus)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_SELECT_ERROR)
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
                result.put(Tools.MESSAGE, CUSTOMER_SELECT_ERROR)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_SELECT_ERROR)
            return result
        }
    }


}
