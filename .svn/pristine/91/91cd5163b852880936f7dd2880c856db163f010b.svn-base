package com.athena.mis.exchangehouse.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.entity.ExhTaskTrace
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired

class ExhTaskTraceService extends BaseService {
    static transactional = false
    @Autowired
    ExhSessionUtil exhSessionUtil

    private static String DEFAULT_SORT_COLUMN = "id";
    private static String DEFAULT_SORT_ORDER = "desc";

    private static final String TASK_TRACE_FIELD_NAMES = """
            id,
            action,
            action_date,
            task_id,
            ref_no,
            current_status,
            remittance_purpose,
            payment_method,
            customer_id,
            beneficiary_id,
            pin_no,
            amount_in_foreign_currency,
            amount_in_local_currency,
            to_currency_id,
            from_currency_id,
            conversion_rate,
            regular_fee,
            discount,
            paid_by,
            paid_by_no,
            company_id,
            agent_id,
            user_id,
            approved_by,
            task_type_id,
            is_gateway_payment_done,
            exh_gain
        """

    /**
     *
     * @param task
     * @param executionDate
     * @param paramAction -
     * @return
     */
    public boolean create(ExhTask task, Date executionDate, Character paramAction) {
        char action = Tools.ACTION_UPDATE // Default action
        if (paramAction) action = paramAction
        AppUser user = exhSessionUtil.appSessionUtil.getAppUser()
        Long traceId = getNextVal()

    String queryTrace = """
            INSERT INTO exh_task_trace (${TASK_TRACE_FIELD_NAMES})
            VALUES(
                ${traceId},
                '${action}',
                '${DateUtility.getDBDateFormatWithSecond(executionDate)}',
                ${task.id},
                '${task.refNo}',
                ${task.currentStatus},
                ${task.remittancePurpose},
                ${task.paymentMethod},
                ${task.customerId},
                ${task.beneficiaryId},
                :pinNo,
                ${task.amountInForeignCurrency},
                ${task.amountInLocalCurrency},
                ${task.toCurrencyId},
                ${task.fromCurrencyId},
                ${task.conversionRate},
                ${task.regularFee},
                ${task.discount},
                ${task.paidBy},
                :paidByNo,
                ${task.companyId},
                ${task.agentId},
                ${user.id},
                ${task.approvedBy},
                ${task.taskTypeId},
				${task.isGatewayPaymentDone},
				${task.exhGain}
                )
        """
        Map queryParams = [paidByNo:task.paidByNo,pinNo:task.pinNo]
        executeInsertSql(queryTrace,queryParams)
        return true
    }

    // get the next id sequence for taskTrace
    private static final String QUERY_TASK_TRACE_NEXT_VAL = "select nextval('exh_task_trace_id_seq')"

    private Long getNextVal() {
        List result = executeSelectSql(QUERY_TASK_TRACE_NEXT_VAL)
        Long traceID = (Long) result[0][0]
        return traceID
    }

    // get current task details info by task id
    public ExhTaskTrace readByTaskId(Long taskId) {
        List<ExhTaskTrace> taskTraceList = ExhTaskTrace.findAllByTaskId(taskId,[sort:DEFAULT_SORT_COLUMN, order:DEFAULT_SORT_ORDER, readOnly:true])
        ExhTaskTrace taskTrace = taskTraceList.size() > 0 ? taskTraceList[0] : null
        return taskTrace
    }

}
