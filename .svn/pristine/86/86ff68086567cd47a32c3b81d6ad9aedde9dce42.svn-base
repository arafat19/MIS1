package com.athena.mis.accounting.actions.acciouslip

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccIouSlip
import com.athena.mis.accounting.service.AccIouSlipService
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select accIouSlip object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectAccIouSlipActionService'
 */
class SelectAccIouSlipActionService extends BaseService implements ActionIntf {

    private static final String ACC_IOU_SLIP_NOT_FOUND_MASSAGE = "Selected IOU slip not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select IOU slip"
    private static final String INDENT_LIST = "indentList"

    AccIouSlipService accIouSlipService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService

    private final Logger log = Logger.getLogger(getClass())

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get accIouSlip object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)   // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            Long accIouSlipId = Long.parseLong(parameterMap.id.toString())
            AccIouSlip accIouSlip = (AccIouSlip) accIouSlipService.read(accIouSlipId)  // get accIouSlip object
            // check whether the accIouSlip object exists or not
            if (!accIouSlip) {
                result.put(Tools.MESSAGE, ACC_IOU_SLIP_NOT_FOUND_MASSAGE)
                return result
            }
            List<GroovyRowResult> indentList = procurementImplService.listIndentByProjectIdIntendId(accIouSlip.projectId, accIouSlip.indentId) // get indent total list by projectId and indentId

            result.put(Tools.ENTITY, accIouSlip)
            result.put(INDENT_LIST, indentList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
     * Build a map with IouSlip  object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method

            List<GroovyRowResult> indentList = (List<GroovyRowResult>) executeResult.get(INDENT_LIST)
            AccIouSlip accIouSlip = (AccIouSlip) executeResult.get(Tools.ENTITY)

            result.put(Tools.ENTITY, accIouSlip)
            result.put(Tools.VERSION, accIouSlip.version)
            result.put(INDENT_LIST, indentList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
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
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj  // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}
