package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.application.utility.NoteEntityTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.utility.ExhPaymentMethodCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.commons.collections.map.LinkedMap
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for task entity note CRUD and list of entity note(s) for grid
 *  For details go through Use-Case doc named 'ShowExhTaskNoteActionService'
 */
class ShowExhTaskNoteActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load task notes information page"
    private final String FAILURE_MSG = "Fail to get task's information."
    private final String TASK_INFO_MAP = "taskInfoMap"
    private final String ENTITY_NOTE_LIST = "entityNoteList"
    private static final String GRID_OBJECT = 'gridObject'
    private static final String TASK_OBJECT = 'task'

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility

    /**
     * Get params from UI and check pre condition
     * 1. check necessary parameters
     * 2. check if task exists
     * 3. check parse exception
     * 4. check companyId of task object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedMap preResult = new LinkedMap()

        try {
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)          // set default value true
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.taskId) {                      // check required params
                preResult.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return preResult
            }

            long taskId = Tools.parseLongInput(params.taskId.toString())

            if (taskId == 0) {                    // check parse exception
                preResult.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return preResult
            }
            ExhTask task = readWithExchangeHouse(taskId)         // get task object by id
            if (!task) {                                  // check whether get task object exists or not
                preResult.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return preResult
            }
            if (exhSessionUtil.appSessionUtil.getCompanyId() != task.companyId) {          // check company's task
                preResult.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return preResult
            }
            preResult.put(TASK_OBJECT, task)
            preResult.put(Tools.IS_ERROR, Boolean.FALSE)
            return preResult

        } catch (Exception ex) {
            log.error(ex.getMessage())
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)
            preResult.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return preResult
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get task obj for UI
     * @param parameters -N/A
     * @param obj -a map returned from pre condition
     * @return -a map containing customerInfoMap objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            ExhTask exhTask = (ExhTask) receivedResult.get(TASK_OBJECT)
            initPager(params)                     // initialize parameters for flexgrid
            LinkedHashMap serviceReturn = (LinkedHashMap) listNote(exhTask.id)     // get list of task note(s)
            Map taskInfoMap = [            // build a map of task for UI
                    id: exhTask.id,
                    customerId: getCustomerCode(exhTask.customerId),
                    refNo: exhTask.refNo,
                    customerName: exhTask.customerName,
                    beneficiaryName: exhTask.beneficiaryName,
                    amount: Tools.formatAmountWithoutCurrency(exhTask.amountInLocalCurrency) + Tools.SINGLE_SPACE + currencyCacheUtility.localCurrency.symbol,
                    paymentType: exhPaymentMethodCacheUtility.read(exhTask.paymentMethod).key
            ]

            result.put(TASK_INFO_MAP, taskInfoMap)
            result.put(ENTITY_NOTE_LIST, serviceReturn)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            log.error(ex.getMessage())
            return result
        }
    }

    /**
     * Wrap list of notes for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {                                         // cast map returned from execute method
            Map executeResult = (Map) obj
            Map entityNoteList = (Map) executeResult.get(ENTITY_NOTE_LIST)
            List<GroovyRowResult> resultList = (List) entityNoteList.resultList
            int count = (int) entityNoteList.count                // total count of note
            List<GroovyRowResult> listNote = wrapNoteListGridData(resultList, start)            // wrap notes
            Map gridObject = [page: pageNumber, total: count, rows: listNote]
            result.put(GRID_OBJECT, gridObject)
            result.put(TASK_INFO_MAP, executeResult.get(TASK_INFO_MAP))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of notes in grid entity
     * @param lstNotes -list of entityNote object(s)
     * @param start -starting index of the page
     * @return -list of wrapped notes
     */
    private List wrapNoteListGridData(List<GroovyRowResult> lstNotes, int start) {
        List<GroovyRowResult> notes = []
        try {
            int counter = start + 1
            for (int i = 0; i < lstNotes.size(); i++) {
                GridEntity obj = new GridEntity()        // build grid data
                GroovyRowResult note = lstNotes[i]
                String createdOn = DateUtility.getLongDateForUI(note.createdOn)          // date format 'dd-Mon-yyyy'
                String updatedOn = DateUtility.getLongDateForUI(note.updatedOn)
                obj.id = note.id
                obj.cell = [
                        counter,
                        note.id,
                        Tools.makeDetailsShort(note.note, 100),          // make note short if more than 100 characters
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj           // cast map returned from execute method
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String QUERY_TASK_ENTITY_NOTE =
        """
            SELECT enote.id id, enote.note, enote.created_on AS createdOn, enote.updated_on AS updatedOn,
                app_user.username AS username
            FROM entity_note enote
                LEFT JOIN app_user ON app_user.id = enote.created_by
            WHERE enote.entity_type_id =:noteEntityType
                AND enote.entity_id =:taskId
                ORDER BY enote.created_on DESC
                LIMIT :resultPerPage OFFSET :start
        """

    private static final String QUERY_COUNT_ENTITY_NOTE =
        """
            SELECT COUNT(entity_id)  AS count
                 FROM entity_note
                 WHERE entity_note.entity_type_id=:noteEntityType
                    AND entity_note.entity_id=:taskId
        """

    /**
     * Get notes and count by task
     */
    private LinkedHashMap listNote(long taskId) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        // pull note entity type(Task) object
        SystemEntity noteEntityTypeTask = (SystemEntity) noteEntityTypeCacheUtility.readByReservedAndCompany(noteEntityTypeCacheUtility.COMMENT_ENTITY_TYPE_TASK, companyId)

        Map queryParamList = [
                taskId: taskId,
                noteEntityType: noteEntityTypeTask.id,
                resultPerPage: resultPerPage,
                start: start
        ]
        Map queryParamCount = [
                noteEntityType: noteEntityTypeTask.id,
                taskId: taskId
        ]
        List<GroovyRowResult> resultList = executeSelectSql(QUERY_TASK_ENTITY_NOTE, queryParamList)
        List<GroovyRowResult> noteListCount = executeSelectSql(QUERY_COUNT_ENTITY_NOTE, queryParamCount)
        int count = noteListCount[0].count

        return [resultList: resultList, count: count]
    }

    /**
     * Get task by id
     * @param id
     * @return task
     */
    private ExhTask readWithExchangeHouse(long id) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        ExhTask task = ExhTask.findByIdAndCompanyId(id, companyId, [readOnly: true])
        return task
    }

    /**
     * Get customer code by customerId
     * @param customerId
     * @return customer code
     */
    private String getCustomerCode(long customerId) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        ExhCustomer exhCustomer = ExhCustomer.findByIdAndCompanyId(customerId, companyId, [readOnly: true])
        return exhCustomer.code
    }
}
