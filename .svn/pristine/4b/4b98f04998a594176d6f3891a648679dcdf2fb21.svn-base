package com.athena.mis.procurement.actions.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Get up approved Purchase Order list
 * For details go through Use-Case doc named 'ListUnApprovedPurchaseOrderActionService'
 */
class ListUnApprovedPurchaseOrderActionService extends BaseService implements ActionIntf {


    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to populate un-approved PO list"
    private static final String UNAPPROVED_LIST = "unApprovedList"
    private static final String GRID_OBJ = "gridObj"
    private static final String PO_ID = "id"

    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get up approved purchased order
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - purchase order list
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            parameterMap.sortname = PO_ID
            if (!parameterMap.rp) {
                parameterMap.rp = 10  // default result per page =10
                parameterMap.page = 1
            }
            initPager(parameterMap)
            List<Long> projectIds = (List<Long>) procSessionUtil.appSessionUtil.getUserProjectIds()
            List<ProcPurchaseOrder> unApprovedList = []
            int count = 0

            if (projectIds.size() > 0) {
                unApprovedList = ProcPurchaseOrder.upapprovedPOListByProjectIds(projectIds).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true)
                count = ProcPurchaseOrder.upapprovedPOListByProjectIds(projectIds).count()
            }
            result.put(UNAPPROVED_LIST, unApprovedList)
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
     * Wrap un approved po for grid
     * @param obj - object from execute method
     * @return - wrapped purchase order object
     */
    private List wrapPo(List<ProcPurchaseOrder> unApprovedList, int start) {
        List lstUnapprovedPOs = []
        int counter = start + 1
        AppUser createdBy
        for (int i = 0; i < unApprovedList.size(); i++) {
            ProcPurchaseOrder purchaseOrder = unApprovedList[i]
            GridEntity obj = new GridEntity()
            obj.id = purchaseOrder.id
            createdBy = (AppUser) appUserCacheUtility.read(purchaseOrder.createdBy)
            obj.cell = [
                    counter,
                    purchaseOrder.id,
                    createdBy.username,
                    purchaseOrder.approvedByDirectorId > 0 ? Tools.YES : Tools.NO,
                    purchaseOrder.approvedByProjectDirectorId > 0 ? Tools.YES : Tools.NO
            ]
            counter++
            lstUnapprovedPOs << obj
        }
        return lstUnapprovedPOs
    }
    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap for grid
     * @param obj - object from execute method
     * @return - wrapped un-approved purchase order object
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj

            List<ProcPurchaseOrder> unApprovedList = (List<ProcPurchaseOrder>) executeResult.get(UNAPPROVED_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List gridRows = wrapPo(unApprovedList, start)
            Map gridObj = [page: pageNumber, total: count, rows: gridRows]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
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
}
