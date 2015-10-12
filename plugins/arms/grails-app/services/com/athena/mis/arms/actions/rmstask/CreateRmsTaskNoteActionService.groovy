package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.EntityNote
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.EntityNoteService
import com.athena.mis.application.utility.NoteEntityTypeCacheUtility
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class CreateRmsTaskNoteActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String CREATE_SUCCESS_MESSAGE = "Task note has been successfully saved."
    private static final String CREATE_FAILURE_MESSAGE = "Task note has not been saved"
    private static final String ENTITY_NOTE = "entityNote"

    EntityNoteService entityNoteService
    RmsTaskService rmsTaskService

    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility

    /**
     * Get parameters from UI and check pre condition and build EntityNote object
     * 1. check necessary parameters
     * 2. check parse exception of taskId
     * 3. check task object exist or not
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)              // set default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.taskId) {                       // check required parameter
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long taskId = Tools.parseLongInput(parameterMap.taskId.toString())
            if (taskId == 0) {                          // check parse exception
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            RmsTask task = rmsTaskService.read(taskId)
            if (!task) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            EntityNote entityNote = buildEntityNote(parameterMap)         // build EntityNote object
            result.put(ENTITY_NOTE, entityNote)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
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
     * Save entity note object in DB
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from executePreCondition method
            EntityNote entityNoteObj = (EntityNote) preResult.get(ENTITY_NOTE)
            entityNoteService.create(entityNoteObj)    // save new note object in DB
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(CREATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Show newly created entity note object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
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
                LinkedHashMap receiveResult = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build entity note object
     * @param params -serialized parameters from UI
     * @return -new entityNote object
     */
    private EntityNote buildEntityNote(GrailsParameterMap params) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        // pull note entity type(Task) object
        SystemEntity noteEntityTypeTask = (SystemEntity) noteEntityTypeCacheUtility.readByReservedAndCompany(NoteEntityTypeCacheUtility.ENTITY_TYPE_RMS_TASK, companyId)
        EntityNote entityNote = new EntityNote(params)
        entityNote.entityId = Long.parseLong(params.taskId)
        entityNote.entityTypeId = noteEntityTypeTask.id
        entityNote.companyId = companyId
        entityNote.pluginId = PluginConnector.ARMS_ID
        entityNote.createdBy = rmsSessionUtil.appSessionUtil.getAppUser().id
        entityNote.createdOn = new Date();
        entityNote.updatedBy = 0L
        entityNote.updatedOn = null
        return entityNote
    }
}
