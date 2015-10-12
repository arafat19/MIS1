package com.athena.mis.accounting.actions.report.accvoucherlist

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Download voucher list within specific date range in pdf format
 * For details go through Use-Case doc named 'DownloadForVoucherListActionService'
 */
class DownloadForVoucherListActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download voucher list."
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accVoucherList'
    private static final String OUTPUT_FILE_NAME = 'voucherList'
    private static final String REPORT_TITLE = 'Voucher List'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String JASPER_FILE_NAME = 'accVoucherList.jasper'
    private static final String JASPER_FILE_NAME_SPECIFIC = 'accVoucherListSpecific.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String VOUCHER_TYPE_NOT_FOUND = "Voucher Type not found."
    private static final String VOUCHER_TYPE_ID = "voucherTypeId"
    private static final String VOUCHER_TYPE_NAME = "voucherTypeName"
    private static final String COMPANY_ID = "companyId"
    private static final String IS_POSTED = "isPosted"

    /**
     * Check all pre-conditions.
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing voucher type name & id & isError(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.voucherTypeId || !params.fromDate || !params.toDate) {
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
            int voucherTypeId = Integer.parseInt(params.voucherTypeId.toString())
            SystemEntity accVoucherType = (SystemEntity) accVoucherTypeCacheUtility.read(voucherTypeId)
            if (!accVoucherType) {
                result.put(Tools.MESSAGE, VOUCHER_TYPE_NOT_FOUND)
                return result
            }

            result.put(VOUCHER_TYPE_NAME, accVoucherType.key)
            result.put(VOUCHER_TYPE_ID, voucherTypeId)
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
     * Get voucher list & generate report
     * @param parameters-N/A
     * @param obj- map received from pre execute operation
     * @return - map containing voucher report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            GrailsParameterMap params = (GrailsParameterMap) parameters
            int voucherTypeId = Integer.parseInt(preResult.get(VOUCHER_TYPE_ID).toString())
            String voucherTypeName = preResult.get(VOUCHER_TYPE_NAME)
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())

            Map report = getVoucherListReport(voucherTypeId, voucherTypeName, fromDate, toDate, params)
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
            if (executeResult.message) {
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
     * Get voucher list and generate report
     * @param voucherTypeId
     * @param voucherTypeName
     * @param fromDate
     * @param toDate
     * @param params
     * @return -report object
     */
    private Map getVoucherListReport(int voucherTypeId, String voucherTypeName, Date fromDate, Date toDate, GrailsParameterMap params) {

        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(VOUCHER_TYPE_ID, voucherTypeId)
        reportParams.put(FROM_DATE, fromDate.toTimestamp())
        reportParams.put(TO_DATE, toDate.toTimestamp())
        reportParams.put(VOUCHER_TYPE_NAME, voucherTypeName)

        reportParams.put(COMPANY_ID, accSessionUtil.appSessionUtil.getAppUser().companyId)
        String jasperFileName = JASPER_FILE_NAME
        if (params.isPosted == 'true' || params.isPosted == 'false') {
            jasperFileName = JASPER_FILE_NAME_SPECIFIC
            reportParams.put(IS_POSTED, Boolean.parseBoolean(params.isPosted.toString()))
        }

        JasperReportDef reportDef = new JasperReportDef(name: jasperFileName, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
