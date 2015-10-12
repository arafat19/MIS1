package com.athena.mis.integration.inventory.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get total approved consumed amount of item for a particular project, used in project status report
 * For details go through Use-Case doc named 'GetTotalApprovedConsumptionByProjectIdImplActionService'
 */
class GetTotalApprovedConsumptionByProjectIdImplActionService extends BaseService implements ActionIntf {

    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    private final Logger log = Logger.getLogger(getClass());

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get total approved consumed amount of item for a particular project
     * @param params -id of project
     * @param obj -N/A
     * @return -total approved consumed amount of item
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        try {
            long projectId = Long.parseLong(params.toString())
            return getTotalApprovedConsumptionByProjectId(projectId)
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return []
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
     * Get total approved consumed amount of item for a particular project
     * @param projectId -id of project
     * @return -total approved consumed amount of item
     */
    private String getTotalApprovedConsumptionByProjectId(long projectId) {
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        String queryStr = """SELECT
                    (to_char(
                    COALESCE(
                            SUM(iitd.actual_quantity*iitd.rate)
                        ,0)
                        ,'${Tools.DB_CURRENCY_FORMAT}')) AS total_consumption
        FROM inv_inventory_transaction_details  iitd
        LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
        WHERE iit.project_id =:projectId
        AND iit.transaction_type_id =:transactionTypeId
        AND iitd.approved_by > 0 AND iitd.is_current = true
        """
        Map queryParams = [
                projectId: projectId,
                transactionTypeId: transactionTypeCons.id
        ]
        List<GroovyRowResult> resultList = executeSelectSql(queryStr, queryParams)
        return resultList[0].total_consumption
    }
}