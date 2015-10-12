package com.athena.mis.accounting.actions.report.accchartofaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.springframework.beans.factory.annotation.Autowired

/**
 * Download chart of account in pdf format.
 * For details go through Use-Case doc named 'DownloadChartOfAccountActionService'
 */
class DownloadChartOfAccountActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download Chart of Account."
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accChartOfAccount'
    private static final String OUTPUT_FILE_NAME = 'ChartOfAccount'
    private static final String REPORT_TITLE = 'Chart Of Account'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'chartOfAccountTree.jasper'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String ACC_SOURCE_NONE = "accSourceNone"
    private static final String COMPANY_ID = "companyId"

    private Logger log = Logger.getLogger(getClass())
    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {

    }
    /**
     * Get all object for generating chart of account report.
     * @param parameters -N/A
     * @param obj -N/A
     * @return - a map containing chart of account object & isError msg(True/False)
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map report = getChartOfAccountReport()  // get chart of account report
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(REPORT, report)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage());
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
     *  Get all chart of account in pdf report
     * @return - a map containing chart of account report objects
     */
    private Map getChartOfAccountReport() {
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeNone = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.ACC_SOURCE_NAME_NONE, companyId)
        int accSourceNoneId = accSourceTypeNone.id
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(ACC_SOURCE_NONE, new Integer(accSourceNoneId))
        reportParams.put(COMPANY_ID, companyId)
        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

}
