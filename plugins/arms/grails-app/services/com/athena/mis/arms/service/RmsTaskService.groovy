package com.athena.mis.arms.service

import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.model.WrapBankDepositTask
import com.athena.mis.arms.model.WrapCashCollectionTask
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.springframework.beans.factory.annotation.Autowired

/**
 * RmsTaskService is used to handle only CRUD related object manipulation (e.g. read, create, update, delete)
 */
class RmsTaskService extends BaseService {

    RmsTaskTraceService rmsTaskTraceService
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil

    private static final String SORT_BY_REF_NO = "refNo"
    private static final String SORT_BY_BENEFICIARY_NAME = "beneficiaryName"

    /**
     * get count of total task
     * @return int -count of total task
     */
    public int countByCompanyIdAndCurrentStatus(long companyId, long status) {
        int count = RmsTask.countByCompanyIdAndCurrentStatus(companyId, status)
        return count
    }

    public List<RmsTask> findAllByIdInList(List<Long> lstIds) {
        List<RmsTask> lstRmsTask = RmsTask.findAllByIdInList(lstIds, [readOnly: true])
        return lstRmsTask
    }

    /**
     * get list and count of task by current status
     * @param exchangeHouseId
     * @param currentStatus
     * @param isRevised
     * @param taskListId
     * @param fromDate
     * @param toDate
     * @param baseService
     * @return -map of task list and count
     */
    public Map listTaskByStatus(long exchangeHouseId, long currentStatus, long paymentMethod, boolean isRevised, long taskListId, Date fromDate, Date toDate, BaseService baseService) {
        List<RmsTask> listOfTasks = RmsTask.withCriteria {
            eq('exchangeHouseId', exchangeHouseId)
            eq('currentStatus', currentStatus)
            eq('isRevised', isRevised)
            eq('taskListId', taskListId)
            between('createdOn', fromDate, toDate)
            if (paymentMethod > 0) {
                eq('paymentMethod', paymentMethod)
            }
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(SORT_BY_REF_NO, ASCENDING_SORT_ORDER)
            setReadOnly(true)
        }
        List counts = RmsTask.withCriteria {
            eq('exchangeHouseId', exchangeHouseId)
            eq('currentStatus', currentStatus)
            eq('isRevised', isRevised)
            eq('taskListId', taskListId)
            between('createdOn', fromDate, toDate)
            if (paymentMethod > 0) {
                eq('paymentMethod', paymentMethod)
            }
            projections { rowCount() }
        }
        int total = counts[0]
        return [listOfTasks: listOfTasks, count: total]
    }

    /**
     * get list and count of task for download
     * @param exchangeHouseId
     * @param currentStatus
     * @param isRevised
     * @param taskListId
     * @param processTypeId
     * @param instrumentTypeId
     * @param fromDate
     * @param toDate
     * @param baseService
     * @return -map of task list and count
     */
    public Map listTaskByStatusForProcessInstrument(Long bankId, Long exchangeHouseId, Long currentStatus, Long taskListId, Long processTypeId, Long instrumentTypeId, Date fromDate, Date toDate, Long branchId, BaseService baseService) {
        List<RmsTask> listOfTasks = RmsTask.withCriteria {
            eq('exchangeHouseId', exchangeHouseId)
            eq('currentStatus', currentStatus)
            eq('isRevised', false)
            eq('taskListId', taskListId)
            if (processTypeId > 0)
                eq('processTypeId', processTypeId)
            if (instrumentTypeId > 0)
                eq('instrumentTypeId', instrumentTypeId)
            if (bankId > 0)
                eq('mappingBankId', bankId)
            if (branchId > 0)
                eq('mappingBranchId', branchId)
            between('createdOn', fromDate, toDate)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(SORT_BY_REF_NO, ASCENDING_SORT_ORDER)
            setReadOnly(true)
        }
        List counts = RmsTask.withCriteria {
            eq('exchangeHouseId', exchangeHouseId)
            eq('currentStatus', currentStatus)
            eq('isRevised', false)
            eq('taskListId', taskListId)
            if (processTypeId)
                eq('processTypeId', processTypeId)
            if (instrumentTypeId)
                eq('instrumentTypeId', instrumentTypeId)
            if (bankId > 0)
                eq('mappingBankId', bankId)
            if (branchId > 0)
                eq('mappingBranchId', branchId)
            between('createdOn', fromDate, toDate)
            projections { rowCount() }
        }
        int total = counts[0]
        return [listOfTasks: listOfTasks, count: total]
    }

    private static final String QUERY_FOR_APPLY_TO_ALL = """
        SELECT id
        FROM rms_task
        WHERE exchange_house_id = :exchangeHouseId
        AND current_status = :currentStatus
        AND created_on BETWEEN :fromDate AND :toDate
        AND is_revised = :isRevised
        AND task_list_id = 0
    """

    /**
     * get list of taskId by exchangeHouseId, currentStatus, fromDate, toDate
     * @param exchangeHouseId
     * @param currentStatus
     * @param fromDate
     * @param toDate
     * @param isRevised
     * @return list -list of taskId
     */
    public List getTaskIds(long exchangeHouseId, long currentStatus, Date fromDate, Date toDate, boolean isRevised) {
        Map queryParams = [
                exchangeHouseId: exchangeHouseId,
                currentStatus: currentStatus,
                fromDate: DateUtility.getSqlFromDateWithSeconds(fromDate),
                toDate: DateUtility.getSqlToDateWithSeconds(toDate),
                isRevised: isRevised
        ]
        List lstTaskIds = executeSelectSql(QUERY_FOR_APPLY_TO_ALL, queryParams)
        return lstTaskIds
    }

    /**
     * Get task object by Id
     * @param id -Id of task
     * @return -object of task
     */
    public RmsTask read(long id) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        RmsTask task = RmsTask.findByIdAndCompanyId(id, companyId, [readOnly: true])
        return task
    }

    /**
     * get list and count of task for specific search
     * @param exchangeHouseId
     * @param currentStatus
     * @param isRevised
     * @param taskListId
     * @param fromDate
     * @param toDate
     * @param baseService
     * @return -map of task list and count
     */
    public Map searchTask(long exchangeHouseId, long currentStatus, long paymentMethod, boolean isRevised, long taskListId, Date fromDate, Date toDate, BaseService baseService) {
        List<RmsTask> listOfTasks = RmsTask.withCriteria {
            eq('exchangeHouseId', exchangeHouseId)
            eq('currentStatus', currentStatus)
            eq('isRevised', isRevised)
            eq('taskListId', taskListId)
            between('createdOn', fromDate, toDate)
            if (paymentMethod > 0) {
                eq('paymentMethod', paymentMethod)
            }
            ilike(baseService.queryType, Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(SORT_BY_REF_NO, ASCENDING_SORT_ORDER)
            setReadOnly(true)
        }
        List counts = RmsTask.withCriteria {
            eq('exchangeHouseId', exchangeHouseId)
            eq('currentStatus', currentStatus)
            eq('isRevised', isRevised)
            eq('taskListId', taskListId)
            between('createdOn', fromDate, toDate)
            if (paymentMethod > 0) {
                eq('paymentMethod', paymentMethod)
            }
            ilike(baseService.queryType, Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE)
            projections { rowCount() }
        }
        int total = counts[0]
        return [listOfTasks: listOfTasks, count: total]
    }

    /**
     * get list and count of task for specific search for download
     * @param exchangeHouseId
     * @param currentStatus
     * @param isRevised
     * @param taskListId
     * @param fromDate
     * @param toDate
     * @param baseService
     * @return -map of task list and count
     */
    public Map searchTaskForProcessInstrument(Long bankId, Long exchangeHouseId, Long currentStatus, Long taskListId, Long processTypeId, Long instrumentTypeId, Date fromDate, Date toDate, Long branchId, BaseService baseService) {
        List<RmsTask> listOfTasks = RmsTask.withCriteria {
            eq('exchangeHouseId', exchangeHouseId)
            eq('currentStatus', currentStatus)
            eq('isRevised', false)
            eq('taskListId', taskListId)
            if (processTypeId > 0)
                eq('processTypeId', processTypeId)
            if (instrumentTypeId > 0)
                eq('instrumentTypeId', instrumentTypeId)
            if (bankId > 0)
                eq('mappingBankId', bankId)
            if (branchId > 0)
                eq('mapping_branch_id', branchId)
            between('createdOn', fromDate, toDate)
            ilike(baseService.queryType, Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(SORT_BY_REF_NO, ASCENDING_SORT_ORDER)
            setReadOnly(true)
        }
        List counts = RmsTask.withCriteria {
            eq('exchangeHouseId', exchangeHouseId)
            eq('currentStatus', currentStatus)
            eq('isRevised', false)
            eq('taskListId', taskListId)
            if (processTypeId)
                eq('processTypeId', processTypeId)
            if (instrumentTypeId)
                eq('instrumentTypeId', instrumentTypeId)
            if (bankId > 0)
                eq('mappingBankId', bankId)
            if (branchId > 0)
                eq('mapping_branch_id', branchId)
            between('createdOn', fromDate, toDate)
            ilike(baseService.queryType, Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE)
            projections { rowCount() }
        }
        int total = counts[0]
        return [listOfTasks: listOfTasks, count: total]
    }

    public Map searchTaskForBeneficiaryDetails(Date fromDate, Date toDate, String searchFieldName, String searchField, BaseService baseService) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        List<Long> lstValidStatus = rmsTaskStatusCacheUtility.listAllValidTaskStatusIds()

        List<RmsTask> listOfTasks = RmsTask.withCriteria {
            eq('companyId', companyId)
            'in'('currentStatus', lstValidStatus)
            between('createdOn', fromDate, toDate)
            ilike(searchFieldName, Tools.PERCENTAGE + searchField + Tools.PERCENTAGE)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(SORT_BY_BENEFICIARY_NAME, ASCENDING_SORT_ORDER)
            setReadOnly(true)
        }
        List counts = RmsTask.withCriteria {
            eq('companyId', companyId)
            between('createdOn', fromDate, toDate)
            ilike(searchFieldName, Tools.PERCENTAGE + searchField + Tools.PERCENTAGE)
            projections { rowCount() }
        }
        int total = counts[0]
        return [listOfTasks: listOfTasks, count: total]
    }

    /**
     * Save task object into DB
     * @param task -task object
     * @return -saved task object
     */
    public RmsTask create(RmsTask task) {
        RmsTask savedTask = task.save(validate: false)  // restrict to double check refNo/PIN no duplicate
        return savedTask
    }

    private static final String UPDATE_QUERY = """
        UPDATE rms_task SET
            version=:newVersion,
            ref_no=:refNo,
            amount=:amount,
            value_date=:valueDate,
            beneficiary_name=:beneficiaryName,
            beneficiary_address=:beneficiaryAddress,
            beneficiary_phone=:beneficiaryPhone,
            account_no=:accountNo,
            outlet_bank=:outletBank,
            outlet_branch=:outletBranch,
            outlet_district=:outletDistrict,
            pin_no=:pinNo,
            identity_type=:identityType,
            identity_no=:identityNo,
            sender_name=:senderName,
            sender_mobile=:senderMobile,
            amount_in_local_currency=:amountInLocalCurrency,
            local_currency_id=:localCurrencyId,
            payment_method=:paymentMethod,
            exchange_house_id=:exchangeHouseId,
            country_id=:countryId
        WHERE
            id=:id AND
            version=:version
    """
    /**
     * Update task object into DB
     * @param task -task object
     * @return -an integer containing the value of update count
     */
    public int update(RmsTask rmsTask) {
        Map queryParams = [
                id: rmsTask.id,
                newVersion: rmsTask.version + 1,
                version: rmsTask.version,
                refNo: rmsTask.refNo,
                amount: rmsTask.amount,
                valueDate: DateUtility.getSqlDate(rmsTask.valueDate),
                beneficiaryName: rmsTask.beneficiaryName,
                beneficiaryAddress: rmsTask.beneficiaryAddress,
                beneficiaryPhone: rmsTask.beneficiaryPhone,
                accountNo: rmsTask.accountNo,
                outletBank: rmsTask.outletBank,
                outletBranch: rmsTask.outletBranch,
                outletDistrict: rmsTask.outletDistrict,
                pinNo: rmsTask.pinNo,
                identityType: rmsTask.identityType,
                identityNo: rmsTask.identityNo,
                senderName: rmsTask.senderName,
                senderMobile: rmsTask.senderMobile,
                amountInLocalCurrency: rmsTask.amountInLocalCurrency,
                localCurrencyId: rmsTask.localCurrencyId,
                paymentMethod: rmsTask.paymentMethod,
                exchangeHouseId: rmsTask.exchangeHouseId,
                countryId: rmsTask.countryId

        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update task information')
        }
        return updateCount;
    }

    private static final String DELETE_QUERY = """
        DELETE FROM rms_task
        WHERE
            id=:id
    """
    /**
     * Delete task object from DB
     * @param id -id of task object
     * @return -an integer containing the value of delete count
     */
    public int delete(long id) {
        Map queryParams = [
                id: id
        ]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete task information')
        }
        return deleteCount
    }

    /**
     * Update task object into DB
     * @param task -task object
     * @return -an integer containing the value of update count
     */
    public int updateForIncludedInList(List<Long> lstTaskIds, long currentStatus, boolean isRevised, long taskListId, String revisionNote) {
        String lstIds = Tools.buildCommaSeparatedStringOfIds(lstTaskIds)
        String queryStr = """
            UPDATE rms_task SET
                version = version+1,
                previous_status = current_status,
                current_status = :currentStatus,
                task_list_id=:taskListId,
                is_revised = :isRevised,
                revision_note = :revisionNote
            WHERE
                id IN (${lstIds})
        """
        Map queryParams = [
                currentStatus: currentStatus,
                taskListId: taskListId,
                isRevised: isRevised,
                revisionNote: revisionNote
        ]
        int updateCount = executeUpdateSql(queryStr, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update task information for included in list')
        }
        return updateCount
    }
    /**
     * common update task object into DB
     * @param task -task object
     * @return -an integer containing the value of update count
     */
    public int updateRmsTaskStatus(List<Long> lstTaskIds, long currentStatus, boolean isRevised, String revisionNote) {
        String lstIds = Tools.buildCommaSeparatedStringOfIds(lstTaskIds)
        String queryStr = """
            UPDATE rms_task SET
                version = version+1,
                previous_status = current_status,
                current_status = :currentStatus,
                is_revised = :isRevised,
                revision_note = :revisionNote
            WHERE
                id IN (${lstIds})
        """
        Map queryParams = [
                currentStatus: currentStatus,
                isRevised: isRevised,
                revisionNote: revisionNote
        ]
        int updateCount = executeUpdateSql(queryStr, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update task information for included in list')
        }
        return updateCount
    }

    /**
     * update task object for disburse
     * @param task -task object
     * @return -an integer containing the value of update count
     */
    public int updateRmsTaskForDisburse(RmsTask rmsTask) {
        String queryStr = """
            UPDATE rms_task SET
                version = version+1,
                previous_status = :previousStatus,
                current_status = :currentStatus,
                identity_type = :identityType,
                identity_no = :identityNo,
                mapping_branch_id = :mappingBranchId,
                mapping_district_id = :mappingDistrictId,
                is_revised = false
            WHERE
                id = :id AND
                version = :version
        """
        Map queryParams = [
                currentStatus: rmsTask.currentStatus,
                previousStatus: rmsTask.previousStatus,
                identityType: rmsTask.identityType,
                identityNo: rmsTask.identityNo,
                mappingBranchId: rmsTask.mappingBranchId,
                mappingDistrictId: rmsTask.mappingDistrictId,
                id: rmsTask.id,
                version: rmsTask.version
        ]
        int updateCount = executeUpdateSql(queryStr, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update task information for disburse')
        }
        return updateCount
    }

    /**
     * update task object for disburse
     * @param task -task object
     * @return -an integer containing the value of update count
     */
    public int updateRmsTaskForSentToBank(List<Long> lstTaskIds, long newTaskStatus) {
        String strIds = Tools.buildCommaSeparatedStringOfIds(lstTaskIds)
        String queryStr = """
            UPDATE rms_task SET
                version = version+1,
                previous_status = current_status,
                current_status = :newTaskStatus
            WHERE
                id IN (${strIds})
        """
        Map queryParams = [newTaskStatus: newTaskStatus]
        int updateCount = executeUpdateSql(queryStr, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update task information for disburse')
        }
        return updateCount
    }

    /**
     * Get total amount of selected task
     * @param lstTaskIds
     * @return -list of total amount
     */
    public double getSelectedTasksAmount(List<Long> lstTaskIds) {
        String lstIds = Tools.buildCommaSeparatedStringOfIds(lstTaskIds)
        String queryStr = """
            SELECT SUM(amount) amount
            FROM rms_task
            WHERE id IN (${lstIds})
        """
        List amount = executeSelectSql(queryStr)
        return amount[0].amount
    }

    /**
     * Update task object for map into DB
     * @param lstTaskIds
     * @param processTypeId
     * @param instrumentTypeId
     * @param bank
     * @param district
     * @param branch
     * @return -an integer containing the value of update count
     */
    public int updateForDecisionTaken(List<Long> lstTaskIds, currentStatus, long processTypeId, long instrumentTypeId, long bank, long district, long branch) {
        String lstIds = Tools.buildCommaSeparatedStringOfIds(lstTaskIds)
        String queryStr = """
            UPDATE rms_task SET
                version = version+1,
                previous_status = current_status,
                current_status = :currentStatus,
                process_type_id = :processTypeId,
                instrument_type_id = :instrumentTypeId,
                mapping_bank_id = :mappingBankId,
                mapping_district_id = :mappingDistrictId,
                mapping_branch_id = :mappingBranchId,
                mapped_on = :mappedOn,
                is_revised = false
            WHERE
                id IN (${lstIds})
        """
        Map queryParams = [
                currentStatus: currentStatus,
                processTypeId: processTypeId,
                instrumentTypeId: instrumentTypeId,
                mappingBankId: bank,
                mappingDistrictId: district,
                mappingBranchId: branch,
                mappedOn: DateUtility.getSqlDateWithSeconds(new Date())
        ]
        int updateCount = executeUpdateSql(queryStr, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update task information for map')
        }
        return updateCount
    }

    public List<RmsTask> findAllByRefNoInList(List lstRefNo) {
        List<RmsTask> lstRmsTask = RmsTask.findAllByRefNoInList(lstRefNo, [readOnly: true])
        return lstRmsTask
    }
    public List<RmsTask> findAllByPinNoInListAndExchangeHouseId(List lstRefNo, long exhId) {
        List<RmsTask> lstRmsTask = RmsTask.findAllByRefNoInListAndExchangeHouseId(lstRefNo, exhId, [readOnly: true])
        return lstRmsTask
    }

    public boolean saveBankDepositBulkTasks(List<WrapBankDepositTask> lstTask, long status) {
        Long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        try {
            for (int i = 0; i < lstTask.size(); i++) {
                RmsTask task = new RmsTask()
                task.beneficiaryName = lstTask[i].beneficiaryName
                task.beneficiaryPhone = lstTask[i].beneficiaryPhone
                task.accountNo = lstTask[i].accountNumber
                task.outletBank = lstTask[i].outletName
                task.outletBranch = lstTask[i].outletBranchName
                task.outletDistrict = lstTask[i].outletDistrictName
                task.senderName = lstTask[i].senderName
                task.refNo = lstTask[i].transactionRefNo      // prefix set in wrapper class
                task.currencyId = lstTask[i].currencyId
                task.amount = lstTask[i].amount
                task.exchangeHouseId = lstTask[i].exHouseId
                task.countryId = lstTask[i].countryId
                task.valueDate = DateUtility.parseMaskedDate(lstTask[i].taskValueDate)
                task.paymentMethod = lstTask[i].paymentMethod
                task.instrumentTypeId = 0L
                task.currentStatus = status
                task.previousStatus = 0L
                task.isRevised = false
                task.createdOn = new Date()
                task.companyId = companyId
                task.commissionDetailsId = 0L
                create(task)
                rmsTaskTraceService.create(task)                         // save task trace obj
            }
            return true
        }
        catch (Exception e) {
            throw new RuntimeException(e)
        }
    }

    public boolean saveCashCollectionBulkTasks(List<WrapCashCollectionTask> tasks, long status) {
        Long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        try {
            for (int i = 0; i < tasks.size(); i++) {
                RmsTask task = new RmsTask()
                task.beneficiaryName = tasks[i].beneficiaryName
                task.beneficiaryPhone = tasks[i].beneficiaryPhone
                task.outletBank = tasks[i].outletName
                task.outletBranch = tasks[i].outletBranchName
                task.outletDistrict = tasks[i].outletDistrictName
                task.pinNo = tasks[i].pinNo
                task.identityType = tasks[i].identityType
                task.identityNo = tasks[i].identityNo
                task.senderName = tasks[i].senderName
                task.refNo = tasks[i].transactionRefNo
                task.currencyId = tasks[i].currencyId
                task.amount = tasks[i].amount
                task.exchangeHouseId = tasks[i].exHouseId
                task.countryId = tasks[i].countryId
                task.valueDate = DateUtility.parseMaskedDate(tasks[i].taskValueDate)
                task.paymentMethod = tasks[i].paymentMethod
                task.currentStatus = status
                task.instrumentTypeId = 0L
                task.previousStatus = 0L
                task.isRevised = false
                task.createdOn = new Date()
                task.companyId = companyId
                task.commissionDetailsId = 0L
                create(task)
                rmsTaskTraceService.create(task)                         // save task trace obj
            }
            return true
        }
        catch (Exception e) {
            throw new RuntimeException(e)
        }
    }

    public Map listTaskForSendToBranch(long exchangeHouseId, long currentStatus, long branchId, long taskListId, Date fromDate, Date toDate, long processTypeId, BaseService baseService) {
        List<RmsTask> listOfTasks = RmsTask.withCriteria {
            eq('exchangeHouseId', exchangeHouseId)
            eq('currentStatus', currentStatus)
            eq('mappingBranchId', branchId)
            eq('taskListId', taskListId)
            eq('processTypeId', processTypeId)
            between('createdOn', fromDate, toDate)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            setReadOnly(true)
        }
        List counts = RmsTask.withCriteria {
            eq('exchangeHouseId', exchangeHouseId)
            eq('currentStatus', currentStatus)
            eq('mappingBranchId', branchId)
            eq('taskListId', taskListId)
            eq('processTypeId', processTypeId)
            between('createdOn', fromDate, toDate)
            projections { rowCount() }
        }
        int total = counts[0]
        return [listOfTasks: listOfTasks, count: total]
    }


    public List<RmsTask> findAllByCurrentStatusAndIdInList(long currentStatus, List lstTaskIds) {
        List<RmsTask> lstRmsTask = RmsTask.findAllByCurrentStatusAndIdInList(currentStatus, lstTaskIds, [readOnly: true])
        return lstRmsTask
    }
    private static final String QUERY_FOR_FORWARD = """
          UPDATE rms_task SET
                version = version+1,
                mapping_bank_id = :mappingBankId,
                mapping_district_id = :mappingDistrictId,
                mapping_branch_id = :mappingBranchId
            WHERE
                id = :taskId
    """

    public int updateTaskForForward(RmsTask rmsTask, long bankId, long districtId, long branchId) {
        Map queryParams = [
                mappingBankId: bankId,
                mappingDistrictId: districtId,
                mappingBranchId: branchId,
                taskId: rmsTask.id
        ]
        int updateCount = executeUpdateSql(QUERY_FOR_FORWARD, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update task information for forward')
        }
        return updateCount
    }

    private static final String COUNT_BY_REF_NO_FOR_CREATE = """
        SELECT COUNT(id)
        FROM rms_task
        WHERE ref_no ilike :refNo AND
        company_id=:companyId
    """

    public int countByRefNoAndCompanyId(RmsTask rmsTask) {

        Map queryParams = [
                refNo: rmsTask.refNo,
                companyId: rmsTask.companyId
        ]
        List<GroovyRowResult> results = executeSelectSql(COUNT_BY_REF_NO_FOR_CREATE, queryParams)
        int count = (int) results[0][0]
        return count
    }

    public int countByPinNoAndExchangeHouseId(RmsTask task) {
        int count = RmsTask.countByPinNoAndExchangeHouseId(task.pinNo, task.exchangeHouseId)
        return count
    }

    private static final String COUNT_BY_REF_NO_FOR_UPDATE = """
        SELECT COUNT(id)
        FROM rms_task
        WHERE ref_no ilike :refNo AND
        company_id=:companyId
        AND id<> :id
    """

    public int countByRefNoForUpdate(RmsTask rmsTask) {
        Map queryParams = [
                refNo: rmsTask.refNo,
                id: rmsTask.id,
                companyId: rmsTask.companyId
        ]
        List<GroovyRowResult> results = (List<GroovyRowResult>) executeSelectSql(COUNT_BY_REF_NO_FOR_UPDATE, queryParams)
        int count = (int) results[0][0]
        return count
    }

    public int countByPinNoAndExchangeHouseIdAndIdNotEqual(RmsTask rmsTask) {
        int count = RmsTask.countByPinNoAndExchangeHouseIdAndIdNotEqual(rmsTask.pinNo, rmsTask.exchangeHouseId, rmsTask.id)
        return count
    }

    public int countByExchangeHouseId(long exhHouseId) {
        int count = RmsTask.countByExchangeHouseId(exhHouseId)
        return count
    }

    public int updateTaskCommissionDetailsId(long rmsTaskId, long commissionDetailsId) {
        String query = """
          UPDATE rms_task SET
                commission_details_id=:commissionDetailsId
            WHERE
                id = :taskId
        """
        Map queryParams = [
                commissionDetailsId: commissionDetailsId,
                taskId: rmsTaskId
        ]
        int updateCount = executeUpdateSql(query, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update task commission details id')
        }
        return updateCount
    }

}
