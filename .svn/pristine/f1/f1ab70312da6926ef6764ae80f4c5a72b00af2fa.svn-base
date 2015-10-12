package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Get bank id of the user and return bank Id
 */
class SearchTaskDetailsForForwardActionService extends BaseService implements ActionIntf{

    RmsTaskService rmsTaskService
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String TASK_OBJECT = "taskObject"
    private static final String TASK_ID = "taskId"
    private static final String IS_FORWARDABLE = "isForwardable"
    private static final String NOT_FOUND = "Task not found within date range"
    private static final String NOT_CASH_COLLECTION = "Task payment method is not cash collection"
    private static final String LOAD_FAILED = "Failed to load task"

    /**
     * Check pre-condition for search task
     * 1. Task  must belong to this bank
     * 2. process-instrument should be Forward-Cash Collection
     * 3. mapping branch can be user's Branch or Any Branch
     * @param parameters - param from UI
     * @param obj - N/A
     * @return - map for execute
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.from_date || !params.to_date || !params.property_name || !params.property_value) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            Date fromDate = DateUtility.parseMaskedFromDate(params.from_date)
            Date toDate = DateUtility.parseMaskedToDate(params.to_date)
            String propertyName = params.property_name
            String propertyValue = params.property_value

            RmsTask rmsTask = getRmsTask(fromDate, toDate, propertyName, propertyValue)
            if (!rmsTask) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()

            // check if Task belongs to bank
            long userBranchId = rmsSessionUtil.appSessionUtil.getUserBankBranchId()
            BankBranch userBankBranch = (BankBranch) bankBranchCacheUtility.read(userBranchId)
            if (userBankBranch.bankId != rmsTask.mappingBankId) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }

            SystemEntity forwardObj = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(RmsProcessTypeCacheUtility.FORWARD, companyId)
            SystemEntity cashCollectionObj = (SystemEntity) rmsInstrumentTypeCacheUtility.readByReservedAndCompany(RmsInstrumentTypeCacheUtility.CASH_COLLECTION, companyId)
            SystemEntity statusApproved = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DECISION_APPROVED, companyId)

            // check if only cash collection task
            if ((rmsTask.processTypeId != forwardObj.id) || rmsTask.instrumentTypeId != cashCollectionObj.id) {
                result.put(Tools.MESSAGE, NOT_CASH_COLLECTION)
                return result
            }

            Boolean isForwardable = Boolean.TRUE      // default value
            if (rmsTask.currentStatus != statusApproved.id) {
                isForwardable = Boolean.FALSE
            }

            // check branch
            if (rmsTask.mappingBranchId != userBranchId) {   // task doesn't belongs to this branch
                // now check if belongs to ANY BRANCH (isGlobal)
                BankBranch mappingBranch = (BankBranch) bankBranchCacheUtility.read(rmsTask.mappingBranchId)
                if (!mappingBranch.isGlobal) {  // task  doesn't belongs to ANY Branch
                    isForwardable = Boolean.FALSE
                }
            }
            result.put(TASK_OBJECT, rmsTask)
            result.put(TASK_ID, rmsTask.id)
            result.put(IS_FORWARDABLE, isForwardable)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, LOAD_FAILED)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            RmsTask rmsTask = (RmsTask) preResult.get(TASK_OBJECT)
            long taskId = (long) preResult.get(TASK_ID)
            Boolean isForwardable = (Boolean) preResult.get(IS_FORWARDABLE)
            result.put(TASK_OBJECT, rmsTask)
            result.put(TASK_ID, taskId)
            result.put(IS_FORWARDABLE, isForwardable)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, LOAD_FAILED)
            return result
        }
    }

    /**
     * do nothing
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * build failure result
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            String msg = preResult.get(Tools.MESSAGE)
            if (msg) {
                result.put(Tools.MESSAGE, msg)
            } else {
                result.put(Tools.MESSAGE, LOAD_FAILED)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, LOAD_FAILED)
            return result
        }
    }

    /**
     * find the rmsTask
     * @return - RmsTask obj
     */
    private RmsTask getRmsTask(Date fromDate, Date toDate, String propertyName, String propertyValue) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        List<RmsTask> lstRmsTask = RmsTask.withCriteria {
            ilike(propertyName, propertyValue)
            between('createdOn', fromDate, toDate)
            eq('companyId', companyId)
            setReadOnly(true)
        }
        if (lstRmsTask.size() > 0) {
            return lstRmsTask[0]
        }
        return null
    }
}
