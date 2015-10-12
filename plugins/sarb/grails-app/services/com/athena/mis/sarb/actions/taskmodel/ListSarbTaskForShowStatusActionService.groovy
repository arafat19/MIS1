package com.athena.mis.sarb.actions.taskmodel

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.sarb.service.SarbTaskModelService
import com.athena.mis.sarb.utility.SarbSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ListSarbTaskForShowStatusActionService extends BaseService implements ActionIntf {

    SarbTaskModelService sarbTaskModelService
    @Autowired
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired
    SarbSessionUtil sarbSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Task status could not found"
    private static final String LST_TASK_STATUS = "lstTaskStatus"
    private static final String GRID_OBJ = "gridObj"
    private static final String LABEL_NO_RED = "<span style='color:red;'>NO</span>"
    private static final String CANCEL = "(Cancel)"

    /**
     * Do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Do nothing for post condition
     */

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get lstTaskStatus list for grid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.rp) {
                parameterMap.rp = 10
            }
            initPager(parameterMap)
            long companyId = sarbSessionUtil.appSessionUtil.getCompanyId()
            Date startDate = DateUtility.parseMaskedFromDate(parameterMap.createdDateFrom)
            Date endDate = DateUtility.parseMaskedToDate(parameterMap.createdDateTo)
            String taskRefNo = parameterMap.taskRefNo?parameterMap.taskRefNo:Tools.EMPTY_SPACE
            List<GroovyRowResult> lstTaskStatus = sarbTaskModelService.listSarbTaskForShowDetails(startDate, endDate,taskRefNo, this, companyId)
            int count = sarbTaskModelService.countSarbTaskForShowDetails(startDate, endDate,taskRefNo, companyId)
            result.put(Tools.COUNT, count)
            result.put(LST_TASK_STATUS, lstTaskStatus)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap lstTaskModelStatus list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {

        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<GroovyRowResult> lstTaskModelStatus = (List<GroovyRowResult>) executeResult.get(LST_TASK_STATUS)
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
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
     * Wrap list of lstTaskModel in grid entity
     * @param lstTaskModel -list of sarbTaskModel object(s)
     * @param start -starting index of the page
     * @return -list of wrapped obj
     */
    private List wrapTaskModel(List<GroovyRowResult> lstTaskModel, int start) {
        List lstWrappedTaskStatus = []
        int counter = start + 1
        for (int i = 0; i < lstTaskModel.size(); i++) {
            GroovyRowResult eachRow = lstTaskModel[i]
            GridEntity obj = new GridEntity()
            obj.id = eachRow.id
            String isAcceptedBySarb = eachRow.is_accepted_by_sarb ? Tools.YES : LABEL_NO_RED
            if(eachRow.is_cancelled) {
                isAcceptedBySarb+= Tools.SINGLE_SPACE + CANCEL
            }
            SystemEntity paymentMethodObj = exchangeHouseImplService.readExhPaymentMethod(eachRow.payment_method)
            obj.cell = [
                    counter,
                    eachRow.id,
                    eachRow.ref_no,
                    eachRow.customer_name,
                    eachRow.beneficiary_name,
                    paymentMethodObj ? paymentMethodObj.key : Tools.EMPTY_SPACE,
                    eachRow.submitted_file_count,
                    eachRow.amount_in_foreign_currency,
                    eachRow.amount_in_local_currency,
                    isAcceptedBySarb
            ]
            lstWrappedTaskStatus << obj
            counter++
        }
        return lstWrappedTaskStatus
    }
}
