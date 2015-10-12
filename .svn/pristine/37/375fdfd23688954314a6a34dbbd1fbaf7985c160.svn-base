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
import org.springframework.beans.factory.annotation.Autowired

/**
 * open Transaction Day
 * for details go through use-case named "OpenRmsTransactionDayActionService"
 */
class OpenRmsTransactionDayActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())
    private static final String DEFAULT_FAILURE_MSG = "Failed to open transaction day"
    private static final String ALREADY_EXISTS = "Transaction day already exists"
    private static final String DAY_OPENED = "Transaction day is not closed on: "
    private static final String SUCCESS_MSG = "Transaction day opened successfully"

    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    RmsTransactionDayService rmsTransactionDayService

    /**
     * check pre-condition to open transaction day
     * 1. If already day is opened
     * 2. if prev. day is closed
     * @param parameters - N/A
     * @param obj - N/A
     * @return - Map for execute
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            String errMsg = null
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            errMsg = checkExistingTransactionDay(companyId)
            if (errMsg) {
                result.put(Tools.MESSAGE, errMsg)
                return result
            }
            errMsg = checkPrevOpenedTransactionDay(companyId)
            if (errMsg) {
                result.put(Tools.MESSAGE, errMsg)
                return result
            }
            RmsTransactionDay rmsTransactionDay = buildTransactionDay(companyId)
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
     * save RmsTransactionDay in DB
     * @param parameters N/A
     * @param obj - preResult
     * @return
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            RmsTransactionDay rmsTransactionDay = (RmsTransactionDay) preResult.get(Tools.ENTITY)
            rmsTransactionDay = rmsTransactionDayService.create(rmsTransactionDay)
            GridEntity object = new GridEntity()
            AppUser appUserOpened = (AppUser) appUserCacheUtility.read(rmsTransactionDay.openedBy)
            object.id = rmsTransactionDay.id
            object.cell = [
                    Tools.LABEL_NEW,
                    DateUtility.getDateFormatAsString(rmsTransactionDay.transactionDate),
                    appUserOpened? appUserOpened.username : Tools.EMPTY_SPACE,
                    DateUtility.getDateTimeFormatAsString(rmsTransactionDay.openedOn),
                    Tools.EMPTY_SPACE,
                    Tools.EMPTY_SPACE
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
     * build RmsTransactionDay obj
     */
    private RmsTransactionDay buildTransactionDay(long companyId) {
        RmsTransactionDay rmsTransactionDay = new RmsTransactionDay()
        rmsTransactionDay.transactionDate = new Date()
        rmsTransactionDay.openedOn = new Date()
        rmsTransactionDay.openedBy = rmsSessionUtil.appSessionUtil.getAppUser().id
        rmsTransactionDay.companyId = companyId
        return rmsTransactionDay
    }

    /**
     * check if transaction day already exists
     */
    private String checkExistingTransactionDay(long companyId) {
        Date trDate = new Date()
        String transactionDate = DateUtility.getDBDateFormat(trDate)
        String query = """
        SELECT count(id) FROM rms_transaction_day where transaction_date = '${transactionDate}' and company_id = :companyId
        """
        Map queryParam = [
                companyId: companyId
        ]
        List result = executeSelectSql(query, queryParam)
        int count = result[0].count
        if(count > 0) {
            return ALREADY_EXISTS
        }
        return null
    }

    /**
     * check if previous transaction day is closed
     */
    private String checkPrevOpenedTransactionDay(long companyId) {
        String query = """
        SELECT count(id), transaction_date FROM rms_transaction_day where closed_on is null and company_id = :companyId GROUP BY transaction_date
        """
        Map queryParam = [
                companyId: companyId
        ]
        List result = executeSelectSql(query, queryParam)
        if(result.size() > 0) {
            int count = result[0].count
            if(count > 0) {
                Date dbDate = result[0].transaction_date
                String date = DateUtility.getDateForUI(dbDate)
                return DAY_OPENED + date
            }
        }
        return null
    }
}
