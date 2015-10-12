package com.athena.mis.accounting.actions.accioupurpose

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccIouPurpose
import com.athena.mis.accounting.entity.AccIouSlip
import com.athena.mis.accounting.service.AccIouPurposeService
import com.athena.mis.accounting.service.AccIouSlipService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete IOU-Purpose object from DB
 *  For details go through Use-Case doc named 'DeleteAccIouPurposeActionService'
 */
class DeleteAccIouPurposeActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String INVALID_INPUT_MESSAGE = "Could not delete IOU-Purpose due to invalid input"
    private static final String SUCCESS_MESSAGE = "IOU purpose has been deleted successfully"
    private static final String FAILURE_MESSAGE = "Can not delete IOU purpose"
    private static final String SLIP_NOT_FOUND_MESSAGE = "IOU slip not found"
    private static final String IOU_SLIP_UPDATE_FAILURE_MSG = "Failed to update IOU slip"
    private static final String PURPOSE_NOT_FOUND_MESSAGE = "IOU purpose not found"
    private static final String ALREADY_APPROVED_MESSAGE = "Approved IOU purpose can not be deleted"
    private static final String ACC_IOU_SLIP_OBJ = "accIouSlipObj"
    private static final String ACC_IOU_PURPOSE_OBJ = "accIouPurposeObj"
    private static final String DELETED = "deleted"
    private static final String ITEM_LIST = "itemList"

    AccIouSlipService accIouSlipService
    AccIouPurposeService accIouPurposeService

    /**
     * Check different criteria to delete accIouPurpose object
     *      1) Check existence of required parameter
     *      2) Check existence of IOU-Purpose object
     *      3) Check existing of Parent(AccIouSlip)
     *      4) Check approval of Parent(AccIouSlip)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing accIouSlip, accIouPurpose object for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) { //Check existence of required parameter
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }

            long accIouPurposeId = Long.parseLong(params.id.toString())
            AccIouPurpose accIouPurpose = accIouPurposeService.read(accIouPurposeId)
            if (!accIouPurpose) {//Checking existing of AccIouPurpose object
                result.put(Tools.MESSAGE, PURPOSE_NOT_FOUND_MESSAGE)
                return result
            }

            AccIouSlip accIouSlip = accIouSlipService.read(accIouPurpose.accIouSlipId)
            if (!accIouSlip) {//Checking existing of Parent(AccIouSlip)
                result.put(Tools.MESSAGE, SLIP_NOT_FOUND_MESSAGE)
                return result
            }

            if (accIouSlip.approvedBy > 0) {//Approved IOU-Purpose can not be deleted
                result.put(Tools.MESSAGE, ALREADY_APPROVED_MESSAGE)
                return result
            }

            //decrease totalPurposeAmount & purposeCount of Parent(AccIouSlip) object
            accIouSlip.totalPurposeAmount = accIouSlip.totalPurposeAmount - accIouPurpose.amount
            accIouSlip.purposeCount = accIouSlip.purposeCount - 1

            result.put(ACC_IOU_SLIP_OBJ, accIouSlip)
            result.put(ACC_IOU_PURPOSE_OBJ, accIouPurpose)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * delete accIouSlip object from DB
     * @param parameters -N/A
     * @param obj -a map containing accIouSlip, accIouPurpose send from executePreCondition
     * @return -itemList(indentDetail List) for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj

            AccIouSlip accIouSlip = (AccIouSlip) receiveResult.get(ACC_IOU_SLIP_OBJ)
            AccIouPurpose accIouPurpose = (AccIouPurpose) receiveResult.get(ACC_IOU_PURPOSE_OBJ)
            accIouPurposeService.delete(accIouPurpose.id)
            updatePurposeCountAndAmount(accIouSlip)

            //get list of indentDetails for create again
            List itemList = getItemList(accIouPurpose.accIouSlipId)
            itemList = Tools.listForKendoDropdown(itemList, null, null)
            result.put(ITEM_LIST, itemList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * contains delete success message & indentList to show on UI
     * @param obj -N/A
     * @return -a map contains isError(false), delete success message  & indentList to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(ITEM_LIST, receivedResult.get(ITEM_LIST))
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    private static final String QUERY_UPDATE = """
              UPDATE acc_iou_slip
                SET purpose_count =:purposeCount,
                    total_purpose_amount =:totalPurposeAmount
              WHERE
                  id =:id
            """
    /**
     * update(decrease) PurposeCount & PurposeAmount of AccIouSlip object
     * @param accIouSlip -AccIouSlip
     * @return -accIouSlip object
     */
    private AccIouSlip updatePurposeCountAndAmount(AccIouSlip accIouSlip) {
        Map queryParams = [
                id: accIouSlip.id,
                purposeCount: accIouSlip.purposeCount,
                totalPurposeAmount: accIouSlip.totalPurposeAmount
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException(IOU_SLIP_UPDATE_FAILURE_MSG)
        }
        return accIouSlip
    }

    /**
     * get list of indentDetails for create again
     * @param accIouSlipId -AccIouSlip.Id
     * @return -list of GroovyRowResult
     */
    private List<GroovyRowResult> getItemList(long accIouSlipId) {
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
            AND idls.id NOT IN (SELECT indent_details_id FROM acc_iou_purpose WHERE acc_iou_slip_id =:accIouSlipId)
            """
        Map queryParams = [
                accIouSlipId: accIouSlipId
        ]
        List itemList = executeSelectSql(queryStr, queryParams)
        return itemList
    }
}
