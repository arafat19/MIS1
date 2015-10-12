package com.athena.mis.integration.procurement.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.DateUtility
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to * get list of valid indent by given projectId and except given indentId
 *  For details go through Use-Case doc named 'ListIndentByProjectIdIntendIdImplActionService'
 */
class ListIndentByProjectIdIntendIdImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * get list of valid indent by given projectId and except given indentId
     * @param params1 - Project.id
     * @param params2 -ProcIndent.id
     * @return -List of groovyRowResult
     */
    @Transactional(readOnly = true)
    public Object execute(Object params1, Object params2) {
        try {
            long projectId = Long.parseLong(params1.toString())
            long indentId = Long.parseLong(params2.toString())
            return listIndentByProjectIdIntendId(projectId, indentId)
        } catch (Exception ex) {
            log.error(ex.getMessage())
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
     * Do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return false
    }

    /**
     * Do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private static final String PROC_INDENT_SELECT_QUERY = """
            SELECT indent.id,
                   indent.id ||' (' ||to_char(indent.from_date,'dd-Mon-yyyy') ||' To '|| to_char(indent.to_date,'dd-Mon-yyyy') ||')' AS indent_date
            FROM  proc_indent indent
            WHERE indent.id IN
                (SELECT indt.id FROM proc_indent indt
                 WHERE indt.project_id =:projectId AND
                       indt.to_date >= :newDate AND
                       indt.item_count > 0 AND
                       indt.id <> :indentId
                )
            OR indent.id =:indentId
            ORDER BY indent.id desc
            """
    /**
     * get list of valid indent by given projectId and except given indentId
     * @param projectId -Project.id
     * @param indentId -ProcIndent.id
     * @return
     */
    private List<GroovyRowResult> listIndentByProjectIdIntendId(long projectId, long indentId) {
        Map queryParams = [
                projectId: projectId,
                newDate: DateUtility.getSqlDate(new Date()),
                indentId: indentId
        ]
        List<GroovyRowResult> listIntend = executeSelectSql(PROC_INDENT_SELECT_QUERY, queryParams)
        return listIntend
    }
}
