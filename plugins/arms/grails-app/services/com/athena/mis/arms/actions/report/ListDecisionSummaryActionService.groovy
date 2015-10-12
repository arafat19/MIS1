package com.athena.mis.arms.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * build decision summary for grid
 * for details go through use-case named 'ListDecisionSummaryActionService'
 */
class ListDecisionSummaryActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    private static final String FAILURE_MSG = "Failed to load decision summary"
    private static final String GRID_OBJ = "gridObj"
    private static final String LST_DECISION = "lstDecision"
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility

    /**
     * do nothing
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * build decision summary for grid
     * @param parameters - serialized parameter from ui
     * @param obj - n/a
     * @return - map for buildSuccess
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try{
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initPager(params)
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Date toDate = DateUtility.parseMaskedToDate(params.toDate)
            List<GroovyRowResult> lstDecisionSummary = listDecisionSummary(this, fromDate, toDate)
            int count = countDecisionSummary(fromDate, toDate)
            result.put(LST_DECISION, lstDecisionSummary)
            result.put(Tools.COUNT, count)
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
     * wrap list for grid
     * @param obj - map from executeResult
     * @return
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try{
            Map executeResult = (Map) obj
            List<GroovyRowResult> lstDecisionSummary = (List<GroovyRowResult>) executeResult.get(LST_DECISION)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedDecision = wrapDecisionSummary(lstDecisionSummary, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedDecision]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * build failure msg for ui
     * @param obj
     * @return
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try{
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if(obj) {
                Map executeResult = (Map) obj
                String msg = executeResult.get(Tools.MESSAGE)
                if(msg) {
                    result.put(Tools.MESSAGE, msg)
                } else {
                    result.put(Tools.MESSAGE, FAILURE_MSG)
                }
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
     * wrap list
     * @param lstDecisionSummary
     * @param start
     * @return
     */
    private List wrapDecisionSummary(List<GroovyRowResult> lstDecisionSummary, int start) {
        List lstWrappedDecision = []
        int counter = start + 1
        for (int i = 0; i < lstDecisionSummary.size(); i++) {
            GroovyRowResult eachRow= lstDecisionSummary[i]
            GridEntity obj = new GridEntity()
            obj.id = counter
            obj.cell = [
                    counter,
                    eachRow.process,
                    eachRow.instrument,
                    eachRow.bank_name,
                    eachRow.branch_name,
                    eachRow.district_name,
                    eachRow.total_task,
                    eachRow.total_amount
            ]
            lstWrappedDecision << obj
            counter++
        }
        return lstWrappedDecision
    }

    /**
     * find the list of DecisionSummary
     * @param baseService
     * @param fromDate
     * @param toDate
     * @return
     */
    private List<GroovyRowResult> listDecisionSummary(BaseService baseService, Date fromDate, Date toDate) {
        String strFromDate = DateUtility.getSqlFromDateWithSeconds(fromDate)
        String strToDate = DateUtility.getSqlToDateWithSeconds(toDate)
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity decisionTaken = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DECISION_TAKEN, companyId)
        SystemEntity decisionApproved = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DECISION_APPROVED, companyId)
        SystemEntity disbursed = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DISBURSED, companyId)

        String sql = """
        SELECT process.key as process, instrument.key as instrument, bank.name as bank_name, branch.name as branch_name, district.name as district_name, count(task.id) as total_task, sum(task.amount) as total_amount
        FROM rms_task task
        LEFT JOIN system_entity process ON process.id = task.process_type_id
        LEFT JOIN system_entity instrument ON instrument.id = task.instrument_type_id
        LEFT JOIN bank ON bank.id = task.mapping_bank_id
        LEFT JOIN bank_branch branch ON branch.id = task.mapping_branch_id
        LEFT JOIN district ON district.id = task.mapping_district_id
        WHERE task.current_status IN (${decisionTaken.id}, ${decisionApproved.id}, ${disbursed.id})
        AND task.company_id = ${companyId}
        AND task.created_on BETWEEN '${strFromDate}' AND '${strToDate}'
        GROUP BY process, instrument, bank_name, branch_name, district_name
        ORDER BY process, instrument, bank_name, district_name
        LIMIT ${baseService.resultPerPage} OFFSET ${baseService.start}
        """
        List<GroovyRowResult> lstDecisionSummary = executeSelectSql(sql)
        return lstDecisionSummary
    }

    /**
     * count the list of DecisionSummary
     * @param baseService
     * @param fromDate
     * @param toDate
     * @return
     */
    private int countDecisionSummary(Date fromDate, Date toDate) {
        String strFromDate = DateUtility.getSqlFromDateWithSeconds(fromDate)
        String strToDate = DateUtility.getSqlToDateWithSeconds(toDate)
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity decisionTaken = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DECISION_TAKEN, companyId)
        SystemEntity decisionApproved = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DECISION_APPROVED, companyId)
        SystemEntity disbursed = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DISBURSED, companyId)

        String sql = """
        SELECT count(id)
        FROM rms_task task
        WHERE task.current_status IN (${decisionTaken.id}, ${decisionApproved.id}, ${disbursed.id})
        AND task.company_id = ${companyId}
        AND created_on BETWEEN '${strFromDate}' AND '${strToDate}'
        GROUP BY task.process_type_id, task.instrument_type_id, task.mapping_bank_id, task.mapping_branch_id, task.mapping_district_id
        """
        List lstCount = executeSelectSql(sql)
        return lstCount.size()
    }
}
