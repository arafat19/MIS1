package com.athena.mis.qs.actions.qsmeasurement

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.qs.model.QsStatusModel
import com.athena.mis.qs.utility.QsSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get QS Measurement Status for Dash Board
 * For details go through Use-Case doc named 'GetQsStatusForDashBoardActionService'
 */
class GetQsStatusForDashBoardActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    QsSessionUtil qsSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private static final String ERROR_MESSAGE = "Failed to get qs status"
    private static final String QS_STATUS_LIST = "qsStatusList"
    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. pull list of project ids
     * 2. initialize pagination
     * 3. pull qs status list
     * @param parameters - serialize parameters from UI
     * @param obj -N/A
     * @return  - QS status list
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            List<Long> lstProjectId = (List<Long>) qsSessionUtil.appSessionUtil.getUserProjectIds()
            if (!parameterMap.rp) {
                parameterMap.rp = 10  // default result per page =10
                parameterMap.page = 1
            }
            initPager(parameterMap)

            List<QsStatusModel> qsStatusList = []
            int total = 0

            if (lstProjectId.size() > 0) {
                qsStatusList = QsStatusModel.listByProjectIds(lstProjectId).list(max: resultPerPage, offset: start)
                total = QsStatusModel.listByProjectIds(lstProjectId).count()
            }

            result.put(QS_STATUS_LIST, qsStatusList)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * @param obj - object receive from execute method
     * @return - wrapped qs status list
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List<QsStatusModel> qsList = (List<QsStatusModel>) receiveResult.get(QS_STATUS_LIST)
            List qsStatusListWrap = wrapGridEntityList(qsList)
            Map output = [page: pageNumber, total: count, rows: qsStatusListWrap]
            result.put(QS_STATUS_LIST, output)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }
    /**
     *
     * @param qsStatusList - qs status list
     * @param start - starting point of index
     * @return - wrapped qs status list
     */
    private List wrapGridEntityList(List<QsStatusModel> qsStatusList) {
        List qss = []
        GridEntity obj
        int count = 1
        for (int i = 0; i < qsStatusList.size(); i++) {
            obj = new GridEntity()
            obj.id = qsStatusList[i].projectId
            obj.cell = [
                    count++,
                    qsStatusList[i].projectId,
                    qsStatusList[i].projectCode,
                    qsStatusList[i].strAchievedIntern,
                    qsStatusList[i].workCertifiedIntern
            ]
            qss << obj
        }
        return qss
    }
}
