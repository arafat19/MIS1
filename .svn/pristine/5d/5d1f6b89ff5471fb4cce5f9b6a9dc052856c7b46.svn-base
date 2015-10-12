package com.athena.mis.sarb.actions.customerdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.sarb.entity.SarbCustomerDetails
import com.athena.mis.sarb.service.SarbCustomerDetailsService
import com.athena.mis.sarb.utility.SarbSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Create new SarbCustomerDetails object
 *  For details go through Use-Case doc named 'CreateSarbCustomerDetailsActionService'
 */
class CreateSarbCustomerDetailsActionService extends BaseService implements ActionIntf{

    SarbCustomerDetailsService sarbCustomerDetailsService
    @Autowired
    SarbSessionUtil sarbSessionUtil

    private static final String SARB_CUSTOMER_SAVE_FAILURE_MESSAGE = "Failed to save sarb customer"
    private final Logger log = Logger.getLogger(getClass())

    /**
     * do nothing for preCondition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for postCondition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build and Create sarbCustomerDetails in DB
     * @param parameters - serialize parameter
     * @param obj - N/A
     * @return - Map containing IS_ERROR(T/F)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try{
            GrailsParameterMap parameterMap = (GrailsParameterMap ) parameters
            SarbCustomerDetails customerDetails = buildSarbCustomerDetails(parameterMap)
            sarbCustomerDetailsService.create(customerDetails)
            result.put(Tools.IS_ERROR, false)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo: rollback on exception
            throw new RuntimeException(SARB_CUSTOMER_SAVE_FAILURE_MESSAGE)
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
     *do nothing for buildFailure
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * build SarbCustomerDetails
     * @param params - serialize parameter
     * @return - SarbCustomerDetails obj
     */
    private SarbCustomerDetails buildSarbCustomerDetails(GrailsParameterMap params) {
        SarbCustomerDetails customerDetails = new SarbCustomerDetails(params)
        customerDetails.version = 0
        customerDetails.companyId = sarbSessionUtil.appSessionUtil.getCompanyId()
        customerDetails.updatedBy = 0L
        return customerDetails
    }
}
