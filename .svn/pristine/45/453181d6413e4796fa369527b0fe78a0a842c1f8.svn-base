package com.athena.mis.procurement.actions.report.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.*
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Search Purchase Order for report
 * For details go through Use-Case doc named 'SearchForPurchaseOrderActionService'
 */
class SearchForPurchaseOrderActionService extends BaseService implements ActionIntf {

    PurchaseOrderService purchaseOrderService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    PaymentMethodCacheUtility paymentMethodCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility

    private static final String PURCHASE_ORDER_NOT_FOUND = "Purchase Order not found."
    private static final String FAILURE_MSG = "Fail to generate Purchase Order."
    private static final String PURCHASE_ORDER_MAP = "purchaseOrderMap"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrder"
    private static final String ITEM_LIST = "itemList"
    private static final String TERMS_AND_CONDITION_LIST = "termsAndConditionList"
    private static final String USER_PROJECT_NOT_MAPPED = "User is not associated with any project"
    private static final String USER_HAS_NOT_ACCESS = "User is not associated with this project"
    private static final String IS_APPROVED = "isApproved"
    private static final String CANCELLED_FLAG = "isCancelled"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * 1. pull project ids related to logged user
     * 2. pull purchase order object
     * 3. check accessibility of project
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return - map containing purchase order object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            if (!params.purchaseOrderId) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                return result
            }

            long purchaseOrderId = Long.parseLong(params.purchaseOrderId)

            List<Long> loggedInUserProjectIds = (List<Long>) procSessionUtil.appSessionUtil.getUserProjectIds()
            if (loggedInUserProjectIds.size() <= 0) {
                result.put(Tools.MESSAGE, USER_PROJECT_NOT_MAPPED)
                return result
            }

            ProcPurchaseOrder purchaseOrder = ProcPurchaseOrder.findByIdAndCompanyId(purchaseOrderId, procSessionUtil.appSessionUtil.getAppUser().companyId, [readOnly: true])

            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                return result
            }

            boolean isAccessible = projectCacheUtility.isAccessible(purchaseOrder.projectId)
            if (!isAccessible) {
                result.put(Tools.MESSAGE, USER_HAS_NOT_ACCESS)
                return result
            }
            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
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
     * 1. receive purchaseOrder object from pre execute method
     * 2. pull item list
     * 3. pull terms & condition list
     * 4. built purchaseOrder object
     * 5. check the purchase order approval
     * @param parameters - N/A
     * @param obj - object from pre execute method
     * @return - newly built purchase order object, item list, terms & condition list
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) preResult.get(PURCHASE_ORDER_OBJ)

            List<GroovyRowResult> itemList = listItemByPurchaseOrderId(purchaseOrder.id)
            List lstItems = buildItemList(itemList)
            List<GroovyRowResult> termsAndConditionList = listTermsAndConditionByPurchaseOrder(purchaseOrder.id)
            List lstTermsAndCon = buildTermsAndCondition(termsAndConditionList)

            LinkedHashMap purchaseOrderMap = buildPurchaseOrderMap(purchaseOrder)

            result.put(IS_APPROVED, Boolean.FALSE)
            //check the purchase order approval
            purchaseOrder = purchaseOrderService.readApprovedPurchaseOrder(purchaseOrder.id)
            if (purchaseOrder) {
                result.put(IS_APPROVED, Boolean.TRUE)
            }
            result.put(PURCHASE_ORDER_MAP, purchaseOrderMap)
            result.put(ITEM_LIST, lstItems as JSON)
            result.put(TERMS_AND_CONDITION_LIST, lstTermsAndCon as JSON)
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
     * @param obj - object from execute method
     * @return - purchase order map, terms & conditions, is_approved, item list
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(CANCELLED_FLAG, Tools.FALSE)
            result.put(PURCHASE_ORDER_MAP, executeResult.get(PURCHASE_ORDER_MAP))
            result.put(IS_APPROVED, executeResult.get(IS_APPROVED))
            result.put(ITEM_LIST, executeResult.get(ITEM_LIST))
            result.put(TERMS_AND_CONDITION_LIST, executeResult.get(TERMS_AND_CONDITION_LIST))
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.message) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
    /**
     * 1. pull project related to specific purchase order
     * 2. pull lastUpdatedBy, approvedByDirector, approvedByProjectDirector from appUser
     * 3. pull supplier object
     * 4. pull entity content for Director & Project Director
     * @param purchaseOrder - purchase order object
     * @return - newly built purchase order map
     */
    private LinkedHashMap buildPurchaseOrderMap(ProcPurchaseOrder purchaseOrder) {
        Project project = (Project) projectCacheUtility.read(purchaseOrder.projectId)
        AppUser lastUpdatedBy = (AppUser) (purchaseOrder.updatedBy > 0 ? appUserCacheUtility.read(purchaseOrder.updatedBy) : appUserCacheUtility.read(purchaseOrder.createdBy))
        Supplier supplier = (Supplier) supplierCacheUtility.read(purchaseOrder.supplierId)
        SystemEntity paymentMethod = (SystemEntity) paymentMethodCacheUtility.read(purchaseOrder.paymentMethodId)
        AppUser approvedByDirector = (AppUser) appUserCacheUtility.read(purchaseOrder.approvedByDirectorId)
        AppUser approvedByProjectDirector = (AppUser) appUserCacheUtility.read(purchaseOrder.approvedByProjectDirectorId)
        LinkedHashMap purchaseOrderMap = [
                purchaseOrderId: purchaseOrder.id,
                lastUpdatedOn: purchaseOrder.updatedOn ? DateUtility.getDateFormatAsString(purchaseOrder.updatedOn) : DateUtility.getDateFormatAsString(purchaseOrder.createdOn), // po date
                printDate: DateUtility.getDateFormatAsString(new Date()),
                lastUpdatedBy: lastUpdatedBy.username,
                projectName: project.name,
                totalTransportCost: Tools.makeAmountWithThousandSeparator(purchaseOrder.trCostTotal),
                netPrice: Tools.makeAmountWithThousandSeparator(purchaseOrder.totalPrice),
                discount: Tools.makeAmountWithThousandSeparator(purchaseOrder.discount),
                totalVatTax: Tools.makeAmountWithThousandSeparator(purchaseOrder.totalVatTax),
                supplierName: supplier.name,
                supplierAddress: supplier.address ? supplier.address : Tools.EMPTY_SPACE,
                paymentMethod: paymentMethod.key, //payment method such as cash, cheque
                paymentTerms: purchaseOrder.modeOfPayment, //modeOfPayment
                approvedByDirector: approvedByDirector ? approvedByDirector.username : Tools.EMPTY_SPACE,
                approvedByProjectDirector: approvedByProjectDirector ? approvedByProjectDirector.username : Tools.EMPTY_SPACE,
                totalItemAmount: getTotalAmountOfItem(purchaseOrder.id),
                itemCount: purchaseOrder.itemCount,
                isCancelled: Tools.FALSE,
                cancelReason: Tools.EMPTY_SPACE
        ]
        return purchaseOrderMap
    }

    // used to show work details for PO report
    private List<GroovyRowResult> listItemByPurchaseOrderId(long purchaseOrderId) {
        String queryStr = """
        SELECT item.name,item.code, to_char(quantity,'${Tools.DB_QUANTITY_FORMAT}') ||' '||item.unit as quantity,
        (to_char(rate,'${Tools.DB_CURRENCY_FORMAT}')) AS rate,
        (to_char(rate*quantity,'${Tools.DB_CURRENCY_FORMAT}')) AS total_cost,
        item_type.name AS  item_type
        FROM proc_purchase_order_details purchase_order_details
        LEFT JOIN item item ON item.id= purchase_order_details.item_id
        LEFT JOIN item_type ON item_type.id= item.item_type_id
        WHERE purchase_order_id=:purchaseOrderId
        AND purchase_order_details.item_id > 0
        ORDER BY item_type.name, item.name
        """
        Map queryParams = [
                purchaseOrderId: purchaseOrderId
        ]
        List<GroovyRowResult> lstPurchaseOrderDetails = executeSelectSql(queryStr, queryParams)
        return lstPurchaseOrderDetails
    }

    private static final String PROC_TERMS_AND_CONDITION_SELECT_QUERY = """
            SELECT id, details
            FROM proc_terms_and_condition
            WHERE purchase_order_id =:purchaseOrderId
            ORDER BY id
        """
    // Proc Terms And Condition List for report
    private List<GroovyRowResult> listTermsAndConditionByPurchaseOrder(long purchaseOrderId) {
        Map queryParams = [
                purchaseOrderId: purchaseOrderId
        ]
        List<GroovyRowResult> resultList = executeSelectSql(PROC_TERMS_AND_CONDITION_SELECT_QUERY, queryParams)

        return resultList
    }

    private String getTotalAmountOfItem(long purchaseOrderId) {
        String queryStr = """SELECT to_char(SUM(quantity*rate),'${Tools.DB_CURRENCY_FORMAT}') AS total_item_amount
            FROM proc_purchase_order_details
            WHERE purchase_order_id =:purchaseOrderId
        """
        Map queryParams = [
                purchaseOrderId: purchaseOrderId
        ]
        List<GroovyRowResult> resultList = executeSelectSql(queryStr, queryParams)
        return resultList[0].total_item_amount
    }

    /**
     * Get item list related to specific po
     * @param lstPurchaseOrderDetails - PO item list
     * @return -item list
     */
    private List buildItemList(List<GroovyRowResult> lstPurchaseOrderDetails) {
        List lstItems = []
        Map poDetails
        for (int i = 0; i < lstPurchaseOrderDetails.size(); i++) {
            GroovyRowResult eachInstance = lstPurchaseOrderDetails[i]
            poDetails = [
                    sl: i + 1,
                    itemType: eachInstance.item_type,
                    itemName: eachInstance.name,
                    itemCode: eachInstance.code,
                    quantity: eachInstance.quantity,
                    rate: eachInstance.rate,
                    totalCost: eachInstance.total_cost,
                    rateStr: eachInstance.rate,
                    totalCostStr: eachInstance.total_cost
            ]
            lstItems << poDetails
        }
        return lstItems
    }

    private List buildTermsAndCondition(List<GroovyRowResult> lstTermsAndCondition) {
        List lstTermsAndCond = []
        Map termsAndCondDetails
        for (int i = 0; i < lstTermsAndCondition.size(); i++) {
            GroovyRowResult eachInstance = lstTermsAndCondition[i]
            termsAndCondDetails = [
                    sl: i + 1,
                    details: eachInstance.details,
            ]
            lstTermsAndCond << termsAndCondDetails
        }
        return lstTermsAndCond
    }
}
