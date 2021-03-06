package com.athena.mis.procurement.actions.report.purchaserequest

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show Purchase Request for report
 * For details go through Use-Case doc named 'ShowForPurchaseRequestActionService'
 */
class ShowForPurchaseRequestActionService extends BaseService implements ActionIntf {

    PurchaseRequestService purchaseRequestService
    BudgetPluginConnector budgetImplService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility

    private static final String FAILURE_MSG = "Fail to show purchase request report."
    private static final String PURCHASE_REQUEST_MAP = "purchaseRequestMap"
    private static final String LST_ITEMS = "lstItems"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. pull purchaseRequest object
     * 2. built new purchaseRequest object
     * 3. built item list
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - newly built purchase request item list
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (params.purchaseRequestId) {
                long purchaseRequestId = Long.parseLong(params.purchaseRequestId.toString())
                ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(purchaseRequestId)
                if (!purchaseRequest) {
                    return result
                }
                LinkedHashMap purchaseRequestMap = buildPurchaseRequestMap(purchaseRequest)
                List<GroovyRowResult> lstItemPRD = listItemByPurchaseRequest(purchaseRequest.id)
                List lstItems = buildItemList(lstItemPRD)
                result.put(LST_ITEMS, lstItems  as JSON)
                result.put(PURCHASE_REQUEST_MAP, purchaseRequestMap)
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
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
    public Object buildSuccessResultForUI(Object result) {
        return null
    }
    /**
     * do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
    /**
     * Get item list related to specific pr
     * @param lstPurchaseRequestDetails - indent details item list
     * @return -item list
     */
    private List buildItemList(List<GroovyRowResult> lstPurchaseRequestDetails) {
        List lstItems = []
        Map prDetails
        for (int i = 0; i < lstPurchaseRequestDetails.size(); i++) {
            GroovyRowResult eachInstance = lstPurchaseRequestDetails[i]
            prDetails = [
                    sl: i + 1,
                    itemType: eachInstance.item_type_name,
                    itemName: eachInstance.name,
                    itemCode: eachInstance.code,
                    quantity: eachInstance.quantity,
                    rate: eachInstance.rate,
                    totalCost: eachInstance.total_cost,
                    rateStr: eachInstance.rate_str,
                    totalCostStr: eachInstance.total_cost_str
            ]
            lstItems << prDetails
        }
        return lstItems
    }
    /**
     * 1. pull budget & budget type object
     * 2. pull unit from system entity
     * 3. pull createdBy, approvedByDirector, approvedByProjectDirector from appUser
     * 4. pull supplier object
     * 5. pull entity content for Director & Project Director
     * @param purchaseRequest - purchase request object
     * @return - newly built purchase request map
     */
    private LinkedHashMap buildPurchaseRequestMap(ProcPurchaseRequest purchaseRequest) {
        Project project = (Project) projectCacheUtility.read(purchaseRequest.projectId)
        AppUser createdBy = (AppUser) appUserCacheUtility.read(purchaseRequest.createdBy)
        AppUser approvedByDirector = (AppUser) appUserCacheUtility.read(purchaseRequest.approvedByDirectorId)
        AppUser approvedByProjectDirector = (AppUser) appUserCacheUtility.read(purchaseRequest.approvedByProjectDirectorId)
        LinkedHashMap purchaseRequestMap = [
                purchaseRequestId: purchaseRequest.id,
                numberOfItems: purchaseRequest.itemCount,
                createdOn: DateUtility.getDateFormatAsString(purchaseRequest.createdOn), // po date
                printDate: DateUtility.getDateFormatAsString(new Date()),
                createdBy: createdBy.username,
                projectName: project.name,
                projectDescription: project.description ? project.description : Tools.EMPTY_SPACE,
                comments: purchaseRequest.comments,
                approvedByDirector: approvedByDirector ? approvedByDirector.username : Tools.EMPTY_SPACE,
                approvedByProjectDirector: approvedByProjectDirector ? approvedByProjectDirector.username : Tools.EMPTY_SPACE,
                grandTotalItemCosts: grandTotalItemsCost(purchaseRequest.id)
        ]
        return purchaseRequestMap
    }

    //@todo:model adjust using PurchaseRequestDetailsModel.listItemByPurchaseRequest(x)
    // used to show material details for PR report
    private List<GroovyRowResult> listItemByPurchaseRequest(long purchaseRequestId) {
        String queryStr = """
        SELECT item.name,item.code, to_char(quantity,'${Tools.DB_QUANTITY_FORMAT}') ||' '||item.unit as quantity,
        to_char(rate,'${Tools.DB_CURRENCY_FORMAT}') AS rate_str,
        (to_char(rate*quantity,'${Tools.DB_CURRENCY_FORMAT}')) AS total_cost_str,
        item_type.name AS item_type_name,
        rate, (rate*quantity) AS total_cost
        FROM proc_purchase_request_details  purchase_request_details
        LEFT JOIN item ON item.id = purchase_request_details.item_id
        LEFT JOIN item_type ON item_type.id= item.item_type_id
        WHERE purchase_request_id=:purchaseRequestId
        ORDER BY item_type.name, item.name
        """
        Map queryParams = [
                purchaseRequestId: purchaseRequestId
        ]
        List<GroovyRowResult> lstPurchaseRequestDetails = executeSelectSql(queryStr, queryParams)
        return lstPurchaseRequestDetails
    }

    private static final String GRAND_TOTAL_COST_QUERY = """
        SELECT SUM((rate*quantity))  AS grand_total_cost
        FROM proc_purchase_request_details  purchase_request_details
        LEFT JOIN item ON item.id = purchase_request_details.item_id
        LEFT JOIN item_type ON item_type.id= item.item_type_id
        WHERE purchase_request_id= :purchaseRequestId;
        """
    private int grandTotalItemsCost(long purchaseRequestId) {
        Map queryParams = [
                purchaseRequestId: purchaseRequestId
        ]
        List countResults = executeSelectSql(GRAND_TOTAL_COST_QUERY, queryParams)
        int grandTotalCost = countResults[0].grand_total_cost
        return grandTotalCost
    }
}
