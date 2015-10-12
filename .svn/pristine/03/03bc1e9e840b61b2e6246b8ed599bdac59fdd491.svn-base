package com.athena.mis.procurement.actions.indentdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.procurement.entity.ProcIndentDetails
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Show list of Indent Details for grid
 * For details go through Use-Case doc named 'ListIndentDetailsActionService'
 */
class ListIndentDetailsActionService extends BaseService implements ActionIntf {

    private static final String ERROR_MESSAGE = "Fail to load Indent details grid"
    private static final String INDENT_DETAILS_LIST = "indentDetailsList"
    private static final String COUNT = "count"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get indent details list for grid
     * 1. pull all indents details by indent id.
     * 2. count total indent
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            initPager(parameterMap)
            long indentId = Long.parseLong(parameterMap.indentId.toString())

            List<ProcIndentDetails> indentDetailsList = ProcIndentDetails.findAllByIndentId(indentId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
            int total = ProcIndentDetails.countByIndentId(indentId)

            result.put(INDENT_DETAILS_LIST, indentDetailsList)
            result.put(COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
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
     * Wrap indent details list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List<ProcIndentDetails> indentDetailsList = (List<ProcIndentDetails>) receiveResult.get(INDENT_DETAILS_LIST)
            List wrappedIndentDetailsGrid = wrapIndentDetails(indentDetailsList, start)
            result = [page: pageNumber, total: count, rows: wrappedIndentDetailsGrid]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
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
                result.put(Tools.MESSAGE, ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Wrap list of indent details in grid entity
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
