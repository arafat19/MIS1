package com.athena.mis.qs.actions.qsmeasurement

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.qs.model.QsMeasurementModel
import com.athena.mis.qs.utility.QsSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show Government QS Measurement in the Grid
 *  For details go through Use-Case doc named 'ShowGovtQsMeasurementActionService'
 */
class ShowGovtQsMeasurementActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InventoryPluginConnector inventoryImplService

    @Autowired
    QsSessionUtil qsSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Can't load QS Measurement"
    private static final String WRAPPED_QS = "wrappedQs"
    private static final String GRID_OBJ_QS = "gridObjQs"
    private static final String COUNT_QS = "countQS"
    private static final String HAVE_NO_ASSOCIATED_PROJECT = "User is not associated with any project"
    private static final String LST_PROJECT_IDS = "lstProjectIds"
    private static final String LST_SITE_OBJ = "lstSiteObj"

    /**
     * 1. pull list of project ids
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -list of project ids
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            List<Long> lstProjectIds = (List<Long>) qsSessionUtil.appSessionUtil.getUserProjectIds()
            if (lstProjectIds.size() <= 0) {
                result.put(Tools.MESSAGE, HAVE_NO_ASSOCIATED_PROJECT)
                return result
            }

            result.put(LST_PROJECT_IDS, lstProjectIds)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * 1. receive project ids from pre execute method
     * 2. pull qsm list
     * 3. count their numbers
     * 4. wrap qsm for grid entity
     * 5. pull site object
     * @param params N/A
     * @param obj - object receive from pre execute method
     * @return - site list, wrapped qsm object
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map receiveResult = (Map) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            List<Long> lstProjectIds = (List) receiveResult.get(LST_PROJECT_IDS)

            initPager(params)
            boolean isGovtQs = true
            resultPerPage = DEFAULT_RESULT_PER_PAGE
            resultPerPage = 20

            List<QsMeasurementModel> qsMeasurementList = []
            int countQs = 0
            if (lstProjectIds.size() > 0) {
                qsMeasurementList = QsMeasurementModel.listByProjectIdsAndIsGovt(lstProjectIds, isGovtQs).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder)
                countQs = QsMeasurementModel.listByProjectIdsAndIsGovt(lstProjectIds, isGovtQs).count()
            }
            List wrappedQsMeasurement = wrapQsMeasurement(qsMeasurementList, start)

            List<Object> lstSiteObj = (List<Object>) inventoryImplService.getUserInventoriesByType(inventoryImplService.getInventoryTypeSiteId())

            result.put(LST_SITE_OBJ, lstSiteObj)
            result.put(WRAPPED_QS, wrappedQsMeasurement)
            result.put(COUNT_QS, countQs)
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
     * 1. receive wrapped qs object from previous method
     * 2. set all for grid entity
     * @param obj - object receive from execute method
     * @return - a map containing qs object, site object
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            Integer countQs = (Integer) receiveResult.get(COUNT_QS)
            List wrappedQs = (List) receiveResult.get(WRAPPED_QS)
            Map gridObjQs = [page: pageNumber, total: countQs, rows: wrappedQs]

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ_QS, gridObjQs)
            result.put(LST_SITE_OBJ, receiveResult.get(LST_SITE_OBJ))
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
     * Wrap list of qsm in grid entity
     * @param qsMeasurementList -list of qsm object(s)
     * @param start -starting index of the page
     * @return -list of wrapped qsm
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
            object.cell = [counter,
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