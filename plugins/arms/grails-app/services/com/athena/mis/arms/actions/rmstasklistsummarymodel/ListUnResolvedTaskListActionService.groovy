package com.athena.mis.arms.actions.rmstasklistsummarymodel

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.arms.model.RmsTaskListSummaryModel
import com.athena.mis.arms.service.RmsTaskListSummaryModelService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ListUnResolvedTaskListActionService extends BaseService implements ActionIntf {

    RmsTaskListSummaryModelService rmsTaskListSummaryModelService

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_FAILURE_MSG = "Failed to load un-resolved task list grid"
    private static final String LST_TASK_LIST = "lstTaskList"

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
            List<RmsTaskListSummaryModel> lstTaskListSummaryModel = rmsTaskListSummaryModelService.listUnResolvedTaskList(this)
            int count = rmsTaskListSummaryModelService.countUnResolvedTaskList()
            result.put(LST_TASK_LIST, lstTaskListSummaryModel)
            result.put(Tools.COUNT, count.toInteger())
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
     * build success result and wrap list of RmsTransactionDay
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<RmsTaskListSummaryModel> lstTaskListSummaryModel = (List<RmsTaskListSummaryModel>) executeResult.get(LST_TASK_LIST)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedTaskList = wrapTaskListSummaryModel(lstTaskListSummaryModel, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedTaskList]
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
    private List wrapTaskListSummaryModel(List<RmsTaskListSummaryModel> lstTaskListSummaryModel, int start) {
        List lstWrappedTransactionDay = []
        int counter = start + 1
        for (int i = 0; i < lstTaskListSummaryModel.size(); i++) {
            RmsTaskListSummaryModel taskListModel = lstTaskListSummaryModel[i]
            GridEntity obj = new GridEntity()
            obj.id = taskListModel.id
            obj.cell = [
                    counter,
                    taskListModel.taskListName,
                    taskListModel.exchangeHouseName,
                    taskListModel.totalCount,
                    taskListModel.includedInListCount,
                    taskListModel.decisionTakenCount,
                    taskListModel.decisionApprovedCount,
                    DateUtility.getDateForSMS(taskListModel.createdOn)
            ]
            lstWrappedTransactionDay << obj
            counter++
        }
        return lstWrappedTransactionDay
    }
}
