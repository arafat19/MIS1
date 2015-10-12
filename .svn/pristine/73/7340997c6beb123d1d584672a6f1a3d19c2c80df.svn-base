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
 *  Show UI for task process and list of task for grid
 *  For details go through Use-Case doc named 'ShowCustomerTaskForAdminActionService'
 */
class ShowCustomerTaskForAdminActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

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
     * Get list of task status for dropDown and Task list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        Map result
        try {
            GrailsParameterMap params= (GrailsParameterMap) parameters
            params.rp = DEFAULT_RESULT_PER_PAGE         // set result per page '15'

            initPager(params)                             // initialize params for flexGrid

            Date startDate = new Date() - DateUtility.DATE_RANGE_SEVEN         // set date range for task query in '7'  days
            startDate = DateUtility.setFirstHour(startDate)                      // set first hour ie 00:00:00

            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity exhCanceledTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_CANCELLED_TASK, companyId)
            SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
            SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)

            long statusNewTask = exhNewTaskSysEntityObject.id
            long statusSentToBank = exhSentToBankSysEntityObject.id
            long statusCancelledTask = exhCanceledTaskSysEntityObject.id
            result = [                            // build a map for UI
                    statusNewTask: statusNewTask,
                    statusSentToBank: statusSentToBank,
                    statusCancelledTask: statusCancelledTask,
                    createdDateFrom: DateUtility.getDateForUI(startDate),     // set UI date format ie 'dd/MM/yyyy'
            ]
            return result
        } catch (Throwable e) {
            log.error(e.message)
            result = [lstTaskStatus: null]
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

