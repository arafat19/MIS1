package com.athena.mis.procurement.service

import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcIndent
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult

class IndentService extends BaseService {

    static transactional = false

    /**
     * Method to read ProcIndent object by id
     * @param id - ProcIndent.id
     * @return - ProcIndent object
     */
    public ProcIndent read(long id) {
        ProcIndent indent = ProcIndent.read(id)
        return indent
    }

    private static final String PROC_INDENT_CREATE_QUERY = """
                INSERT INTO proc_indent(id, version, comments, created_by, created_on, approved_by,
                item_count, total_price, project_id, updated_by, from_date, to_date, company_id)
        VALUES (NEXTVAL('proc_indent_id_seq'),0,:comments,:createdBy,:createdOn,:approvedBy,
                :itemCount,:totalPrice,:projectId,:updatedBy,:fromDate,:toDate,:companyId);
        """

    /**
     * Method to save ProcIndent object
     * @param indent - ProcIndent object
     * @return - ProcIndent object
     */
    public ProcIndent create(ProcIndent indent) {
        Map queryParams = [
                comments: indent.comments ? Tools.SINGLE_QUOTE + indent.comments + Tools.SINGLE_QUOTE : null,
                createdBy: indent.createdBy,
                createdOn: DateUtility.getSqlDate(indent.createdOn),
                approvedBy: indent.approvedBy,
                itemCount: indent.itemCount,
                totalPrice: indent.totalPrice,
                projectId: indent.projectId,
                updatedBy: indent.updatedBy,
                fromDate: DateUtility.getSqlDate(indent.fromDate),
                toDate: DateUtility.getSqlDate(indent.toDate),
                companyId: indent.companyId
        ]
        List result = executeInsertSql(PROC_INDENT_CREATE_QUERY, queryParams)
        int indentId = (int) result[0][0]
        if (indentId > 0) {  // if indentId>0 then the method returns the ProcIndent object
            indent.id = indentId
            return indent
        }
        return null
    }

    private static final String PROC_INDENT_UPDATE_QUERY = """
            UPDATE proc_indent
                SET
                    version=:newVersion,
                    comments=:comments,
                    project_id=:projectId,
                    updated_by =:updatedBy,
                    updated_on =:updatedOn,
                    from_date=:fromDate,
                    to_date=:toDate
                WHERE id=:id AND
                    version=:version
    """

    /**
     * Method to update ProcIndent object
     * @param indent - ProcIndent object
     * @return - updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(ProcIndent indent) {
        Map queryParams = [
                id: indent.id,
                version: indent.version,
                newVersion: indent.version + 1,
                comments: indent.comments,
                projectId: indent.projectId,
                updatedBy: indent.updatedBy,
                updatedOn: DateUtility.getSqlDate(indent.updatedOn),
                fromDate: DateUtility.getSqlDate(indent.fromDate),
                toDate: DateUtility.getSqlDate(indent.toDate)
        ]
        int updateCount = executeUpdateSql(PROC_INDENT_UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update indent')
        }
        return updateCount
    }

    private static final String SELECT_QUERY_LIST_IND_PRO_IND = """
        SELECT indent.id, indent.id || ' (From ' || to_char(from_date,'dd-Mon-yyyy') || ' To ' || to_char(to_date,'dd-Mon-yyyy')||')' AS indent_date
        FROM proc_indent indent
        WHERE project_id =:projectId
            AND indent.id IN(
                SELECT indt.id FROM proc_indent indt
                JOIN budg_budget budget on budget.project_id = indent.project_id
                WHERE indt.project_id = budget.project_id
                AND indt.to_date >= :newDate
                AND indt.id <> :indentId
                AND indt.item_count > 0
                AND indt.approved_by > 0
        )
            OR indent.id =:indentId
        ORDER BY indent.id ASC

            """
    /**
     * Method to get the list of indent by projectId and indentId
     * @param projectId - Project.id
     * @param indentId - ProcIndent.id
     * @return - list of indents
     */
    public List<GroovyRowResult> listByProjectAndIndentId(long projectId, long indentId) {
        Date newDate = DateUtility.getSqlDate(new Date())
        Map queryParams = [
                projectId: projectId,
                newDate: newDate,
                indentId: indentId
        ]
        List<GroovyRowResult> lstIntend = executeSelectSql(SELECT_QUERY_LIST_IND_PRO_IND, queryParams)
        return lstIntend
    }

    private static final String DELETE_QUERY = """
                                            DELETE FROM proc_indent
                                                WHERE id=:id
                                        """

    /**
     * Method to delete ProcIndent object
     * @param id - ProcIndent.id
     * @return - if deleteCount <= 0 then throw exception to rollback transaction
     */
    public int delete(long id) {
        int deleteCount = executeUpdateSql(DELETE_QUERY, [id: id])
        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while deleting ProcIndent')
        }
        return deleteCount
    }
}
