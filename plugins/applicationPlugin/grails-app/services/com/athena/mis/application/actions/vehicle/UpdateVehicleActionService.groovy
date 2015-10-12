package com.athena.mis.application.actions.vehicle

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.application.service.VehicleService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.VehicleCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update vehicle object and grid data
 *  For details go through Use-Case doc named 'UpdateVehicleActionService'
 */
class UpdateVehicleActionService extends BaseService implements ActionIntf {

    private static final String VEHICLE_UPDATE_FAILURE_MESSAGE = "Vehicle could not be updated"
    private static final String VEHICLE_UPDATE_SUCCESS_MESSAGE = "Vehicle has been updated successfully"
    private static final String VEHICLE_ALREADY_EXIST = "Same Vehicle name already exist"
    private static final String INVALID_INPUT_MSG = "Failed to update vehicle due to invalid input"
    private static final String OBJ_CHANGED_MSG = "Selected vehicle has been changed, Refresh the page again"
    private static final String VEHICLE = "vehicle"

    VehicleService vehicleService
    @Autowired
    VehicleCacheUtility vehicleCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private final Logger log = Logger.getLogger(getClass())
    /**
     * 1. receive vehicle object from controller
     * 2. check user access as Admin role
     * 3. check input validation
     * 4. duplicate check for vehicle-name
     * @param parameters -N/A
     * @param obj - receive vehicle object from controller
     * @return - a map containing isError(true/false) depending on method success &  relevant message.
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            //Check parameters
            if ((!params.id) || (!params.version) || (!params.name)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            long id = Long.parseLong(params.id.toString())
            int version = Integer.parseInt(params.version.toString())

            //Check existing of Obj and version matching
            Vehicle oldVehicle = (Vehicle) vehicleCacheUtility.read(id)
            if ((!oldVehicle) || (oldVehicle.version != version)) {
                result.put(Tools.MESSAGE, OBJ_CHANGED_MSG)
                return result
            }
            // Check existing of same vehicle name
            String name = params.name.toString()
            int duplicateCount = vehicleCacheUtility.countByNameIlikeAndIdNotEqual(name, oldVehicle.id)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, VEHICLE_ALREADY_EXIST)
                return result
            }

            Vehicle vehicle = buildObject(params, oldVehicle)

            result.put(VEHICLE, vehicle)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }
    /**
     * 1. receive vehicle object from pre execute method
     * 2. Update new vehicle
     * 3. update new vehicle to corresponding cache utility & sort cache utility
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters - N/A
     * @param obj - receive vehicle object from pre execute method
     * @return - Integer value(e.g- 1 for success & 0 for failure)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Vehicle vehicle = (Vehicle) preResult.get(VEHICLE)
            vehicleService.update(vehicle)
            vehicleCacheUtility.update(vehicle, vehicleCacheUtility.SORT_ON_NAME, vehicleCacheUtility.SORT_ORDER_ASCENDING)
            result.put(VEHICLE, vehicle)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(VEHICLE_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, VEHICLE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap list of vehicle in grid entity
     * @param obj -vehicle object
     * @return -list of wrapped vehicle
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Vehicle vehicle = (Vehicle) executeResult.get(VEHICLE)
            GridEntity object = new GridEntity()
            object.id = vehicle.id
            object.cell = [
                    Tools.LABEL_NEW,
                    vehicle.id,
                    vehicle.name,
                    vehicle.description
            ]
            Map resultMap = [entity: object, version: vehicle.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, VEHICLE_UPDATE_SUCCESS_MESSAGE)
            result.put(VEHICLE, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, VEHICLE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -N/A
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
                result.put(Tools.MESSAGE, VEHICLE_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, VEHICLE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build Vehicle object
     * @param parameterMap -serialized parameters from UI
     * @param oldVehicle -object of Vehicle
     * @return -new Vehicle object
     */
    private Vehicle buildObject(GrailsParameterMap parameterMap, Vehicle oldVehicle) {
        oldVehicle.name = parameterMap.name.toString()
        oldVehicle.updatedBy = appSessionUtil.getAppUser().id
        oldVehicle.updatedOn = new Date()
        return oldVehicle
    }
}