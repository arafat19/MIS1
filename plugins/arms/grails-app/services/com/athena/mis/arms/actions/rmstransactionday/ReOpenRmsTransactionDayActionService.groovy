package com.athena.mis.arms.actions.rmstransactionday

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.arms.entity.RmsTransactionDay
import com.athena.mis.arms.service.RmsTransactionDayService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * open Transaction Day
 * for details go through use-case named "ReOpenRmsTransactionDayActionService"
 */
class ReOpenRmsTransactionDayActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())
    private static final String DEFAULT_FAILURE_MSG = "Failed to re-open transaction day"
    private static final String CANNOT_REOPEN_PREV = "Previous transaction day cannot be re-opened"
    private static final String NOT_FOUND = "Transaction day not found"
    private static final String ALREADY_OPENED = "Transaction day already opened"
    private static final String SUCCESS_MSG = "Transaction day re-opened successfully"

    @Autowired
    AppUserCacheUtility appUserCacheUtility

    RmsTransactionDayService rmsTransactionDayService

    /**
     * check pre-condition to reopen transaction day
     * 1. If transaction day exists
     * 2. if day is opened
     * 3. if transaction day is present day
     * @param parameters - params with Id of RmsTransactionDay
     * @param obj - N/A
     * @return - Map for execute
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if(!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long transactionDayId = Tools.parseLongInput(params.id)
            RmsTransactionDay rmsTransactionDay = rmsTransactionDayService.read(transactionDayId)
            if(!rmsTransactionDay) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }
            String errMsg = checkAlreadyOpened(rmsTransactionDay)
            if (errMsg) {
                result.put(Tools.MESSAGE, errMsg)
                return result
            }
            errMsg = checkPreConditionForReOpen(rmsTransactionDay)
            if (errMsg) {
                result.put(Tools.MESSAGE, errMsg)
                return result
            }
            buildTransactionDay(rmsTransactionDay)
            result.put(Tools.ENTITY, rmsTransactionDay)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * do nothing
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * reopen transaction day and update RmsTransactionDay obj in DB
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            RmsTransactionDay rmsTransactionDay = (RmsTransactionDay) preResult.get(Tools.ENTITY)
            rmsTransactionDayService.updateForReOpen(rmsTransactionDay)
            GridEntity object = new GridEntity()
            AppUser appUserOpened = (AppUser) appUserCacheUtility.read(rmsTransactionDay.openedBy)
            AppUser appUserClosed = (AppUser) appUserCacheUtility.read(rmsTransactionDay.closedBy)
            object.id = rmsTransactionDay.id
            object.cell = [
                    Tools.LABEL_NEW,
                    DateUtility.getDateFormatAsString(rmsTransactionDay.transactionDate),
                    appUserOpened? appUserOpened.username : Tools.EMPTY_SPACE,
                    DateUtility.getDateTimeFormatAsString(rmsTransactionDay.openedOn),
                    appUserClosed? appUserClosed.username : Tools.EMPTY_SPACE,
                    rmsTransactionDay.closedOn? DateUtility.getDateTimeFormatAsString(rmsTransactionDay.closedOn) : Tools.EMPTY_SPACE
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MSG)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * do nothing
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * build transaction day for reopen
     */
    private void buildTransactionDay(RmsTransactionDay rmsTransactionDay) {
        rmsTransactionDay.closedBy = 0L
        rmsTransactionDay.closedOn = null
    }

    /**
     * check if transaction day is already opened
     */
    private String checkAlreadyOpened(RmsTransactionDay rmsTransactionDay) {
        if(rmsTransactionDay.closedBy == 0) {
            return ALREADY_OPENED
        }
        return null
    }

    /**
     * check if transaction day is the present day
     * prev. transaction can not be reopened
     */
    private String checkPreConditionForReOpen(RmsTransactionDay rmsTransactionDay) {
        String transactionDate = DateUtility.getDBDateFormat(rmsTransactionDay.transactionDate)
        String newDate = DateUtility.getDBDateFormat(new Date())
        if(!transactionDate.equals(newDate)){
            return CANNOT_REOPEN_PREV
        }
        return null
    }
}
