package com.athena.mis.integration.qsmeasurement.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Get gross receivable amount of QsMeasurement(both internal and govt.) by project id, used in project status report
 * For details go through Use-Case doc named 'GetGrossReceivableQsImplActionService'
 */
class GetGrossReceivableQsImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass());

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get gross receivable amount of QsMeasurement by project id
     * @param parameters -id of project
     * @param obj -N/A
     * @return -gross receivable amount of QsMeasurement
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            long projectId = Long.parseLong(parameters.toString())
            return getGrossReceivableQs(projectId)
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success result for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Get gross receivable amount of QsMeasurement by project id
     * @param projectId -id of project
     * @return -gross receivable amount of QsMeasurement(both internal and govt.)
     */
    private Map getGrossReceivableQs(long projectId) {
        String queryStr = """SELECT
        (COALESCE(
					SUM(budget.contract_rate *
						(SELECT COALESCE(SUM(qsm.quantity),0)
							FROM qs_measurement qsm
								WHERE budget.id = qsm.budget_id
									AND qsm.is_govt_qs = false)
					)
				,0)) AS gross_receivable_internal,
        (to_char(
			COALESCE(
					SUM(budget.contract_rate *
						(SELECT COALESCE(SUM(qsm.quantity),0)
							FROM qs_measurement qsm
								WHERE budget.id = qsm.budget_id
									AND qsm.is_govt_qs = false)
					)
				,0)
		,'${Tools.DB_CURRENCY_FORMAT}')) AS gross_receivable_internal_str,
		(to_char(
			COALESCE(
					SUM(budget.contract_rate *
						(SELECT COALESCE(SUM(qsm.quantity),0)
							FROM qs_measurement qsm
								WHERE budget.id = qsm.budget_id
									AND qsm.is_govt_qs = true)
					)
				,0)
		,'${Tools.DB_CURRENCY_FORMAT}')) AS gross_receivable_gov_str
        FROM budg_budget budget
        WHERE budget.project_id =:projectId
        AND budget.billable = true
        """
        Map queryParams = [
                projectId: projectId
        ]
        List<GroovyRowResult> grossReceivable = executeSelectSql(queryStr, queryParams)
        Map result = [grossReceivableInternal: grossReceivable[0].gross_receivable_internal,
                grossReceivableInternalStr: grossReceivable[0].gross_receivable_internal_str,
                grossReceivableGovStr: grossReceivable[0].gross_receivable_gov_str]
        return result
    }
}