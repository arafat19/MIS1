package com.athena.mis.accounting.actions.report.sourcewisebalance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.*
import com.athena.mis.accounting.utility.*
import com.athena.mis.application.entity.*
import com.athena.mis.application.utility.*
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Download voucher list against source id
 * after selecting source details button in UI
 * For details go through Use-Case doc named 'DownloadForVoucherListBySourceIdActionService'
 */
class DownloadForVoucherListBySourceIdActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    CustomerCacheUtility customerCacheUtility
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    AccLcCacheUtility accLcCacheUtility
    @Autowired
    AccIpcCacheUtility accIpcCacheUtility
    @Autowired
    AccLeaseAccountCacheUtility accLeaseAccountCacheUtility
    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    SupplierTypeCacheUtility supplierTypeCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    DesignationCacheUtility designationCacheUtility

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Failed to generate voucher list report"
    private static final String REPORT_FOLDER = 'sourceWiseBalance'
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = 'sourceWiseBalanceDetails'
    private static final String SOURCE_TYPE_ID = "sourceTypeId"
    private static final String SOURCE_ID = "sourceId"
    private static final String COA_ID = "coaId"
    private static final String COA_CODE = "coaCode"
    private static final String SOURCE_TYPE_NAME = "sourceTypeName"
    private static final String SOURCE_NAME = "sourceName"
    private static final String TO_DATE = "toDate"
    private static final String FROM_DATE = "fromDate"
    private static final String DATE_RANGE = "dateRange"
    private static final String REPORT_FILE_FORMAT_PDF = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT_TITLE = 'Source Wise Balance Details List'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE_PDF_WITHOUT_COA = 'accVoucherListBySource.jasper'
    private static final String JASPER_FILE_PDF_WITH_COA = 'accVoucherListBySourceByCoa.jasper'
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String COMPANY_ID = "companyId"
    private static final String POSTED_BY_PARAM = "postedByParam"
    private static final String PROJECT_IDS = "projectIds"
    private static final String SOURCE_CATEGORY_IDS = "sourceCategoryIds"
    private static final String PROJECT_NAME = "projectName"
    private static final String PREVIOUS_BALANCE_STR = "previousBalanceStr"
    private static final String PREVIOUS_BALANCE = "previousBalance"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check input fields from UI
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if ((!params.sourceTypeId) || (!params.sourceId) || (!params.toDate) || (!params.fromDate)) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            long sourceTypeId = Long.parseLong(params.sourceTypeId.toString())
            long sourceId = Long.parseLong(params.sourceId.toString())
            long coaId = params.coaId.equals(Tools.EMPTY_SPACE) ? 0L : Long.parseLong(params.coaId.toString())
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())
            Date fromDate = DateUtility.parseMaskedToDate(params.fromDate.toString())

            result.put(SOURCE_TYPE_ID, sourceTypeId)
            result.put(SOURCE_ID, sourceId)
            result.put(COA_ID, coaId)
            result.put(TO_DATE, toDate)
            result.put(FROM_DATE, fromDate)
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
     * Get desired report providing all required parameters
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            long sourceTypeId = Long.parseLong(preResult.get(SOURCE_TYPE_ID).toString())
            long sourceId = Long.parseLong(preResult.get(SOURCE_ID).toString())
            long coaId = Long.parseLong(preResult.get(COA_ID).toString())
            Date toDate = (Date) preResult.get(TO_DATE)
            Date fromDate = (Date) preResult.get(FROM_DATE)

            List<Long> projectIds = [] //main list of projectIds
            String projectName
            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {
                projectName = Tools.ALL
                List<Long> tempProjectIdList = accSessionUtil.appSessionUtil.getUserProjectIds()
                if (tempProjectIdList.size() <= 0) { //if tempList is null then set 0 at main list, So that cache don't update
                    projectIds << new Long(0)
                } else {  //if tempList is not null then set tempProjectIdList at main list
                    projectIds = tempProjectIdList
                }
            } else {
                long projectId = Long.parseLong(parameterMap.projectId.toString())
                projectIds << new Long(projectId)
                Project project = (Project) projectCacheUtility.read(projectId)
                projectName = project.name
            }

            List<Long> lstSourceCategoryIds = []
            if (parameterMap.sourceCategoryId.equals(Tools.EMPTY_SPACE)) {
                lstSourceCategoryIds = listSourceCategoryIds(sourceTypeId)
            } else {
                long sourceCategoryId = Long.parseLong(parameterMap.sourceCategoryId)
                lstSourceCategoryIds << sourceCategoryId
            }

            /*if postedByParam  = 0 the show Only Posted Voucher
              if postedByParam  = -1 the show both Posted & Unposted Voucher*/
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }
            Map report = getVoucherListReport(sourceTypeId, sourceId, coaId, toDate, fromDate, postedByParam, projectIds, projectName, lstSourceCategoryIds)
            result.put(REPORT, report)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for success operation
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
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Generate report by given data
     * @param sourceTypeId - source type id
     * @param coaId - chart of account id
     * @param toDate - current date(today)
     * @param fromDate - start date
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @param projectIds - all project ids
     * @param projectName - project name
     * @return - generated report with required params
     */
    private Map getVoucherListReport(long sourceTypeId, long sourceId, long coaId, Date toDate, Date fromDate, long postedByParam, List<Long> projectIds, String projectName, List<Long> lstSourceCategoryIds) {
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(SOURCE_TYPE_ID, sourceTypeId)
        reportParams.put(SOURCE_ID, sourceId)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(COA_ID, coaId)
        reportParams.put(PROJECT_IDS, projectIds)
        reportParams.put(SOURCE_CATEGORY_IDS, lstSourceCategoryIds)
        reportParams.put(PROJECT_NAME, projectName)

        SystemEntity sourceType = (SystemEntity) accSourceCacheUtility.read(sourceTypeId)
        reportParams.put(SOURCE_TYPE_NAME, sourceType.key)
        reportParams.put(SOURCE_NAME, getSourceName(sourceTypeId, sourceId))

        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(COMPANY_ID, accSessionUtil.appSessionUtil.getAppUser().companyId)
        reportParams.put(POSTED_BY_PARAM, postedByParam)

        String dateRange = DateUtility.getLongDateForUI(fromDate) + Tools.TO + DateUtility.getLongDateForUI(toDate)
        reportParams.put(DATE_RANGE, dateRange)

        Map previousBalance = getPreviousBalance(sourceTypeId, sourceId, fromDate, postedByParam, projectIds)
        String prevBalanceStr = previousBalance.prevBalanceStr
        double prevBalance = previousBalance.prevBalance
        String coaDescription = Tools.NOT_APPLICABLE
        String jasperName = JASPER_FILE_PDF_WITHOUT_COA
        if (coaId > 0) {
            jasperName = JASPER_FILE_PDF_WITH_COA
            AccChartOfAccount coa = (AccChartOfAccount) accChartOfAccountCacheUtility.read(coaId)
            coaDescription = coa.description + Tools.PARENTHESIS_START + coa.code + Tools.PARENTHESIS_END
        }
        reportParams.put(PREVIOUS_BALANCE_STR, prevBalanceStr)
        reportParams.put(PREVIOUS_BALANCE, prevBalance)
        reportParams.put(COA_CODE, coaDescription)

        JasperReportDef reportDef = new JasperReportDef(name: jasperName, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT_PDF]
    }

    /**
     * Get coa source name by source type & source id
     * @param sourceTypeId - id of sourceType (e.g: Employee, Customer, Item etc.)
     * @param sourceId - id of source (e.g: Employee.id, Customer.id, Item.id)
     * @return - source name
     */
    private String getSourceName(long sourceTypeId, long sourceId) {
        String sourceName = Tools.EMPTY_SPACE
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(sourceTypeId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.ACC_SOURCE_NAME_NONE:
                break
            case accSourceCacheUtility.SOURCE_TYPE_CUSTOMER:
                Customer customer = (Customer) customerCacheUtility.read(sourceId)
                sourceName = customer.fullName
                break
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                Employee employee = (Employee) employeeCacheUtility.read(sourceId)
                sourceName = employee.fullName
                break
            case accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT:
                AccSubAccount subAccount = (AccSubAccount) accSubAccountCacheUtility.read(sourceId)
                sourceName = subAccount.description
                break
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                Supplier supplier = (Supplier) supplierCacheUtility.read(sourceId)
                sourceName = supplier.name
                break
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                Item item = (Item) itemCacheUtility.read(sourceId)
                sourceName = item.name
                break
            case accSourceCacheUtility.SOURCE_TYPE_LC:
                AccLc accLc = (AccLc) accLcCacheUtility.read(sourceId)
                sourceName = accLc.lcNo
                break
            case accSourceCacheUtility.SOURCE_TYPE_IPC:
                AccIpc accIpc = (AccIpc) accIpcCacheUtility.read(sourceId)
                sourceName = accIpc.ipcNo
                break
            case accSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT:
                AccLeaseAccount leaseAccount = (AccLeaseAccount) accLeaseAccountCacheUtility.read(sourceId)
                sourceName = leaseAccount.institution
                break
            default:
                break
        }
        return sourceName
    }

    /**
     * Get previous balance of specific coa, if coa id not found then consider all coa
     * @param sourceTypeId - coa source type
     * @param sourceId - coa source id
     * @param fromDate - start date
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @param projectIds - project ids
     * @return - previous balance of specific or all chart of account
     */
    private Map getPreviousBalance(long sourceTypeId, long sourceId, Date fromDate, long postedByParam, List<Long> projectIds) {

        String projectIdList = Tools.buildCommaSeparatedStringOfIds(projectIds)

        String queryStr = """
            SELECT to_char(SUM(avd.amount_dr-avd.amount_cr),'${Tools.DB_CURRENCY_FORMAT}') AS str_prev_balance,
            SUM(avd.amount_dr-avd.amount_cr) AS prev_balance
            FROM acc_voucher_details avd
            LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
            WHERE avd.source_type_id = :sourceTypeId
            AND avd.source_id = :sourceId
            AND av.voucher_date < :fromDate
            AND av.company_id = :companyId
            AND av.posted_by > :postedByParam
            AND av.project_id IN (${projectIdList})
        """
        Map queryParams = [
                sourceTypeId: sourceTypeId,
                sourceId: sourceId,
                fromDate: DateUtility.getSqlDateWithSeconds(fromDate),
                companyId: accSessionUtil.appSessionUtil.getCompanyId(),
                postedByParam: postedByParam
        ]
        List<GroovyRowResult> previousBalance = executeSelectSql(queryStr, queryParams)

        String prevBalanceStr = previousBalance[0].str_prev_balance ? previousBalance[0].str_prev_balance : '0.00'
        double prevBalance = previousBalance[0].prev_balance ? previousBalance[0].prev_balance : 0.00
        return [prevBalanceStr: prevBalanceStr, prevBalance: prevBalance]
    }

    /**
     * Get list of source category ids
     * @param sourceTypeId -id pf source type
     * @return -a list of source category ids
     */
    private List<Long> listSourceCategoryIds(long sourceTypeId) {
        List lstSourceCategory = []
        List<Long> lstSourceCategoryIds = []
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(sourceTypeId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                lstSourceCategory = supplierTypeCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                lstSourceCategory = itemTypeCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                lstSourceCategory = designationCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT:
                lstSourceCategory = ListSubAccount()
                break
            default:
                break
        }
        if (lstSourceCategory.size() > 0) {
            lstSourceCategoryIds = Tools.getIds(lstSourceCategory)
        }
        lstSourceCategoryIds << 0L
        return lstSourceCategoryIds
    }

    private static final String SELECT_SUB_ACCOUNT_QUERY = """
        SELECT DISTINCT coa_id AS id
        FROM acc_sub_account
        WHERE company_id=:companyId
    """

    /**
     * Give Custom Source Category List For Sub Account
     * @return - list of custom Source Category
     */
    private List ListSubAccount() {
        Map queryParams = [
                companyId: accSessionUtil.appSessionUtil.getAppUser().companyId
        ]
        List<GroovyRowResult> lstSubAccount = executeSelectSql(SELECT_SUB_ACCOUNT_QUERY, queryParams)
        return lstSubAccount
    }
}