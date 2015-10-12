package com.athena.mis.arms.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.arms.entity.RmsTaskList
import com.athena.mis.arms.service.RmsTaskListService
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.Tools
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * search task list plan
 * for details go through use case named "SearchTaskListPlanActionService"
 */
class SearchTaskListPlanActionService extends BaseService implements ActionIntf {

    private static Logger log = Logger.getLogger(getClass())
    private static final String FAILURE_MESSAGE = "Failed to load task list plan"
    private static final String TASK_LIST_OBJ = "taskListObj"
    private static final String GRAND_TOTAL = "grandTotal"
    private static final String PURCHASE_SUB_TOTAL = "purchaseSubTotal"
    private static final String BRANCH_SUB_TOTAL = "branchSubTotal"
    private static final String PRINCIPLE_BRANCH_SUB_TOTAL = "principleBranchSubTotal"
    private static final String SME_SUB_TOTAL = "smeSubTotal"
    private static final String PURCHASE_LIST_VIEW_OBJ = "purchaseListViewObj"
    private static final String BRANCH_LIST_VIEW_OBJ = "branchListViewObj"
    private static final String PRINCIPLE_BRANCH_LIST_VIEW_OBJ = "principleBranchListViewObj"
    private static final String SME_LIST_VIEW_OBJ = "smeListViewObj"

    private double grandTotal = 0

    RmsTaskListService rmsTaskListService
    RmsTaskService rmsTaskService
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility

    /**
     * check if taskListId exists and valid
     * @param parameters - serialize parameter from ui
     * @param obj - N/A
     * @return
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.taskListId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long taskListId = Tools.parseLongInput(params.taskListId)
            RmsTaskList rmsTaskList = rmsTaskListService.read(taskListId)
            if (!rmsTaskList) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(TASK_LIST_OBJ, rmsTaskList)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build ListViewObj , subtotal, GrandTotal for UI of (Purchase, Branch, PrincipleBranch, SME)
     * @param parameters - N/A
     * @param obj - executePreResult obj
     * @return
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            grandTotal = 0
            RmsTaskList rmsTaskList = (RmsTaskList) preResult.get(TASK_LIST_OBJ)
            buildResultForPurchase(rmsTaskList.id, result)
            buildResultForBranch(rmsTaskList.id, result)
            buildResultForPrincipleBranch(rmsTaskList.id, result)
            buildResultForSME(rmsTaskList.id, result)
            result.put(GRAND_TOTAL, grandTotal.round(2))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for buildSuccess
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map serviceMap = (Map) obj
            String msg = serviceMap.get(Tools.MESSAGE)
            if (msg) {
                result.put(Tools.MESSAGE, msg)
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * get list of ProcessType - Purchase
     */
    private void buildResultForPurchase(long taskListId, Map result) {
        List<Map> listViewObj = []
        double subTotal = 0
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity purchaseObj = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(RmsProcessTypeCacheUtility.PURCHASE, companyId)
        String SQL = """
                        select bank.name as bank, sum(amount) as amount from rms_task task
                        left join bank on task.mapping_bank_id = bank.id
                        where task.process_type_id = :processTypeId and task.company_id = :companyId and task_list_id = :taskListId
                        group by task.mapping_bank_id,bank.name
                    """
        Map queryParam = [processTypeId: purchaseObj.id, companyId: companyId, taskListId: taskListId]
        List<GroovyRowResult> lstTask = executeSelectSql(SQL, queryParam)
        for (int i = 0; i < lstTask.size(); i++) {
            GroovyRowResult rmsTask = lstTask[i]
            double amount = rmsTask.amount.round(2)
            subTotal += amount
            listViewObj << [bank: rmsTask.bank, amount: amount]
        }
        grandTotal += subTotal
        result.put(PURCHASE_LIST_VIEW_OBJ, listViewObj as JSON)
        result.put(PURCHASE_SUB_TOTAL, subTotal.round(2))
    }

    /**
     * get list of processType forward, not systemBankPrincipleBranch, not SME service center
     */
    private void buildResultForBranch(long taskListId, Map result) {
        List<Map> listViewObj = []
        double subTotal = 0
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity forwardObj = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(RmsProcessTypeCacheUtility.FORWARD, companyId)
        Bank systemBank = bankCacheUtility.getSystemBank()
        BankBranch systemBankPrincipleBranch = bankBranchCacheUtility.getPrincipleBankBranch(systemBank.id)
        String SQL = """
                        select branch.name as branch, district.name as district, sum(amount) as amount from rms_task task
                        left join bank_branch branch on task.mapping_branch_id = branch.id
                        left join district on task.mapping_district_id = district.id
                        where task.process_type_id = :processTypeId and task.company_id = :companyId and task_list_id = :taskListId
                        and branch.id != :principleBranchId and branch.is_sme_service_center = 'f'
                        group by task.mapping_bank_id,branch,district
                    """
        Map queryParam = [processTypeId: forwardObj.id, companyId: companyId, taskListId: taskListId, principleBranchId: systemBankPrincipleBranch.id]
        List<GroovyRowResult> lstTask = executeSelectSql(SQL, queryParam)
        for (int i = 0; i < lstTask.size(); i++) {
            GroovyRowResult rmsTask = lstTask[i]
            double amount = rmsTask.amount.round(2)
            String branch = rmsTask.branch + Tools.COMA + Tools.SINGLE_SPACE + rmsTask.district
            subTotal += amount
            listViewObj << [branch: branch, amount: amount]
        }
        grandTotal += subTotal
        result.put(BRANCH_LIST_VIEW_OBJ, listViewObj as JSON)
        result.put(BRANCH_SUB_TOTAL, subTotal.round(2))
    }

    /**
     * get list of systemBank principleBranch
     */
    private void buildResultForPrincipleBranch(long taskListId, Map result) {
        List<Map> listViewObj = []
        double subTotal = 0
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        Bank systemBank = bankCacheUtility.getSystemBank()
        BankBranch systemBankPrincipleBranch = bankBranchCacheUtility.getPrincipleBankBranch(systemBank.id)
        String SQL = """
                        select sys_entity.value as instrument, sum(amount) as amount from rms_task task
                        left join bank_branch branch on task.mapping_branch_id = branch.id
                        left join system_entity sys_entity on task.instrument_type_id = sys_entity.id
                        where task.company_id = :companyId and task_list_id = :taskListId
                        and branch.id = :principleBranchId and branch.is_sme_service_center = 'f'
                        group by task.mapping_bank_id,instrument
                    """
        Map queryParam = [companyId: companyId, taskListId: taskListId, principleBranchId: systemBankPrincipleBranch.id]
        List<GroovyRowResult> lstTask = executeSelectSql(SQL, queryParam)
        for (int i = 0; i < lstTask.size(); i++) {
            GroovyRowResult rmsTask = lstTask[i]
            double amount = rmsTask.amount.round(2)
            subTotal += amount
            listViewObj << [instrument: rmsTask.instrument, amount: amount]
        }
        grandTotal += subTotal
        result.put(PRINCIPLE_BRANCH_LIST_VIEW_OBJ, listViewObj as JSON)
        result.put(PRINCIPLE_BRANCH_SUB_TOTAL, subTotal.round(2))
    }

    /**
     * get list of processType forward, not systemBankPrincipleBranch and SME service center
     */
    private void buildResultForSME(long taskListId, Map result) {
        List<Map> listViewObj = []
        double subTotal = 0
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity forwardObj = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(RmsProcessTypeCacheUtility.FORWARD, companyId)
        Bank systemBank = bankCacheUtility.getSystemBank()
        BankBranch systemBankPrincipleBranch = bankBranchCacheUtility.getPrincipleBankBranch(systemBank.id)
        String SQL = """
                        select branch.name as branch, district.name as district, sum(amount) as amount from rms_task task
                        left join bank_branch branch on task.mapping_branch_id = branch.id
                        left join district on task.mapping_district_id = district.id
                        where task.process_type_id = :processTypeId and task.company_id = :companyId and task_list_id = :taskListId
                        and branch.id != :principleBranchId and branch.is_sme_service_center = 't'
                        group by task.mapping_bank_id,branch,district
                    """
        Map queryParam = [processTypeId: forwardObj.id, companyId: companyId, taskListId: taskListId, principleBranchId: systemBankPrincipleBranch.id]
        List<GroovyRowResult> lstTask = executeSelectSql(SQL, queryParam)
        for (int i = 0; i < lstTask.size(); i++) {
            GroovyRowResult rmsTask = lstTask[i]
            double amount = rmsTask.amount.round(2)
            subTotal += amount
            String sme = rmsTask.branch + Tools.COMA + Tools.SINGLE_SPACE + rmsTask.district
            listViewObj << [sme: sme, amount: amount]
        }
        grandTotal += subTotal
        result.put(SME_LIST_VIEW_OBJ, listViewObj as JSON)
        result.put(SME_SUB_TOTAL, subTotal.round(2))
    }

}
