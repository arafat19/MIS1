package com.athena.mis.accounting.actions.report.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccCancelledVoucher
import com.athena.mis.accounting.entity.AccCancelledVoucherDetails
import com.athena.mis.accounting.entity.AccVoucher
import com.athena.mis.accounting.entity.AccVoucherDetails
import com.athena.mis.accounting.utility.AccInstrumentTypeCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.N2W
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Download voucher in pdf format
 * For details go through Use-Case doc named 'DownloadForVoucherActionService'
 */
class DownloadForVoucherActionService extends BaseService implements ActionIntf {

    JasperService jasperService

    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AccInstrumentTypeCacheUtility accInstrumentTypeCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download voucher"
    private static final String VOUCHER_NOT_FOUND_MESSAGE = "Voucher not found"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accVoucher'
    private static final String OUTPUT_FILE_NAME = 'Voucher'
    private static final String REPORT_TITLE = 'Voucher Report'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String ACC_VOUCHER_OBJ = "accVoucher"
    private static final String JASPER_FILE_NAME = 'Journal.jasper'
    private static final String CANCELLED_JASPER_FILE_NAME = 'cancelledJournal.jasper'
    private static final String VOUCHER_MAP = "voucherMap"
    private static final String VOUCHER_ID = "voucherId"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String SOURCE_TYPE_ID_NONE = 'sourceTypeIdNone'
    private static final String SOURCE_TYPE_ID_CUSTOMER = 'sourceTypeIdCustomer'
    private static final String SOURCE_TYPE_ID_EMPLOYEE = 'sourceTypeIdEmployee'
    private static final String SOURCE_TYPE_ID_SUB_ACCOUNT = 'sourceTypeIdSubAccount'
    private static final String SOURCE_TYPE_ID_SUPPLIER = 'sourceTypeIdSupplier'
    private static final String SOURCE_TYPE_ID_ITEM = "sourceTypeIdItem"
    private static final String SOURCE_TYPE_ID_LC = "sourceTypeIdLC"
    private static final String SOURCE_TYPE_ID_IPC = "sourceTypeIdIPC"
    private static final String SOURCE_TYPE_ID_LEASE_ACCOUNT = "sourceTypeIdLeaseAccount"

    /**
     * Get voucher object against voucher id received from UI.
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing voucher object & isError(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            long voucherId = Long.parseLong(params.voucherId.toString())

            if(params.cancelledVoucher == 'true'){
                AccCancelledVoucher accVoucher = AccCancelledVoucher.findByIdAndCompanyId(voucherId, accSessionUtil.appSessionUtil.getCompanyId(), [readOnly: true])
                if (!accVoucher) {
                    result.put(Tools.MESSAGE, VOUCHER_NOT_FOUND_MESSAGE)
                    return result
                }

            result.put(ACC_VOUCHER_OBJ, accVoucher)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
            }

            AccVoucher accVoucher = AccVoucher.findByIdAndCompanyId(voucherId, accSessionUtil.appSessionUtil.getCompanyId(), [readOnly: true])
            if (!accVoucher) {
                result.put(Tools.MESSAGE, VOUCHER_NOT_FOUND_MESSAGE)
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
     * Get voucher object & generate report
     * @param parameters-N/A
     * @param obj- map received from pre execute operation
     * @return - map containing voucher report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)  // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            LinkedHashMap preResult = (LinkedHashMap) obj  // cast object received from pre execute method
            Map report

            if(params.cancelledVoucher == 'true'){
                AccCancelledVoucher cancelledVoucher = (AccCancelledVoucher) preResult.get(ACC_VOUCHER_OBJ)
                report = getCancelledVoucherReport(cancelledVoucher) // get voucher report
                result.put(REPORT, report)
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                return result
            }

            AccVoucher accVoucher = (AccVoucher) preResult.get(ACC_VOUCHER_OBJ)
            report = getVoucherReport(accVoucher) // get voucher report

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
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast object received from pre previous method
            result.put(Tools.IS_ERROR, previousResult.get(Tools.IS_ERROR))
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
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
     * Get voucher object and generate report
     * @param accVoucher - voucher object
     * @return  - report object
     */
    private Map getVoucherReport(AccVoucher accVoucher) {

        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + Tools.UNDERSCORE + accVoucher.traceNo + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        Map voucherMap = buildVoucherMap(accVoucher)
        reportParams.put(VOUCHER_MAP, voucherMap)
        reportParams.put(VOUCHER_ID, accVoucher.id)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)

        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeNone = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.ACC_SOURCE_NAME_NONE, companyId)
        SystemEntity accSourceTypeCustomer = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_CUSTOMER, companyId)
        SystemEntity accSourceTypeEmployee = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE, companyId)
        SystemEntity accSourceTypeSubAccount = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT, companyId)
        SystemEntity accSourceTypeSupplier = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUPPLIER, companyId)
        SystemEntity accSourceTypeItem = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_ITEM, companyId)
        SystemEntity accSourceTypeLc = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_LC, companyId)
        SystemEntity accSourceTypeIpc = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_IPC, companyId)
        SystemEntity accSourceTypeLeaseAccount = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT, companyId)

        reportParams.put(SOURCE_TYPE_ID_NONE, accSourceTypeNone.id)
        reportParams.put(SOURCE_TYPE_ID_CUSTOMER, accSourceTypeCustomer.id)
        reportParams.put(SOURCE_TYPE_ID_EMPLOYEE, accSourceTypeEmployee.id)
        reportParams.put(SOURCE_TYPE_ID_SUB_ACCOUNT, accSourceTypeSubAccount.id)
        reportParams.put(SOURCE_TYPE_ID_SUPPLIER, accSourceTypeSupplier.id)
        reportParams.put(SOURCE_TYPE_ID_ITEM, accSourceTypeItem.id)
        reportParams.put(SOURCE_TYPE_ID_LC, accSourceTypeLc.id)
        reportParams.put(SOURCE_TYPE_ID_IPC, accSourceTypeIpc.id)
        reportParams.put(SOURCE_TYPE_ID_LEASE_ACCOUNT, accSourceTypeLeaseAccount.id)

        String jasperFileName = JASPER_FILE_NAME

        JasperReportDef reportDef = new JasperReportDef(name: jasperFileName, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
    /**
     * Get organized voucher object for report
     * @param accVoucher - voucher object
     * @return - a map containing voucher params
     */
    private LinkedHashMap buildVoucherMap(AccVoucher accVoucher) {
        AppUser preparedBy = (AppUser) appUserCacheUtility.read(accVoucher.createdBy)
        AppUser approvedBy = (AppUser) appUserCacheUtility.read(accVoucher.postedBy)
        List totalDebitedList = AccVoucherDetails.withCriteria {     // get total debit amount for a voucher
            eq('voucherId', accVoucher.id)
            projections {
                sum('amountDr')
            }
        }
        double totalDebitedAmount = totalDebitedList[0]
        List totalCreditedList = AccVoucherDetails.withCriteria {    // get total credit amount for a voucher
            eq('voucherId', accVoucher.id)
            projections {
                sum('amountCr')
            }
        }
        double totalCreditedAmount = totalCreditedList[0]

        SystemEntity accVoucherType = (SystemEntity) accVoucherTypeCacheUtility.read(accVoucher.voucherTypeId)

        Project project = (Project) projectCacheUtility.read(accVoucher.projectId)

        String instrumentTypeName = Tools.NOT_APPLICABLE      // default value for instrument type
        if (accVoucher.instrumentId > 0) {    // get instrument type name(key) against instrument type id
            SystemEntity accInstrumentType = (SystemEntity) accInstrumentTypeCacheUtility.read(accVoucher.instrumentTypeId)
            instrumentTypeName = accInstrumentType.key
        }

        LinkedHashMap voucherMap = [          // build map containing voucher object
                voucherTypeName: accVoucherType.key,
                voucherId: accVoucher.id,
                traceNo: accVoucher.traceNo,
                chequeNo: accVoucher.chequeNo ? accVoucher.chequeNo : Tools.EMPTY_SPACE,
                voucherDate: DateUtility.getDateFormatAsString(accVoucher.voucherDate),
                preparedBy: preparedBy.username,
                printDate: new Date().format(DateUtility.dd_MMMM_yyyy_DASH),
                approvedBy: approvedBy ? approvedBy.username : Tools.EMPTY_SPACE,
                totalDebitedAmountInFormat: Tools.makeAmountWithThousandSeparator(totalDebitedAmount),
                totalAmountInWord: N2W.convert(totalDebitedAmount),
                totalCreditedAmountInFormat: Tools.makeAmountWithThousandSeparator(totalCreditedAmount),
                projectName: project.name,
                instrumentType: instrumentTypeName,
                instrumentNo: accVoucher.instrumentId > 0 ? accVoucher.instrumentId : Tools.NOT_APPLICABLE
        ]
        return voucherMap
    }

    /**
     * Get voucher object and generate report
     * @param accVoucher - voucher object
     * @return  - report object
     */
    private Map getCancelledVoucherReport(AccCancelledVoucher cancelledVoucher) {

        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + Tools.UNDERSCORE + cancelledVoucher.traceNo + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        Map voucherMap = buildCancelledVoucherMap(cancelledVoucher)
        reportParams.put(VOUCHER_MAP, voucherMap)
        reportParams.put(VOUCHER_ID, cancelledVoucher.id)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)

        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeNone = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.ACC_SOURCE_NAME_NONE, companyId)
        SystemEntity accSourceTypeCustomer = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_CUSTOMER, companyId)
        SystemEntity accSourceTypeEmployee = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE, companyId)
        SystemEntity accSourceTypeSubAccount = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT, companyId)
        SystemEntity accSourceTypeSupplier = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUPPLIER, companyId)
        SystemEntity accSourceTypeItem = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_ITEM, companyId)
        SystemEntity accSourceTypeLc = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_LC, companyId)
        SystemEntity accSourceTypeIpc = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_IPC, companyId)
        SystemEntity accSourceTypeLeaseAccount = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT, companyId)

        reportParams.put(SOURCE_TYPE_ID_NONE, accSourceTypeNone.id)
        reportParams.put(SOURCE_TYPE_ID_CUSTOMER, accSourceTypeCustomer.id)
        reportParams.put(SOURCE_TYPE_ID_EMPLOYEE, accSourceTypeEmployee.id)
        reportParams.put(SOURCE_TYPE_ID_SUB_ACCOUNT, accSourceTypeSubAccount.id)
        reportParams.put(SOURCE_TYPE_ID_SUPPLIER, accSourceTypeSupplier.id)
        reportParams.put(SOURCE_TYPE_ID_ITEM, accSourceTypeItem.id)
        reportParams.put(SOURCE_TYPE_ID_LC, accSourceTypeLc.id)
        reportParams.put(SOURCE_TYPE_ID_IPC, accSourceTypeIpc.id)
        reportParams.put(SOURCE_TYPE_ID_LEASE_ACCOUNT, accSourceTypeLeaseAccount.id)

        String jasperFileName = CANCELLED_JASPER_FILE_NAME

        JasperReportDef reportDef = new JasperReportDef(name: jasperFileName, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
    /**
     * Get organized voucher object for report
     * @param accVoucher - voucher object
     * @return - a map containing voucher params
     */
    private LinkedHashMap buildCancelledVoucherMap(AccCancelledVoucher accVoucher) {
        AppUser preparedBy = (AppUser) appUserCacheUtility.read(accVoucher.createdBy)
        AppUser approvedBy = (AppUser) appUserCacheUtility.read(accVoucher.postedBy)
        AppUser cancelledBy = (AppUser) appUserCacheUtility.read(accVoucher.cancelledBy)

        List totalDebitedList = AccCancelledVoucherDetails.withCriteria {     // get total debit amount for a voucher
            eq('voucherId', accVoucher.id)
            projections {
                sum('amountDr')
            }
        }
        double totalDebitedAmount = totalDebitedList[0]
        List totalCreditedList = AccCancelledVoucherDetails.withCriteria {    // get total credit amount for a voucher
            eq('voucherId', accVoucher.id)
            projections {
                sum('amountCr')
            }
        }
        double totalCreditedAmount = totalCreditedList[0]

        SystemEntity accVoucherType = (SystemEntity) accVoucherTypeCacheUtility.read(accVoucher.voucherTypeId)

        Project project = (Project) projectCacheUtility.read(accVoucher.projectId)

        String instrumentTypeName = Tools.NOT_APPLICABLE      // default value for instrument type
        if (accVoucher.instrumentId > 0) {    // get instrument type name(key) against instrument type id
            SystemEntity accInstrumentType = (SystemEntity) accInstrumentTypeCacheUtility.read(accVoucher.instrumentTypeId)
            instrumentTypeName = accInstrumentType.key
        }

        LinkedHashMap voucherMap = [          // build map containing voucher object
                voucherTypeName: accVoucherType.key + ' (Cancelled)',
                voucherId: accVoucher.id,
                traceNo: accVoucher.traceNo,
                chequeNo: accVoucher.chequeNo ? accVoucher.chequeNo : Tools.EMPTY_SPACE,
                voucherDate: DateUtility.getDateFormatAsString(accVoucher.voucherDate),
                cancelledOn: DateUtility.getDateFormatAsString(accVoucher.cancelledOn),
                cancelReason: accVoucher.cancelReason,
                preparedBy: preparedBy.username,
                cancelledBy: cancelledBy.username,
                printDate: new Date().format(DateUtility.dd_MMMM_yyyy_DASH),
                approvedBy: approvedBy ? approvedBy.username : Tools.EMPTY_SPACE,
                totalDebitedAmountInFormat: Tools.makeAmountWithThousandSeparator(totalDebitedAmount),
                totalAmountInWord: N2W.convert(totalDebitedAmount),
                totalCreditedAmountInFormat: Tools.makeAmountWithThousandSeparator(totalCreditedAmount),
                projectName: project.name,
                instrumentType: instrumentTypeName,
                instrumentNo: accVoucher.instrumentId > 0 ? accVoucher.instrumentId : Tools.NOT_APPLICABLE
        ]
        return voucherMap
    }
}