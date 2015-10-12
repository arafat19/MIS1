package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.service.ExhCustomerTraceService
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap


class UnblockExhCustomerActionService extends BaseService implements ActionIntf{

    ExhCustomerService exhCustomerService
    ExhCustomerTraceService exhCustomerTraceService

    private Logger log = Logger.getLogger(getClass())

    private static final String CUSTOMER_UNBLOCKED_FAILURE_MESSAGE = "Failed to unblock customer"
    private static final String CUSTOMER_UNBLOCKED_SUCCESS_MESSAGE = "Customer unblocked successfully"
    private static final String ONLY_BLOCKED_CUSTOMER_CAN_BE_UNBLOCKED = "Only blocked customer can be unblocked"

    /**
     * Get customerId from parameters
     * @param parameters- params
     * @param obj-N/A
     * @return - a map containing all object necessary for execute
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj){
        Map result= new LinkedHashMap()
        try{
            result.put(Tools.IS_ERROR,Boolean.TRUE)
            GrailsParameterMap parameterMap=(GrailsParameterMap) parameters
            if(!parameterMap.customerId){
                result.put(Tools.MESSAGE,Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long customerId=Long.parseLong(parameterMap.customerId.toString())
            ExhCustomer exhCustomer=exhCustomerService.read(customerId)
            if(!exhCustomer.isBlocked){
                result.put(Tools.MESSAGE,ONLY_BLOCKED_CUSTOMER_CAN_BE_UNBLOCKED)
                return result
            }
            result.put(Tools.ENTITY,exhCustomer)
            result.put(Tools.IS_ERROR,Boolean.FALSE)
            return result
        }catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_UNBLOCKED_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for executePostCondition
     */
    public Object executePostCondition(Object parameters, Object obj){
        return null
    }

    /**
     * 1. update exh_customer- isBlocked :false
     * @param parameters- returned from executePreCondition
     * @param obj-N/A
     * @return - true/ false based on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj){
        Map result= new LinkedHashMap()
        try{
            Map executeResult=(Map)parameters
            ExhCustomer exhCustomer=(ExhCustomer)executeResult.get(Tools.ENTITY)
            exhCustomer.isBlocked=false             // isBlocked false if exhCustomer is unblocked
            exhCustomerService.updateIsBlockedCustomer(exhCustomer)
            exhCustomerTraceService.create(exhCustomer,new Date(),Tools.ACTION_UPDATE)
            result.put(Tools.IS_ERROR,Boolean.FALSE)
            return result
        }catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_UNBLOCKED_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build success message for UI
     * @param obj-N/A
     * @return - success message to indicate success event
     */
    public Object buildSuccessResultForUI(Object obj){
        Map result= new LinkedHashMap()
        result.put(Tools.MESSAGE,CUSTOMER_UNBLOCKED_SUCCESS_MESSAGE)
        result.put(Tools.IS_ERROR,Boolean.FALSE)
        return result
    }

    /**
     * Build failure message for UI to show in case of any failure
     * @param obj - returned from previous method may be null
     * @return - failure message to show failure event
     */
    public Object buildFailureResultForUI(Object obj){
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, CUSTOMER_UNBLOCKED_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_UNBLOCKED_FAILURE_MESSAGE)
            return result
        }
    }
}
