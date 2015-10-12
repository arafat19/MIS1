package com.athena.mis.accounting.actions.report.supplierwisepayment

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * supplier-wise payment details in pdf format.
 * For details go through Use-Case doc named 'DownloadForSupplierWisePaymentActionService'
 */
class DownloadForSupplierWisePaymentActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to Download Supplier PO"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accSupplierWisePayment'
    private static final String OUTPUT_FILE_NAME = 'SupplierWisePayment'
    private static final String REPORT_TITLE = 'SupplierWisePayment'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String SUPPLIER_NAME = "supplierName"
    private static final String SUPPLIER_ID = "supplierId"
    private static final String SUPPLIER = "supplier"
    private static final String SUPPLIER_PO_REPORT = 'accSupplierWisePayment.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String PAID_TOTAL = "paidTotal"
    private static final String COMPANY_ID = "companyId"
    private static final String LST_PROJECT_ID = "lstProjectId"
    private static final String PROJECT_NAME = "projectName"
    private static final String PROJECT_NAME_ALL = "All"
    private static final String POSTED_BY_PARAM = "postedByParam"
    private static final String SUPPLIER_NOT_FOUND = "Selected supplier not found"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"

    /**
     * Check all pre conditions for input data
     * @param parameters - serialized parameters received from UI.
     * @param obj - N/A
     * @return - a map containing supplier list, project list and isError msg(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.fromDate || !params.toDate || !params.supplierId || !params.projectId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            Date startDate = DateUtility.parseMaskedDate(params.fromDate)
            Date endDate = DateUtility.parseMaskedDate(params.toDate)
            AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.getCurrentFinancialYear()
            if (accFinancialYear) {
                String exceedsFinancialYear = DateUtility.checkFinancialDateRange(startDate, endDate, accFinancialYear)
                if (exceedsFinancialYear) {
                    result.put(Tools.MESSAGE, exceedsFinancialYear)
                    return result
                }
            }

            Long supplierId = Long.parseLong(params.supplierId.toString())
            Supplier supplier = (Supplier) supplierCacheUtility.read(supplierId)
            if (!supplier) {
                result.put(Tools.MESSAGE, SUPPLIER_NOT_FOUND)
                return result
            }
            long projectId = Long.parseLong(params.projectId.toString())
            List<Long> lstProjectIds = []
            if (projectId <= 0) {
                lstProjectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
                result.put(PROJECT_NAME, PROJECT_NAME_ALL)
            } else {
                lstProjectIds << new Long(projectId)
                Project project = (Project) projectCacheUtility.read(projectId)
                result.put(PROJECT_NAME, project.name)
            }
            result.put(SUPPLIER, supplier)
            result.put(LST_PROJECT_ID, lstProjectIds)
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
     * @param obj - N/A
     * @return -a map containing report
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            GrailsParameterMap params = (GrailsParameterMap) parameters
            Supplier supplier = (Supplier) preResult.get(SUPPLIER)
            String projectName = preResult.get(PROJECT_NAME)
            List lstProjectIds = (List) preResult.get(LST_PROJECT_ID)
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())

            /*if postedByParam  = 0 the show Only Posted Voucher
              if postedByParam  = -1 the show both Posted & Unposted Voucher*/
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }
            String paidTotal = getTotalSupplierPaidAmountBySupplierAndDateRange(supplier.id, lstProjectIds, fromDate, toDate, postedByParam)

            Map report = getSupplierPoReport(supplier, lstProjectIds, projectName, fromDate, toDate, paidTotal, postedByParam)

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
     * @param supplier - supplier object
     * @param lstProjectIds - list of project ids
     * @param projectName- project name
     * @param fromDate- start date
     * @param toDate - end date
     * @param paidTotal - total paid amount to supplier
     * @param postedByParam-determines whether fetch posted voucher or both posted & un-posted voucher
     * @return- generated report with required params
     */
    private Map getSupplierPoReport(Supplier supplier, List lstProjectIds, String projectName, Date fromDate, Date toDate, String paidTotal, long postedByParam) {
        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(PAID_TOTAL, paidTotal)
        reportParams.put(SUPPLIER_ID, supplier.id)
        reportParams.put(SUPPLIER_NAME, supplier.name)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(LST_PROJECT_ID, lstProjectIds)
        reportParams.put(FROM_DATE, fromDate.toTimestamp())
        reportParams.put(TO_DATE, toDate.toTimestamp())
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(COMPANY_ID, accSessionUtil.appSessionUtil.getAppUser().companyId)
        reportParams.put(POSTED_BY_PARAM, postedByParam)

        JasperReportDef reportDef = new JasperReportDef(name: SUPPLIER_PO_REPORT, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
    /**
     * Get total supplier paid amount
     * @param supplierId - supplier id
     * @param lstProjectIds - list of project ids
     * @param fromDate- start date
     * @param toDate - end date
     * @param postedByParam-determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - a string of total supplier paid amount
     */
    private String getTotalSupplierPaidAmountBySupplierAndDateRange(long supplierId, List lstProjectIds, Date fromVoucherDate, Date toVoucherDate, long postedByParam) {
        String projectIdList = Tools.buildCommaSeparatedStringOfIds(lstProjectIds)
        String queryStr = """
            SELECT to_char(SUM(total_paid),'${Tools.DB_CURRENCY_FORMAT}') AS str_total_paid
            FROM vw_acc_supplier_payment
            WHERE voucher_date BETWEEN :fromVoucherDate AND :toVoucherDate
            AND source_id = :supplierId
            AND posted_by > :postedByParam
            AND company_id = :companyId
            AND project_id IN (${projectIdList})
        """
        Map queryParams = [
                fromVoucherDate: DateUtility.getSqlDateWithSeconds(fromVoucherDate),
                toVoucherDate: DateUtility.getSqlDateWithSeconds(toVoucherDate),
                supplierId: supplierId,
                postedByParam: postedByParam,
                companyId: accSessionUtil.appSessionUtil.getAppUser().companyId
        ]
        List<GroovyRowResult> paidTotal = executeSelectSql(queryStr, queryParams)

        return paidTotal[0].str_total_paid
    }
}
