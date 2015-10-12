package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.Country
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhAgent
import com.athena.mis.exchangehouse.utility.ExhAgentCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.exchangehouse.utility.ExhTaskTypeCacheUtility
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
 * Download agent wise commission report in pdf format for Admin
 * For details go through Use-Case doc named 'DownloadAgentWiseCommissionForAdminActionService'
 */
class DownloadAgentWiseCommissionForAdminActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    JasperService jasperService

    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'agentWiseCommission'
    private static final String AGENT_WISE_COMMISSION = 'AgentWiseCommissionReport.jasper'
    private static final String REPORT_TITLE = 'Agent Wise Commission'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String ERROR_AGENT_NOT_FOUND = "Agent not found"
    private static final String ERROR_EXCEPTION = "Failed to generate commission report"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = "AgentWiseCommission"

    private static final String AGENT_ID = "agentId"
    private static final String EXH_AGENT = "exhAgent"

    private static final String AGENT_NAME = 'agentName'
    private static final String AGENT_ADDRESS = 'agentAddress'
    private static final String AGENT_CITY = 'agentCity'
    private static final String AGENT_COUNTRY = 'agentCountry'

    private static final String STATUS_NEW_TASK = 'newTask'
    private static final String STATUS_SENT_TO_BANK = 'sentToBank'
    private static final String STATUS_SENT_TO_OTHER_BANK = 'sentToOtherBank'
    private static final String STATUS_RESOLVED_BY_OTHER_BANK = 'resolvedByOtherBank'

    private static final String FROM_FULL_DATE = 'fromFullDate'
    private static final String TO_FULL_DATE = 'toFullDate'
    private static final String TASK_TYPE_ID = 'taskTypeId'
    private static final String LOCAL_CURRENCY_NAME = 'localCurrencyName'

    /**
     * 1. pull agent from cache &  check agent existence
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing budget object & isError(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)                // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameters

            long agentId = Long.parseLong(params.agentId.toString())

            ExhAgent exhAgent = (ExhAgent) exhAgentCacheUtility.read(agentId)         // pull agent from cache
            if (!exhAgent) {
                result.put(Tools.MESSAGE, ERROR_AGENT_NOT_FOUND)
                return result
            }
            result.put(EXH_AGENT, exhAgent)
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
     * 1. Build a map for agent wise commission report map by given parameters
     * @param params - serialized parameters from UI
     * @param obj - receive map from executePreCondition
     * @return -a map containing report
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj       // cast map returned from previous method
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            ExhAgent exhAgent = (ExhAgent) preResult.get(EXH_AGENT)
            Map report = getCommissionReport(exhAgent, parameterMap)
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
     * Generate agent commission report with assigned parameters
     * @param parameterMap - serialized parameters from UI
     * @param exhAgent - an object of ExhAgent
     * @return - generated report with required params
     */
    private Map getCommissionReport(ExhAgent exhAgent, GrailsParameterMap parameterMap) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity agentTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_AGENT_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        String reportDir = Tools.getExchangeHouseReportDirectory() + File.separator + REPORT_FOLDER

        Date fromFullDate = DateUtility.parseMaskedFromDate(parameterMap.createdDateFrom)
        Date toFullDate = DateUtility.parseMaskedToDate(parameterMap.createdDateTo)

        long newTask = exhNewTaskSysEntityObject.id
        long sentToBank = exhSentToBankSysEntityObject.id
        long sentToOtherBank = exhSentToOtherBankSysEntityObject.id
        long resolvedByOtherBank = exhResolvedByOtherBankSysEntityObject.id

        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)

        reportParams.put(AGENT_ID, exhAgent.id)
        reportParams.put(AGENT_NAME, exhAgent.name)
        String exhAgentAddress = exhAgent.address.replace(Tools.NEW_LINE, Tools.SINGLE_SPACE)
        reportParams.put(AGENT_ADDRESS, exhAgentAddress)
        reportParams.put(AGENT_CITY, exhAgent.city)
        Company company = (Company) companyCacheUtility.read(exhAgent.companyId)
        Country country = (Country) countryCacheUtility.read(company.countryId)
        reportParams.put(AGENT_COUNTRY, country.name)

        reportParams.put(FROM_FULL_DATE, fromFullDate.toTimestamp())
        reportParams.put(TO_FULL_DATE, toFullDate.toTimestamp())
        reportParams.put(TASK_TYPE_ID, agentTaskObj.id)

        reportParams.put(STATUS_NEW_TASK, newTask)
        reportParams.put(STATUS_SENT_TO_BANK, sentToBank)
        reportParams.put(STATUS_SENT_TO_OTHER_BANK, sentToOtherBank)
        reportParams.put(STATUS_RESOLVED_BY_OTHER_BANK, resolvedByOtherBank)
        reportParams.put(LOCAL_CURRENCY_NAME, currencyCacheUtility.localCurrency.symbol)

        JasperReportDef reportDef = new JasperReportDef(name: AGENT_WISE_COMMISSION, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)    // generate report
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
