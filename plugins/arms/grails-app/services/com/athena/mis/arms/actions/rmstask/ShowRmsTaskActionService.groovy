package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Country
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.arms.utility.RmsPaymentMethodCacheUtility
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
 * Get list of Task
 * For details go through Use-Case doc named 'ShowRmsTaskActionService'
 */
class ShowRmsTaskActionService extends BaseService implements ActionIntf {

    RmsTaskService rmsTaskService
    @Autowired
    RmsPaymentMethodCacheUtility rmsPaymentMethodCacheUtility
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    RmsPaymentMethodCacheUtility rmsPaymentMethodCacheUtility1

    private final Logger log = Logger.getLogger(getClass())

    private static final String LST_TASK = "lstTasks"
    private static final String DEFAULT_FAILURE_MSG_SHOW_TASK = "Failed to load task page"
    private static final String GRID_OBJ = "gridObj"
    private static final String CURRENT_DATE = "currentDate"
    private static final String COUNTRY_NAME = "countryName"
    private static final String EXH_NAME = "exhName"
    private static final String HAS_NAV_TASK_ID = "hasNavTaskId"

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
     * 1. Get Task list for grid
     * 2. Get count of total Task
     * 3. Get BankDepositId and CashCollectionId by ReservedId
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap();
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameterMap)                // initialize parameters

            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity newTaskObj = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.NEW_TASK, companyId)
            SystemEntity pendingTaskObj = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.PENDING_TASK, companyId)
            long status = newTaskObj.id
            boolean isExhUser = false
            if (parameterMap.isExhUser) {
                isExhUser = true
                status = pendingTaskObj.id
                RmsExchangeHouse rmsExchangeHouse = (RmsExchangeHouse) rmsExchangeHouseCacheUtility.read(rmsSessionUtil.getUserExchangeHouseId())
                Country country = (Country) countryCacheUtility.read(rmsExchangeHouse.countryId)
                result.put(COUNTRY_NAME, country.name)
                result.put(EXH_NAME, rmsExchangeHouse.name)
            }
            long taskId=0L
            result.put(HAS_NAV_TASK_ID,Boolean.FALSE)
            if(parameterMap.navTaskId){
                taskId= Long.parseLong(parameterMap.navTaskId)
                RmsTask rmsTask= rmsTaskService.read(taskId)
                result.put(Tools.ENTITY,rmsTask)
                result.put(HAS_NAV_TASK_ID,Boolean.TRUE)
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                return result
            }
            List<GroovyRowResult> lstTasks = list(this, isExhUser, status)          // get list of Task from DB
            int count = rmsTaskService.countByCompanyIdAndCurrentStatus(companyId, status)  // get count of total Task from DB
            result.put(LST_TASK, lstTasks)
            result.put(Tools.COUNT, count.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_TASK)
            return result
        }
    }

    /**
     * 1. Wrap Task list for grid
     * 2. Put BankDepositId and CashCollectionId in map
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            List lstWrappedTask
            int count=0
            Map executeResult = (Map) obj                   // cast map returned from execute method
            Boolean navTaskId=(Boolean) executeResult.get(HAS_NAV_TASK_ID)
            if(navTaskId){
                RmsTask rmsTask=(RmsTask)executeResult.get(Tools.ENTITY)
                lstWrappedTask = wrapSingleTask(rmsTask, start)
                count=1
            }else {
                List<GroovyRowResult> lstTasks = (List<GroovyRowResult>) executeResult.get(LST_TASK)
                count = (int) executeResult.get(Tools.COUNT)
                lstWrappedTask = wrapTask(lstTasks, start)
            }

            String countryName = executeResult.get(COUNTRY_NAME)
            String exhName = executeResult.get(EXH_NAME)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedTask]
            result.put(GRID_OBJ, gridObj)
            String currentDate = DateUtility.getDateForUI(new Date())
            if(countryName && exhName) {
                result.put(COUNTRY_NAME, countryName)
                result.put(EXH_NAME, exhName)
            }
            result.put(CURRENT_DATE, currentDate)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_TASK)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_TASK)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_TASK)
            return result
        }
    }

    /**
     * Wrap list of Task in grid entity
     * @param lstTasks -list of Task object(s)
     * @param start -starting index of the page
     * @return -list of wrapped Task
     */
    private List wrapTask(List<GroovyRowResult> lstTasks, int start) {
        List lstWrappedTask = []
        int counter = start + 1
        for (int i = 0; i < lstTasks.size(); i++) {
            GroovyRowResult groovyRowResult = lstTasks[i]
            GridEntity obj = new GridEntity()
            obj.id = groovyRowResult.id
            obj.cell = [
                    counter,
                    groovyRowResult.id,
                    groovyRowResult.ref_no,
                    groovyRowResult.amount,
                    DateUtility.getLongDateForUI(groovyRowResult.value_date),
                    groovyRowResult.beneficiary_name,
                    groovyRowResult.outlet,
                    groovyRowResult.payment_method,
                    DateUtility.getLongDateForUI(groovyRowResult.created_on),
                    groovyRowResult.exchange_house
            ]
            lstWrappedTask << obj
            counter++
        }
        return lstWrappedTask
    }

    private List<RmsTask> wrapSingleTask(RmsTask rmsTask, int start) {
        List<RmsTask> lstWrappedTask = []
        int counter = start + 1
        SystemEntity paymentMethod= (SystemEntity) rmsPaymentMethodCacheUtility.read(rmsTask.paymentMethod)
        RmsExchangeHouse exhHouse= (RmsExchangeHouse) rmsExchangeHouseCacheUtility.read(rmsTask.exchangeHouseId)
        GridEntity obj = new GridEntity()
        obj.id = rmsTask.id
        obj.cell = [
                counter,
                rmsTask.id,
                rmsTask.refNo,
                rmsTask.amount,
                DateUtility.getLongDateForUI(rmsTask.valueDate),
                rmsTask.beneficiaryName,
                rmsTask.getFullOutletName(),
                paymentMethod.value,
                DateUtility.getLongDateForUI(rmsTask.createdOn),
                exhHouse.name
        ]
        lstWrappedTask << obj
    }

    /**
     * Get list of Task object
     * @param baseService
     * @return -list of Task list
     */
    private List<GroovyRowResult> list(BaseService baseService, boolean isExhUser, long status) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        String strExh = Tools.EMPTY_SPACE
        if (isExhUser) {
            long exhId = rmsSessionUtil.getUserExchangeHouseId()
            strExh = "AND task.exchange_house_id = ${exhId}"
        }
        String QUERY_STR = """
            SELECT task.id, task.version, task.ref_no, task.amount, task.value_date, task.beneficiary_name,
                    task.outlet_bank || ', ' || task.outlet_branch || ', ' || task.outlet_district as outlet, payMethod.key payment_method,
                    task.created_on, exchangeHouse.name exchange_house
            FROM rms_task task
                LEFT JOIN rms_exchange_house exchangeHouse ON task.exchange_house_id = exchangeHouse.id
                LEFT JOIN system_entity payMethod ON task.payment_method = payMethod.id
            WHERE task.task_list_id = 0
                AND task.company_id = :companyId
                AND task.current_status = :status
                ${strExh}
            ORDER BY ref_no
            LIMIT :resultPerPage OFFSET :start
        """
        Map queryParams = [
                companyId: companyId,
                status: status,
                resultPerPage: baseService.resultPerPage,
                start: baseService.start
        ]
        List<GroovyRowResult> lstTasks = executeSelectSql(QUERY_STR, queryParams)
        return lstTasks
    }
}
