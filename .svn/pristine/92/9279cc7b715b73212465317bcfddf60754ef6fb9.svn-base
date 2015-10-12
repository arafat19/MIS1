package com.athena.mis.arms.actions.rmsexchangehouse

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Country
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Get list of ExchangeHouse
 * For details go through Use-Case doc named 'ShowRmsExchangeHouseActionService'
 */
class ShowRmsExchangeHouseActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String LST_EXCHANGE_HOUSES = "lstExchangeHouses"
    private static final String DEFAULT_FAILURE_MSG_SHOW_EXCHANGE_HOUSE = "Failed to load exchange house page"
    private static final String GRID_OBJ = "gridObj"

    /**
     * Do nothing for pre operation
     */
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * 1. Get ExchangeHouse list for grid
     * 2. Get count of total ExchangeHouse
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap();
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameterMap)                // initialize parameters
            // get list of ExchangeHouse from cache
            List<RmsExchangeHouse> lstExchangeHouses = rmsExchangeHouseCacheUtility.list(this)
            int count = rmsExchangeHouseCacheUtility.count()        // get count of total ExchangeHouse from cache utility
            result.put(LST_EXCHANGE_HOUSES, lstExchangeHouses)
            result.put(Tools.COUNT, count.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex){
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_EXCHANGE_HOUSE)
            return result
        }
    }

    /**
     * Wrap ExchangeHouse list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj                   // cast map returned from execute method
            List<RmsExchangeHouse> lstExchangeHouses = (List<RmsExchangeHouse>) executeResult.get(LST_EXCHANGE_HOUSES)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            // Wrap list of ExchangeHouse in grid entity
            List lstWrappedExchangeHouses = wrapExchangeHouse(lstExchangeHouses, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedExchangeHouses]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_EXCHANGE_HOUSE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj                   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_EXCHANGE_HOUSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_EXCHANGE_HOUSE)
            return result
        }
    }

    /**
     * Wrap list of ExchangeHouse in grid entity
     * @param lstExchangeHouses -list of ExchangeHouse object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ExchangeHouses
     */
    private List wrapExchangeHouse(List<RmsExchangeHouse> lstExchangeHouses, int start) {
        List lstWrappedExchangeHouses = []
        int counter = start + 1
        for (int i = 0; i < lstExchangeHouses.size(); i++) {
            RmsExchangeHouse rmsExchangeHouse = lstExchangeHouses[i]
            long countryId = rmsExchangeHouse.countryId
            Country country = (Country) countryCacheUtility.read(countryId)
            GridEntity obj = new GridEntity()
            obj.id = rmsExchangeHouse.id
            obj.cell = [
                    counter,
                    rmsExchangeHouse.id,
                    rmsExchangeHouse.name,
                    rmsExchangeHouse.code,
                    country.name,
                    rmsExchangeHouse.balance
            ]
            lstWrappedExchangeHouses << obj
            counter++
        }
        return lstWrappedExchangeHouses
    }
}
