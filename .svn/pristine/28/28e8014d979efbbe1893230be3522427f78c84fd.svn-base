package com.athena.mis.application.actions.vehicle

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.VehicleCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select vehicle object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectVehicleActionService'
 */
class SelectVehicleActionService extends BaseService implements ActionIntf {

    private static final String VEHICLE_NOT_FOUND_MASSAGE = "Selected vehicle is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select vehicle"

    private final Logger log = Logger.getLogger(getClass())

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
     * 1. pull vehicle list from cache utility
     * 2. check vehicle existence
     * @param params -serialize parameters from UI
     * @param obj -N/A
     * @return - vehicle list and relevant message
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long vehicleId = Long.parseLong(parameterMap.id.toString())
            Vehicle vehicleInstance = (Vehicle) vehicleUtility.read(vehicleId)
            if (vehicleInstance) {
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                result.put(Tools.ENTITY, vehicleInstance)
            } else {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, VEHICLE_NOT_FOUND_MASSAGE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, VEHICLE_NOT_FOUND_MASSAGE)
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
     * Build a map with vehicle object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Vehicle vehicleInstance = (Vehicle) executeResult.get(Tools.ENTITY)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, vehicleInstance)
            result.put(Tools.VERSION, vehicleInstance.version)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.message)
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}
