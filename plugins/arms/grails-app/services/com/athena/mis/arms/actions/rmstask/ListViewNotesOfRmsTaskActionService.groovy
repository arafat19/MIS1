package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.NoteEntityTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired


class ListViewNotesOfRmsTaskActionService extends BaseService implements ActionIntf {

    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_FAILURE_MESSAGE = "Failed to found task(s)"
    private static final String LST_TASKS = "lstTasks"
    private static final String GRID_OBJ = "gridObj"

    /**Do nothing for executePreCondition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for executePostCondition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get serialized parameters from UI
     * @param parameters -params
     * @param obj -N/A
     * @return-a map containing all object necessary for build success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameterMap)
            Map searchResults = listTasksForViewNotes(this)
            List<GroovyRowResult> lstTasks = searchResults.lstTasks
            int count = searchResults.count
            result.put(LST_TASKS, lstTasks)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * Build success message
     * @param obj -returned from execute method
     * @return-grid obj to show in grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> lstTasks = (List<GroovyRowResult>) executeResult.get(LST_TASKS)
            int count = (int) executeResult.get(Tools.COUNT)
            List wrapList = wrapTaskForViewNotes(lstTasks, start)
            Map gridObj = [page: pageNumber, total: count, rows: wrapList]
            result.put(GRID_OBJ, gridObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * Build failure message
     * @param obj -returned from previous method may be null
     * @return-failure result to indicate success event
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
            return result
        }
    }

    private List<GroovyRowResult> wrapTaskForViewNotes(List<GroovyRowResult> lstTasks, int start) {
        List lstWrappedTasks = []
        int counter = start + 1
        for (int i = 0; i < lstTasks.size(); i++) {
            GroovyRowResult eachRow = lstTasks[i]
            GridEntity obj = new GridEntity()
            obj.id = eachRow.id
            obj.cell = [
                    counter,
                    eachRow.id,
                    eachRow.ref_no,
                    eachRow.note,
                    eachRow.username,
                    DateUtility.getDateFormatAsString(eachRow.create_date)
            ]
            lstWrappedTasks << obj
            counter++
        }
        return lstWrappedTasks
    }

    private Map listTasksForViewNotes(BaseService baseService) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        List<Long> lstValidStatus = rmsTaskStatusCacheUtility.listAllValidTaskStatusIds()
        String strStatusIds = Tools.buildCommaSeparatedStringOfIds(lstValidStatus)


        String queryForViewNotes = """
        SELECT r.id,note, username, date(e.created_on) create_date, ref_no
        FROM entity_note e
            LEFT JOIN rms_task r ON e.entity_id=r.id
            LEFT JOIN app_user a ON a.id=e.created_by
        WHERE e.entity_type_id=:entityTypeId
        AND r.company_id= :companyId
        AND r.current_status IN (${strStatusIds})
        ORDER BY e.created_on DESC
        LIMIT :resultPerPage OFFSET :start
    """
        String countForViewNotes = """
        SELECT COUNT(e.id)
        FROM entity_note e LEFT JOIN rms_task r ON e.entity_id=r.id
        LEFT JOIN app_user a ON a.id=e.created_by
        WHERE e.entity_type_id=:entityTypeId
        AND r.company_id= :companyId
        AND r.current_status IN (${strStatusIds})
       """


        SystemEntity noteEntityType = (SystemEntity) noteEntityTypeCacheUtility.readByReservedAndCompany(noteEntityTypeCacheUtility.ENTITY_TYPE_RMS_TASK, companyId)
        Map queryParamsForList = [
                entityTypeId: noteEntityType.id,
                resultPerPage: baseService.resultPerPage,
                start: baseService.start,
                companyId: companyId
        ]
        List<GroovyRowResult> lstTasks = executeSelectSql(queryForViewNotes, queryParamsForList)


        Map queryParamsForCount = [
                entityTypeId: noteEntityType.id,
                companyId: companyId
        ]
        List<GroovyRowResult> result = (List<GroovyRowResult>) executeSelectSql(countForViewNotes, queryParamsForCount)
        int count = (int) result[0][0]
        Map searchResult = [lstTasks: lstTasks, count: count]
        return searchResult

    }
}
