package com.athena.mis.arms.actions.instrument

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.District
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.entity.RmsTaskList
import com.athena.mis.arms.service.RmsTaskListService
import com.athena.mis.arms.utility.*
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
 *  download task report for forward cash collection
 *  For details go through Use-Case doc named 'DownloadTaskReportForForwardCashCollectionActionService'
 */
class DownloadTaskReportForForwardCashCollectionActionService extends BaseService implements ActionIntf {

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
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String INVALID_INPUT = "Task not found"
    private static final String FAILURE_MSG = "Failed to generate task report"
    private static final String EXCHANGE_HOUSE_ID = "exchangeHouseId"
    private static final String EXCHANGE_HOUSE_NAME = "exchangeHouseName"
    private static final String TASK_LIST_ID = "taskListId"
    private static final String TASK_LIST_NAME = "taskListName"
    private static final String TASK_STATUS_NAME = "taskStatusName"
    private static final String CURRENT_STATUS_ID = "currentStatusId"
    private static final String PROCESS_TYPE_ID = "processTypeId"
    private static final String INSTRUMENT_TYPE_ID = "instrumentTypeId"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String COMPANY_CODE = "companyCode"
    private static final String BRANCH_NAME = "branchName"
    private static final String DISTRICT_NAME = "districtName"
    private static final String BANK_ID = "bankId"
    private static final String BRANCH_ID = "branchId"

    // report related variables
    private static final String REPORT_FOLDER = 'instrument'
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = 'forwardCashCollection'
    private static final String CSV_EXTENSION = ".csv"

    private static final String REPORT_FILE_FORMAT = 'csv'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'forwardCashCollectionCsv.jasper'

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
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            Company company = (Company) companyCacheUtility.read(companyId)
            long forwardReservedId = rmsProcessTypeCacheUtility.FORWARD
            SystemEntity process = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(forwardReservedId, companyId)
            long cashCollectionReservedId = rmsInstrumentTypeCacheUtility.CASH_COLLECTION
            SystemEntity instrument = (SystemEntity) rmsInstrumentTypeCacheUtility.readByReservedAndCompany(cashCollectionReservedId, companyId)
            Date startDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Timestamp fromDate = DateUtility.getSqlFromDateWithSeconds(startDate)
            Date endDate = DateUtility.parseMaskedToDate(params.toDate)
            Timestamp toDate = DateUtility.getSqlToDateWithSeconds(endDate)
            RmsTaskList rmsTaskList = rmsTaskListService.read(taskListId)
            String branchName = Tools.EMPTY_SPACE
            String districtName = Tools.EMPTY_SPACE
            long userBranchId = rmsSessionUtil.appSessionUtil.getUserBankBranchId()
            long bankId = 0L
            BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(userBranchId)
            if (bankBranch) {
                branchName = bankBranch.name
                District district = (District) districtCacheUtility.read(bankBranch.districtId)
                if (district) {
                    districtName = district.name
                }
                bankId = bankBranch.bankId
            }
            result.put(EXCHANGE_HOUSE_ID, exhHouseId)
            result.put(EXCHANGE_HOUSE_NAME, exchangeHouseName)
            result.put(TASK_LIST_ID, taskListId)
            result.put(TASK_LIST_NAME, rmsTaskList.name)
            result.put(PROCESS_TYPE_ID, process.id)
            result.put(INSTRUMENT_TYPE_ID, instrument.id)
            result.put(FROM_DATE, fromDate)
            result.put(TO_DATE, toDate)
            result.put(COMPANY_CODE, company)
            result.put(BRANCH_NAME, branchName)
            result.put(DISTRICT_NAME, districtName)
            result.put(BANK_ID, bankId)
            result.put(BRANCH_ID, userBranchId)
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
            long processTypeId = (long) preResult.get(PROCESS_TYPE_ID)
            long instrumentTypeId = (long) preResult.get(INSTRUMENT_TYPE_ID)
            Timestamp fromDate = (Timestamp) preResult.get(FROM_DATE)
            Timestamp toDate = (Timestamp) preResult.get(TO_DATE)
            Company company = (Company) preResult.get(COMPANY_CODE)
            String branchName = (String) preResult.get(BRANCH_NAME)
            String districtName = (String) preResult.get(DISTRICT_NAME)
            long bankId = (long) preResult.get(BANK_ID)
            long branchId = (long) preResult.get(BRANCH_ID)
            Map report = getTaskReport(exhHouseId, exchangeHouseName, taskListId, taskListName, processTypeId, instrumentTypeId, fromDate, toDate, company.code, branchName, districtName, bankId, branchId)
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
    private Map getTaskReport(long exhHouseId, String exchangeHouseName, long taskListId, String taskListName, long processTypeId,
                              long instrumentTypeId, Timestamp fromDate, Timestamp toDate, String companyCode,
                              String branchName, String districtName, long bankId, long branchId) {
        Map reportParams = new LinkedHashMap()

        String reportDir = Tools.getArmsReportDirectory() + File.separator + REPORT_FOLDER
        SystemEntity currentStatus = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DISBURSED, rmsSessionUtil.appSessionUtil.getCompanyId())
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())

        reportParams.put(EXCHANGE_HOUSE_ID, exhHouseId)
        reportParams.put(EXCHANGE_HOUSE_NAME, exchangeHouseName)
        reportParams.put(TASK_LIST_ID, taskListId)
        reportParams.put(TASK_LIST_NAME, taskListName)
        reportParams.put(TASK_STATUS_NAME, currentStatus.key)
        reportParams.put(CURRENT_STATUS_ID, currentStatus.id)
        reportParams.put(PROCESS_TYPE_ID, processTypeId)
        reportParams.put(INSTRUMENT_TYPE_ID, instrumentTypeId)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(COMPANY_CODE, companyCode)
        reportParams.put(BRANCH_NAME, branchName)
        reportParams.put(DISTRICT_NAME, districtName)
        reportParams.put(BANK_ID, bankId)
        reportParams.put(BRANCH_ID, branchId)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
