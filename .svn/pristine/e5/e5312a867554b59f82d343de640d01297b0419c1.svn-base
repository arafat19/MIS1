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
 * Download source ledger report group by source in pdf format
 * For details go through Use-Case doc named 'DownloadForSourceLedgerGroupBySourceActionService'
 */

class DownloadForSourceLedgerGroupBySourceActionService extends BaseService implements ActionIntf {

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
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accLedger'
    private static final String OUTPUT_FILE_NAME = 'accSourceLedger'
    private static final String REPORT_TITLE = 'accSourceLedger'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String ACC_SOURCE_LEDGER = 'accSourceLedgerGroupBySource.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String PROJECT_IDS = "projectIds"
    private static final String PREVIOUS_BALANCE = "previousBalance"
    private static final String PREVIOUS_BALANCE_STR = "previousBalanceStr"
    private static final String IS_POSITIVE = "isPositive"
    private static final String ACC_SOURCE_TYPE_ID = "accSourceTypeId"
    private static final String SOURCE_TYPE_NAME = "sourceTypeName"
    private static final String LST_SOURCE_IDS = "lstSourceIds"
    private static final String SOURCE_NAME = "sourceName"
    private static final String SOURCE_CATEGORY_NAME = "sourceCategoryName"
    private static final String ALL_PROJECT = "All Projects"
    private static final String PROJECT_NAME = "projectName"
    private static final String PROJECT_NOT_FOUND_MESSAGE = "User is not mapped with any project"
    private static final String COMPANY_ID = "companyId"
    private static final String POSTED_BY_PARAM = "postedByParam"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String SOURCE_TYPE_ID_CUSTOMER = 'sourceTypeIdCustomer'
    private static final String SOURCE_TYPE_ID_EMPLOYEE = 'sourceTypeIdEmployee'
    private static final String SOURCE_TYPE_ID_SUB_ACCOUNT = 'sourceTypeIdSubAccount'
    private static final String SOURCE_TYPE_ID_SUPPLIER = 'sourceTypeIdSupplier'
    private static final String SOURCE_TYPE_ID_ITEM = "sourceTypeIdItem"
    private static final String SOURCE_TYPE_ID_LC = "sourceTypeIdLC"
    private static final String SOURCE_TYPE_ID_IPC = "sourceTypeIdIPC"
    private static final String SOURCE_TYPE_ID_LEASE_ACCOUNT = "sourceTypeIdLeaseAccount"
    private static final String SOURCE_TYPE_ID_NONE = "sourceTypeIdNone"
    private static final String DATE_RANGE = "dateRange"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check input fields from UI
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            // check required fields
            if ((!params.accSourceTypeId) || (!params.fromDate) || (!params.toDate) || (!params.sourceId) || (!params.sourceCategoryId)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
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
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing report
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
            String sourceName = params.sourceName.toString()

            List<Long> projectIds = []
            String projectName
            if (params.projectId.equals(Tools.EMPTY_SPACE)) {
                projectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
                if (projectIds.size() <= 0) {
                    result.put(Tools.MESSAGE, PROJECT_NOT_FOUND_MESSAGE)
                    return result
                }
                projectName = ALL_PROJECT
            } else {
                long projectId = Long.parseLong(params.projectId.toString())
                projectIds << new Long(projectId)
                Project project = (Project) projectCacheUtility.read(projectId)
                projectName = project.name
            }

            //if postedByParam  = 0 the show Only Posted Voucher
            //if postedByParam  = -1 the show both Posted & Un-posted Voucher
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
            // get source ledger PDF report group by source
            Map report = getSourceLedgerReportMap(accSourceTypeId, lstSourceIds, fromDate, toDate, projectIds, sourceName, projectName, postedByParam, sourceCategoryName)

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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success operation
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
     * @param accSourceTypeId -acc Source Type id from params
     * @param lstSourceIds -list of source id(s) from params
     * @param fromDate -start date from UI
     * @param toDate -current date(today)
     * @param projectIds -all project ids
     * @param sourceName -source name from params
     * @param projectName -project name from execute method for the report
     * @param postedByParam -determines whether fetch posted voucher or both posted & un-posted voucher
     * @param sourceCategoryName -sourceCategoryName comes from getSourceCategoryName() method by sourceCategoryId
     * @return -generated report with required params
     */
    private Map getSourceLedgerReportMap(long accSourceTypeId, List lstSourceIds, Date fromDate, Date toDate, List projectIds, String sourceName, String projectName, long postedByParam, String sourceCategoryName) {
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeCustomer = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_CUSTOMER, companyId)
        SystemEntity accSourceTypeEmployee = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE, companyId)
        SystemEntity accSourceTypeSubAccount = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT, companyId)
        SystemEntity accSourceTypeSupplier = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUPPLIER, companyId)
        SystemEntity accSourceTypeItem = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_ITEM, companyId)
        SystemEntity accSourceTypeLc = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_LC, companyId)
        SystemEntity accSourceTypeIpc = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_IPC, companyId)
        SystemEntity accSourceTypeLeaseAccount = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT, companyId)
        SystemEntity accSourceTypeNone = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.ACC_SOURCE_NAME_NONE, companyId)

        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(accSourceTypeId)
        List prevLedgerBalanceList = AccVoucherModel.previousBalanceBySourceTypeAndSource(accSourceTypeId, lstSourceIds, fromDate, projectIds, postedByParam, companyId).list()
        double prevLedgerBalance = prevLedgerBalanceList[0] ? prevLedgerBalanceList[0] : 0d
        String previousLedgerBalanceStr = Tools.makeAmountWithThousandSeparator(prevLedgerBalance)
        String strFromDate = DateUtility.getDateFormatAsString(fromDate)
        String strToDate = DateUtility.getDateFormatAsString(toDate)
        String dateRange = strFromDate + Tools.TO + strToDate

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(ACC_SOURCE_TYPE_ID, accSourceTypeId)
        reportParams.put(LST_SOURCE_IDS, lstSourceIds)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(PROJECT_IDS, projectIds)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(SOURCE_TYPE_NAME, accSourceType.key)
        reportParams.put(SOURCE_NAME, sourceName)
        reportParams.put(SOURCE_CATEGORY_NAME, sourceCategoryName)
        reportParams.put(PREVIOUS_BALANCE_STR, previousLedgerBalanceStr)
        reportParams.put(PREVIOUS_BALANCE, prevLedgerBalance)
        reportParams.put(IS_POSITIVE, (prevLedgerBalance >= 0) ? Tools.TRUE : Tools.FALSE)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(COMPANY_ID, companyId)
        reportParams.put(POSTED_BY_PARAM, postedByParam)
        reportParams.put(SOURCE_TYPE_ID_CUSTOMER, accSourceTypeCustomer.id)
        reportParams.put(SOURCE_TYPE_ID_EMPLOYEE, accSourceTypeEmployee.id)
        reportParams.put(SOURCE_TYPE_ID_SUB_ACCOUNT, accSourceTypeSubAccount.id)
        reportParams.put(SOURCE_TYPE_ID_SUPPLIER, accSourceTypeSupplier.id)
        reportParams.put(SOURCE_TYPE_ID_ITEM, accSourceTypeItem.id)
        reportParams.put(SOURCE_TYPE_ID_LC, accSourceTypeLc.id)
        reportParams.put(SOURCE_TYPE_ID_IPC, accSourceTypeIpc.id)
        reportParams.put(SOURCE_TYPE_ID_LEASE_ACCOUNT, accSourceTypeLeaseAccount.id)
        reportParams.put(SOURCE_TYPE_ID_NONE, accSourceTypeNone.id)
        reportParams.put(DATE_RANGE, dateRange)

        JasperReportDef reportDef = new JasperReportDef(name: ACC_SOURCE_LEDGER, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        //Generate a report based on jasper file
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

    /**
     * Give Source List
     * @param sourceCategoryId -sourceCategoryId from params
     * @param accSourceTypeId -accSourceTypeId from params
     * @return -source list
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
            return [new Long(0)]    // in case unknown type OR cacheUtil returns empty List, fill list with Zero to avoid exception in query
        }

        sourceList = Tools.getIds(sourceList)
        return sourceList
    }

    /**
     * Give source category name
     * @param sourceCategoryId -sourceCategoryId from params
     * @param accSourceTypeId -accSourceTypeId from params
     * @return -source Category Name
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
