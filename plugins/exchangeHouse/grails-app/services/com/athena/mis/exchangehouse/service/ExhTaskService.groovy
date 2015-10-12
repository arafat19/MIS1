package com.athena.mis.exchangehouse.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.utility.ExhPaymentMethodCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

class ExhTaskService extends BaseService {
    static transactional = false

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility

    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * 1. generate task ref no together company code & task.id
     * 2. generate unique pin no if task payment method type cash collection
     * Save customer object into DB
     * @param customer -ExhCustomer object
     * @return -saved task object
     */
    public ExhTask create(ExhTask task) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)

        task.id = getNextVal()
        task.version = 0
        Company company = (Company) companyCacheUtility.read(companyId)
        task.refNo = company.code + task.id.toString()

        if ((task.paymentMethod == exhPaymentMethodCashObj.id)) {
            task.pinNo = company.code + new Date().format('ddMMHHssSSS')
        }

        String query = """
            INSERT INTO exh_task (
                id,
                version,
                amount_in_foreign_currency,
                amount_in_local_currency,
                beneficiary_id,
                beneficiary_name,
                conversion_rate,
                created_on,
                current_status,
                customer_id,
                customer_name,
                discount,
                from_currency_id,
                to_currency_id,
                paid_by,
                paid_by_no,
                payment_method,
                outlet_bank_id,
                outlet_district_id,
                outlet_branch_id,
                pin_no,
                ref_no,
                regular_fee,
                remittance_purpose,
                company_id,
                agent_id,
                user_id,
                approved_by,
                task_type_id,
                commission,
				is_gateway_payment_done,
				exh_gain,
                refund_task_id
            ) VALUES(
                ${task.id},
                ${task.version},
                ${task.amountInForeignCurrency},
                ${task.amountInLocalCurrency},
                ${task.beneficiaryId},
                '${task.beneficiaryName}',
                ${task.conversionRate},
                '${DateUtility.getDBDateFormatWithSecond(task.createdOn)}',
                ${task.currentStatus},
                ${task.customerId},
                '${task.customerName}',
                ${task.discount},
                ${task.fromCurrencyId},
                ${task.toCurrencyId},
                ${task.paidBy},
                :paidByNo,
                ${task.paymentMethod},
                ${task.outletBankId},
                ${task.outletDistrictId},
                ${task.outletBranchId},
                :pinNo,
                '${task.refNo}',
                ${task.regularFee},
                ${task.remittancePurpose},
                ${task.companyId},
                ${task.agentId},
                ${task.userId},
                ${task.approvedBy},
                ${task.taskTypeId},
                ${task.commission},
                ${task.isGatewayPaymentDone},
				${task.exhGain},
				${task.refundTaskId}
            )
        """
        Map queryParams = [paidByNo: task.paidByNo, pinNo: task.pinNo]
        executeInsertSql(query, queryParams)
/*
        boolean saveTrace = exhTaskTraceService.create(task, new Date(), Tools.ACTION_CREATE)
        if (!saveTrace) throw new RuntimeException("Task Trace save failed. Object:" + task)*/

        return task
    }

    public ExhTask read(Long id) {
        ExhTask task = ExhTask.read(id)
        return task
    }

    // get the next id sequence for task
    private static final String SELECT_TASK_NEXT_VAL = "select nextval('exh_task_id_seq')"

    private Long getNextVal() {
        List result = executeSelectSql(SELECT_TASK_NEXT_VAL)
        Long taskID = (Long) result[0][0]
        return taskID
    }

    public Integer update(ExhTask task) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)

        if ((task.paymentMethod == exhPaymentMethodCashObj.id)) {
            if (task.pinNo == null) {
                Company company = (Company) companyCacheUtility.read(companyId)
                task.pinNo = company.code + new Date().format('ddMMHHssSSS')
            }
        } else {
            task.pinNo = null
        }

        String query = """UPDATE exh_task SET
                          version=${task.version + 1},
                          remittance_purpose=${task.remittancePurpose},
                          payment_method=${task.paymentMethod},
                          outlet_bank_id=${task.outletBankId},
                          outlet_district_id=${task.outletDistrictId},
                          outlet_branch_id=${task.outletBranchId},
                          pin_no=:pinNo,
                          amount_in_foreign_currency=${task.amountInForeignCurrency},
                          amount_in_local_currency=${task.amountInLocalCurrency},
                          from_currency_id=${task.fromCurrencyId},
                          to_currency_id=${task.toCurrencyId},
                          conversion_rate=${task.conversionRate},
                          regular_fee=${task.regularFee}, commission=${task.commission},
                          discount=${task.discount},
                          paid_by=${task.paidBy},
                          paid_by_no=:paidByNo,
                          is_gateway_payment_done=:isGatewayPaymentDone,
                          exh_gain=${task.exhGain}
                      WHERE
                          id=${task.id} AND
                          version=${task.version}"""

        Map queryParams = [
                paidByNo: task.paidByNo,
                pinNo: task.pinNo,
				isGatewayPaymentDone: task.isGatewayPaymentDone
        ]
        int updateCount = executeUpdateSql(query, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Task")
        }
        task.version = task.version + 1

        /*if (updateCount <= 0) throw new RuntimeException("Task update failed. Object:" + task)

        boolean saveTrace = exhTaskTraceService.create(task, new Date(), Tools.ACTION_UPDATE)
        if (!saveTrace) throw new RuntimeException("Task Trace save failed. Object:" + task)*/
        return (new Integer(updateCount));
    }

    public Integer updateForSentToBank(ExhTask task) {
        String query = """UPDATE exh_task SET
                          version=${task.version + 1},
                          current_status=${task.currentStatus}
                      WHERE
                          id=${task.id} AND
                          version=${task.version}"""
        int updateCount = executeUpdateSql(query);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Task")
        }
        task.version = task.version + 1

        /*if (updateCount <= 0) throw new RuntimeException("Task update failed for sent to bank. Object:" + task)

        boolean saveTrace = exhTaskTraceService.create(task, new Date(), Tools.ACTION_UPDATE)
        if (!saveTrace) throw new RuntimeException("Task Trace save failed. Object:" + task)


        task.discard()*/
        return (new Integer(updateCount));
    }

    /**
     *
     * @param task
     * @return update count
     */
    public Integer updateForResolvedByOtherBank(ExhTask task) {
        String query = """UPDATE exh_task SET
                          version=${task.version + 1},
                          current_status=${task.currentStatus}
                      WHERE
                          id=${task.id} AND
                          version=${task.version}"""
        int updateCount = executeUpdateSql(query);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Task")
        }
        task.version = task.version + 1

        /*if (updateCount <= 0) throw new RuntimeException("Task update failed for resolved by other bank. Object:" + task)

        boolean saveTrace = exhTaskTraceService.create(task, new Date(), Tools.ACTION_UPDATE)
        if (!saveTrace) throw new RuntimeException("Task Trace save failed. Object:" + task)


        task.discard()*/
        return (new Integer(updateCount));
    }

    public Integer cancelTask(ExhTask task) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhCanceledTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_CANCELLED_TASK, companyId)

        task.currentStatus = exhCanceledTaskSysEntityObject.id
        String query = """
                          UPDATE exh_task SET
                            version=${task.version + 1},
                            current_status=${task.currentStatus}
                        WHERE
                             id=${task.id} AND
                            version=${task.version}
                """

        int updateCount = executeUpdateSql(query);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Task")
        }
        task.version = task.version + 1
        /*if (updateCount <= 0) throw new RuntimeException("Task cancel failed. Object:" + task)

        boolean saveTrace = exhTaskTraceService.create(task, new Date(), Tools.ACTION_UPDATE)
        if (!saveTrace) throw new RuntimeException("Task Trace save failed. Object:" + task)*/

//        task.discard()
        return (new Integer(updateCount));
    }


    private static final String QUERY_UPDATE_FOR_PRN = """UPDATE exh_task SET is_gateway_payment_done = :isPaymentDone WHERE id = :id AND version=:version"""
    /**
     * Update task after successful PRN create from payPoint
     * @param task - ExhTask object
     * @return - update count
     */
    public int updateForPaymentResponseNotification(ExhTask task) {
        Map queryParams = [isPaymentDone: task.isGatewayPaymentDone, id: task.id, version: task.version]
        int updateCount = executeUpdateSql(QUERY_UPDATE_FOR_PRN, queryParams)
        return updateCount
    }

}
