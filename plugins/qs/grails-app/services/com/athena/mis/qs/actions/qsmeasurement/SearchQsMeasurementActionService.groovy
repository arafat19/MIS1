package com.athena.mis.qs.actions.qsmeasurement

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.qs.model.QsMeasurementModel
import com.athena.mis.qs.utility.QsSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Search QS Measurement
 * For details go through Use-Case doc named 'SearchQsMeasurementActionService'
 */
class SearchQsMeasurementActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    QsSessionUtil qsSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Can't load QS Measurement"
    private static final String WRAPPED_QS_MEASUREMENT = "wrappedQsMeasurement"
    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. initialize pagination
     * 2. pull list of project ids
     * 3. pull qs status list
     * 4. wrap qs status for grid entity
     * @param parameters - serialize parameters from UI
     * @param obj -N/A
     * @return  - wrapped QSM object
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            initSearch(params)

            boolean isGovtQs = Boolean.parseBoolean(params.isGovtQs)
            List<Long> lstProjectIds = (List<Long>) qsSessionUtil.appSessionUtil.getUserProjectIds()

            List<QsMeasurementModel> qsMeasurementList = []
            int total = 0

            if (lstProjectIds.size() > 0) {
                qsMeasurementList = QsMeasurementModel.searchByProjectIdsAndIsGovtAndQueryIlike(lstProjectIds, isGovtQs, queryType, query).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder)
                total = QsMeasurementModel.searchByProjectIdsAndIsGovtAndQueryIlike(lstProjectIds, isGovtQs, queryType, query).count()
            }
            List wrappedQsMeasurement = wrapQsMeasurement(qsMeasurementList, this.start)

            result.put(WRAPPED_QS_MEASUREMENT, wrappedQsMeasurement)
            result.put(Tools.COUNT, total)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * @param obj - object receive from execute method
     * @return - wrapped qsm list
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List wrappedQsDetails = (List) receiveResult.get(WRAPPED_QS_MEASUREMENT)
            result = [page: pageNumber, total: count, rows: wrappedQsDetails]
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
    /**
     *
     * @param qsMeasurementList - qsm list
     * @param start - starting point of index
     * @return - wrapped qsm list
     */
    private List wrapQsMeasurement(List<QsMeasurementModel> qsMeasurementList, int start) {
        List lstQsMeasurement = []
        int counter = start + 1
        QsMeasurementModel qsMeasurement
        GridEntity object
        for (int i = 0; i < qsMeasurementList.size(); i++) {
            qsMeasurement = qsMeasurementList[i]
            object = new GridEntity()
            object.id = qsMeasurement.qsmId
            object.cell = [
                    counter,
                    qsMeasurement.qsmId,
                    qsMeasurement.budgetItem,
                    qsMeasurement.siteName,
                    qsMeasurement.qsmQuantity,
                    qsMeasurement.strQsmDate
            ]
            lstQsMeasurement << object
            counter++
        }
        return lstQsMeasurement
    }
}
