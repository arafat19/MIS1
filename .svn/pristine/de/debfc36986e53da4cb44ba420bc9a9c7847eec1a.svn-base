package com.athena.mis.procurement.actions.report.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.PaymentMethodCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show Purchase Order for report
 * For details go through Use-Case doc named 'ShowForPurchaseOrderActionService'
 */
class ShowForPurchaseOrderActionService extends BaseService implements ActionIntf {

    PurchaseOrderService purchaseOrderService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    PaymentMethodCacheUtility paymentMethodCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility

    private static final String FAILURE_MSG = "Fail to generate Purchase Order."
    private static final String PURCHASE_ORDER_MAP = "purchaseOrderMap"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrder"
    private static final String ITEM_LIST = "itemList"
    private static final String TERMS_AND_CONDITION_LIST = "termsAndConditionList"
    private static final String PURCHASE_ORDER_NOT_APPROVED = "Purchase Order is not approved."
    private static final String IS_APPROVED = "isApproved"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * 1. pull purchase order object
     * 2. check purchase order approval
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return - map containing purchase order object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcPurchaseOrder purchaseOrderInstance = (ProcPurchaseOrder) obj
            //check the purchase order approval
            ProcPurchaseOrder purchaseOrder = purchaseOrderService.readApprovedPurchaseOrder(purchaseOrderInstance.id)
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_APPROVED)
                return result
            }

            result.put(PURCHASE_ORDER_OBJ, purchaseOrderInstance)
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

            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (params.purchaseOrderId) {
                long purchaseOrderId = Long.parseLong(params.purchaseOrderId.toString())
                ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(purchaseOrderId)
                if (!purchaseOrder) {
                    return result
                }
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
            }
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
        Object supplier = supplierCacheUtility.read(purchaseOrder.supplierId)
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
                isCancelled: Tools.FALSE

        ]
        return purchaseOrderMap
    }

    // used to show work details for PO report
    private List<GroovyRowResult> listItemByPurchaseOrderId(long purchaseOrderId) {
        String queryStr = """
        SELECT item.name,item.code, to_char(quantity,'${Tools.DB_QUANTITY_FORMAT}') ||' '||item.unit as quantity,
        (to_char(rate,'${Tools.DB_CURRENCY_FORMAT}')) AS rate,
        (to_char(rate*quantity,'${Tools.DB_CURRENCY_FORMAT}')) AS total_cost,
        item_type.name AS item_type
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

