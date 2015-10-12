package com.athena.mis.arms.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.entity.RmsTaskList
import com.athena.mis.arms.service.RmsTaskListService
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

import java.sql.Timestamp

/**
 *  download list wise status report
 *  For details go through Use-Case doc named 'DownloadListWiseStatusReportActionService'
 */
class DownloadListWiseStatusReportActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    RmsTaskListService rmsTaskListService
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String INVALID_INPUT = "Required parameters not found"
    private static final String FAILURE_MSG = "Failed to generate task report"
    private static final String EXCHANGE_HOUSE_ID = "exchangeHouseId"
    private static final String EXCHANGE_HOUSE_NAME = "exchangeHouseName"
    private static final String TASK_LIST_ID = "taskListId"
    private static final String TASK_LIST_NAME = "taskListName"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"

    // report related variables
    private static final String REPORT_FOLDER = 'listWiseStatus'
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = 'listWiseStatus'
    private static final String PDF_EXTENSION = ".pdf"

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'listWiseStatusPdf.jasper'


    /**
     * Check existence of required parameters send from UI
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if ((!params.taskListId) || (!params.exhHouseId) || (!params.toDate) || (!params.fromDate)) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            long exhHouseId = Long.parseLong(params.exhHouseId)
            RmsExchangeHouse exchangeHouse = (RmsExchangeHouse) rmsExchangeHouseCacheUtility.read(exhHouseId)
            String exchangeHouseName = exchangeHouse.name
            long taskListId = Long.parseLong(params.taskListId)
            RmsTaskList taskList = rmsTaskListService.read(taskListId)
            String taskListName = taskList.name
            Date startDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Timestamp fromDate = DateUtility.getSqlFromDateWithSeconds(startDate)
            Date endDate = DateUtility.parseMaskedToDate(params.toDate)
            Timestamp toDate = DateUtility.getSqlToDateWithSeconds(endDate)

            result.put(EXCHANGE_HOUSE_ID, exhHouseId)
            result.put(EXCHANGE_HOUSE_NAME, exchangeHouseName)
            result.put(TASK_LIST_ID, taskListId)
            result.put(TASK_LIST_NAME, taskListName)
            result.put(FROM_DATE, fromDate)
            result.put(TO_DATE, toDate)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Method to get map that contains required parameters to generate report in CSV format
     * @param parameters -N/A
     * @param obj -parameters send from executePreCondition method
     * @return -map contains parameters to generate report & isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            long exhHouseId = (long) preResult.get(EXCHANGE_HOUSE_ID)
            String exchangeHouseName = (String) preResult.get(EXCHANGE_HOUSE_NAME)
            long taskListId = (long) preResult.get(TASK_LIST_ID)
            String taskListName = (String) preResult.get(TASK_LIST_NAME)
            Timestamp fromDate = (Timestamp) preResult.get(FROM_DATE)
            Timestamp toDate = (Timestamp) preResult.get(TO_DATE)
            Map report = getTaskReport(exhHouseId, exchangeHouseName, taskListId, taskListName, fromDate, toDate)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(REPORT, report)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success result
     */
    Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous method, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!executeResult.message) {
                result.put(Tools.MESSAGE, FAILURE_MSG)
                return result
            }
            result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Method to get required parameters for jasper report
     * @param exhHouseId -exchange house id
     * @param exchangeHouseName -exchange house name
     * @param taskListId -task list id
     * @param fromDate -start date
     * @param toDate -end date
     * @return -a map contains parameters for jasper report
     */
    private Map getTaskReport(long exhHouseId, String exchangeHouseName, long taskListId, String taskListName, Timestamp fromDate, Timestamp toDate) {
        Map reportParams = new LinkedHashMap()

        String reportDir = Tools.getArmsReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())

        reportParams.put(EXCHANGE_HOUSE_ID, exhHouseId)
        reportParams.put(EXCHANGE_HOUSE_NAME, exchangeHouseName)
        reportParams.put(TASK_LIST_ID, taskListId)
        reportParams.put(TASK_LIST_NAME, taskListName)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
