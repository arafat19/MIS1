package com.athena.mis.accounting.actions.report.accledger

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.model.AccVoucherModel
import com.athena.mis.accounting.utility.*
import com.athena.mis.application.entity.*
import com.athena.mis.application.utility.*
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/*
 * Download source ledger in CSV format
 * For details go through Use-Case doc named 'DownloadForSourceLedgerCsvActionService'
 */

class DownloadForSourceLedgerCsvActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    CustomerCacheUtility customerCacheUtility
    @Autowired
    AccLcCacheUtility accLcCacheUtility
    @Autowired
    AccIpcCacheUtility accIpcCacheUtility
    @Autowired
    AccLeaseAccountCacheUtility accLeaseAccountCacheUtility
    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    SupplierTypeCacheUtility supplierTypeCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    DesignationCacheUtility designationCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download source ledger report"
    private static final String REPORT_FILE_FORMAT = 'csv'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accLedger'
    private static final String OUTPUT_FILE_NAME = 'accSourceLedger'
    private static final String REPORT_TITLE = 'accSourceLedger'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String CSV_EXTENSION = ".csv"
    private static final String REPORT = "report"
    private static final String ACC_SOURCE_LEDGER_CSV = 'accSourceLedgerCsv.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String PROJECT_IDS = "projectIds"
    private static final String PREVIOUS_BALANCE = "previousBalance"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String IS_POSITIVE = "isPositive"
    private static final String SOURCE_TYPE_NAME = "sourceTypeName"
    private static final String ACC_SOURCE_TYPE_ID = "accSourceTypeId"
    private static final String LST_SOURCE_IDS = "lstSourceIds"
    private static final String SOURCE_NAME = "sourceName"
    private static final String SOURCE_CATEGORY_NAME = "sourceCategoryName"
    private static final String ALL_PROJECT = "All Projects"
    private static final String PROJECT_NAME = "projectName"
    private static final String PROJECT_NOT_FOUND_MESSAGE = "User is not mapped with any project"
    private static final String COMPANY_ID = "companyId"
    private static final String POSTED_BY_PARAM = "postedByParam"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check input fields from UI
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            // check required fields
            if ((!params.accSourceTypeId) || (!params.fromDate) || (!params.toDate) || (!params.sourceId) || (!params.sourceCategoryId)) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            Date startDate = DateUtility.parseMaskedDate(params.fromDate.toString())
            Date endDate = DateUtility.parseMaskedDate(params.toDate.toString())
            AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.getCurrentFinancialYear()
            if (accFinancialYear) {
                String exceedsFinancialYear = DateUtility.checkFinancialDateRange(startDate, endDate, accFinancialYear)
                if (exceedsFinancialYear) {
                    result.put(Tools.MESSAGE, exceedsFinancialYear)
                    return result
                }
            }

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
     * Get desired report providing all required parameters
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())
            long accSourceTypeId = Long.parseLong(params.accSourceTypeId.toString())
            long sourceCategoryId = Long.parseLong(params.sourceCategoryId.toString())
            long sourceId = Long.parseLong(params.sourceId.toString())
            long projectId = params.projectId.equals(Tools.EMPTY_SPACE) ? 0L : Long.parseLong(params.projectId.toString())
            String sourceName = params.sourceName.toString()

            List<Long> projectIds = []
            String projectName
            if (projectId <= 0) {
                projectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
                projectName = ALL_PROJECT
            } else {
                projectIds << new Long(projectId)
                Project project = (Project) projectCacheUtility.read(projectId)
                projectName = project.name
            }

            if (projectIds.size() == 0) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND_MESSAGE)
                return result
            }

            //if postedByParam  = 0 the show Only Posted Voucher
            //if postedByParam  = -1 the show both Posted & Unposted Voucher
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            String sourceCategoryName = Tools.ALL
            if (sourceCategoryId > 0) {
                sourceCategoryName = getSourceCategoryName(sourceCategoryId, accSourceTypeId)
            }

            List<Long> lstSourceIds = []
            if (sourceId > 0) {
                lstSourceIds << sourceId
            } else {
                lstSourceIds = getSourceList(sourceCategoryId, accSourceTypeId)
            }

            Map report = getSourceLedgerReportMap(accSourceTypeId, lstSourceIds, fromDate, toDate, projectIds, sourceName, projectName, postedByParam, sourceCategoryName)
            // get source ledger CSV report

            result.put(REPORT, report)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for build success operation
     */
    public Object buildSuccessResultForUI(Object result) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Generate report by given data
     * @param accSourceTypeId - acc Source Type id from params
     * @param lstSourceIds - list of source id(s) from params
     * @param fromDate - start date from UI
     * @param toDate - current date(today)
     * @param projectIds - all project ids
     * @param sourceName - source name from params
     * @param projectName - project name from execute method for the report
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @param sourceCategoryName - sourceCategoryName comes from getSourceCategoryName() method by sourceCategoryId
     * @return - generated report with required params
     */
    private Map getSourceLedgerReportMap(long accSourceTypeId, List lstSourceIds, Date fromDate, Date toDate, List projectIds, String sourceName, String projectName, long postedByParam, String sourceCategoryName) {

        Map reportParams = new LinkedHashMap()

        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)

        reportParams.put(ACC_SOURCE_TYPE_ID, accSourceTypeId)
        reportParams.put(LST_SOURCE_IDS, lstSourceIds)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(PROJECT_IDS, projectIds)
        reportParams.put(PROJECT_NAME, projectName)

        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(accSourceTypeId)
        reportParams.put(SOURCE_TYPE_NAME, accSourceType.key)
        reportParams.put(SOURCE_NAME, sourceName)
        reportParams.put(SOURCE_CATEGORY_NAME, sourceCategoryName)

        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        List prevLedgerBalanceList = AccVoucherModel.previousBalanceBySourceTypeAndSource(accSourceTypeId, lstSourceIds, fromDate, projectIds, postedByParam, companyId).list()
        double prevLedgerBalance = prevLedgerBalanceList[0] ? prevLedgerBalanceList[0] : 0d
        reportParams.put(PREVIOUS_BALANCE, prevLedgerBalance)
        reportParams.put(IS_POSITIVE, (prevLedgerBalance >= 0) ? Tools.TRUE : Tools.FALSE)

        reportParams.put(COMPANY_ID, companyId)
        reportParams.put(POSTED_BY_PARAM, postedByParam)

        JasperReportDef reportDef = new JasperReportDef(name: ACC_SOURCE_LEDGER_CSV, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)

        //Generate a report based on jasper file
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

    /**
     * Give Source List
     * @param sourceCategoryId - sourceCategoryId from params
     * @param accSourceTypeId - accSourceTypeId from params
     * @return - source list
     */
    private List getSourceList(long sourceCategoryId, long accSourceTypeId) {
        List sourceList = []
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(accSourceTypeId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                sourceList = (sourceCategoryId > 0) ? supplierCacheUtility.listBySupplierTypeId(sourceCategoryId) : supplierCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                sourceList = (sourceCategoryId > 0) ? itemCacheUtility.listByItemTypeId(sourceCategoryId) : itemCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                sourceList = (sourceCategoryId > 0) ? employeeCacheUtility.listByDesignationForDropDown(sourceCategoryId) : employeeCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_CUSTOMER:
                sourceList = customerCacheUtility.listByCompanyForDropDown()
                break
            case accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT:
                sourceList = (sourceCategoryId > 0) ? accSubAccountCacheUtility.searchByCoaIdAndCompany(sourceCategoryId) : accSubAccountCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_LC:
                sourceList = accLcCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_IPC:
                sourceList = accIpcCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT:
                sourceList = accLeaseAccountCacheUtility.list()
                break
            default:
                break
        }
        if (sourceList.size() == 0) {
            return [new Long(0)]
            // in case unknown type OR cacheUtil returns empty List, fill list with Zero to avoid exception in query
        }
        sourceList = Tools.getIds(sourceList)
        return sourceList
    }

    /**
     * Give source category name
     * @param sourceCategoryId - source Category Id from params
     * @param accSourceTypeId - Source Type Id from params
     * @return - source Category Name
     */
    private String getSourceCategoryName(long sourceCategoryId, long accSourceTypeId) {
        String sourceCategoryName = Tools.ALL
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(accSourceTypeId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                SystemEntity sourceCategorySupplier = (SystemEntity) supplierTypeCacheUtility.read(sourceCategoryId)
                sourceCategoryName = sourceCategorySupplier.key
                break
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                ItemType itemType = (ItemType) itemTypeCacheUtility.read(sourceCategoryId)
                sourceCategoryName = itemType.name
                break
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                Designation designation = (Designation) designationCacheUtility.read(sourceCategoryId)
                sourceCategoryName = designation.name
                break
            default:
                break
        }
        return sourceCategoryName
    }
}
