package com.athena.mis.arms.service

import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.entity.RmsTaskTrace
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

class RmsTaskTraceService extends BaseService {

    RmsTaskService rmsTaskService
    @Autowired
    RmsSessionUtil rmsSessionUtil

    public RmsTaskTrace create(RmsTask rmsTask) {
        RmsTaskTrace rmsTaskTrace = new RmsTaskTrace()
        rmsTaskTrace.taskId = rmsTask.id
        rmsTaskTrace.previousStatus = rmsTask.previousStatus
        rmsTaskTrace.currentStatus = rmsTask.currentStatus
        rmsTaskTrace.isRevised = rmsTask.isRevised
        rmsTaskTrace.createdBy = rmsSessionUtil.appSessionUtil.getAppUser().id
        rmsTaskTrace.companyId = rmsSessionUtil.appSessionUtil.companyId
        rmsTaskTrace.save(flush: true)
        return rmsTaskTrace
    }

    public void create(List<RmsTask> lstRmsTask) {
        for (int i = 0; i < lstRmsTask.size(); i++){
            RmsTask rmsTask = lstRmsTask[i]
            RmsTaskTrace rmsTaskTrace = new RmsTaskTrace()
            rmsTaskTrace.taskId = rmsTask.id
            rmsTaskTrace.previousStatus = rmsTask.previousStatus
            rmsTaskTrace.currentStatus = rmsTask.currentStatus
            rmsTaskTrace.isRevised = rmsTask.isRevised
            rmsTaskTrace.createdBy = rmsSessionUtil.appSessionUtil.getAppUser().id
            rmsTaskTrace.companyId = rmsSessionUtil.appSessionUtil.companyId
            rmsTaskTrace.save(flush: true)
        }
    }

    private static final String INSERT_QUERY = """
            INSERT INTO rms_task_trace(id, company_id, created_by, created_on, current_status, is_revised,
            task_id, previous_status)
            VALUES (NEXTVAL('rms_task_trace_id_seq'),:companyId, :createdBy, :createdOn,
            :currentStatus, :isRevised, :taskId, :previousStatus);
    """

    public RmsTaskTrace create(RmsTaskTrace rmsTaskTrace) {
        Map queryParams = [
                companyId: rmsTaskTrace.companyId,
                createdBy: rmsTaskTrace.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(rmsTaskTrace.createdOn),
                currentStatus: rmsTaskTrace.currentStatus,
                isRevised: rmsTaskTrace.isRevised,
                taskId: rmsTaskTrace.taskId,
                previousStatus: rmsTaskTrace.previousStatus
        ]
        List<RmsTaskTrace> result = (List<RmsTaskTrace>)executeInsertSql(INSERT_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert postal code information')
        }
        int RmsTaskTraceId = (int) result[0][0]
        rmsTaskTrace.id = RmsTaskTraceId
        return rmsTaskTrace
    }

    public List<RmsTaskTrace> findAllByTaskId(long taskId) {
        List<RmsTaskTrace> lstRmsTaskTrace = RmsTaskTrace.findAllByCompanyIdAndTaskId(rmsSessionUtil.appSessionUtil.getCompanyId(), taskId, [readOnly: true])
        return lstRmsTaskTrace
    }
}
