package com.athena.mis.application.actions.vehicle

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.VehicleCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for vehicle CRUD and list of project for grid
 *  For details go through Use-Case doc named 'ShowVehicleActionService'
 */
class ShowVehicleActionService extends BaseService implements ActionIntf {

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load vehicle page"
    private static final String VEHICLE_LIST = "vehicleList"

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    VehicleCacheUtility vehicleUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * 1. check user access as Admin role
     * @param params -N/A
     * @param obj - N/A
     * @return - a map containing isAccess(true/false) depending on method success &  relevant message.
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }
    /**
     * 1. initialize params for pagination of Flexi-grid
     * 2. pull vehicle list from cache utility
     * @param params -serialize parameters from UI
     * @param obj -N/A
     * @return - vehicle list
     */
    public Object execute(Object params, Object obj) {
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if ((!parameterMap.sortname) || (parameterMap.sortname.toString().equals(ID))) {
                parameterMap.sortname = vehicleUtility.SORT_ON_NAME
                parameterMap.sortorder = ASCENDING_SORT_ORDER
            }
            initPager(params)
            int count = vehicleUtility.count()
            List vehicleList = vehicleUtility.list(this)
            return [vehicleList: vehicleList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap vehicle list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            List<Vehicle> vehicleList = (List<Vehicle>) executeResult.vehicleList
            int count = (int) executeResult.count
            List resultVehicleList = wrapListInGridEntityList(vehicleList, start)
            Map output = [page: pageNumber, total: count, rows: resultVehicleList]
            result.put(VEHICLE_LIST, output)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Build failure result in case of any error
     * @param obj -N/A
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
        return result
    }
    /**
     * Wrap list of vehicle in grid entity
     * @param vehicleList -list of vehicle
     * @param start -starting index of the page
     * @return -list of wrapped vehicle
     */
    private List wrapListInGridEntityList(List<Vehicle> vehicleList, int start) {
        List vehicles = []
        try {
            int counter = start + 1
            for (int i = 0; i < vehicleList.size(); i++) {
                Vehicle vehicle = vehicleList[i]
                GridEntity obj = new GridEntity()
                obj.id = vehicle.id
                obj.cell = [
                        counter,
                        vehicle.id,
                        vehicle.name,
                        vehicle.description ? vehicle.description : Tools.EMPTY_SPACE
                ]
                vehicles << obj
                counter++
            }
            return vehicles
        } catch (Exception ex) {
            log.error(ex.getMessage())
            vehicles = []
            return vehicles
        }
    }
}
