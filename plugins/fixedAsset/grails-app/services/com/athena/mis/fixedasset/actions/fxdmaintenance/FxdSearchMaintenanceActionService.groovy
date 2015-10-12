package com.athena.mis.fixedasset.actions.fxdmaintenance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.fixedasset.model.FxdMaintenanceModel
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search fixed asset maintenance and show related list of fixed asset maintenance for grid
 *  For details go through Use-Case doc named 'FxdSearchMaintenanceActionService'
 */
class FxdSearchMaintenanceActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load Fixed Asset Maintenance grid"
    private static final String FXD_MAINTENANCE_LIST = "fxdMaintenanceList"
    private static final String COUNT = "count"

    @Autowired
    FxdSessionUtil fxdSessionUtil

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. initialize pagination
     * 2. pull fixed asset details object
     * 3. pull maintenance list
     * @param parameters - serialize parameters from UI
     * @param obj - N/A
     * @return - a map containing fixed asset maintenance list , fixed asset details object
     *  and isError(true/false)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object ob) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initSearch(parameterMap)
            long companyId = fxdSessionUtil.appSessionUtil.getCompanyId()
            List<FxdMaintenanceModel> fxdMaintenanceList
            int count
            if (parameterMap.fixedAssetDetailsId) {
                long fixedAssetDetailsId = Long.parseLong(parameterMap.fixedAssetDetailsId.toString())
                fxdMaintenanceList = FxdMaintenanceModel.searchWithFixedAssetDetailsId(fixedAssetDetailsId, queryType, query, companyId).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true)
                count = FxdMaintenanceModel.searchWithFixedAssetDetailsId(fixedAssetDetailsId, queryType, query, companyId).count()
            } else {
                fxdMaintenanceList = FxdMaintenanceModel.search(queryType, query, companyId).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true)
                count = FxdMaintenanceModel.search(queryType, query, companyId).count()
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(FXD_MAINTENANCE_LIST, fxdMaintenanceList)
            result.put(COUNT, count)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. receive fixed asset maintenance from execute method
     * @param obj- object returned from execute method
     * @return - a map containing wrapped fixed asset maintenance for grid show
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<FxdMaintenanceModel> fxdMaintenanceList = (List<FxdMaintenanceModel>) executeResult.get(FXD_MAINTENANCE_LIST)
            int count = (int) executeResult.get(COUNT)
            List wrapFxdMaintenanceList = wrapListInGridEntityList(fxdMaintenanceList, start)
            result = [page: pageNumber, total: count, rows: wrapFxdMaintenanceList]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * wrap fixed asset maintenance for grid entity
     * @param fxdMaintenanceList - list of fixed asset maintenance
     * @param start - starting point of index
     * @return - wrapped fixed asset maintenance for grid entity
     */
    private static List wrapListInGridEntityList(List<FxdMaintenanceModel> fxdMaintenanceList, int start) {
        List lstFxdMaintenance = []
        int counter = start + 1
        int len = 0
        if (fxdMaintenanceList != null) {
            len = fxdMaintenanceList.size()
        }
        FxdMaintenanceModel fxdMaintenanceModel
        for (int i = 0; i < len; i++) {
            fxdMaintenanceModel = fxdMaintenanceList[i]
            String description = Tools.makeDetailsShort(fxdMaintenanceModel.description, Tools.DEFAULT_LENGTH_DETAILS_OF_BUDGET)

            GridEntity obj = new GridEntity()
            obj.id = fxdMaintenanceModel.maintenanceId
            obj.cell = [
                    counter,
                    fxdMaintenanceModel.itemName + Tools.SINGLE_SPACE + Tools.PARENTHESIS_START + fxdMaintenanceModel.modelName + Tools.PARENTHESIS_END,
                    fxdMaintenanceModel.maintenanceTypeName,
                    fxdMaintenanceModel.strAmount,
                    fxdMaintenanceModel.strMaintenanceDate,
                    fxdMaintenanceModel.createdByUserName,
                    description
            ]
            lstFxdMaintenance << obj
            counter++
        }
        return lstFxdMaintenance
    }
}
