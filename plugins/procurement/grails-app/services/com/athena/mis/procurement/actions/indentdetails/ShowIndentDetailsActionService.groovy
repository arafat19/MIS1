package com.athena.mis.procurement.actions.indentdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.procurement.entity.ProcIndent
import com.athena.mis.procurement.entity.ProcIndentDetails
import com.athena.mis.procurement.service.IndentService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for indent details CRUD and list of indent details for grid
 *  For details go through Use-Case doc named 'ShowIndentDetailsActionService'
 */
class ShowIndentDetailsActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load Indent Details"
    private static final String INDENT_NOT_FOUND = "Indent not found"
    private static final String INDENT_INFO = "indentInfo"
    private static final String GRID_OBJ = "gridObj"
    private static final String INDENT = "indent"
    private static final String INDENT_DETAILS_LIST = "indentDetailsList"
    private static final String COUNT = "count"

    IndentService indentService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    /**
     * 1. check input validation
     * 2. pull indent object
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -indent object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long indentId = Long.parseLong(params.indentId.toString())
            if (indentId < 0) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            ProcIndent indent = indentService.read(indentId)
            if (!indent) {
                result.put(Tools.MESSAGE, INDENT_NOT_FOUND)
                return result
            }

            result.put(INDENT, indent)
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
     * 1. receive indent object from pre execute method
     * 2. pull indent details list
     * 3. count their numbers
     * @param params N/A
     * @param obj - object receive from pre execute method
     * @return - indent details list, indent object
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            ProcIndent indent = (ProcIndent) preResult.get(INDENT)

            initPager(params)
            List<ProcIndentDetails> indentDetailsList = ProcIndentDetails.findAllByIndentId(indent.id, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
            int total = ProcIndentDetails.countByIndentId(indent.id)

            result.put(INDENT_DETAILS_LIST, indentDetailsList)
            result.put(COUNT, total)
            result.put(INDENT, indent)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. receive indent from previous method
     * 2. pull indent details by indent id
     * 3. build indent object
     * 4. wrapped indent details for grid entity
     * @param obj - object receive from execute method
     * @return - a map containing indent object, wrapped grid object
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map receiveResult = (LinkedHashMap) obj

            ProcIndent indent = (ProcIndent) receiveResult.get(INDENT)
            int total = (int) receiveResult.get(COUNT)
            List<ProcIndentDetails> indentDetailsList = (List<ProcIndentDetails>) receiveResult.get(INDENT_DETAILS_LIST)

            Map indentInfo = buildIndentMap(indent)
            List wrappedIndentDetails = wrapIndentDetails(indentDetailsList, start)
            Map gridObject = [page: pageNumber, total: total, rows: wrappedIndentDetails]

            result.put(INDENT_INFO, indentInfo)
            result.put(GRID_OBJ, gridObject)
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
            if (receiveResult.message) {
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
     * @param indent - indent object
     * @return - newly created indent object
     */
    Map buildIndentMap(ProcIndent indent) {
        Project project = (Project) projectCacheUtility.read(indent.projectId)

        Map indentMap = [
                projectId: project.id,
                projectName: project.name,
                indentId: indent.id
        ]
        return indentMap
    }
    /**
     * Wrap list of indent in grid entity
     * @param indentDetailsList -list of indent details object(s)
     * @param start -starting index of the page
     * @return -list of wrapped indent details
     */
    private List wrapIndentDetails(List<ProcIndentDetails> indentDetailsList, int start) {
        List lstIndentDetails = [] as List
        int counter = start + 1
        GridEntity obj
        for (int i = 0; i < indentDetailsList.size(); i++) {
            ProcIndentDetails indentDetails = indentDetailsList[i]
            obj = new GridEntity()
            obj.id = indentDetails.id
            obj.cell = [
                    counter,
                    indentDetails.itemDescription,
                    Tools.formatAmountWithoutCurrency(indentDetails.quantity) + Tools.SINGLE_SPACE + indentDetails.unit,
                    Tools.makeAmountWithThousandSeparator(indentDetails.rate),
                    Tools.makeAmountWithThousandSeparator(indentDetails.quantity * indentDetails.rate)
            ]
            lstIndentDetails << obj
            counter++
        }
        return lstIndentDetails
    }
}
