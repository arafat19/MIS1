package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.District
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search specific list of task for approve
 *  For details go through Use-Case doc named 'SearchTaskForApproveTaskActionService'
 */
class SearchTaskForApproveTaskActionService extends BaseService implements ActionIntf {

    RmsTaskService rmsTaskService
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

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load task"
    private static final String LST_TASK = "lstTasks"
    private static final String GRID_OBJ = "gridObj";
    private static final String ON = "on";

    /**
     * Do nothing for pre operation
     */
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get Task list through specific search
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initSearch(parameters)          // initialize parameters
            long exhHouseId = Long.parseLong(params.exhHouseId)
            long currentStatus = Long.parseLong(params.currentStatus)
            long paymentMethod = 0
                    boolean isRevised = false
            if(params.isRevised && (params.isRevised.toString().equals(ON))) {
                isRevised = true
            }
            long taskListId = Long.parseLong(params.taskListId)
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Date toDate = DateUtility.parseMaskedToDate(params.toDate)
            Map searchResult = rmsTaskService.searchTask(exhHouseId, currentStatus, paymentMethod, isRevised, taskListId, fromDate, toDate, this)
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
     * Wrap Task list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     */
    Object buildSuccessResultForUI(Object obj) {
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    Object buildFailureResultForUI(Object obj) {
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
            SystemEntity process = (SystemEntity) rmsProcessTypeCacheUtility.read(task.processTypeId)
            SystemEntity instrument = (SystemEntity) rmsInstrumentTypeCacheUtility.read(task.instrumentTypeId)

            Bank bank=(Bank)bankCacheUtility.read(task.mappingBankId)
            BankBranch branch=(BankBranch)bankBranchCacheUtility.read(task.mappingBranchId)
            District district=(District)districtCacheUtility.read(task.mappingDistrictId)

            GridEntity obj = new GridEntity()
            obj.id = task.id
            obj.cell = [
                    counter,
                    task.id,
                    task.refNo,
                    task.amount,
                    DateUtility.getLongDateForUI(task.createdOn),
                    task.beneficiaryName,
                    process.key,
                    instrument.key,
                    bank.name+Tools.EMPTY_SPACE_COMA+branch.name+Tools.EMPTY_SPACE_COMA+district.name
            ]
            lstWrappedTask << obj
            counter++
        }
        return lstWrappedTask
    }


}
