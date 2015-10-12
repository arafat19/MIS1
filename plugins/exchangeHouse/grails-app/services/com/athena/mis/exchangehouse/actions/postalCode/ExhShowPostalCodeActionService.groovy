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
 *  Show lstPostalCode object and show in grid
 *  For details go through Use-Case doc named 'ExhShowPostalCodeActionService'
 */
class ExhShowPostalCodeActionService extends BaseService implements ActionIntf{


    @Autowired
    ExhPostalCodeCacheUtility exhPostalCodeCacheUtility

    private static final String LST_POSTAL_CODE="lstPostalCode"
    private static final String DEFAULT_FAILURE_MSG_SHOW_POSTAL_CODE="Failed to load page"
    private static final String GRID_OBJ="gridObj"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public  Object executePreCondition(Object parameters, Object obj){
        return null
    }
    /**
     * Do nothing for post operation
     */
    public  Object executePostCondition(Object parameters, Object obj){
         return null
    }
    /**
     * Get postalCode list for grid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    public  Object execute(Object parameters, Object obj){
        Map result=new LinkedHashMap()
        try{
            GrailsParameterMap parameterMap=(GrailsParameterMap)parameters
            initPager(parameterMap)
            int count=exhPostalCodeCacheUtility.count()
            List<ExhPostalCode> lstPostalCode= (List<ExhPostalCode>)exhPostalCodeCacheUtility.list(this)
            result.put(LST_POSTAL_CODE,lstPostalCode)
            result.put(Tools.COUNT,count.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch(Exception ex){
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_POSTAL_CODE)
            return result
        }
    }
    /**
     * Wrap postalCode list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public  Object buildSuccessResultForUI(Object obj){
        Map result= new LinkedHashMap()
        try{
            Map executeResult= (Map)obj
            List<ExhPostalCode> lstPostalCode=(List<ExhPostalCode>)executeResult.get(LST_POSTAL_CODE)
            Integer count= (Integer)executeResult.get(Tools.COUNT)
            List lstWrappedPostalCode=wrapPostalCode(lstPostalCode,start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedPostalCode]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            return result

        }catch(Exception ex){
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_POSTAL_CODE)
            return result
        }

    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
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
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_POSTAL_CODE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_POSTAL_CODE)
            return result
        }
    }

    /**
     * Wrap list of postalCode in grid entity
     * @param lstPostalCodes -list of postalCode object(s)
     * @param start -starting index of the page
     * @return -list of wrapped postalCode
     */
    private List wrapPostalCode(List<ExhPostalCode> lstPostalCode, int start) {
        List<ExhPostalCode> lstWrappedPostalCode = []
        int counter = start + 1
        for (int i = 0; i < lstPostalCode.size(); i++) {
            ExhPostalCode exhPostalCode = lstPostalCode[i]
            GridEntity obj = new GridEntity()
            obj.id = exhPostalCode.id
            obj.cell = [
                    counter,
                    exhPostalCode.code
            ]
            lstWrappedPostalCode << obj
            counter++
        }
        return lstWrappedPostalCode
    }


}
