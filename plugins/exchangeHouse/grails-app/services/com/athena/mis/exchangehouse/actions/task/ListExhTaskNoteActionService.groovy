package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.NoteEntityTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of task entityNote for grid
 *  For details go through Use-Case doc named 'ListExhTaskNoteActionService'
 */
class ListExhTaskNoteActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load notes information page"
    private final String TASK_NOT_FOUND_MSG = "Task not found."
    private final String ENTITY_NOTE_LIST = "entityNoteList"
    private static final String GRID_OBJECT = 'gridObject'

    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get entityNote list for grid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)            // set default
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            if (!parameterMap.taskId) {             // check required parameter
                result.put(Tools.MESSAGE, TASK_NOT_FOUND_MSG)
                return result
            }

            initPager(parameterMap)         // initialize parameters for flexGrid

            long taskId = Long.parseLong(parameterMap.taskId)
            LinkedHashMap serviceReturn = (LinkedHashMap) entityNoteList(taskId)      // get list of entity note
            result.put(ENTITY_NOTE_LIST, serviceReturn)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of entity notes in grid entity
     * @param lstNotes -list of entityNote object(s)
     * @param start -starting index of the page
     * @return -list of wrapped notes
     */
    private List wrapNoteListGridData(List<GroovyRowResult> lstNotes, int start) {
        List<GroovyRowResult> notes = []
        try {
            int counter = start + 1
            for (int i = 0; i < lstNotes.size(); i++) {
                GridEntity obj = new GridEntity()             // build grid entity object
                GroovyRowResult note = lstNotes[i]
                String createdOn = DateUtility.getLongDateForUI(note.createdOn)      // get date format 'dd-Mon-yyyy'
                String updatedOn = DateUtility.getLongDateForUI(note.updatedOn)
                obj.id = note.id
                obj.cell = [
                        counter,
                        note.id,
                        Tools.makeDetailsShort(note.note, 100),       // make note short more than 100 characters
                        note.username,
                        createdOn,
                        updatedOn
                ]
                notes << obj
                counter++
            }
            return notes
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            notes = []
            return notes
        }
    }

    /**
     * Wrap entity note list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj       // cast map returned from execute method
            Map entityNoteList = (Map) executeResult.get(ENTITY_NOTE_LIST)
            List<GroovyRowResult> resultList = (List) entityNoteList.noteList
            int count = (int) entityNoteList.count
            List<GroovyRowResult> wrappedNotes = wrapNoteListGridData(resultList, start)     // wrap notes
            Map gridObject = [page: pageNumber, total: count, rows: wrappedNotes]            // build a map for grid
            result.put(GRID_OBJECT, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String QUERY_TASK_ENTITY_NOTE_LIST =
        """
            SELECT enote.id id, enote.note, enote.created_on AS createdOn, enote.updated_on AS updatedOn, app_user.username AS username
            FROM entity_note enote
                LEFT JOIN app_user ON app_user.id = enote.created_by
            WHERE enote.entity_type_id =:noteEntityType
                AND enote.entity_id =:taskId
                ORDER BY enote.created_on DESC
                LIMIT :resultPerPage OFFSET :start
        """

    private static final String QUERY_TASK_ENTITY_NOTE_COUNT =
        """
            SELECT COUNT(entity_id)  AS count
            FROM entity_note
            WHERE entity_note.entity_type_id=:noteEntityType
            AND entity_note.entity_id=:taskId
        """

    /**
     * Get list & count of entity notes by task
     */
    private LinkedHashMap entityNoteList(long taskId) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        // pull note entity type(Task) object
        SystemEntity noteEntityTypeTask = (SystemEntity) noteEntityTypeCacheUtility.readByReservedAndCompany(noteEntityTypeCacheUtility.COMMENT_ENTITY_TYPE_TASK, companyId)

        Map queryListParam = [
                taskId: taskId,
                noteEntityType: noteEntityTypeTask.id,
                resultPerPage: resultPerPage,
                start: start
        ]
        Map queryCountParam = [
                taskId: taskId,
                noteEntityType: noteEntityTypeTask.id
        ]
        List<GroovyRowResult> noteList = executeSelectSql(QUERY_TASK_ENTITY_NOTE_LIST, queryListParam)
        List<GroovyRowResult> noteListCount = executeSelectSql(QUERY_TASK_ENTITY_NOTE_COUNT, queryCountParam)
        int count = noteListCount[0].count

        return [noteList: noteList, count: count]
    }
}
