package com.athena.mis.procurement.service

import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcTermsAndCondition
import com.athena.mis.utility.DateUtility
import groovy.sql.GroovyRowResult

class ProcTermsAndConditionService extends BaseService {

    static transactional = false

    PurchaseOrderService purchaseOrderService

    private static final String PROC_TERMS_AND_CONDITION_SELECT_QUERY = """
                SELECT *
                    FROM proc_terms_and_condition
                WHERE id =:id
            """
    /**
     * Method to read ProcTermsAndCondition object by id
     * 1. Build procurement terms and condition by buildProcTermsAndCondition method
     * @param id - ProcTermsAndCondition.id
     * @return - object of ProcTermsAndCondition
     */
    public ProcTermsAndCondition read(long id) {
        ProcTermsAndCondition procTermsAndCondition = null
        Map queryParams = [
                id: id
        ]
        List<GroovyRowResult> resultList = executeSelectSql(PROC_TERMS_AND_CONDITION_SELECT_QUERY, queryParams)
        if (resultList.size() > 0) { // if resultList.size() > 0 then the method build the object of terms and condition
            procTermsAndCondition = buildProcTermsAndCondition(resultList[0])
        }
        return procTermsAndCondition
    }

    private static final String READ_BY_ID_AND_VERSION_QUERY = """
            SELECT *
                FROM proc_terms_and_condition
            WHERE id =:id
                AND version =:version
            """
    /**
     * Method to read ProcTermsAndCondition object by ProcTermsAndCondition.id & ProcTermsAndCondition.version
     * @param id - ProcTermsAndCondition.id
     * @param version - ProcTermsAndCondition.version
     * @return - the object of ProcTermsAndCondition
     */
    public ProcTermsAndCondition readByIdAndVersion(long id, int version) {
        ProcTermsAndCondition procTermsAndCondition = null
        Map queryParams = [
                id: id,
                version: version
        ]
        List<GroovyRowResult> resultList = executeSelectSql(READ_BY_ID_AND_VERSION_QUERY, queryParams)
        if (resultList.size() > 0) {   // if resultList.size() > 0 then the method build the object of terms and condition
            procTermsAndCondition = buildProcTermsAndCondition(resultList[0])
        }
        return procTermsAndCondition
    }

    private static final String PROC_TERMS_AND_CONDITION_CREATE_QUERY = """
            INSERT INTO proc_terms_and_condition(
                id, version, company_id, created_by, created_on, project_id, purchase_order_id,
                details, updated_by, updated_on)
            VALUES (
                NEXTVAL('proc_terms_and_condition_id_seq'), :version, :companyId, :createdBy,
                :createdOn, :projectId, :purchaseOrderId,
                :details, :updatedBy, null);
                """

    /**
     * Method to save ProcTermsAndCondition object
     * @param procTermsAndCondition - ProcTermsAndCondition object
     * @return - newly object of ProcTermsAndCondition
     */
    public ProcTermsAndCondition create(ProcTermsAndCondition procTermsAndCondition) {
        Map queryParams = [
                version: procTermsAndCondition.version,
                companyId: procTermsAndCondition.companyId,
                purchaseOrderId: procTermsAndCondition.purchaseOrderId,
                createdBy: procTermsAndCondition.createdBy,
                projectId: procTermsAndCondition.projectId,
                details: procTermsAndCondition.details,
                updatedBy: procTermsAndCondition.updatedBy,
                createdOn: DateUtility.getSqlDate(procTermsAndCondition.createdOn)
        ]

        List result = executeInsertSql(PROC_TERMS_AND_CONDITION_CREATE_QUERY, queryParams)
        if (result.size() <= 0) { // if resultList.size() <= 0 then the method throw an exception
            throw new RuntimeException("Fail to create Terms and condition")
        }
        int procTermsAndConditionId = (int) result[0][0]
        procTermsAndCondition.id = procTermsAndConditionId
        return procTermsAndCondition
    }

    private static final String PROC_TERMS_AND_CONDITION_UPDATE_QUERY = """
                      UPDATE proc_terms_and_condition
                      SET
                          details=:details,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy,
                          version=:newVersion
                      WHERE
                          id= :id AND
                          version= :version
                          """

    /**
     * Method to update ProcTermsAndCondition object
     * @param procTermsAndCondition - ProcTermsAndCondition object
     * @return- updateCount ( intValue ) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(ProcTermsAndCondition procTermsAndCondition) {
        Map queryParams = [
                id: procTermsAndCondition.id,
                version: procTermsAndCondition.version,
                newVersion: procTermsAndCondition.version + 1,
                updatedBy: procTermsAndCondition.updatedBy,
                details: procTermsAndCondition.details,
                updatedOn: DateUtility.getSqlDate(procTermsAndCondition.updatedOn)
        ]

        int updateCount = executeUpdateSql(PROC_TERMS_AND_CONDITION_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update Terms and condition")
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
                                            DELETE FROM proc_terms_and_condition
                                                WHERE id=:id
                                        """

    /**
     * Method to delete ProcTermsAndCondition object by id
     * @param id - ProcTermsAndCondition.id
     * @return - if deleteCount <= 0 then throw exception to rollback transaction
     */
    public int delete(long id) {
        int deleteCount = executeUpdateSql(DELETE_QUERY, [id: id])
        if (deleteCount <= 0) {
            throw new RuntimeException("Fail to delete Terms and condition")
        }

        return deleteCount
    }

    /**
     * Build ProcTermsAndCondition object
     * @param result - GroovyRowResult from the caller method
     * @return - object of ProcTermsAndCondition
     */
    private ProcTermsAndCondition buildProcTermsAndCondition(GroovyRowResult result) {
        ProcTermsAndCondition procTermsAndCondition = new ProcTermsAndCondition()
        procTermsAndCondition.id = result.id
        procTermsAndCondition.version = result.version
        procTermsAndCondition.companyId = result.company_id
        procTermsAndCondition.projectId = result.project_id
        procTermsAndCondition.purchaseOrderId = result.purchase_order_id
        procTermsAndCondition.details = result.details
        procTermsAndCondition.createdBy = result.created_by
        procTermsAndCondition.createdOn = result.created_on
        procTermsAndCondition.updatedBy = result.updated_by
        procTermsAndCondition.updatedOn = result.updated_on

        return procTermsAndCondition
    }
}
