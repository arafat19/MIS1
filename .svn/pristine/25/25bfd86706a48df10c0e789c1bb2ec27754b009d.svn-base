package com.athena.mis.application.actions.vehicle

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
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
 *  Delete vehicle object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteVehicleActionService'
 */
class DeleteVehicleActionService extends BaseService implements ActionIntf {

    private static final String DELETE_VEHICLE_SUCCESS_MESSAGE = "Vehicle has been deleted successfully"
    private static final String DELETE_VEHICLE_FAILURE_MESSAGE = "Vehicle could not be deleted, Please refresh the Vehicle List"
    private static final String HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION = " inventory transaction is associated with selected vehicle"

    private final Logger log = Logger.getLogger(getClass())

    VehicleService vehicleService
    @Autowired
    VehicleCacheUtility vehicleCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * 1. check user access as Admin role
     * 2. pull vehicle object from cache utility
     * 3. check for vehicle existence
     * 4. association check for vehicle with inventory transaction
     * @param parameters - serialize parameters from UI
     * @param obj -N/A
     * @return - a map containing isError(true/false) depending on method success &  relevant message.
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long vehicleId = Long.parseLong(params.id.toString())

            Vehicle vehicle = (Vehicle) vehicleCacheUtility.read(vehicleId)

            if (!vehicle) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            Map preResult = (Map) hasAssociation(vehicle)

            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)

            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_VEHICLE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Delete vehicle object from DB and cache utility
     * 1. delete selected vehicle
     * 2. delete from cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -boolean value true/false depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long vehicleId = Long.parseLong(params.id.toString())
            Boolean deleteVehicle = vehicleService.delete(vehicleId)
            Vehicle vehicle = (Vehicle) vehicleCacheUtility.read(vehicleId)
            vehicleCacheUtility.delete(vehicle.id)
            return deleteVehicle
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete vehicle')
            return Boolean.FALSE
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: DELETE_VEHICLE_SUCCESS_MESSAGE]
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
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_VEHICLE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_VEHICLE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * 1. check association with vehicle id of inventory transaction
     * @param vehicle - vehicle object
     * @return - a map containing hasAccess(true/false) & relevant association check message
     */
    private LinkedHashMap hasAssociation(Vehicle vehicle) {
        LinkedHashMap result = new LinkedHashMap()
        long vehicleId = vehicle.id
        Integer count = 0
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
            count = countInventoryTransaction(vehicleId)
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION)
                return result
            }
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String INV_INVENTORY_TRANSACTION_DETAILS = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction_details
            WHERE vehicle_id = :vehicleId """
    /**
     * Get total vehicle number of given vehicle-id
     * @param vehicleId - vehicle id
     * @return - total vehicle number
     */
    private int countInventoryTransaction(long vehicleId) {

        List results = executeSelectSql(INV_INVENTORY_TRANSACTION_DETAILS, [vehicleId: vehicleId])
        int count = results[0].count
        return count
    }
}
