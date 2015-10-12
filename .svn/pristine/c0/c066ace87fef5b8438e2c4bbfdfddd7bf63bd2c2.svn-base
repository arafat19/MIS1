package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Country
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.exchangehouse.config.ExhSysConfigurationCacheUtility
import com.athena.mis.exchangehouse.config.ExhSysConfigurationIntf
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.entity.ExhPhotoIdType
import com.athena.mis.exchangehouse.entity.ExhPostalCode
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.service.ExhCustomerTraceService
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhPostalCodeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.integration.sarb.SarbPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new customer object and show in grid
 *  For details go through Use-Case doc named 'ExhCreateCustomerActionService'
 */
class ExhCreateCustomerActionService extends BaseService implements ActionIntf {
	private final Logger log = Logger.getLogger(getClass());

	private static final String CUSTOMER_OBJ = 'customerObj'
	private static final String CUSTOMER_SAVE_SUCCESS_MESSAGE = "Customer saved successfully"
	private static final String CUSTOMER_SAVE_FAILURE_MESSAGE = "Failed to save customer"
	private static final String INVALID_POSTAL_CODE = "Invalid Postal Code."
	private static final String SARB_CUSTOMER_SAVE_FAILURE_MESSAGE = "Failed to save SarbCustomerDetails"
	private static final String SARB_CUSTOMER_TRACE_SAVE_FAILURE_MESSAGE = "Failed to save SarbCustomerDetailsTrace"
	private static final String ON = "on"
	private static final String ERROR_PHOTO_ID_NO = "errorPhotoIdNo"
	private static final String ERROR_POSTAL_CODE = "errorPostalCode"
	private static final String VALUE_1 = "1"

	ExhCustomerService exhCustomerService
	ExhCustomerTraceService exhCustomerTraceService
	@Autowired(required = false)
	SarbPluginConnector sarbImplService
	@Autowired
	ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
	@Autowired
	ExhSysConfigurationCacheUtility exhSysConfigurationCacheUtility
	@Autowired
	ExhPostalCodeCacheUtility exhPostalCodeCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility

	/**
	 * Get params from UI and build customer object
	 * @param params -serialized parameters from UI
	 * @param obj -N/A
	 * @return -a map containing all objects necessary for execute
	 * map contains isError(true/false) depending on method success
	 */
	public Object executePreCondition(Object params, Object obj) {
		LinkedHashMap result = new LinkedHashMap()
		try {
			GrailsParameterMap paramsMap = (GrailsParameterMap) params
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			ExhCustomer customer = buildCustomerObject(paramsMap)       // build customer object
			customer.code = Tools.EMPTY_SPACE                            // initially make it not null
			customer.userId = exhSessionUtil.appSessionUtil.getAppUser().id            // set user id which will not change on update customer

			if(!isValidPostalCode(customer.postCode)) {
				result.put(Tools.MESSAGE, INVALID_POSTAL_CODE)
				result.put(ERROR_POSTAL_CODE, Boolean.TRUE)
				return result
			}
			if(sarbImplService){
                ExhPhotoIdType customerPhotoType = (ExhPhotoIdType) exhPhotoIdTypeCacheUtility.read(customer.photoIdTypeId.longValue())
                Country country = (Country) countryCacheUtility.read(customer.countryId)
				String errorMsg = sarbImplService.validatePhotoIdNo(customer.photoIdNo, customerPhotoType.code, country.code)
				if(errorMsg) {
					result.put(ERROR_PHOTO_ID_NO, Boolean.TRUE)
					result.put(Tools.MESSAGE, errorMsg)
					return result
				}
			}
			result.put(CUSTOMER_OBJ, customer)
			result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception e) {
			log.error(e.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, CUSTOMER_SAVE_FAILURE_MESSAGE)
			return result
		}
	}

	/**
	 * 1. Save customer and customerTrace object in DB accordingly
	 * 2. If SARB Plugin is installed, also insert sarbInfo to DB
	 * This method is in transactional boundary and will roll back in case of any exception
	 * @param params -N/A
	 * @param obj -map returned from executePreCondition method
	 * @return -a map containing all objects necessary for buildSuccessResultForUI
	 * map contains isError(true/false) depending on method success
	 */
	@Transactional
	public Object execute(Object params, Object obj) {
		LinkedHashMap result = new LinkedHashMap()
		try {
			GrailsParameterMap parameterMap = (GrailsParameterMap) params
			LinkedHashMap preResult = (LinkedHashMap) obj  // cast map returned from executePreCondition method
			ExhCustomer customer = (ExhCustomer) preResult.get(CUSTOMER_OBJ)
			customer.companyId = exhSessionUtil.appSessionUtil.getCompanyId()         // get companyId from cache
			ExhCustomer exhCustomer = exhCustomerService.create(customer)    // save new customer object in DB
			long customerTraceId = exhCustomerTraceService.create(exhCustomer, new Date(), Tools.ACTION_CREATE)   // save customer trace obj in DB
			if (sarbImplService) {
				boolean isError = sarbImplService.createSarbCustomerDetails(parameterMap, exhCustomer.id)
				if (isError) {
					throw new RuntimeException(SARB_CUSTOMER_SAVE_FAILURE_MESSAGE)
				}
				Map sarbCustomerDetailsTrace = (Map) buildSarbCustomerDetailsTrace(parameterMap, customerTraceId, exhCustomer.id)
				isError = sarbImplService.createSarbCustomerDetailsTrace(sarbCustomerDetailsTrace)
				if(isError) {
					throw new RuntimeException(SARB_CUSTOMER_TRACE_SAVE_FAILURE_MESSAGE)
				}
			}
			result.put(CUSTOMER_OBJ, exhCustomer)
			result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception e) {
			log.error(e.getMessage())
			//@todo:rollback
			throw new RuntimeException(CUSTOMER_SAVE_FAILURE_MESSAGE)
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, CUSTOMER_SAVE_FAILURE_MESSAGE)
			return result
		}
	}

	/**
	 * do nothing for post operation
	 */
	public Object executePostCondition(Object params, Object obj) {
		return null
	}

	/**
	 * Show newly created customer object in grid
	 * Show success message
	 * @param obj -map from execute method
	 * @return -a map containing all objects necessary for grid view
	 * map contains isError(true/false) depending on method success
	 */
	public Object buildSuccessResultForUI(Object obj) {
		LinkedHashMap result = new LinkedHashMap()
		try {
			LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
			ExhCustomer customer = (ExhCustomer) executeResult.get(CUSTOMER_OBJ)
			GridEntity object = new GridEntity();     //build grid object
			object.id = customer.id;
			String dateOfBirth = DateUtility.getDateFormatAsString(customer.dateOfBirth)      // date format i.e. 'dd-MMMM-yyyy'
			object.cell = [Tools.LABEL_NEW,
					customer.code,
					customer.fullName,
					dateOfBirth,(customer.photoIdTypeId ? exhPhotoIdTypeCacheUtility.read(customer.photoIdTypeId).name : Tools.NONE),
					(customer.photoIdNo ? customer.photoIdNo : Tools.NONE),
					(customer.phone ? customer.phone : Tools.NONE)
			]

			result.put(Tools.ENTITY, object)
			result.put(Tools.VERSION, customer.version)
			result.put(Tools.MESSAGE, CUSTOMER_SAVE_SUCCESS_MESSAGE)
			result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception e) {
			log.error(e.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, CUSTOMER_SAVE_FAILURE_MESSAGE)
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
				Map preResult = (Map) obj         // cast map returned from previous method
				result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
				if(preResult.get(ERROR_POSTAL_CODE)) {
					result.put(ERROR_POSTAL_CODE, Boolean.TRUE)
				}
				if(preResult.get(ERROR_PHOTO_ID_NO)) {
					result.put(ERROR_PHOTO_ID_NO, Boolean.TRUE)
				}
			} else {
				result.put(Tools.MESSAGE, CUSTOMER_SAVE_FAILURE_MESSAGE)
			}
			return result
		} catch (Exception e) {
			log.error(e.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, CUSTOMER_SAVE_FAILURE_MESSAGE)
			return result
		}
	}

	/**
	 * Build customer object
	 * @param params -serialized parameters from UI
	 * @return -new customer object
	 */
	private ExhCustomer buildCustomerObject(GrailsParameterMap params) {
		ExhCustomer customer = new ExhCustomer(params)    // initialize customer object

		if (params.visaExpireDate) {        // check required params
			customer.visaExpireDate = DateUtility.parseMaskedDate(params.visaExpireDate)  // format date i.e  'dd/MM/yyyy'
		} else {
			customer.visaExpireDate = null
		}

		if (params.isCorporate.toString().equals(ON)) {                 // check required params  if exist
			customer.companyRegNo = params.companyRegNo              // company registration no
			customer.dateOfIncorporation = params.dateOfIncorporation     // date
		}

		if (params.smsSubscription.toString().equals(ON)) {
			customer.smsSubscription = true
		}

		if (params.mailSubscription.toString().equals(ON)) {
			customer.mailSubscription = true
		}

		if (params.addressVerifiedBy) {        // check required params   if exist
			customer.addressVerifiedStatus = Tools.CUSTOMER_ADDRESS_VERIFIED       //   set address verified value '1'
		} else {
			customer.addressVerifiedStatus = Tools.CUSTOMER_ADDRESS_NOT_VERIFIED     // or not set value '0'
		}
		//setDefaultPassword
		customer.companyId = exhSessionUtil.appSessionUtil.getCompanyId()
		customer.agentId = Long.parseLong(params.agentId)             // set agentId = '0' if create  customer  by cashier else  agent user id
		customer.dateOfBirth = DateUtility.parseMaskedDate(params.customerDateOfBirth)     // format date i.e  'dd/MM/yyyy'
		customer.createdOn = new Date();
		customer.photoIdExpiryDate = DateUtility.parseMaskedDate(params.customerPhotoIdExpiryDate)
		customer.declarationStart = DateUtility.parseMaskedDate(params.declarationStart)
		customer.declarationEnd = DateUtility.parseMaskedDate(params.declarationEnd)
        customer.isBlocked=Boolean.FALSE
		return customer
	}

	/**
	 * build SarbCustomerDetailsTrace in a Map
	 * @param params - serialize parameter from UI
	 * @param customerTraceId - ExhCustomerTrace.id
	 * @return
	 */
	private Map buildSarbCustomerDetailsTrace(GrailsParameterMap params, long customerTraceId, long customerId) {
		Map parameterMap = new LinkedHashMap()
		parameterMap.customerTraceId = customerTraceId
		parameterMap.customerId = customerId.toString()
		parameterMap.suburb = params.suburb
		parameterMap.provinceId = params.provinceId
		parameterMap.city = params.city
		parameterMap.contactSurname = params.contactSurname
		parameterMap.contactName = params.contactName
		return parameterMap
	}

	private boolean isValidPostalCode(String code) {
		long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
		SysConfiguration sysConfig = exhSysConfigurationCacheUtility.readByKeyAndCompanyId(ExhSysConfigurationIntf.EXH_POSTAL_CODE_VALIDATION, companyId)
		boolean validation = false //default value
		if(sysConfig) {
			validation = sysConfig.value.equals(VALUE_1)
		}
		if(validation) {
			ExhPostalCode exhPostalCode = exhPostalCodeCacheUtility.readByCodeAndCompanyId(code, companyId)
			if(!exhPostalCode) {
				return false
			}
		}
		return true
	}
}
