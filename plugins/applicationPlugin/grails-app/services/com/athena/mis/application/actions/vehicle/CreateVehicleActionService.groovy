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
 *  Create new vehicle object and show in grid
 *  For details go through Use-Case doc named 'CreateVehicleActionService'
 */
class CreateVehicleActionService extends BaseService implements ActionIntf {

    private static final String VEHICLE_SAVE_SUCCESS_MESSAGE = "Vehicle has been saved successfully"
    private static final String VEHICLE_SAVE_FAILURE_MESSAGE = "Vehicle could not be saved"
    private static final String VEHICLE_ALREADY_EXIST = "Same Vehicle name already exist"
    private static final String INVALID_INPUT_MSG = "Failed to create vehicle due to invalid input"
    private static final String VEHICLE = "vehicle"

    private final Logger log = Logger.getLogger(getClass())

    VehicleService vehicleService
    @Autowired
    VehicleCacheUtility vehicleCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * 1. receive vehicle object
     * 2. check user access as Admin role
     * 3. duplicate check for vehicle-name
     * @param params -N/A
     * @param obj - receive vehicle object from controller
     * @return - a map containing isError(true/false) depending on method success &  relevant message.
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameterMap, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameterMap

            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            //Check parameters
            if ((!params.name)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }
            int duplicateCount = vehicleCacheUtility.countByNameIlike(params.name)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, VEHICLE_ALREADY_EXIST)
                return result
            }

            Vehicle vehicle = buildObject(params)

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
     * 1. receive vehicle object from controller
     * 2. create new vehicle
     * 3. add new vehicle to corresponding cache utility & sort cache utility
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters - N/A
     * @param obj - receive vehicle object from controller
     * @return - newly built vehicle object.
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Vehicle vehicle = (Vehicle) preResult.get(VEHICLE)
            Vehicle newVehicle = vehicleService.create(vehicle)
            vehicleCacheUtility.add(newVehicle, vehicleCacheUtility.SORT_ON_NAME, vehicleCacheUtility.SORT_ORDER_ASCENDING)
            result.put(VEHICLE, newVehicle)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(VEHICLE_SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, VEHICLE_SAVE_FAILURE_MESSAGE)
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
     * 1. Show newly created vehicle object in grid
     * 2. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj   // cast map returned from execute method
            Vehicle vehicleInstance = (Vehicle) receiveResult.get(VEHICLE)
            GridEntity object = new GridEntity()
            object.id = vehicleInstance.id
            object.cell = [
                    Tools.LABEL_NEW,
                    vehicleInstance.id,
                    vehicleInstance.name,
                    vehicleInstance.description ? vehicleInstance.description : Tools.EMPTY_SPACE
            ]
            Map resultMap = [entity: object, version: vehicleInstance.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, VEHICLE_SAVE_SUCCESS_MESSAGE)
            result.put(VEHICLE, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, VEHICLE_SAVE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, VEHICLE_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, VEHICLE_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build Vehicle object
     * @param parameterMap -serialized parameters from UI
     * @return -new designation object
     */
    private Vehicle buildObject(GrailsParameterMap parameterMap) {
        Vehicle vehicle = new Vehicle(parameterMap)
        vehicle.companyId = appSessionUtil.getCompanyId()
        vehicle.createdOn = new Date()
        vehicle.createdBy = appSessionUtil.getAppUser().id
        vehicle.updatedOn = null
        vehicle.updatedBy = 0
        return vehicle
    }
}