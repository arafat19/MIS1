package com.athena.mis.sarb.actions.province

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.sarb.entity.SarbProvince
import com.athena.mis.sarb.utility.SarbProvinceCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
/**
 *  Show list of province for grid
 *  For details go through Use-Case doc named 'ShowSarbProvinceActionService'
 */
class ShowSarbProvinceActionService extends BaseService implements ActionIntf {

    @Autowired
    SarbProvinceCacheUtility sarbProvinceCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String LST_SARB_PROVINCE ="lstSarbProvince"
    private static final String DEFAULT_FAILURE_MSG_SHOW_PROVINCE ="Province could not found"
    private static final String GRID_OBJ="gridObj"

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
     * Get province list for grid
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
            int count=sarbProvinceCacheUtility.count()
            List<SarbProvince> lstSarbProvince= (List<SarbProvince>)sarbProvinceCacheUtility.list(this)
            result.put(LST_SARB_PROVINCE,lstSarbProvince)
            result.put(Tools.COUNT,count.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch(Exception ex){
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PROVINCE)
            return result
        }
    }

    /**
     * Wrap province list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public  Object buildSuccessResultForUI(Object obj){

        Map result= new LinkedHashMap()
        try{
            Map executeResult= (Map)obj
            List<SarbProvince> lstSarbProvince=(List<SarbProvince>)executeResult.get(LST_SARB_PROVINCE)
            Integer count= (Integer)executeResult.get(Tools.COUNT)
            List lstWrappedProvince=wrapProvince(lstSarbProvince,start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedProvince]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            return result

        }catch(Exception ex){
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PROVINCE)
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
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PROVINCE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PROVINCE)
            return result
        }
    }
    /**
     * Wrap list of province in grid entity
     * @param lstProvince -list of province object(s)
     * @param start -starting index of the page
     * @return -list of wrapped province
     */
    private List wrapProvince(List<SarbProvince> lstProvince, int start) {
        List<SarbProvince> lstWrappedProvince = []
        int counter = start + 1
        for (int i = 0; i < lstProvince.size(); i++) {
            SarbProvince sarbProvince = lstProvince[i]
            GridEntity obj = new GridEntity()
            obj.id = sarbProvince.id
            obj.cell = [
                    counter,
                    sarbProvince.name
            ]
             lstWrappedProvince << obj
            counter++
        }
        return  lstWrappedProvince
    }
}

