package com.athena.mis.application.actions.entitynote

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.EntityNote
import com.athena.mis.application.service.EntityNoteService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new EntityNote object and show in grid
 *  For details go through Use-Case doc named 'CreateEntityNoteActionService'
 */
class CreateEntityNoteActionService extends BaseService implements ActionIntf {

    EntityNoteService entityNoteService
    @Autowired
    AppSessionUtil appSessionUtil

    private Logger log = Logger.getLogger(getClass())

    private static final String SAVE_SUCCESS_MESSAGE = "Note has been successfully saved"
    private static final String SAVE_FAILURE_MESSAGE = "Could not save note"
    private static final String ENTITY_NOTE = "entityNote"

    /**
     * 1. check required parameters
     * 2. build EntityNote object to create with parameters
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing EntityNote object for execute method
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if ((!parameterMap.pluginId) || (!parameterMap.entityTypeId) || (!parameterMap.entityId)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            // build EntityNote object to create
            EntityNote entityNote = buildEntityNoteObject(parameterMap)
            result.put(ENTITY_NOTE, entityNote)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Save EntityNote object in DB
     * @param parameters -N/A
     * @param obj -EntityNote object send from executePreCondition method
     * @return -newly created EntityNote object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            EntityNote entityNote = (EntityNote) preResult.get(ENTITY_NOTE)
            EntityNote newEntityNote = entityNoteService.create(entityNote)
            result.put(ENTITY_NOTE, newEntityNote)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap newly created EntityNote object to show on grid
     * @param obj -newly created EntityNote object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            EntityNote entityNote = (EntityNote) receiveResult.get(ENTITY_NOTE)
            GridEntity object = new GridEntity()
            object.id = entityNote.id
            object.cell = [
                    Tools.LABEL_NEW,
                    entityNote.id,
                    entityNote.note
            ]

            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build EntityNote object to save in DB
     * @param parameterMap -serialized parameters from UI
     * @return -EntityNote object
     */
    private EntityNote buildEntityNoteObject(GrailsParameterMap parameterMap) {
        EntityNote entityNote = new EntityNote(parameterMap)
        entityNote.createdBy = appSessionUtil.getAppUser().id
        entityNote.createdOn = new Date()
        entityNote.companyId = appSessionUtil.getCompanyId()
        return entityNote
    }
}
