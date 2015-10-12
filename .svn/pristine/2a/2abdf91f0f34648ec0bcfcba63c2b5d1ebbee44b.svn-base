package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for task process and list of task for grid for other bank
 *  For details go through Use-Case doc named 'ExhShowTasksForOtherBankUserActionService'
 */
class ExhShowTasksForOtherBankUserActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = 'Failed to load page'
    private static final String STATUS_SEND_TO_OTHER_BANK = 'SendToOtherBank'
    private static final String STATUS_RESOLVED = 'Resolved'

    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null

    }

    /**
     * Get list of task status for dropDown
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        Map result
        try {
            GrailsParameterMap params=(GrailsParameterMap) parameters
            params.rp = DEFAULT_RESULT_PER_PAGE      // set result per page '15'
            initPager(params)                         // initialize params for flexGrid
            Date startDateStr = new Date() -DateUtility.DATE_RANGE_SEVEN    // set date range for task query in '7'  days
            startDateStr = DateUtility.setFirstHour(startDateStr)             // set first hour ie 00:00:00
            List lstTaskStatus = []
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
            SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

            SystemEntity sentToOtherBankStatus = (SystemEntity) exhTaskStatusCacheUtility.read(exhSentToOtherBankSysEntityObject.id)   // get task list status ie- SENT_TO_OTHER_BANK
            SystemEntity resolvedByOtherBankStatus = (SystemEntity) exhTaskStatusCacheUtility.read(exhResolvedByOtherBankSysEntityObject.id)    // get task list status ie- RESOLVED_BY_OTHER_BANK
            lstTaskStatus << [id: sentToOtherBankStatus.id, label: STATUS_SEND_TO_OTHER_BANK]
            lstTaskStatus << [id: resolvedByOtherBankStatus.id, label: STATUS_RESOLVED]

            int statusSentToOtherBank = sentToOtherBankStatus.id.intValue()
            int statusResolvedByOtherBank = resolvedByOtherBankStatus.id.intValue()

            result = [                          // build a for UI
                    isError: false,
                    createdDateFrom: DateUtility.getDateForUI(startDateStr),
                    lstTaskStatus: lstTaskStatus,
                    statusSentToOtherBank: statusSentToOtherBank,
                    statusResolvedByOtherBank: statusResolvedByOtherBank
            ]
            return result
        } catch (Throwable e) {
            log.error(e.message)
            result = [isError: true, message: FAILURE_MESSAGE]
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
     * do nothing for build success result for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

}
