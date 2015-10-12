package com.athena.mis.accounting.actions.accioupurpose

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccIouPurpose
import com.athena.mis.accounting.service.AccIouPurposeService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional


/**
 *  Select specific accIouPurpose object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectAccIouPurposeActionService'
 */
class SelectAccIouPurposeActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    AccIouPurposeService accIouPurposeService

    private static final String INVALID_INPUT_MESSAGE = "Could not select IOU-Purpose due to invalid input"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to select purpose"
    private static final String PURPOSE_NOT_FOUND_MESSAGE = "Purpose not found"
    private static final String ACC_IOU_PURPOSE_OBJ = "accIouPurposeObj"
    private static final String ITEM_LIST = "itemList"

    /**
     * Check different criteria to delete accIouPurpose object
     *      1) Check existence of required parameter
     *      2) Check existence of IOU-Purpose object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing accIouSlip object for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap paramMap = (GrailsParameterMap) params
            if (!paramMap.id) { //Check existence of required parameter
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }

            long accIouPurposeId = Long.parseLong(paramMap.id.toString())
            AccIouPurpose accIouPurpose = accIouPurposeService.read(accIouPurposeId)
            if (!accIouPurpose) {//Checking existing of AccIouPurpose object
                result.put(Tools.MESSAGE, PURPOSE_NOT_FOUND_MESSAGE)
                return result
            }

            result.put(ACC_IOU_PURPOSE_OBJ, accIouPurpose)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * @param parameters -N/A
     * @param obj -a map containing accIouSlip object from executePreCondition
     * @return -a map containing accIouSlip object necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            AccIouPurpose accIouPurpose = (AccIouPurpose) receiveResult.get(ACC_IOU_PURPOSE_OBJ)

            result.put(Tools.ENTITY, accIouPurpose)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with necessary objects to show on UI
     * @param obj -map contains accIouSlip object
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj

            AccIouPurpose accIouPurpose = (AccIouPurpose) receiveResult.get(Tools.ENTITY)

            //get list of indentDetails for drop-down
            List itemList = getItemList(accIouPurpose.accIouSlipId, accIouPurpose.id)
            itemList = Tools.listForKendoDropdown(itemList, null, null)
            result.put(ITEM_LIST, itemList)
            result.put(Tools.ENTITY, accIouPurpose)
            result.put(Tools.VERSION, accIouPurpose.version)
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * get list of indentDetails for drop-down
     * @param accIouSlipId -AccIouSlip.Id
     * @param accIouPurposeId -AccIouPurpose.Id
     * @return -list of GroovyRowResult
     */
    private List<GroovyRowResult> getItemList(long accIouSlipId, long accIouPurposeId) {
        String queryStr = """
            SELECT idls.id,
                CASE
                   WHEN length(idls.item_description) > 25 THEN
                        rtrim(substr(idls.item_description,0,25)) || '${Tools.THREE_DOTS}'
                   ELSE
                        idls.item_description
                  END AS name,
             idls.item_description
            FROM proc_indent_details  idls
            LEFT JOIN acc_iou_slip ais ON ais.indent_id = idls.indent_id
            WHERE ais.id =:accIouSlipId
            AND idls.id NOT IN (SELECT indent_details_id FROM acc_iou_purpose
                                WHERE acc_iou_slip_id =:accIouSlipId AND id <> :accIouPurposeId)
            """
        Map queryParams = [
                accIouSlipId: accIouSlipId,
                accIouPurposeId: accIouPurposeId
        ]
        List itemList = executeSelectSql(queryStr, queryParams)
        return itemList
    }
}
