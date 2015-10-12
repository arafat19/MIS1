package com.athena.mis.arms.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Country
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.arms.utility.RmsPaymentMethodCacheUtility
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search beneficiary details
 *  For details go through Use-Case doc named 'SearchBeneficiaryDetailsActionService'
 */
class SearchBeneficiaryDetailsActionService extends BaseService implements ActionIntf {

    RmsTaskService rmsTaskService
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsPaymentMethodCacheUtility rmsPaymentMethodCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String BENEFICIARY_NOT_FOUND_MESSAGE = "Beneficiary not found"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to search beneficiary"
    private static final String LST_TASK = "lstTask"
    private static final String GRID_OBJ = "gridObj"
    private static final String INFO_MAP = "infoMap"

    private static final String BENEFICIARY_NAME = "beneficiaryName"
    private static final String BENEFICIARY_PHONE = "beneficiaryPhone"
    private static final String ACCOUNT_NO = "accountNo"
    private static final String BANK = "bank"
    private static final String EXCHANGE_HOUSE = "exchangeHouse"
    private static final String COUNTRY = "country"
    private static final String REF_NO = "refNo"
    private static final String CREATED_ON = "createdOn"
    private static final String AMOUNT = "amount"
    private static final String CURRENT_STATUS = "currentStatus"
    private static final String PAYMENT_METHOD = "paymentMethod"

    /**
     * Do nothing for pre operation
     */
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get parameters from UI and build Task object for select
     * 1. Check validity for input
     * 3. Get task object by beneficiaryName or accountNo
     * 2. Check existence of Task object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.rp) params.rp = 10
            if (!params.page) params.page = 1
            initPager(params)
            if (!params.propertyValue) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Date toDate = DateUtility.parseMaskedToDate(params.toDate)
            String searchField = params.propertyValue
            String searchFieldName = params.propertyName
            Map searchResult = rmsTaskService.searchTaskForBeneficiaryDetails(fromDate, toDate, searchFieldName, searchField, this)
            List<RmsTask> task = (List<RmsTask>) searchResult.listOfTasks
            Integer count = (Integer) searchResult.count
            if (count == 0) {
                result.put(Tools.MESSAGE, BENEFICIARY_NOT_FOUND_MESSAGE)
                return result
            }
            result.put(LST_TASK, task)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BENEFICIARY_NOT_FOUND_MESSAGE)
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
     * Build a map with Task object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */

    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(INFO_MAP, Tools.EMPTY_SPACE)
            result.put(GRID_OBJ, Tools.EMPTY_SPACE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            // cast map returned from execute method
            List<RmsTask> lstTasks = (List<RmsTask>) executeResult.get(LST_TASK)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedTask = wrapTask(lstTasks, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedTask]
            result.put(GRID_OBJ, gridObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
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
            if (!obj) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
                return result
            }
            Map previousResult = (Map) obj
            result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build map of beneficiary information
     * @param task -task object
     * @return -a map containing beneficiary information
     */
    private Map buildInfoMap(RmsTask task) {
        LinkedHashMap infoMap = new LinkedHashMap()
        RmsExchangeHouse exchangeHouse = (RmsExchangeHouse) rmsExchangeHouseCacheUtility.read(task.exchangeHouseId)
        Country country = (Country) countryCacheUtility.read(task.countryId)
        SystemEntity currentStatus = (SystemEntity) rmsTaskStatusCacheUtility.read(task.currentStatus)
        SystemEntity paymentMethod = (SystemEntity) rmsPaymentMethodCacheUtility.read(task.paymentMethod)

        infoMap.put(BENEFICIARY_NAME, task.beneficiaryName)
        infoMap.put(BENEFICIARY_PHONE, task.beneficiaryPhone)
        infoMap.put(ACCOUNT_NO, task.accountNo)
        infoMap.put(BANK, task.getFullOutletName())
        infoMap.put(EXCHANGE_HOUSE, exchangeHouse.name)
        infoMap.put(COUNTRY, country.name)
        infoMap.put(REF_NO, task.refNo)
        infoMap.put(CREATED_ON, DateUtility.getDateFormatAsString(task.createdOn))
        infoMap.put(AMOUNT, task.amount)
        infoMap.put(CURRENT_STATUS, currentStatus.key)
        infoMap.put(PAYMENT_METHOD, paymentMethod.key)
        return infoMap
    }

    /**
     * Wrap list of Task in grid entity
     * @param lstTasks -list of Task object(s)
     * @param start -starting index of the page
     * @return -list of wrapped Task
     */
    private List wrapTask(List<RmsTask> lstTasks, int start) {
        List lstWrappedTask = []
        int counter = start + 1
        for (int i = 0; i < lstTasks.size(); i++) {
            RmsTask task = lstTasks[i]
            RmsExchangeHouse exchangeHouse = (RmsExchangeHouse) rmsExchangeHouseCacheUtility.read(task.exchangeHouseId)
            Country country = (Country) countryCacheUtility.read(task.countryId)
            SystemEntity currentStatus = (SystemEntity) rmsTaskStatusCacheUtility.read(task.currentStatus)
            SystemEntity paymentMethod = (SystemEntity) rmsPaymentMethodCacheUtility.read(task.paymentMethod)
            GridEntity obj = new GridEntity()
            obj.id = task.id
            obj.cell = [
                    counter,
                    task.refNo,
                    task.beneficiaryName,
                    task.amount,
                    task.getFullOutletName()
            ]
            lstWrappedTask << obj
            counter++
        }
        return lstWrappedTask
    }
}
