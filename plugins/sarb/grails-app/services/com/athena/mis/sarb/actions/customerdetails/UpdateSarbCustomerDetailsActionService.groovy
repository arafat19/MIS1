package com.athena.mis.sarb.actions.customerdetails


import com.athena.mis.sarb.entity.SarbCustomerDetails
import com.athena.mis.sarb.service.SarbCustomerDetailsService
import com.athena.mis.sarb.utility.SarbSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Update SarbCustomerDetails
 *  For details go through Use-Case doc named 'UpdateSarbCustomerDetailsActionService'
 */
class UpdateSarbCustomerDetailsActionService {

	SarbCustomerDetailsService sarbCustomerDetailsService
    @Autowired
    SarbSessionUtil sarbSessionUtil

	private static final String SARB_CUSTOMER_UPDATE_FAILURE_MESSAGE = "Failed to update sarb customer"
	private static final String CUSTOMER_DETAILS_OBJ = "customerDetailsObj"
	private final Logger log = Logger.getLogger(getClass())

	/**
	 * 1. check if id(ExhCustomer.id) exists
	 * 2. check if sarbCustomerDetails exists by customerId
	 * @param parameters - serialize parameter
	 * @param obj - N/A
	 * @return - Map for execute
	 */
	@Transactional(readOnly = true)
	public Object executePreCondition(Object parameters, Object obj) {
		Map result = new LinkedHashMap()
		try{
			GrailsParameterMap params = (GrailsParameterMap) parameters
			result.put(Tools.IS_ERROR, true)
			if(!params.id) {
				return result
			}
			long customerId = Long.parseLong(params.id)
			SarbCustomerDetails customerDetails = sarbCustomerDetailsService.readByCustomerId(customerId)
			if(!customerDetails) {
				return result
			}
			customerDetails = buildSarbCustomerDetails(params, customerDetails)
			result.put(CUSTOMER_DETAILS_OBJ, customerDetails)
			result.put(Tools.IS_ERROR, false)
			return result
		}
		catch(Exception ex) {
			log.error(ex.getMessage())
			result.put(Tools.IS_ERROR, true)
			return result
		}
	}

	/**
	 * do nothing for postCondition
	 */
	public Object executePostCondition(Object parameters, Object obj) {
		return null
	}

	/**
	 * update SarbCustomerDetails in DB
	 * @param parameters - N/A
	 * @param obj - executePreResult
	 * @return
	 */
	@Transactional
	public Object execute(Object parameters, Object obj) {
		Map result = new LinkedHashMap()
		try{
			LinkedHashMap preResult = (LinkedHashMap) obj
			SarbCustomerDetails customerDetails = (SarbCustomerDetails) preResult.get(CUSTOMER_DETAILS_OBJ)
			sarbCustomerDetailsService.update(customerDetails)
			result.put(Tools.IS_ERROR, false)
			return result
		}
		catch (Exception ex) {
			log.error(ex.getMessage())
			//@todo: rollback on exception
			throw new RuntimeException(SARB_CUSTOMER_UPDATE_FAILURE_MESSAGE)
			result.put(Tools.IS_ERROR, true)
			return result
		}
	}
	/**
	 * do nothing for buildSuccess
	 */
	public Object buildSuccessResultForUI(Object obj) {
		return null
	}

	/**
	 * do nothing for buildFailure
	 */
	public Object buildFailureResultForUI(Object obj) {
		return null
	}

	/**
	 * build new SarbCustomerDetails for update
	 * @param params - serialize parameter
	 * @param customerDetails - old sarbCustomerDetails
	 * @return - new SarbCustomerDetails
	 */
	private SarbCustomerDetails buildSarbCustomerDetails(GrailsParameterMap params, SarbCustomerDetails customerDetails) {
		SarbCustomerDetails newCustomerDetails = new SarbCustomerDetails(params)
		customerDetails.suburb = newCustomerDetails.suburb
		customerDetails.provinceId = newCustomerDetails.provinceId
		customerDetails.city = newCustomerDetails.city
		customerDetails.contactSurname = newCustomerDetails.contactSurname
		customerDetails.contactName = newCustomerDetails.contactName
		customerDetails.updatedBy = sarbSessionUtil.appSessionUtil.getAppUser().id
		customerDetails.updatedOn = new Date()
		return customerDetails
	}
}
