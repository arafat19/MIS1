package com.athena.mis.arms.actions.rmstransactionday

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.arms.entity.RmsTransactionDay
import com.athena.mis.arms.service.RmsTransactionDayService
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * list rmsTransactionDay
 * for details go through use case named "ListRmsTransactionDayActionService"
 */
class ListRmsTransactionDayActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    RmsTransactionDayService rmsTransactionDayService

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_FAILURE_MSG = "Failed to load transaction day grid"
    private static final String LST_TRANSACTION_DAY = "lstTransactionDay"

    /**
     * do nothing
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * find list of RmsTransactionDay
     * @param parameters - grid parameters
     * @param obj
     * @return
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap();
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameterMap)
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            List<RmsTransactionDay> lstTransactionDay = rmsTransactionDayService.list(this, companyId)
            int count = rmsTransactionDayService.count(companyId)
            result.put(LST_TRANSACTION_DAY, lstTransactionDay)
            result.put(Tools.COUNT, count.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex){
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * build success result and wrap list of RmsTransactionDay
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<RmsTransactionDay> rmsTransactionDayList = (List<RmsTransactionDay>) executeResult.get(LST_TRANSACTION_DAY)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedTransactionDay = wrapTransactionDay(rmsTransactionDayList, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedTransactionDay]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
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
     * wrap list of RmsTransactionDay for grid
     */
    private List wrapTransactionDay(List<RmsTransactionDay> lstTransactionDay, int start) {
        List lstWrappedTransactionDay = []
        int counter = start + 1
        for (int i = 0; i < lstTransactionDay.size(); i++) {
            RmsTransactionDay rmsTransactionDay = lstTransactionDay[i]
            AppUser appUserOpened = (AppUser) appUserCacheUtility.read(rmsTransactionDay.openedBy)
            AppUser appUserClosed = (AppUser) appUserCacheUtility.read(rmsTransactionDay.closedBy)
            GridEntity obj = new GridEntity()
            obj.id = rmsTransactionDay.id
            obj.cell = [
                    counter,
                    DateUtility.getDateFormatAsString(rmsTransactionDay.transactionDate),
                    appUserOpened? appUserOpened.username : Tools.EMPTY_SPACE,
                    DateUtility.getDateTimeFormatAsString(rmsTransactionDay.openedOn),
                    appUserClosed? appUserClosed.username : Tools.EMPTY_SPACE,
                    rmsTransactionDay.closedOn? DateUtility.getDateTimeFormatAsString(rmsTransactionDay.closedOn) : Tools.EMPTY_SPACE
            ]
            lstWrappedTransactionDay << obj
            counter++
        }
        return lstWrappedTransactionDay
    }
}
