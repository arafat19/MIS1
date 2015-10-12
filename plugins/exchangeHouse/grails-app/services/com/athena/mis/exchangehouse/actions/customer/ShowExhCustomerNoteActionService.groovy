package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.application.utility.NoteEntityTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for customer note CRUD and list of notes for grid
 *  For details go through Use-Case doc named 'ShowExhCustomerNoteActionService'
 */
class ShowExhCustomerNoteActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass());

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load note information page"
    private final String FAILURE_MSG = "Fail to get customer's information."
    private final String CUSTOMER_INFO_MAP = "customerInfoMap"
    private final String ENTITY_NOTE_LIST = "entityNoteList"
    private static final String GRID_OBJECT = 'gridObject'
    private static final String CUSTOMER = "customer"

    ExhCustomerService exhCustomerService

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility

    /**
     * Get parameters from UI and check customer object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)            // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.customerId) {   // check required params
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long customerId = Tools.parseLongInput(params.customerId)
            if (customerId == 0) {                                  // check parse exception
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            ExhCustomer customer = exhCustomerService.read(customerId)      // get customer object by id
            if (!customer) {             // check whether get customer object exists or not
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            if (customer.companyId != exhSessionUtil.appSessionUtil.getCompanyId()) {     // check company's customer
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(CUSTOMER, customer)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get customer obj for UI
     * @param parameters -N/A
     * @param obj -a map returned from pre condition
     * @return -a map containing customerInfoMap objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj        // cast map returned from execute method
            ExhCustomer customer = (ExhCustomer) preResult.get(CUSTOMER)
            initPager(parameters)                      // initialize parameters for flexgrid
            LinkedHashMap serviceReturn = (LinkedHashMap) noteList(customer.id)

            Map customerInfoMap = [           // a map build for UI information
                    id: customer.id,
                    code: customer.code,
                    name: customer.fullName,
                    nationality: countryCacheUtility.read(customer.countryId).nationality,   // get nationality from country
                    phone: customer.phone ? customer.phone : Tools.EMPTY_SPACE,
                    photoIdType: customer.photoIdTypeId ? exhPhotoIdTypeCacheUtility.read(customer.photoIdTypeId).name : Tools.EMPTY_SPACE,
                    email: customer.email ? customer.email : Tools.EMPTY_SPACE,
                    address: customer.address ? customer.address : Tools.EMPTY_SPACE,
                    sourceOfFund: customer.sourceOfFund ? customer.sourceOfFund : Tools.EMPTY_SPACE,
            ]

            result.put(CUSTOMER_INFO_MAP, customerInfoMap)
            result.put(ENTITY_NOTE_LIST, serviceReturn)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
        try {
            Map executeResult = (Map) obj     // cast map returned from execute method
            Map lstNotes = (Map) executeResult.get(ENTITY_NOTE_LIST)
            List<GroovyRowResult> resultList = (List) lstNotes.noteList
            int count = (int) lstNotes.count              // total count of note
            List<GroovyRowResult> wrappedNotes = wrapNotes(resultList, start)           // wrap notes
            Map gridObject = [page: pageNumber, total: count, rows: wrappedNotes]

            result.put(GRID_OBJECT, gridObject)
            result.put(CUSTOMER_INFO_MAP, executeResult.get(CUSTOMER_INFO_MAP))
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
    private List wrapNotes(List<GroovyRowResult> lstNotes, int start) {
        List<GroovyRowResult> notes = []

        int counter = start + 1
        for (int i = 0; i < lstNotes.size(); i++) {
            GridEntity obj = new GridEntity()         // build grid data
            GroovyRowResult rowResult = lstNotes[i]
            String createdOn = DateUtility.getLongDateForUI(rowResult.createdOn)          // date format 'dd-Mon-yyyy'
            String updatedOn = DateUtility.getLongDateForUI(rowResult.updatedOn)
            obj.id = rowResult.id
            obj.cell = [
                    counter,
                    rowResult.id,
                    Tools.makeDetailsShort(rowResult.note, 100),      // make note short if more than 100 characters
                    rowResult.username,
                    createdOn,
                    updatedOn
            ]
            notes << obj
            counter++
        }
        return notes
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
                LinkedHashMap preResult = (LinkedHashMap) obj          // cast map returned from execute method
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

    /**
     * Get notes and count by customer
     */
    private LinkedHashMap noteList(long customerId) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        // pull note entity type(Customer) object
        SystemEntity noteEntityTypeCustomer = (SystemEntity) noteEntityTypeCacheUtility.readByReservedAndCompany(noteEntityTypeCacheUtility.COMMENT_ENTITY_TYPE_CUSTOMER, companyId)

        String strQuery = """
            SELECT enote.id id, enote.note, enote.created_on AS createdOn, enote.updated_on AS updatedOn,
            app_user.username AS username
            FROM entity_note enote
                LEFT JOIN app_user ON app_user.id = enote.created_by
            WHERE enote.entity_type_id =:entityNoteType
                AND enote.entity_id =:customerId
                ORDER BY enote.created_on DESC
                LIMIT ${resultPerPage} OFFSET ${start}
        """

        String queryCount = """
            SELECT COUNT(entity_id)  AS count
                FROM entity_note
            WHERE entity_note.entity_type_id=:entityNoteType
            AND entity_note.entity_id=:customerId

        """
        Map queryParams = [
                customerId: customerId,
                entityNoteType: noteEntityTypeCustomer.id
        ]

        List<GroovyRowResult> lstNotes = executeSelectSql(strQuery, queryParams)
        List<GroovyRowResult> noteListCount = executeSelectSql(queryCount, queryParams)
        int count = noteListCount[0].count

        return [noteList: lstNotes, count: count]
    }
}
