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
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search specific list of ExchangeHouse
 *  For details go through Use-Case doc named 'SearchRmsExchangeHouseActionService'
 */
class SearchRmsExchangeHouseActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load exchange house List"
    private static final String LST_RMS_EXCHANGE_HOUSES = "lstExchangeHouses"

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
     * Get ExchangeHouse list and count through specific search
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            initSearch(parameters)          // initialize parameters
            // Get ExchangeHouse list and count through specific search
            Map searchResult = rmsExchangeHouseCacheUtility.search(queryType, query, this)
            List<RmsExchangeHouse> lstExchangeHouses = (List<RmsExchangeHouse>) searchResult.list
            Integer count = (Integer) searchResult.count
            result.put(LST_RMS_EXCHANGE_HOUSES, lstExchangeHouses)
            result.put(Tools.COUNT, count)
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
     * Wrap ExchangeHouse list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     */
    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<RmsExchangeHouse> lstExchangeHouses = (List<RmsExchangeHouse>) executeResult.get(LST_RMS_EXCHANGE_HOUSES)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedExchangeHouses = wrapExchangeHouses(lstExchangeHouses, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedExchangeHouses]
            return output
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
    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of ExchangeHouse in grid entity
     * @param lstExchangeHouses -list of ExchangeHouse object
     * @param start -starting index of the page
     * @return -list of wrapped ExchangeHouses
     */
    private List wrapExchangeHouses(List<RmsExchangeHouse> lstExchangeHouses, int start) {
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
