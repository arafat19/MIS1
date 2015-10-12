package com.athena.mis.sarb.actions.taskmodel

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.sarb.model.SarbTaskModel
import com.athena.mis.sarb.service.SarbTaskModelService
import com.athena.mis.sarb.utility.SarbSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * List all refund task for show status
 * for details go through use-case named "ListRefundTaskForShowStatusActionService"
 */
class ListRefundTaskForShowStatusActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Task status could not found"
    private static final String LST_TASK_STATUS = "lstTaskStatus"
    private static final String GRID_OBJ = "gridObj"
    private static final String LABEL_NO_RED = "<span style='color:red;'>NO</span>"
    private static final String CANCEL = "(Cancel)"

    ExchangeHousePluginConnector exchangeHouseImplService
    SarbTaskModelService sarbTaskModelService
    @Autowired
    SarbSessionUtil sarbSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * list all refund task
     * @param parameters - params from ui
     * @param obj - n/a
     * @return - map containing list and count
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initPager(params)
            if (!params.taskId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long taskId = Tools.parseLongInput(params.taskId.toString())
            long companyId = sarbSessionUtil.appSessionUtil.getCompanyId()
            List<SarbTaskModel> sarbTaskModelList = sarbTaskModelService.findAllByRefundTaskId(taskId, this, companyId)
            int count = sarbTaskModelService.countByRefundTaskId(taskId, companyId)
            result.put(Tools.COUNT, count)
            result.put(LST_TASK_STATUS, sarbTaskModelList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * build and wrap grid object
     * @param obj - executeResult
     * @return - wrapped grid obj
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<SarbTaskModel> lstTaskModelStatus = (List<SarbTaskModel>) executeResult.get(LST_TASK_STATUS)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedTaskStatus = wrapTaskModel(lstTaskModelStatus, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedTaskStatus]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * build failure message for ui
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * wrap list of task for grid
     * @param lstTaskModel
     * @param start
     * @return - list of wrappedTask
     */
    private List wrapTaskModel(List<SarbTaskModel> lstTaskModel, int start) {
        List lstWrappedTask = []
        int counter = start + 1
        for (int i = 0; i < lstTaskModel.size(); i++) {
            SarbTaskModel eachRow = lstTaskModel[i]
            GridEntity obj = new GridEntity()
            obj.id = eachRow.id
            String isAcceptedBySarb = eachRow.isAcceptedBySarb ? Tools.YES : LABEL_NO_RED
            if (eachRow.isCancelled) {
                isAcceptedBySarb += Tools.SINGLE_SPACE + CANCEL
            }
            SystemEntity paymentMethodObj = exchangeHouseImplService.readExhPaymentMethod(eachRow.paymentMethod)
            obj.cell = [
                    counter,
                    eachRow.id,
                    eachRow.refNo,
                    eachRow.customerName,
                    eachRow.beneficiaryName,
                    paymentMethodObj ? paymentMethodObj.key : Tools.EMPTY_SPACE,
                    eachRow.submittedFileCount,
                    eachRow.amountInForeignCurrency,
                    eachRow.amountInLocalCurrency,
                    isAcceptedBySarb
            ]
            lstWrappedTask << obj
            counter++
        }
        return lstWrappedTask
    }
}
