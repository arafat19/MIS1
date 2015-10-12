package com.athena.mis.sarb.service

import com.athena.mis.BaseService
import com.athena.mis.sarb.model.SarbTaskModel
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult

class SarbTaskModelService extends BaseService {

    public SarbTaskModel read(long id) {
        SarbTaskModel sarbTaskModel = SarbTaskModel.read(id)
        return sarbTaskModel
    }

    public List<SarbTaskModel> listAllTaskForSendToSarb(List<Long> lstTaskStatus, long companyId, long reviseStatusId, BaseService baseService) {
        List<SarbTaskModel> sarbTaskModelList = SarbTaskModel.findAllByCurrentStatusInListAndCompanyIdAndReviseStatusAndEnabled(
                lstTaskStatus, companyId, reviseStatusId, Boolean.FALSE,
                [offset: baseService.start, max: baseService.resultPerPage])
        return sarbTaskModelList
    }

    public int countAllTaskForSendToSarb(List<Long> lstTaskStatus, long companyId, long reviseStatusId) {
        int count = SarbTaskModel.countByCurrentStatusInListAndCompanyIdAndReviseStatusAndEnabled(
                lstTaskStatus, companyId, reviseStatusId, Boolean.FALSE)
        return count
    }

    private static final String QUERY_LIST_FOR_SHOW_STATUS = """
            SELECT id, ref_no, amount_in_foreign_currency, amount_in_local_currency, customer_name, beneficiary_name, is_accepted_by_sarb,
            is_cancelled, sarb_ref_no, response_of_reference, payment_method,submitted_file_count,created_on, original_file_name
            FROM vw_sarb_task_model
            WHERE created_on BETWEEN :startDate AND :endDate
            AND sarb_ref_no IS NOT NULL
            AND ref_no ilike :refNo
            AND company_id = :companyId
            LIMIT :limit
            OFFSET :start
        """

    public List<GroovyRowResult> listSarbTaskForShowDetails(Date startDate, Date endDate, String refNo, BaseService baseService, long companyId) {

        Map queryParams = [
                startDate: DateUtility.getSqlDateWithSeconds(startDate),
                endDate: DateUtility.getSqlDateWithSeconds(endDate),
                refNo: Tools.PERCENTAGE + refNo + Tools.PERCENTAGE,
                companyId: companyId,
                limit: baseService.resultPerPage,
                start: baseService.start
        ]
        List<GroovyRowResult> lstSarbTask = executeSelectSql(QUERY_LIST_FOR_SHOW_STATUS, queryParams)
        return lstSarbTask
    }

    private static final String QUERY_COUNT_FOR_SHOW_STATUS = """
            SELECT count(id)
            FROM vw_sarb_task_model
            WHERE created_on BETWEEN :startDate AND :endDate
            AND sarb_ref_no IS NOT NULL
            AND ref_no ilike :refNo
            AND company_id = :companyId
        """

    public int countSarbTaskForShowDetails(Date startDate, Date endDate, String refNo, long companyId) {

        Map queryParams = [
                startDate: DateUtility.getSqlDateWithSeconds(startDate),
                endDate: DateUtility.getSqlDateWithSeconds(endDate),
                companyId: companyId,
                refNo: Tools.PERCENTAGE + refNo + Tools.PERCENTAGE
        ]
        List<GroovyRowResult> result = executeSelectSql(QUERY_COUNT_FOR_SHOW_STATUS, queryParams)
        int count = (int) result[0][0]
        return count
    }

    private static final String RETRIEVE_RESPONSE_QUERY = """
            SELECT id, ref_no, amount_in_foreign_currency, amount_in_local_currency, customer_name, beneficiary_name,
            created_on, original_file_name
            FROM vw_sarb_task_model
            WHERE sarb_ref_no IS NULL
            AND is_submitted_to_sarb= true
            AND company_id =:companyId
            AND enabled = true
            LIMIT :limit
            OFFSET :start
        """

    public List<GroovyRowResult> listForRetrieveResponse(long companyId, BaseService baseService) {

        Map queryParams = [
                companyId: companyId,
                limit: baseService.resultPerPage,
                start: baseService.start
        ]
        List<GroovyRowResult> lstSarbTask = executeSelectSql(RETRIEVE_RESPONSE_QUERY, queryParams)
        return lstSarbTask
    }

    private static final String COUNT_RETRIEVE_RESPONSE_QUERY =
            """
        SELECT count(id)
        FROM vw_sarb_task_model
        WHERE sarb_ref_no IS NULL
        AND is_submitted_to_sarb= true
        AND company_id =:companyId
        AND enabled = true
    """

    public int countRetrieveResponse(long companyId) {

        Map queryParams = [
                companyId: companyId
        ]
        List<GroovyRowResult> result = executeSelectSql(COUNT_RETRIEVE_RESPONSE_QUERY, queryParams)
        int count = (int) result[0][0]
        return count
    }

    public List<SarbTaskModel> findAllByRefundTaskId(long taskId, BaseService baseService, long companyId) {
        List<SarbTaskModel> sarbTaskModelList = SarbTaskModel.findAllByRefundTaskIdAndCompanyIdAndEnabled(
                taskId, companyId, Boolean.TRUE,
                [offset: baseService.start, max: baseService.resultPerPage, readOnly: true])
        return sarbTaskModelList
    }

    public int countByRefundTaskId(long taskId, long companyId) {
        int count = SarbTaskModel.countByRefundTaskIdAndCompanyIdAndEnabled(taskId, companyId, Boolean.TRUE)
        return count
    }
}