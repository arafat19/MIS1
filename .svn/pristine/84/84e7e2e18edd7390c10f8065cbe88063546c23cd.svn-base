package com.athena.mis.accounting.actions.report.sourcewisebalance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.SupplierTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

//import SysConfiguration
/*
 * download source wise balance in pdf format
 * For details go through Use-Case doc named 'DownloadForSourceWiseBalanceActionService'
 */

class DownloadForSourceWiseBalanceActionService extends BaseService implements ActionIntf {

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
    ProjectCacheUtility projectCacheUtility
    @Autowired
    SupplierTypeCacheUtility supplierTypeCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    DesignationCacheUtility designationCacheUtility

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Failed to generate source wise balance report"
    private static final String SUBREPORT_DIR = 'SUBREPORT_DIR'
    private static final String REPORT_FOLDER = 'sourceWiseBalance'
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = 'Source-Wise-Balance'
    private static final String SOURCE_TYPE_ID = "sourceTypeId"
    private static final String COA_ID = "coaId"
    private static final String COA_CODE = "coaCode"
    private static final String SOURCE_TYPE_NAME = "sourceTypeName"
    private static final String TO_DATE = "toDate"
    private static final String FROM_DATE = "fromDate"
    private static final String DATE_RANGE = "dateRange"
    private static final String SOURCE_TYPE_ID_CUSTOMER = 'sourceTypeIdCustomer'
    private static final String SOURCE_TYPE_ID_EMPLOYEE = 'sourceTypeIdEmployee'
    private static final String SOURCE_TYPE_ID_SUB_ACCOUNT = 'sourceTypeIdSubAccount'
    private static final String SOURCE_TYPE_ID_SUPPLIER = 'sourceTypeIdSupplier'
    private static final String SOURCE_TYPE_ID_ITEM = "sourceTypeIdItem"
    private static final String SOURCE_TYPE_ID_LC = "sourceTypeIdLC"
    private static final String SOURCE_TYPE_ID_IPC = "sourceTypeIdIPC"
    private static final String SOURCE_TYPE_ID_LEASE_ACCOUNT = "sourceTypeIdLeaseAccount"
    private static final String REPORT_FILE_FORMAT_PDF = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT_TITLE = 'Source Wise Balance'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE_WITH_COA = 'sourceWiseBalanceByCoaId.jasper'
    private static final String JASPER_FILE_WITHOUT_COA = 'sourceWiseBalance.jasper'
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String COMPANY_ID = "companyId"
    private static final String POSTED_BY_PARAM = "postedByParam"
    private static final String PROJECT_IDS = "projectIds"
    private static final String SOURCE_CATEGORY_IDS = "sourceCategoryIds"
    private static final String PROJECT_NAME = "projectName"

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
            if ((!params.sourceTypeId) || (!params.toDate) || (!params.fromDate)) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            long sourceTypeId = Long.parseLong(params.sourceTypeId.toString())
            long coaId = params.coaId.equals(Tools.EMPTY_SPACE) ? 0L : Long.parseLong(params.coaId.toString())
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
            result.put(SOURCE_TYPE_ID, sourceTypeId)
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
     * @param obj -object received from pre condition
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            long sourceTypeId = Long.parseLong(preResult.get(SOURCE_TYPE_ID).toString())
            long coaId = Long.parseLong(preResult.get(COA_ID).toString())
            Date toDate = (Date) preResult.get(TO_DATE)
            Date fromDate = (Date) preResult.get(FROM_DATE)

            List<Long> projectIds = [] //main list of projectIds
            long projectId = parameterMap.projectId.equals(Tools.EMPTY_SPACE) ? 0L : Long.parseLong(parameterMap.projectId.toString())
            String projectName
            if (projectId <= 0) {
                projectName = Tools.ALL
                List<Long> tempProjectIdList = accSessionUtil.appSessionUtil.getUserProjectIds()
                if (tempProjectIdList.size() <= 0) {
                    //if tempList is null then set 0 at main list, So that cache don't update
                    projectIds << new Long(0)
                } else {  //if tempList is not null then set tempProjectIdList at main list
                    projectIds = tempProjectIdList
                }
            } else {
                projectIds << new Long(projectId)
                Project project = (Project) projectCacheUtility.read(projectId)
                projectName = project.name
            }

            List<Long> lstSourceCategoryIds = []
            if (parameterMap.sourceCategoryId.equals(Tools.EMPTY_SPACE)) {
                lstSourceCategoryIds = listSourceCategoryIds(sourceTypeId)
            } else {
                long sourceCategoryId = Long.parseLong(parameterMap.sourceCategoryId.toString())
                lstSourceCategoryIds << sourceCategoryId
            }

            /*if postedByParam  = 0 the show Only Posted Voucher
              if postedByParam  = -1 the show both Posted & Unposted Voucher*/
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            Map report = getSourceWiseBalanceReport(sourceTypeId, coaId, toDate, fromDate, postedByParam, projectIds, projectName, lstSourceCategoryIds)

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
    private Map getSourceWiseBalanceReport(long sourceTypeId, long coaId, Date toDate, Date fromDate, long postedByParam, List<Long> projectIds, String projectName, List<Long> lstSourceCategoryIds) {
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        String dateRange = DateUtility.getDateFormatAsString(fromDate) + ' To ' + DateUtility.getDateFormatAsString(toDate)
        String subReportDir = reportDir + File.separator

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUBREPORT_DIR, subReportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(SOURCE_TYPE_ID, sourceTypeId)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(DATE_RANGE, dateRange)
        reportParams.put(COA_CODE, Tools.NOT_APPLICABLE)
        reportParams.put(PROJECT_IDS, projectIds)
        reportParams.put(SOURCE_CATEGORY_IDS, lstSourceCategoryIds)
        reportParams.put(PROJECT_NAME, projectName)

        SystemEntity sourceType = (SystemEntity) accSourceCacheUtility.read(sourceTypeId)
        reportParams.put(SOURCE_TYPE_NAME, sourceType.key)

        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeCustomer = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_CUSTOMER, companyId)
        SystemEntity accSourceTypeEmployee = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE, companyId)
        SystemEntity accSourceTypeSubAccount = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT, companyId)
        SystemEntity accSourceTypeSupplier = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUPPLIER, companyId)
        SystemEntity accSourceTypeItem = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_ITEM, companyId)
        SystemEntity accSourceTypeLc = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_LC, companyId)
        SystemEntity accSourceTypeIpc = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_IPC, companyId)
        SystemEntity accSourceTypeLeaseAccount = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT, companyId)

        reportParams.put(SOURCE_TYPE_ID_CUSTOMER, accSourceTypeCustomer.id)
        reportParams.put(SOURCE_TYPE_ID_EMPLOYEE, accSourceTypeEmployee.id)
        reportParams.put(SOURCE_TYPE_ID_SUB_ACCOUNT, accSourceTypeSubAccount.id)
        reportParams.put(SOURCE_TYPE_ID_SUPPLIER, accSourceTypeSupplier.id)
        reportParams.put(SOURCE_TYPE_ID_ITEM, accSourceTypeItem.id)
        reportParams.put(SOURCE_TYPE_ID_LC, accSourceTypeLc.id)
        reportParams.put(SOURCE_TYPE_ID_IPC, accSourceTypeIpc.id)
        reportParams.put(SOURCE_TYPE_ID_LEASE_ACCOUNT, accSourceTypeLeaseAccount.id)

        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(COMPANY_ID, companyId)
        reportParams.put(POSTED_BY_PARAM, postedByParam)

        String jasperName = JASPER_FILE_WITHOUT_COA
        if (coaId > 0) {
            AccChartOfAccount chartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(coaId)
            String coaDescription = chartOfAccount.description + Tools.PARENTHESIS_START + chartOfAccount.code + Tools.PARENTHESIS_END
            // coa description including code
            jasperName = JASPER_FILE_WITH_COA
            reportParams.put(COA_ID, coaId)
            reportParams.put(COA_CODE, coaDescription)
        }

        JasperReportDef reportDef = new JasperReportDef(name: jasperName, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT_PDF]
    }

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
                lstSourceCategory = listSubAccount()
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
    private List listSubAccount() {
        Map queryParams = [
                companyId: accSessionUtil.appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstSubAccount = executeSelectSql(SELECT_SUB_ACCOUNT_QUERY, queryParams)
        return lstSubAccount
    }
}