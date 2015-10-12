package com.athena.mis.arms.actions.instrument

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.District
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Get list of task for process instrument
 *  For details go through Use-Case doc named 'ListTaskForProcessInstrumentActionService'
 */
class ListTaskForProcessInstrumentActionService extends BaseService implements ActionIntf {

    RmsTaskService rmsTaskService
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    DistrictCacheUtility districtCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String LST_TASK = "lstTask"
    private static final String FAILURE_MESSAGE = "Failed to load task";
    private static final String GRID_OBJ = "gridObj";

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load task"
    private static final String HAS_ACCESS = "hasAccess";
    private static final String EXH_HOUSE = "exhHouse";
    private static final String CURRENT_STATUS = "currentStatus";
    private static final String TASK_LIST_ID = "taskListId";
    private static final String PROCESS_TYPE_ID = "processTypeId";
    private static final String INSTRUMENT_TYPE_ID = "instrumentTypeId";
    private static final String FROM_DATE = "fromDate";
    private static final String TO_DATE = "toDate";
    private static final String BANK_ID = "bankId";
    private static final String BRANCH_ID = "branchId";

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initSearch(parameters)          // initialize parameters
            long exhHouseId = Tools.parseLongInput(params.exhHouseId)
            long currentStatus = Tools.parseLongInput(params.currentStatus)
            long taskListId = Tools.parseLongInput(params.taskListId)
            long processTypeId = Tools.parseLongInput(params.process)
            long instrumentTypeId = Tools.parseLongInput(params.instrument)
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Date toDate = DateUtility.parseMaskedToDate(params.toDate)
            Map resultMap = getBankIdBranchIdAndCheckAccess(processTypeId, params)
            long bankId = resultMap.bankId
            long branchId = resultMap.branchId
            boolean hasAccess = resultMap.hasAccess
            result.put(EXH_HOUSE, exhHouseId)
            result.put(CURRENT_STATUS, currentStatus)
            result.put(TASK_LIST_ID, taskListId)
            result.put(PROCESS_TYPE_ID, processTypeId)
            result.put(INSTRUMENT_TYPE_ID, instrumentTypeId)
            result.put(FROM_DATE, fromDate)
            result.put(TO_DATE, toDate)
            result.put(BANK_ID, bankId)
            result.put(BRANCH_ID, branchId)
            result.put(HAS_ACCESS, hasAccess)
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
     * 1. Get task list
     * 2. Get count of task
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) parameters
            Long bankId = (Long) executeResult.get(BANK_ID)
            Long exhHouseId = (Long) executeResult.get(EXH_HOUSE)
            Long currentStatus = (Long) executeResult.get(CURRENT_STATUS)
            Long taskListId = (Long) executeResult.get(TASK_LIST_ID)
            Long processTypeId = (Long) executeResult.get(PROCESS_TYPE_ID)
            Long instrumentTypeId = (Long) executeResult.get(INSTRUMENT_TYPE_ID)
            Date fromDate = (Date) executeResult.get(FROM_DATE)
            Date toDate = (Date) executeResult.get(TO_DATE)
            Long branchId = (Long) executeResult.get(BRANCH_ID)
            Map searchResult = rmsTaskService.listTaskByStatusForProcessInstrument(bankId, exhHouseId, currentStatus, taskListId, processTypeId, instrumentTypeId, fromDate, toDate, branchId, this)
            List<RmsTask> lstTasks = (List<RmsTask>) searchResult.listOfTasks
            Integer count = (Integer) searchResult.count
            result.put(LST_TASK, lstTasks)
            result.put(Tools.COUNT, count)
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
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap task list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<RmsTask> lstTask = (List<RmsTask>) executeResult.get(LST_TASK)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedTask = wrapTask(lstTask, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedTask]
            result.put(GRID_OBJ, gridObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of task in grid entity
     * @param lstTask -list of task object
     * @param start -starting index of the page
     * @return -list of wrapped task
     */
    private List wrapTask(List<RmsTask> lstTask, int start) {
        List lstWrappedTask = []
        int counter = start + 1
        for (int i = 0; i < lstTask.size(); i++) {
            RmsTask task = lstTask[i]
            SystemEntity instrument = (SystemEntity) rmsInstrumentTypeCacheUtility.read(task.instrumentTypeId)

            Bank bank = (Bank) bankCacheUtility.read(task.mappingBankId)
            BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(task.mappingBranchId)
            District district = (District) districtCacheUtility.read(task.mappingDistrictId)

            GridEntity obj = new GridEntity()
            obj.id = task.id
            obj.cell = [
                    counter,
                    task.id,
                    task.refNo,
                    task.amount,
                    DateUtility.getLongDateForUI(task.createdOn),
                    task.beneficiaryName,
                    bank.name + Tools.EMPTY_SPACE_COMA + bankBranch.name + Tools.EMPTY_SPACE_COMA + district.name,
                    instrument ? instrument.key : Tools.EMPTY_SPACE
            ]
            lstWrappedTask << obj
            counter++
        }
        return lstWrappedTask
    }
    /**
     * Admin can see only tasks of SEBL
     * @param processTypeId -issue/forward/purchase
     * @param params -params for bankId
     * @return a map containing bankId,branchId and hasAccess
     */
    private Map getBankIdBranchIdAndCheckAccess(long processTypeId, GrailsParameterMap params) {
        long bankId = 0L
        long branchId = 0L
        Boolean hasAccess = true
        SystemEntity forwardProcess = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(rmsProcessTypeCacheUtility.FORWARD, appSessionUtil.getCompanyId())
        SystemEntity purchaseProcess = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(rmsProcessTypeCacheUtility.PURCHASE, appSessionUtil.getCompanyId())
        SystemEntity issueProcess = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(rmsProcessTypeCacheUtility.ISSUE, appSessionUtil.getCompanyId())
        boolean isPowerUser = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_ADMIN)
        boolean isConfigUser = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_DEVELOPMENT_USER)
        boolean hasRemittanceUserRole = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_REMITTANCE_USER)
        if (forwardProcess.id == processTypeId) {
            boolean hasBranchRole = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_BRANCH_USER)
            boolean hasOtherBankRole = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_OTHER_BANK_USER)
            if (!hasBranchRole && !hasOtherBankRole) {
                hasAccess = false
            }
            branchId = rmsSessionUtil.appSessionUtil.getUserBankBranchId()
            BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(branchId)
            bankId = bankBranch.bankId
        } else if (issueProcess.id == processTypeId) {
            if (!isPowerUser && !isConfigUser && !hasRemittanceUserRole)
                hasAccess = false
        } else if (purchaseProcess.id == processTypeId) {
            if (!isPowerUser && !isConfigUser && !hasRemittanceUserRole)
                hasAccess = false
            bankId = Tools.parseLongInput(params.bankId)
        }
        Map result = [bankId: bankId, branchId: branchId, hasAccess: hasAccess]
        return result
    }
}
