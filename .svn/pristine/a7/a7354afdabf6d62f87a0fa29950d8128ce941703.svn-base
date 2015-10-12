package com.athena.mis.arms.actions.instrument

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.entity.RmsTaskList
import com.athena.mis.arms.service.RmsTaskListService
import com.athena.mis.arms.utility.*
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

import java.sql.Timestamp

/**
 *  download task report for instrument purchase
 *  For details go through Use-Case doc named 'DownloadTaskReportForPurchaseInstrumentActionService'
 */
class DownloadTaskReportForPurchaseInstrumentActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    RmsTaskListService rmsTaskListService
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String INVALID_INPUT = "Task not found"
    private static final String FAILURE_MSG = "Failed to generate task report"
    private static final String EXCHANGE_HOUSE_ID = "exchangeHouseId"
    private static final String EXCHANGE_HOUSE_NAME = "exchangeHouseName"
    private static final String TASK_LIST_ID = "taskListId"
    private static final String TASK_LIST_NAME = "taskListName"
    private static final String CURRENT_STATUS = "currentStatus"
    private static final String PROCESS_TYPE_ID = "processTypeId"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String COMPANY_CODE = "companyCode"
    private static final String BANK_ID = "bankId"
    private static final String TASK_STATUS = "taskStatus"

    // report related variables
    private static final String REPORT_FOLDER = 'instrument'
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = 'purchaseInstrument'
    private static final String CSV_EXTENSION = ".csv"

    private static final String REPORT_FILE_FORMAT = 'csv'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'purchaseInstrumentCsv.jasper'


    /**
     * Check existence of required parameters send from UI
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -map contains isError(true/false) depending on method success
     */
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
            long bankId = Long.parseLong(params.bankId)
            RmsTaskList rmsTaskList= (RmsTaskList) rmsTaskListService.read(taskListId)
            String taskListName= rmsTaskList.name
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            long currentStatus = Long.parseLong(params.currentStatus)
            long processTypeId = Long.parseLong(params.process)
            Date startDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Timestamp fromDate = DateUtility.getSqlFromDateWithSeconds(startDate)
            Date endDate = DateUtility.parseMaskedToDate(params.toDate)
            Timestamp toDate = DateUtility.getSqlToDateWithSeconds(endDate)
            Company company=(Company)companyCacheUtility.read(companyId)
            String companyCode= company.code
            String taskStatus=null
            SystemEntity decisionApproved=(SystemEntity)rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DECISION_APPROVED,companyId)
            SystemEntity disbursed=(SystemEntity)rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DISBURSED,companyId)
            if(currentStatus==decisionApproved.id)
                taskStatus=decisionApproved.value
            if(currentStatus==disbursed.id)
                taskStatus=disbursed.value
            result.put(BANK_ID, bankId)
            result.put(COMPANY_CODE, companyCode)
            result.put(EXCHANGE_HOUSE_ID, exhHouseId)
            result.put(EXCHANGE_HOUSE_NAME, exchangeHouseName)
            result.put(TASK_LIST_ID, taskListId)
            result.put(TASK_LIST_NAME, taskListName)
            result.put(CURRENT_STATUS, currentStatus)
            result.put(PROCESS_TYPE_ID, processTypeId)
            result.put(FROM_DATE, fromDate)
            result.put(TO_DATE, toDate)
            result.put(TASK_STATUS, taskStatus)
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
            String companyCode = (String) preResult.get(COMPANY_CODE)
            long bankId = (long) preResult.get(BANK_ID)
            long exhHouseId = (long) preResult.get(EXCHANGE_HOUSE_ID)
            String exchangeHouseName = (String) preResult.get(EXCHANGE_HOUSE_NAME)
            long taskListId = (long) preResult.get(TASK_LIST_ID)
            String taskListName = (String) preResult.get(TASK_LIST_NAME)
            long currentStatus = (long) preResult.get(CURRENT_STATUS)
            long processTypeId = (long) preResult.get(PROCESS_TYPE_ID)
            Timestamp fromDate = (Timestamp) preResult.get(FROM_DATE)
            Timestamp toDate = (Timestamp) preResult.get(TO_DATE)
            String taskStatus = (String) preResult.get(TASK_STATUS)
            Map report = getTaskReport(bankId, exhHouseId, exchangeHouseName,taskListName, taskListId, currentStatus, processTypeId, fromDate, toDate,companyCode,taskStatus)
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
     * @param currentStatus system entity id
     * @param fromDate -start date
     * @param toDate -end date
     * @return -a map contains parameters for jasper report
     */
    private Map getTaskReport(long bankId, long exhHouseId, String exchangeHouseName,String taskListName, long taskListId, long currentStatus, long processTypeId, Timestamp fromDate, Timestamp toDate, String companyCode,String taskStatus) {
        Map reportParams = new LinkedHashMap()

        String reportDir = Tools.getArmsReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())

        reportParams.put(COMPANY_CODE, companyCode)
        reportParams.put(BANK_ID, bankId)
        reportParams.put(EXCHANGE_HOUSE_ID, exhHouseId)
        reportParams.put(EXCHANGE_HOUSE_NAME, exchangeHouseName)
        reportParams.put(TASK_LIST_ID, taskListId)
        reportParams.put(TASK_LIST_NAME, taskListName)
        reportParams.put(CURRENT_STATUS, currentStatus)
        reportParams.put(PROCESS_TYPE_ID, processTypeId)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(TASK_STATUS, taskStatus)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
