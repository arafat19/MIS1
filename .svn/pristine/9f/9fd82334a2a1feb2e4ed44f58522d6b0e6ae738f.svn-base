package com.athena.mis.sarb.actions.taskmodel

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.sarb.model.SarbTaskModel
import com.athena.mis.sarb.service.SarbTaskModelService
import com.athena.mis.sarb.utility.SarbSessionUtil
import com.athena.mis.sarb.utility.SarbTaskReviseStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * list cancel task for send to sarb
 * for details go through use-case named "ListCancelTaskForSendToSarbActionService"
 */
class ListCancelTaskForSendToSarbActionService extends BaseService implements ActionIntf {

    @Autowired
    SarbSessionUtil sarbSessionUtil
    @Autowired
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired
    SarbTaskReviseStatusCacheUtility sarbTaskReviseStatusCacheUtility
    SarbTaskModelService sarbTaskModelService

    private static final String LOAD_FAILED = "Failed to load task lists."
    private static final String LST_TASK = "lstTask"
    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for executePreCondition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for executePostCondition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get parameters from UI and list,count all cancel task from db
     * @param parameters
     * @param obj -N/A
     * @return list and count
     */
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initPager(params)
            long companyId = sarbSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity reviseStatus = (SystemEntity) sarbTaskReviseStatusCacheUtility.readByReservedAndCompany(SarbTaskReviseStatusCacheUtility.MOVED_FOR_CANCEL, companyId)
            List<Long> lstStatusForSendTask = exchangeHouseImplService.listTaskStatusForSarb()
            List<SarbTaskModel> lstSarbTaskModel = sarbTaskModelService.listAllTaskForSendToSarb(lstStatusForSendTask, companyId, reviseStatus.id, this)
            int count = sarbTaskModelService.countAllTaskForSendToSarb(lstStatusForSendTask, companyId, reviseStatus.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(LST_TASK, lstSarbTaskModel)
            result.put(Tools.COUNT, count)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, LOAD_FAILED)
            return result
        }
    }

    /**
     * Wrap sarb task details list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<SarbTaskModel> lstSarbTask = (List<SarbTaskModel>) executeResult.get(LST_TASK)
            int count = (int) executeResult.get(Tools.COUNT)
            List wrappedSarbTaskModel = wrapListSarbTaskModel(lstSarbTask, start)
            Map output = [page: pageNumber, total: count, rows: wrappedSarbTaskModel]
            return output
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, LOAD_FAILED)
            return result
        }
    }

    /**
     * Do nothing
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Wrap list of sarb task in grid entity
     * @param lstSarbTaskModel -list of  object(s)
     * @param start -starting index of the page
     * @return -list of wrapped objects
     */
    private List wrapListSarbTaskModel(List<SarbTaskModel> lstSarbTaskModel, int start) {
        List SarbTaskModelList = []
        int counter = start + 1
        for (int i = 0; i < lstSarbTaskModel.size(); i++) {
            SarbTaskModel sarbTaskModel = lstSarbTaskModel[i]
            GridEntity obj = new GridEntity()
            obj.id = sarbTaskModel.id
            obj.cell = [
                    counter,
                    sarbTaskModel.id,
                    sarbTaskModel.refNo,
                    sarbTaskModel.amountInForeignCurrency,
                    sarbTaskModel.amountInLocalCurrency,
                    sarbTaskModel.customerName,
                    sarbTaskModel.beneficiaryName,
                    DateUtility.getLongDateForUI(sarbTaskModel.createdOn)
            ]
            SarbTaskModelList << obj
            counter++
        }
        return SarbTaskModelList
    }
}
