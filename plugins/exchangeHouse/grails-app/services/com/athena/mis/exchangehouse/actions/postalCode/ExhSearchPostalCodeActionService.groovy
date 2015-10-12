package com.athena.mis.exchangehouse.actions.postalCode

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.exchangehouse.entity.ExhPostalCode
import com.athena.mis.exchangehouse.utility.ExhPostalCodeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search postalCode and show specific list of postalCode for grid
 *  For details go through Use-Case doc named 'ExhSearchPostalCodeActionService'
 */
class ExhSearchPostalCodeActionService extends BaseService implements ActionIntf{

    @Autowired
    ExhPostalCodeCacheUtility exhPostalCodeCacheUtility

    private static final String LST_POSTAL_CODE="lstPostalCode"
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
     * Get postalCode list for grid through specific search
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    public  Object execute(Object parameters, Object obj){

        Map result = new LinkedHashMap()
        try {
            initSearch(parameters)  // initialize parameters for flexGrid
            Map searchResult = exhPostalCodeCacheUtility.search(queryType, query, this)
            List<ExhPostalCode> lstPostalCodes = searchResult.list  // get sub list of postalCode by search keyword
            int count = searchResult.count
            result.put(Tools.COUNT, count)
            result.put(LST_POSTAL_CODE, lstPostalCodes)
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
     * Wrap postalCode list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public  Object buildSuccessResultForUI(Object obj){

        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<ExhPostalCode> lstPostalCodes = (List<ExhPostalCode>) executeResult.get(LST_POSTAL_CODE)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedPostalCode = wrapPostalCode(lstPostalCodes, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedPostalCode]
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
     * Wrap list of postalCode in grid entity
     * @param lstPostalCOde -list of postalCode object(s)
     * @param start -starting index of the page
     * @return -list of wrapped postalCode
     */
    private List wrapPostalCode(List<ExhPostalCode> lstPostalCOde, int start) {
        List lstWrappedPostalCode = []
        int counter = start + 1
        for (int i = 0; i < lstPostalCOde.size(); i++) {
            ExhPostalCode postalCode = lstPostalCOde[i]
            GridEntity obj = new GridEntity()
            obj.id = postalCode.id
            obj.cell = [
                    counter,
                    postalCode.code
            ]
            lstWrappedPostalCode << obj
            counter++
        }
        return lstWrappedPostalCode
    }
}
