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
 *  Search EntityNote and show specific list of EntityNote for grid
 *  For details go through Use-Case doc named 'SearchEntityNoteActionService'
 */
class SearchEntityNoteActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    EntityNoteService entityNoteService
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String DEFAULT_ERROR_MESSAGE = "Could not search note list"
    private static final String LST_ENTITY_NOTE = "lstEntityNote"

    /**
     * Do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get list of EntityNote objects by specific search key word
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains list of EntityNote objects  and count
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            // check required parameters
            if (!parameters.entityTypeId || !parameters.entityId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            initSearch(params)
            long companyId = appSessionUtil.getCompanyId()
            long entityTypeId = Long.parseLong(parameters.entityTypeId.toString())
            long entityId = Long.parseLong(parameters.entityId.toString())
            String note = Tools.PERCENTAGE + query + Tools.PERCENTAGE
            List<EntityNote> lstEntityNote = entityNoteService.findAllByEntityTypeIdAndEntityIdAndCompanyIdAndNoteIlike(entityTypeId, entityId, companyId, note, this)
            int count = entityNoteService.countByEntityTypeIdAndEntityIdAndCompanyIdAndNoteIlike(entityTypeId, entityId, companyId, note)
            result.put(LST_ENTITY_NOTE, lstEntityNote)
            result.put(Tools.COUNT, count)
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
     * Wrap EntityNote objects list to show on grid
     * @param obj -a map contains EntityNote objects list and count
     * @return -wrapped EntityNote objects list to show on grid
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
}
