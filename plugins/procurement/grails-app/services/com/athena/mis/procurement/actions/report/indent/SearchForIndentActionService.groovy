package com.athena.mis.procurement.actions.report.indent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.procurement.entity.ProcIndent
import com.athena.mis.procurement.entity.ProcIndentDetails
import com.athena.mis.procurement.service.IndentService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Search Indent for report
 * For details go through Use-Case doc named 'SearchForIndentActionService'
 */
class SearchForIndentActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    IndentService indentService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private static final String NOT_FOUND = "Indent not found."
    private static final String FAILURE_MSG = "Fail to generate indent report."
    private static final String INDENT_MAP = "indentMap"
    private static final String ITEM_LIST = "itemList"
    private static final String INDENT_OBJ = "indent"
    /**
     * 1. pull indent object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return - map containing indent object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long indentId = Long.parseLong(params.id.toString())
            ProcIndent indent = indentService.read(indentId)
            if (!indent) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }
            result.put(INDENT_OBJ, indent)
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
     * 1. receive indent object from pre execute method
     * 2. pull items if indent.itemCount > 0
     * 3. built indent object
     * 4. built item list
     * @param parameters - N/A
     * @param obj - object from pre execute method
     * @return - newly built indent object, item list
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcIndent indent = (ProcIndent) preResult.get(INDENT_OBJ)
            List<ProcIndentDetails> indentDetailsItemList = []

            if (indent.itemCount > 0) {
                indentDetailsItemList = ProcIndentDetails.findAllByIndentIdAndCompanyId(indent.id, procSessionUtil.appSessionUtil.getAppUser().companyId, [readOnly: true])
            }

            LinkedHashMap indentMap = buildIndentMap(indent)
            List itemList = buildItemList(indentDetailsItemList)

            result.put(INDENT_MAP, indentMap)
            result.put(ITEM_LIST, itemList  as JSON)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj

            preResult.put(Tools.IS_ERROR, preResult.get(Tools.IS_ERROR))
            if (preResult.message) {
                preResult.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                preResult.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return preResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
    /**
     * Create indent object
     * 1. pull project object
     * 2. pull createdBy & approvedBy from appUser
     * @param indent
     * @return - a map containing indent field value
     */
    private LinkedHashMap buildIndentMap(ProcIndent indent) {
        Project project = (Project) projectCacheUtility.read(indent.projectId)
        AppUser createdBy = (AppUser) appUserCacheUtility.read(indent.createdBy)
        AppUser approvedBy = (AppUser) appUserCacheUtility.read(indent.approvedBy)
        LinkedHashMap indentMap = [
                indentId: indent.id,
                createdOn: DateUtility.getDateFormatAsString(indent.createdOn), // po date
                printDate: DateUtility.getDateFormatAsString(new Date()),
                createdBy: createdBy.username,
                approvedBy: approvedBy ? approvedBy.username : Tools.EMPTY_SPACE,
                totalAmount: Tools.formatAmountWithoutCurrency(indent.totalPrice),
                projectName: project.name,
                projectDescription: project.description,
                fromDate: DateUtility.getDateFormatAsString(indent.fromDate),
                toDate: DateUtility.getDateFormatAsString(indent.toDate),
                comments: indent.comments,
        ]
        return indentMap
    }
    /**
     * Get item list related to specific indent
     * @param indentDetailsItemList - indent details item list
     * @return  -item list
     */
    private List buildItemList(List<ProcIndentDetails> indentDetailsItemList) {
        List itemList = []
        Map indentDetailsMap
        for (int i = 0; i < indentDetailsItemList.size(); i++) {
            ProcIndentDetails indentDetails = indentDetailsItemList[i]
            indentDetailsMap = [
                    sl: i+1,
                    name: indentDetails.itemDescription,
                    quantity: Tools.formatAmountWithoutCurrency(indentDetails.quantity) + Tools.SINGLE_SPACE + indentDetails.unit,
                    rate: Tools.makeAmountWithThousandSeparator(indentDetails.rate),
                    amount: Tools.makeAmountWithThousandSeparator(indentDetails.rate * indentDetails.quantity)
            ]
            itemList << indentDetailsMap
        }
        return itemList
    }
}
