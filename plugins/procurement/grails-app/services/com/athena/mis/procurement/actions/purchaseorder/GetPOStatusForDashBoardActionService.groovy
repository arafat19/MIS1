package com.athena.mis.procurement.actions.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.procurement.model.ProcPOStatusModel
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get Purchase Order Status For showing in the Dash Board
 * For details go through Use-Case doc named 'GetPOStatusForDashBoardActionService'
 */
class GetPOStatusForDashBoardActionService extends BaseService implements ActionIntf {

    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String ERROR_MESSAGE = "Failed to get po status"
    private static final String PO_STATUS_LIST = "poStatusList"

    /**
     * Do nothing for pre operation
     */
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get purchase status list
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return - A map containing pr status and isError msg(True/False)
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            List<Long> lstProjectId = (List<Long>) procSessionUtil.appSessionUtil.getUserProjectIds()
            if (!parameterMap.rp) {
                parameterMap.rp = 10  // default result per page =10
                parameterMap.page = 1
            }
            initPager(parameterMap)

            List<ProcPOStatusModel> poStatusList = []
            int total = 0

            if (lstProjectId.size() > 0) {
                poStatusList = ProcPOStatusModel.listByProjectIds(lstProjectId).list(max: resultPerPage, offset: start)
                total = ProcPOStatusModel.listByProjectIds(lstProjectId).count()
            }

            result.put(PO_STATUS_LIST, poStatusList)
            result.put(Tools.COUNT, total)
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
     * Wrap grid object
     * @param poStatusList - purchase status list
     * @return - wrapped object for showing Dash Board grid
     */
    private List wrapGridEntityList(List<ProcPOStatusModel> poStatusList) {
        List pos = [] as List
        GridEntity obj
        int count = 1
        for (int i = 0; i < poStatusList.size(); i++) {
            obj = new GridEntity()
            obj.id = poStatusList[i].projectId
            obj.cell = [
                    count++,
                    poStatusList[i].projectId, poStatusList[i].projectCode,
                    poStatusList[i].totalBudget, poStatusList[i].poCount,
                    poStatusList[i].totalPo]
            pos << obj
        }
        return pos
    }
    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap grid for Dash Board
     * @param parameters -N/A
     * @param obj - object from execute method
     * @return -map contains isError(true/false)& relative msg depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List<ProcPOStatusModel> poList = (List<ProcPOStatusModel>) receiveResult.get(PO_STATUS_LIST)
            List poStatusListWrap = wrapGridEntityList(poList)
            Map output = [page: pageNumber, total: count, rows: poStatusListWrap]
            result.put(PO_STATUS_LIST, output)
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
}
