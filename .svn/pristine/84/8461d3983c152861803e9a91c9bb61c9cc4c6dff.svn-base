package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.EntityNote
import com.athena.mis.application.service.EntityNoteService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update task  entityNote object and grid data
 *  For details go through Use-Case doc named 'UpdateExhTaskNoteActionService'
 */
class UpdateExhTaskNoteActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_FAILURE_MESSAGE = "Note could not be updated"
    private static final String UPDATE_SUCCESS_MESSAGE = "Note has been updated successfully"
    private static final String ENTITY_NOTE = "entityNote"
    private static final String NOT_FOUND_MASSAGE = "Selected note is not found"

    EntityNoteService entityNoteService

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * Get parameters from UI and build entityNote object for update
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            if (!parameterMap.id) {   // check required parameters
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long id = Long.parseLong(parameterMap.id.toString())
            EntityNote entityNote = entityNoteService.read(id)         // get entityNote object

            if (entityNote) {                // check whether selected note object exists or not
                buildEntityNote(entityNote, parameterMap)     // if exists build note object for update
                result.put(Tools.ENTITY, entityNote)
            } else {
                result.put(Tools.MESSAGE, NOT_FOUND_MASSAGE)          // or message show not found
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Update entityNote object in DB
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj        // cast map returned from executePreCondition method
            EntityNote entityNote = (EntityNote) preResult.get(Tools.ENTITY)
            updateEntityNote(entityNote)                 // update entityNote

            result.put(ENTITY_NOTE, entityNote)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Show updated entity note object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj      // cast map returned from execute method
            EntityNote entityNote = (EntityNote) executeResult.get(ENTITY_NOTE)
            String createdOn = DateUtility.getLongDateForUI(entityNote.createdOn)     // get date format 'dd-Mon-yyyy'
            String updatedOn = DateUtility.getLongDateForUI(entityNote.updatedOn)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(entityNote.createdBy)
            GridEntity object = new GridEntity()           // build grid entity object
            object.id = entityNote.id
            object.cell = [
                    Tools.LABEL_NEW,
                    entityNote.id,
                    Tools.makeDetailsShort(entityNote.note, 100),       // make short note more than 100 characters
                    createdBy.username,
                    createdOn,
                    updatedOn

            ]
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, entityNote.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)

            return result
        }
    }

    /**
     * Build entity note object for update
     * @param parameterMap -serialized parameters from UI
     * @param existingObject -old note object
     * @return -updated existingObject object
     */
    private EntityNote buildEntityNote(EntityNote existingObject, GrailsParameterMap parameterMap) {
        EntityNote entityNote = new EntityNote(parameterMap)
        existingObject.note = entityNote.note
        existingObject.updatedBy = exhSessionUtil.appSessionUtil.getAppUser().id
        existingObject.updatedOn = new Date()
        return existingObject
    }

    private static final UPDATE_QUERY_NOTE =
    """
        UPDATE entity_note
            SET
                  version=version+1,
                  note=:entityNote,
                  updated_on=:updatedOn,
                  updated_by=:updatedBy
            WHERE
                  id=:id AND
                  version=:version
    """
    public EntityNote updateEntityNote(EntityNote entityNote) {
        Map queryParams = [
                id: entityNote.id,
                version: entityNote.version,
                entityNote: entityNote.note,
                updatedBy: entityNote.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(entityNote.updatedOn)
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY_NOTE, queryParams)
        if (updateCount < 0) {
            throw new RuntimeException('Failed to update task note')
        }
        entityNote.version = entityNote.version + 1
        return entityNote
    }
}
