package com.athena.mis.application.actions.entitynote

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.EntityNote
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.EntityNoteService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.NoteEntityTypeCacheUtility
import com.athena.mis.integration.projecttrack.ProjectTrackPluginConnector
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for EntityNote CRUD and list of EntityNote for grid
 *  For details go through Use-Case doc named 'ShowEntityNoteActionService'
 */
class ShowEntityNoteActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    EntityNoteService entityNoteService
    @Autowired(required = false)
    ProjectTrackPluginConnector projectTrackImplService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Could not load note list"
    private static final String LST_ENTITY_NOTE = "lstEntityNote"
    private static final String ENTITY_NOTE_MAP = "entityNoteMap"
    private static final String ENTITY_TYPE_ID = "entityTypeId"
    private static final String ENTITY_ID = "entityId"

    /**
     * Do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get list of EntityNote object(s) for grid
     * @param parameters -parameters send from UI
     * @param obj -N/A
     * @return -a map containing list of item object(s) for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            if (!parameters.entityTypeId || !parameters.entityId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            if (!parameters.rp) {
                parameters.rp = 20
            }
            initPager(parameters)
            long companyId = appSessionUtil.getCompanyId()
            long entityTypeId = Long.parseLong(parameters.entityTypeId)
            long entityId = Long.parseLong(parameters.entityId)
            List<EntityNote> lstEntityNote = entityNoteService.findAllByEntityTypeIdAndEntityIdAndCompanyId(entityTypeId, entityId, companyId, this)
            int count = entityNoteService.countByEntityTypeIdAndEntityIdAndCompanyId(entityTypeId, entityId, companyId)

            SystemEntity systemEntity = (SystemEntity) noteEntityTypeCacheUtility.read(entityTypeId)
            Map entityNoteMap = buildEntityNoteMap(systemEntity, entityId)
            result.put(ENTITY_NOTE_MAP, entityNoteMap)
            result.put(LST_ENTITY_NOTE, lstEntityNote)
            result.put(Tools.COUNT, count)
            result.put(ENTITY_TYPE_ID, entityTypeId)
            result.put(ENTITY_ID, entityId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap EntityNote object list to show on grid
     * @param obj -a map contains all necessary objects receives from execute method
     * @return -all necessary objects to show on UI
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List<EntityNote> lstEntityNote = (List<EntityNote>) receiveResult.get(LST_ENTITY_NOTE)
            int total = (int) receiveResult.get(Tools.COUNT)
            List<EntityNote> lstWrappedEntityNote = wrapEntityNoteList(lstEntityNote, start)
            Map gridObject = [page: pageNumber, total: total, rows: lstWrappedEntityNote]
            result.put(ENTITY_NOTE_MAP, receiveResult.get(ENTITY_NOTE_MAP))
            result.put(ENTITY_TYPE_ID, receiveResult.get(ENTITY_TYPE_ID))
            result.put(ENTITY_ID, receiveResult.get(ENTITY_ID))
            result.put(LST_ENTITY_NOTE, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap EntityNote objects list
     * @param lstEntityNote -list of EntityNote
     * @param start -start index
     * @return -wrapped EntityNote objects list
     */
    private List wrapEntityNoteList(List<EntityNote> lstEntityNote, int start) {
        List lstWrappedEntityNote = []
        int counter = start + 1
        for (int i = 0; i < lstEntityNote.size(); i++) {
            EntityNote entityNote = lstEntityNote[i]
            GridEntity obj = new GridEntity()
            obj.id = entityNote.id
            obj.cell = [
                    counter,
                    entityNote.id,
                    entityNote.note
            ]
            lstWrappedEntityNote << obj
            counter++
        }
        return lstWrappedEntityNote
    }

    /**
     * Build entity note map with necessary objects
     * @param systemEntity -object of SystemEntity
     * @param entityId -entity id
     * @return -a map containing entity type name, entity name, plugin id and corresponding left menu link
     */
    private Map buildEntityNoteMap(SystemEntity systemEntity, long entityId) {
        String entityTypeName = systemEntity.key + Tools.COLON
        String entityName = Tools.EMPTY_SPACE
        String leftMenu = Tools.EMPTY_SPACE
        String panelTitle = Tools.EMPTY_SPACE
        int pluginId = 1
        switch (systemEntity.reservedId) {
            case noteEntityTypeCacheUtility.COMMENT_ENTITY_TYPE_PT_TASK:
                Object ptTask = projectTrackImplService.readTask(entityId)
                entityName = ptTask.idea
                pluginId = PluginConnector.PROJECT_TRACK_ID
                leftMenu = '#ptBacklog/showMyBacklog'
                panelTitle = 'Create Note for Task'
                break
            default:
                break
        }
        Map entityNoteMap = [
                entityTypeName: entityTypeName,
                entityName: entityName,
                pluginId: pluginId,
                leftMenu: leftMenu,
                panelTitle: panelTitle
        ]
        return entityNoteMap
    }
}
