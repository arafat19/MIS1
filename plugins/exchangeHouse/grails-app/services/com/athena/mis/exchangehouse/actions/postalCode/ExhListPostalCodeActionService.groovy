package com.athena.mis.exchangehouse.actions.postalCode

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.exchangehouse.entity.ExhPostalCode
import com.athena.mis.exchangehouse.utility.ExhPostalCodeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired


/**
 *  Show list of postalCode for grid
 *  For details go through Use-Case doc named 'ExhListPostalCodeActionService'
 */
class ExhListPostalCodeActionService extends BaseService implements ActionIntf{

    private static final String LST_POSTAL_CODE="lstPostalCode"
    private static final String SHOW_POSTAL_CODE_FAILURE_MESSAGE="Failed to load PostalCode page"

    @Autowired
    ExhPostalCodeCacheUtility exhPostalCodeCacheUtility

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
     * Get postalCode list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public  Object execute(Object parameters, Object obj){

        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameterMap) // initialize parameters for flexGrid
            int count = exhPostalCodeCacheUtility.count()    // get count total postalCode
            List lstPostalCode = exhPostalCodeCacheUtility.list(this)    // get sub list of postal code
            result.put(LST_POSTAL_CODE, lstPostalCode)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_POSTAL_CODE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap postalCode list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public  Object buildSuccessResultForUI(Object obj){

        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj    // cast map returned from execute method
            List lstPostalCode = (List) executeResult.get(LST_POSTAL_CODE)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedPostalCode = wrapPostalCode(lstPostalCode, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedPostalCode]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_POSTAL_CODE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, SHOW_POSTAL_CODE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_POSTAL_CODE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of postalCode in grid entity
     * @param lstPostalCode -list of postalCode object(s)
     * @param start -starting index of the page
     * @return -list of wrapped postalCode
     */
    private List wrapPostalCode(List<ExhPostalCode> lstPostalCode, int start) {
        List lstWrappedPostalCode = []
        int counter = start + 1
        for (int i = 0; i < lstPostalCode.size(); i++) {
            ExhPostalCode postalCode = lstPostalCode[i]
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
