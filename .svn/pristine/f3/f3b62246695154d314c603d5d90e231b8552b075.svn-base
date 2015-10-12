package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Country
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ContentEntityTypeCacheUtility
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.application.utility.NoteEntityTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.entity.ExhPhotoIdType
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Download customer history report in pdf format for Admin & Cashier
 * For details go through Use-Case doc named 'DownloadCustomerHistoryActionService'
 */
class DownloadCustomerHistoryActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    JasperService jasperService

    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'customerHistory'
    private static final String SUB_REPORT_FOLDER = 'subreports'
    private static final String CUSTOMER_HISTORY = 'CustomerHistory.jasper'
    private static final String REPORT_TITLE = 'Customer History'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String SUB_REPORT_DIR = 'SUBREPORT_DIR'
    private static final String ERROR_CUSTOMER_NOT_FOUND = "Customer not found"
    private static final String ERROR_EXCEPTION = "Failed to generate customer history report"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = "CustomerHistory"

    private static final String CUSTOMER_ID = "customerId"
    private static final String CUSTOMER_NAME = "customerName"
    private static final String CUSTOMER_CODE = "code"
    private static final String CUSTOMER_PHONE = "phone"
    private static final String CUSTOMER_ADDRESS = "address"
    private static final String CUSTOMER_NATIONALITY = "nationality"
    private static final String CUSTOMER_PHOTO_ID = "photoId"
    private static final String CUSTOMER_SOURCE_OF_FUND = "sourceOfFund"
    private static final String CUSTOMER_DECLARATION_AMOUNT = "declarationAmount"
    private static final String CUSTOMER_ID_EXPIRE_DATE = "idExpireDate"
    private static final String CUSTOMER_VISA_EXPIRE_DATE = "visaExpireDate"
    private static final String ID_EXPIRE_DATE_NOT_GIVEN = "Not given"
    private static final String EXH_CUSTOMER = "exhCustomer"

    private static final String FROM_DATE = 'fromDate'
    private static final String TO_DATE = 'toDate'
    private static final String NOTE_ENTITY_TYPE_ID = 'noteEntityTypeCustomer'
    private static final String TASK_ENTITY_TYPE_ID = 'noteEntityTypeTask'
    private static final String CONTENT_ENTITY_TYPE_ID = 'contentEntityTypeCustomer'
    private static final String CUSTOMER_MAP = 'customerMap'
    private static final String CURRENCY_SYMBOL = 'currencySymbol'
    private static final String TASK_STATUS_LIST_IDS = 'taskStatusListIds'
    private static final String PARENTHESIS_START = " ("
    private static final String PARENTHESIS_END = ") "
    private static final String COMPANY_ID = "companyId"

    /**
     * 1. pull agent from DB &  check customer existence
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing customer object & isError(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            String code = (String) params.customerId

            ExhCustomer customer = readByCodeAndExchangeHouse(code)
            if (!customer) {
                result.put(Tools.MESSAGE, ERROR_CUSTOMER_NOT_FOUND)
                return result
            }
            result.put(EXH_CUSTOMER, customer)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_EXCEPTION)
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
     * Get desired report providing all required parameters
     * 1. Build a map for customer history report map by given parameters
     * @param params - serialized parameters from UI
     * @param obj - receive map from executePreCondition
     * @return -a map containing report
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj                // cast map returned from previous method
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            ExhCustomer exhCustomer = (ExhCustomer) preResult.get(EXH_CUSTOMER)
            Map report = getCustomerHistory(exhCustomer, parameterMap)
            result.put(REPORT, report)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error ex.getMessage()
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_EXCEPTION)
            return result
        }
    }

    /**
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Get customer by code and companyId
     */
    private ExhCustomer readByCodeAndExchangeHouse(String code) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        ExhCustomer customer = ExhCustomer.findByCodeAndCompanyId(code, companyId, [readOnly: true])
        return customer
    }

    /**
     * Generate customer history report with assigned parameters
     * @param parameterMap - serialized parameters from UI
     * @param exhCustomer - an object of ExhCustomer
     * @return - generated report with required params
     */
    private Map getCustomerHistory(ExhCustomer exhCustomer, GrailsParameterMap parameterMap) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        // pull system entity type(Customer) object
        SystemEntity contentEntityTypeCustomer = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_EXH_CUSTOMER, companyId)
        // pull note entity type(Task & Customer) object
        SystemEntity noteEntityTypeTask = (SystemEntity) noteEntityTypeCacheUtility.readByReservedAndCompany(noteEntityTypeCacheUtility.COMMENT_ENTITY_TYPE_TASK, companyId)
        SystemEntity noteEntityTypeCustomer = (SystemEntity) noteEntityTypeCacheUtility.readByReservedAndCompany(noteEntityTypeCacheUtility.COMMENT_ENTITY_TYPE_CUSTOMER, companyId)

        String reportDir = Tools.getExchangeHouseReportDirectory() + File.separator + REPORT_FOLDER
        String subReportDir = reportDir + File.separator + SUB_REPORT_FOLDER + File.separator

        Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.createdDateFrom)
        Date toDate = DateUtility.parseMaskedToDate(parameterMap.createdDateTo)

        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        Country country = (Country) countryCacheUtility.read(exhCustomer.countryId)
        ExhPhotoIdType photoIdType = (ExhPhotoIdType) exhPhotoIdTypeCacheUtility.read(exhCustomer.photoIdTypeId)

        SystemEntity exhCanceledTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_CANCELLED_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        List<Long> taskStatusListIds = [
                exhNewTaskSysEntityObject.id,
                exhSentToBankSysEntityObject.id,
                exhSentToOtherBankSysEntityObject.id,
                exhResolvedByOtherBankSysEntityObject.id,
                exhCanceledTaskSysEntityObject.id]

        Map customerMap = new LinkedHashMap()
        customerMap.put(CUSTOMER_CODE, exhCustomer.code)
        customerMap.put(CUSTOMER_NAME, exhCustomer.fullName)
        customerMap.put(CUSTOMER_ADDRESS, exhCustomer.address.replace(Tools.NEW_LINE, Tools.SINGLE_SPACE))
        customerMap.put(CUSTOMER_PHONE, exhCustomer.phone)
        String declarationAmountStr = Tools.formatAmountWithoutCurrency(exhCustomer.declarationAmount).toString() + PARENTHESIS_START + currencyCacheUtility.localCurrency.symbol + PARENTHESIS_END
        customerMap.put(CUSTOMER_DECLARATION_AMOUNT, declarationAmountStr)
        customerMap.put(CUSTOMER_SOURCE_OF_FUND, exhCustomer.sourceOfFund)
        customerMap.put(CUSTOMER_VISA_EXPIRE_DATE, exhCustomer.visaExpireDate ? exhCustomer.visaExpireDate : Tools.NOT_APPLICABLE)
        customerMap.put(CUSTOMER_ID_EXPIRE_DATE, exhCustomer.photoIdExpiryDate ? exhCustomer.photoIdExpiryDate.format(DateUtility.dd_MMM_yyyy_DASH).toString() : ID_EXPIRE_DATE_NOT_GIVEN)
        customerMap.put(CUSTOMER_NATIONALITY, country.nationality)
        customerMap.put(CUSTOMER_PHOTO_ID, photoIdType.name)

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUB_REPORT_DIR, subReportDir)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(CUSTOMER_ID, exhCustomer.id)
        reportParams.put(CUSTOMER_MAP, customerMap)
        reportParams.put(FROM_DATE, fromDate.toTimestamp())
        reportParams.put(TO_DATE, toDate.toTimestamp())
        reportParams.put(NOTE_ENTITY_TYPE_ID, noteEntityTypeCustomer.id)
        reportParams.put(TASK_ENTITY_TYPE_ID, noteEntityTypeTask.id)
        reportParams.put(CONTENT_ENTITY_TYPE_ID, contentEntityTypeCustomer.id)
        reportParams.put(CURRENCY_SYMBOL, currencyCacheUtility.localCurrency.symbol)
        reportParams.put(TASK_STATUS_LIST_IDS, taskStatusListIds)
        reportParams.put(COMPANY_ID, exhSessionUtil.appSessionUtil.getCompanyId())

        JasperReportDef reportDef = new JasperReportDef(
                name: CUSTOMER_HISTORY,
                fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)        // generate pdf
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

}
