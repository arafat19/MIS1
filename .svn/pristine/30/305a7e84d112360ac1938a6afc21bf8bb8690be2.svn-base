package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.service.ExhCustomerTraceService
import com.athena.mis.exchangehouse.utility.ExhUserCustomerCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired


class BlockExhCustomerActionService extends BaseService implements ActionIntf{

    @Autowired
    ExhUserCustomerCacheUtility userExhCustomerCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    ExhCustomerService exhCustomerService
    ExhCustomerTraceService exhCustomerTraceService
    AppUserService appUserService

    private Logger log = Logger.getLogger(getClass())

    private static final String CUSTOMER_BLOCKED_FAILURE_MESSAGE="Failed to block customer"
    private static final String CUSTOMER_BLOCKED_SUCCESSFULLY="Customer has blocked successfully"
    private static final String EXH_CUSTOMER="exhCustomer"
    private static final String CUSTOMER_IS_BLOCKED_ALREADY="Customer is already blocked"

    /**
     * @param parameters-parameters from params
     * @param obj-N/A
     * @return map containing all object necessary for execute
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj){
        Map result= new LinkedHashMap()
        try{
            result.put(Tools.IS_ERROR,Boolean.TRUE)
            GrailsParameterMap parameterMap=(GrailsParameterMap) parameters
            if(!parameterMap.id){
                result.put(Tools.MESSAGE,CUSTOMER_BLOCKED_FAILURE_MESSAGE)
                return result
            }
            long customerId=Long.parseLong(parameterMap.id)
            ExhCustomer exhCustomer=exhCustomerService.read(customerId)
            if(exhCustomer.isBlocked){
                result.put(Tools.MESSAGE,CUSTOMER_IS_BLOCKED_ALREADY)
                return result
            }
            result.put(EXH_CUSTOMER,exhCustomer)
            result.put(Tools.IS_ERROR,Boolean.FALSE)
            return result
        }catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_BLOCKED_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for executePostCondition
     */
    public  Object executePostCondition(Object parameters, Object obj){
        return null
    }

    /**
     * Block customer
     * AppUser- enable false if true
     * @param parameters- parameters from execute PreCondition
     * @param obj-N/A
     * @return - a map containing all object necessary for execute
     */
    @Transactional
    public Object execute(Object parameters, Object obj){
        Map result= new LinkedHashMap()
        try{
            result.put(Tools.IS_ERROR,Boolean.TRUE)
            Map executeResult=(Map)parameters
            ExhCustomer exhCustomer=(ExhCustomer)executeResult.get(EXH_CUSTOMER)
            exhCustomer.isBlocked=true                  //isBlocked true if customer is blocked
            exhCustomerService.updateIsBlockedCustomer(exhCustomer)
            exhCustomerTraceService.create(exhCustomer,new Date(),Tools.ACTION_UPDATE)
            disableAppUser(exhCustomer)
            result.put(Tools.IS_ERROR,Boolean.FALSE)
            return result
        }catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_BLOCKED_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Make block customer as disable
     * update enable property of appUserService as well as cacheUtility
     * @param exhCustomer - exhCustomer obj
     */
    private void disableAppUser(ExhCustomer exhCustomer) {
        AppUserEntity appUserEntity = userExhCustomerCacheUtility.readByCustomerId(exhCustomer.id)
        if (appUserEntity) {
            AppUser appUser = appUserService.read(appUserEntity.appUserId)
            if (appUser.enabled) {
                appUser.enabled = false
                appUserService.updateEnabled(appUser)
                appUserCacheUtility.update(appUser, appUserCacheUtility.SORT_ON_NAME, appUserCacheUtility.SORT_ORDER_ASCENDING)
            }
        }
    }

    /**
     * Build success result for UI
     * @param obj-N/A
     * @return- success result to show indicate success event
     */

    public Object buildSuccessResultForUI(Object obj){
        Map result=new LinkedHashMap()
        result.put(Tools.MESSAGE,CUSTOMER_BLOCKED_SUCCESSFULLY)
        result.put(Tools.IS_ERROR,Boolean.FALSE)
        return result
    }

    /**
     * Build failure result for UI
     * @param obj-N/A may be null
     * @return- failure message to indicate failure event
     */
    public  Object buildFailureResultForUI(Object obj){

        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, CUSTOMER_BLOCKED_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_BLOCKED_FAILURE_MESSAGE)
            return result
        }
    }
}
