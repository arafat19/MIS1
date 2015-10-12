package com.athena.mis.fixedasset.actions.fxdcategorymaintenancetype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * List of Category Maintenance Type.
 * For details go through Use-Case doc named 'FxdListCategoryMaintenanceTypeActionService'
 */
class FxdListCategoryMaintenanceTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    FxdSessionUtil fxdSessionUtil

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load Category-Maintenance Type grid"
    private static final String FXD_CATEGORY_MAINTENANCE_TYPE_LIST = "fxdCategoryMaintenanceTypeList"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred due to invalid input"

    /**
     * check access permission to show category-maintenance type mapping CRUD page
     * @param parameters - Params from UI
     * @param obj -N/A
     * @return -a map containing input validation message
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.itemId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * 1. initialize for pagination
     * 2. pull category maintenance type from cache utility
     * @param params - serialized parameters from UI
     * @param obj -N/A
     * @return - list of category maintenance type
     */
    public Object execute(Object parameters, Object ob) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameters)

            long itemId = Long.parseLong(parameterMap.itemId.toString())

            Map serviceReturn = listCatMaintenanceType(itemId)
            List<GroovyRowResult> fxdCategoryMaintenanceTypeList = (List<GroovyRowResult>) serviceReturn.catMaintenanceList
            int count = (int) serviceReturn.count


            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(FXD_CATEGORY_MAINTENANCE_TYPE_LIST, fxdCategoryMaintenanceTypeList)
            result.put(Tools.COUNT, count)
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
     * 1. receive category maintenance type from execute method
     * @param obj - object returned from execute method
     * @return - a map containing wrapped category maintenance type for grid show
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> fxdCategoryMaintenanceTypeList = (List<GroovyRowResult>) executeResult.get(FXD_CATEGORY_MAINTENANCE_TYPE_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List categoryMaintenanceTypeList = wrapListInGridEntityList(fxdCategoryMaintenanceTypeList, start)
            result = [page: pageNumber, total: count, rows: categoryMaintenanceTypeList]
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
     * wrap category maintenance type for grid entity
     * @param fxdCategoryMaintenanceTypeList - list of category maintenance type
     * @param start - starting point of index
     * @return - wrapped category maintenance type for grid entity
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> fxdCategoryMaintenanceTypeList, int start) {
        List lstCategoryMaintenanceType = []
        int counter = start + 1

        for (int i = 0; i < fxdCategoryMaintenanceTypeList.size(); i++) {
            GroovyRowResult fxdCategoryMaintenanceType = fxdCategoryMaintenanceTypeList[i]
            GridEntity obj = new GridEntity()
            obj.id = fxdCategoryMaintenanceType.id
            obj.cell = [
                    counter,
                    fxdCategoryMaintenanceType.fxd_maintenance_type_name
            ]
            lstCategoryMaintenanceType << obj
            counter++
        }
        return lstCategoryMaintenanceType
    }

    private static final String SELECT_QUERY = """
            SELECT fcmt.id, item.name AS item_name, fmt.name AS fxd_maintenance_type_name, item.id AS item_id
            FROM fxd_category_maintenance_type fcmt
            LEFT JOIN item ON item.id = fcmt.item_id
            LEFT JOIN fxd_maintenance_type fmt ON fmt.id = fcmt.maintenance_type_id
            WHERE fcmt.item_id = :itemId
            AND fcmt.company_id = :companyId
            ORDER BY fcmt.id ASC
            LIMIT :resultPerPage OFFSET :start
        """

    private static final String COUNT_QUERY = """
                SELECT COUNT(fcmt.id)
                FROM fxd_category_maintenance_type fcmt
                LEFT JOIN item ON item.id = fcmt.item_id
                LEFT JOIN fxd_maintenance_type fmt ON fmt.id = fcmt.maintenance_type_id
                WHERE fcmt.item_id = :itemId
                AND fcmt.company_id = :companyId
        """

    private Map listCatMaintenanceType(long itemId) {
        Map queryParams = [
                companyId: fxdSessionUtil.appSessionUtil.getCompanyId(),
                itemId: itemId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstCatMaintenanceType = executeSelectSql(SELECT_QUERY, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(COUNT_QUERY, queryParams)
        int count = (int) resultCount[0][0]
        return [catMaintenanceList: lstCatMaintenanceType, count: count]
    }
}
