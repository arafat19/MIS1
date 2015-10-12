package com.athena.mis.sarb.actions.province

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.sarb.entity.SarbProvince
import com.athena.mis.sarb.utility.SarbProvinceCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search province and show specific list of province for grid
 *  For details go through Use-Case doc named 'SearchSarbProvinceActionService'
 */
class SearchSarbProvinceActionService extends BaseService implements ActionIntf {

    @Autowired
    SarbProvinceCacheUtility sarbProvinceCacheUtility

    private static final String LST_PROVINCE ="lstProvince"
    private static final String DEFAULT_ERROR_MESSAGE="Could not found"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * do nothing for pre operation
     */
    public  Object executePreCondition(Object parameters, Object obj){
        return null

    }

    /**
     * do nothing for post operation
     */
    public  Object executePostCondition(Object parameters, Object obj){
        return null
    }

    /**
     * Get province list for grid through specific search
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    public  Object execute(Object parameters, Object obj){

        Map result = new LinkedHashMap()
        try {
            initSearch(parameters)  // initialize parameters for flexGrid
            Map searchResult = sarbProvinceCacheUtility.search(queryType, query, this)
            List<SarbProvince> lstProvince = searchResult.list  // get sub list of province by search keyword
            int count = searchResult.count
            result.put(Tools.COUNT, count)
            result.put(LST_PROVINCE, lstProvince)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }
    }

    /**
     * Wrap province list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public  Object buildSuccessResultForUI(Object obj){
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<SarbProvince> lstProvince = (List<SarbProvince>) executeResult.get(LST_PROVINCE)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedProvince = wrapProvince(lstProvince, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedProvince]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }

    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public  Object buildFailureResultForUI(Object obj){

        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
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
     * Wrap list of province in grid entity
     * @param lstProvince -list of province object(s)
     * @param start -starting index of the page
     * @return -list of wrapped province
     */
    private List wrapProvince(List<SarbProvince> lstPorovince, int start) {
        List lstWrappedProvince = []
        int counter = start + 1
        for (int i = 0; i < lstPorovince.size(); i++) {
            SarbProvince province = lstPorovince[i]
            GridEntity obj = new GridEntity()
            obj.id = province.id
            obj.cell = [
                    counter,
                    province.name
            ]
            lstWrappedProvince << obj
            counter++
        }
        return lstWrappedProvince
    }
}
