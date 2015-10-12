package com.athena.mis.procurement.actions.proctermsandcondition

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcTermsAndCondition
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for Terms and condition CRUD and list of Terms and condition for grid
 *  For details go through Use-Case doc named 'ShowProcTermsAndConditionActionService'
 */
class ShowProcTermsAndConditionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass());

    PurchaseOrderService purchaseOrderService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Can't load terms and condition"
    private static final String PURCHASE_ORDER_NOT_FOUND = "purchase order not found"
    private static final String TERMS_AND_CONDITION_LIST = "termsAndConditionList"
    private static final String PURCHASE_ORDER_MAP = "purchaseOrderMap"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrder"

    /**
     * 1. check po existence
     * 2. pull po object
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -po object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE);
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.purchaseOrderId) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND);
                return
            }

            long purchaseOrderId = Long.parseLong(params.purchaseOrderId.toString())

            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(purchaseOrderId)
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND);
                return result
            }

            result.put(PURCHASE_ORDER_OBJ, purchaseOrder);
            result.put(Tools.IS_ERROR, Boolean.FALSE);
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * 1. receive po from pre execute method
     * 2. pull terms & conditions list
     * 3. build new po object
     * 4. wrap terms & conditions for grid entity
     * @param parameters - parameters from UI
     * @param obj - object from pre execute method
     * @return - a map containing po object, terms & conditions list
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {

        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            Map receiveResult = (Map) obj
            initPager(params)
            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) receiveResult.get(PURCHASE_ORDER_OBJ)
            List<ProcTermsAndCondition> procTermsAndConditionList = []
            int total = 0

            if (purchaseOrder.id > 0) {
                procTermsAndConditionList = ProcTermsAndCondition.findAllByPurchaseOrderId(purchaseOrder.id, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                total = ProcTermsAndCondition.countByPurchaseOrderId(purchaseOrder.id)
            }

            Map purchaseOrderMap = buildPurchaseOrderMap(purchaseOrder)
            List termsAndConditionListWrap = wrapTermsAndConditionListInGrid(procTermsAndConditionList, start)

            result.put(PURCHASE_ORDER_MAP, purchaseOrderMap)
            result.put(TERMS_AND_CONDITION_LIST, termsAndConditionListWrap)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Wrap list of terms & conditions in grid entity
     * @param procTermsAndConditionList -list of terms & conditions object(s)
     * @param start -starting index of the page
     * @return -list of wrapped terms & conditions
     */
    private List wrapTermsAndConditionListInGrid(List<ProcTermsAndCondition> procTermsAndConditionList, int start) {
        List procTermsAndConditions = [] as List
        int counter = start + 1
        ProcTermsAndCondition procTermsAndCondition
        GridEntity object
        AppUser user
        for (int i = 0; i < procTermsAndConditionList.size(); i++) {
            procTermsAndCondition = procTermsAndConditionList[i]
            object = new GridEntity();
            object.id = procTermsAndCondition.id;
            user = (AppUser) appUserCacheUtility.read(procTermsAndCondition.createdBy)
            object.cell = [counter,
                    procTermsAndCondition.details,
                    user.username
            ]
            procTermsAndConditions << object
            counter++
        }
        return procTermsAndConditions;
    }
    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }
    /**
     * 1. receive terms & conditions from previous method
     * 2. wrapped terms & conditions for grid entity
     * @param obj - object receive from execute method
     * @return - a map containing terms & conditions list, wrapped terms & conditions object
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List procTermsAndConditionList = (List) receiveResult.get(TERMS_AND_CONDITION_LIST)
            Map gridOutput = [page: pageNumber, total: count, rows: procTermsAndConditionList]

            result.put(PURCHASE_ORDER_MAP, receiveResult.get(PURCHASE_ORDER_MAP))
            result.put(TERMS_AND_CONDITION_LIST, gridOutput)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
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
        LinkedHashMap failureResult = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                failureResult.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                failureResult.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return failureResult
        } catch (Exception ex) {
            log.error(ex.getMessage());
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            failureResult.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return failureResult
        }
    }
    /**
     *
     * @param purchaseOrder - po object
     * @return -new purchase order object
     */
    private Map buildPurchaseOrderMap(ProcPurchaseOrder purchaseOrder) {
        Project project = (Project) projectCacheUtility.read(purchaseOrder.projectId)

        Map purchaseOrderMap = [
                purchaseOrderId: purchaseOrder.id,
                projectName: project.name
        ]
        return purchaseOrderMap
    }
}