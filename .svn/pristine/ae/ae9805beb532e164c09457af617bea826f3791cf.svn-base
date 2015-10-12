package com.athena.mis.accounting.actions.report.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccSubAccount
import com.athena.mis.accounting.entity.AccVoucher
import com.athena.mis.accounting.model.AccVoucherModel
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.accounting.utility.AccSubAccountCacheUtility
import com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility
import com.athena.mis.application.entity.Customer
import com.athena.mis.application.entity.Employee
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CustomerCacheUtility
import com.athena.mis.application.utility.EmployeeCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.utility.N2W
import com.athena.mis.utility.Tools
import org.apache.commons.lang.WordUtils
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Download to preview voucher(only for bank cheque) in pdf format
 * For details go through Use-Case doc named 'DownloadForVoucherBankChequePreviewActionService'
 */
class DownloadForVoucherBankChequePreviewActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    CustomerCacheUtility customerCacheUtility
    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download voucher."
    private static final String VOUCHER_NOT_FOUND_MESSAGE = "Voucher not found."
    private static final String CHEQUE_NOT_APPLICABLE_MESSAGE = "Cheque not applicable for multiple debit sources."
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accVoucher'
    private static final String OUTPUT_FILE_NAME = 'cheque_'
    private static final String REPORT_TITLE = 'Bank Cheque Report'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String ACC_VOUCHER_OBJ = "accVoucher"
    private static final String JASPER_FILE_NAME = 'bankChequePreview.jasper'
    private static final String VOUCHER_MAP = "voucherMap"
    private static final String CHEQUE_DATE = "chequeDate"
    private static final String THE_SUM_OF_TAKA = "theSumOfTaka"
    private static final String CHEQUE_AMOUNT = "amount"
    private static final String PAY_TO = "payTo"
    private static final String IS_ACCOUNT_PAYABLE = "isAccountPayable"
    private static final String SP = " "

    /**
     * Check pre conditions and get voucher object
     * @param parameters- serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing voucher object & isError msg(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            long voucherId = Long.parseLong(params.voucherId.toString())
            AccVoucher accVoucher = AccVoucher.read(voucherId)
            if (!accVoucher) {
                result.put(Tools.MESSAGE, VOUCHER_NOT_FOUND_MESSAGE)
                return result
            }
            // check voucher type whether it is bank cheque or not
            SystemEntity voucherTypeObj = (SystemEntity) accVoucherTypeCacheUtility.readByReservedAndCompany(accVoucherTypeCacheUtility.PAYMENT_VOUCHER_BANK_ID, accSessionUtil.appSessionUtil.getCompanyId())

            if (accVoucher.voucherTypeId != voucherTypeObj.id) {
                result.put(Tools.MESSAGE, VOUCHER_NOT_FOUND_MESSAGE)
                return result
            }
            // check for for multiple debit sources
            if (accVoucher.drCount > 1) {
                result.put(Tools.MESSAGE, CHEQUE_NOT_APPLICABLE_MESSAGE)
                return result
            }

            result.put(ACC_VOUCHER_OBJ, accVoucher)
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
     * Get all parameters to generate report
     * @param parameters-serialized parameters from UI
     * @param obj- map received from pre execute method
     * @return - a map containing voucher report & isError msg(True/False)
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            String accountName = params.accountName.toString()
            boolean isAccountPayable = Boolean.parseBoolean(params.isAccountPayable.toString())

            LinkedHashMap preResult = (LinkedHashMap) obj
            AccVoucher accVoucher = (AccVoucher) preResult.get(ACC_VOUCHER_OBJ)
            Map report = getVoucherReport(accVoucher, accountName, isAccountPayable)
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
     * Get voucher report
     * @param accVoucher - voucher object
     * @param accountName - account name
     * @param isAccountPayable
     * @return - generated report object
     */
    private Map getVoucherReport(AccVoucher accVoucher, String accountName, boolean isAccountPayable) {

        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + Tools.UNDERSCORE + accVoucher.traceNo + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        Map voucherMap = buildVoucherMap(accVoucher, accountName, isAccountPayable)
        reportParams.put(VOUCHER_MAP, voucherMap)
        String jasperFileName = JASPER_FILE_NAME

        JasperReportDef reportDef = new JasperReportDef(name: jasperFileName, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
    /**
     *
     * @param accVoucher - voucher object
     * @param accountName - account name
     * @param isAccountPayable - true/false
     * @param reportDir  - report directory
     * @return - a map containing cheque issue date with exact position in the cheque, total amount,
     * total amount in words & is payable or not.
     */
    private LinkedHashMap buildVoucherMap(AccVoucher accVoucher, String accountName, boolean isAccountPayable) {
        LinkedHashMap voucherMap = new LinkedHashMap()

        Calendar calDate = Calendar.getInstance()
        calDate.setTime(accVoucher.chequeDate);
        String d = calDate.get(Calendar.DAY_OF_MONTH)
        int month = calDate.get(Calendar.MONTH)
        String m = (String) (month + 1)      // since Calendar control starts month from 0, add 1 to get actual
        String y = calDate.get(Calendar.YEAR)
        // pad zero (if necessary)
        d = d.padLeft(2, Tools.STR_ZERO)
        m = m.padLeft(2, Tools.STR_ZERO)
        // Build pattern exactly like "9    9 -  9   9 -  9   9    9    9"
        voucherMap.put(CHEQUE_DATE, d[0] + (SP * 5) + d[1] + SP + Tools.HYPHEN +
                (SP * 2) + m[0] + (SP * 4) + m[1] + (SP * 2) + Tools.HYPHEN +
                (SP * 2) + y[0] + (SP * 4) + y[1] + (SP * 4) + y[2] + (SP * 4) + y[3])

        String inWord = WordUtils.wrap(N2W.convert(accVoucher.amount),57)  //wrap text upto max of 31 space + 57 char
        voucherMap.put(THE_SUM_OF_TAKA, (SP * 31) + inWord)
        voucherMap.put(CHEQUE_AMOUNT, accVoucher.amount)
        voucherMap.put(IS_ACCOUNT_PAYABLE, isAccountPayable)

        if (accountName) {
            voucherMap.put(PAY_TO, accountName)
            return voucherMap
        }
        String payTo = Tools.EMPTY_SPACE
        List<AccVoucherModel> accVoucherModelList = AccVoucherModel.listByVoucherId(accVoucher.id, accSessionUtil.appSessionUtil.getCompanyId()).list()
        long sourceId = accVoucherModelList[0].sourceId
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(accVoucherModelList[0].sourceTypeId)
        switch (accSourceType.reservedId) {      // fetch voucher source name according to their type
            case accSourceCacheUtility.SOURCE_TYPE_CUSTOMER:
                Customer customer = (Customer) customerCacheUtility.read(sourceId)
                payTo = customer.fullName
                break;
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                Employee employee = (Employee) employeeCacheUtility.read(sourceId)
                payTo = employee.fullName
                break;
            case accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT:
                AccSubAccount accSubAccount = (AccSubAccount) accSubAccountCacheUtility.read(sourceId)
                payTo = accSubAccount.description
                break;
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                Supplier supplier = (Supplier) supplierCacheUtility.read(sourceId)
                payTo = supplier.accountName
                break;
            default:
                break
        }
        voucherMap.put(PAY_TO, payTo)
        return voucherMap
    }
}