package com.athena.mis.integration.sarb

import com.athena.mis.sarb.actions.customerdetails.CreateSarbCustomerDetailsActionService
import com.athena.mis.sarb.actions.customerdetails.UpdateSarbCustomerDetailsActionService
import com.athena.mis.sarb.actions.customerdetails.ValidateSarbCustomerPhotoIdNoActionService
import com.athena.mis.sarb.actions.taskmodel.ValidateSarbTaskDetailsActionService
import com.athena.mis.sarb.config.SarbSysConfigurationCacheUtility
import com.athena.mis.sarb.service.SarbCustomerDetailsService
import com.athena.mis.sarb.service.SarbCustomerDetailsTraceService
import com.athena.mis.sarb.utility.SarbProvinceCacheUtility
import com.athena.mis.sarb.utility.SarbSessionUtil
import com.athena.mis.sarb.utility.SarbTaskReviseStatusCacheUtility
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class SarbImplService extends SarbPluginConnector {

	static transactional = false
	static lazyInit = false

	@Autowired
	SarbProvinceCacheUtility sarbProvinceCacheUtility
    @Autowired
    SarbSessionUtil sarbSessionUtil
    @Autowired
    SarbSysConfigurationCacheUtility sarbSysConfigurationCacheUtility
    @Autowired
    SarbTaskReviseStatusCacheUtility sarbTaskReviseStatusCacheUtility

	SarbBootStrapService sarbBootStrapService
	SarbDefaultDataBootStrapService sarbDefaultDataBootStrapService
	SarbSchemaUpdateBootStrapService sarbSchemaUpdateBootStrapService
	CreateSarbCustomerDetailsActionService createSarbCustomerDetailsActionService
	UpdateSarbCustomerDetailsActionService updateSarbCustomerDetailsActionService
	SarbCustomerDetailsService sarbCustomerDetailsService
	SarbCustomerDetailsTraceService sarbCustomerDetailsTraceService
	ValidateSarbCustomerPhotoIdNoActionService validateSarbCustomerPhotoIdNoActionService
    ValidateSarbTaskDetailsActionService validateSarbTaskDetailsActionService

	// registering plugin in servletContext
	@Override
	public boolean initialize() {
		setPlugin(SARB, this);
		return true
	}

	@Override
	public String getName() {
		return SARB;
	}

	@Override
	public int getId() {
		return SARB_ID;
	}

	public List provinceList() {
		long companyId = sarbSessionUtil.appSessionUtil.getCompanyId()
		List lstProvince = sarbProvinceCacheUtility.list(companyId)
		return lstProvince
	}

	// init bootstrap of exh plugin
	public void bootStrap(boolean isSchema, boolean isData) {
		if (isSchema) sarbSchemaUpdateBootStrapService.init()
		if (isData) sarbDefaultDataBootStrapService.init()
		sarbBootStrapService.init()
	}

	/**
	 * create SarbCustomerDetails in DB
	 * @param params - serialize parameter
	 * @param customerId - exhCustomer.id
	 * @return true/false
	 */
	public boolean createSarbCustomerDetails(Object params, long customerId) {
		GrailsParameterMap parameterMap = (GrailsParameterMap) params
		parameterMap << [customerId: customerId]
		Map result = (Map) createSarbCustomerDetailsActionService.execute(params, null)
		Boolean isError = result.isError
		return isError.booleanValue()
	}

	/**
	 * update SarbCustomerDetails
	 * @param params - serialize parameter
	 * @return true/false
	 */
	public boolean updateSarbCustomerDetails(Object params) {
		Boolean isError
		GrailsParameterMap parameterMap = (GrailsParameterMap) params
		Map preResult = (Map) updateSarbCustomerDetailsActionService.executePreCondition(parameterMap, null)
		isError = preResult.isError
		if (isError.booleanValue()) {
			return isError.booleanValue()
		}
		Map executeResult = (Map) updateSarbCustomerDetailsActionService.execute(null, preResult)
		isError = executeResult.isError
		return isError.booleanValue()
	}

	/**
	 * Create SarbCustomerDetailsTrace
	 * @return - isError true/false
	 */
	public boolean createSarbCustomerDetailsTrace(Object params) {
		Boolean isError
		Map parameterMap = (Map) params
		boolean success = sarbCustomerDetailsTraceService.create(parameterMap)
		isError = !success
		return isError
	}

	/**
	 * Select SarbCustomerDetails
	 * @param customerId - exhCustomer.id
	 * @return SarbCustomerDetails obj
	 */
	public Object readSarbCustomerDetails(long customerId) {
		return sarbCustomerDetailsService.readByCustomerId(customerId)
	}

	/**
	 * validate customer photoIdNo
	 * @param photoIdNo - param from UI
	 * @return true/false
	 */
	public String validatePhotoIdNo(String photoIdNo, String customerPhotoTypeCode, String countryCode) {
		Boolean isError
        Map params = [photoIdNo: photoIdNo, customerPhotoTypeCode: customerPhotoTypeCode, countryCode: countryCode]
		Map result = (Map) validateSarbCustomerPhotoIdNoActionService.execute(params, null)
		isError = result.isError
		if(isError) {
            return result.message
        }
        return null
	}

    /**
     * Init sys_configuration cacheUtility
     */
    public void initSarbSysConfiguration() {
        sarbSysConfigurationCacheUtility.init()
    }

    //validated on exchange house task create
    public String validateTaskDetails(String remittancePurposeCode, photoIdTypeCode) {
        Boolean isError
        Map params = [remittancePurposeCode: remittancePurposeCode, photoIdTypeCode: photoIdTypeCode]
        Map result = (Map) validateSarbTaskDetailsActionService.execute(params, null)
        isError = result.isError
        if(isError) {
            return result.message
        }
        return null
    }

    public void initSarbTaskReviseStatus() {
        sarbTaskReviseStatusCacheUtility.init()
    }

    public List<Object> listSarbTaskReviseStatus() {
        return sarbTaskReviseStatusCacheUtility.listByIsActive()
    }

    public Object readByReservedSarbTaskReviseStatus(long reservedId, long companyId) {
        return sarbTaskReviseStatusCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }
}
