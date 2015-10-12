package com.athena.mis.accounting.actions.accioupurpose

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccIouPurpose
import com.athena.mis.accounting.entity.AccIouSlip
import com.athena.mis.accounting.service.AccIouPurposeService
import com.athena.mis.accounting.service.AccIouSlipService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create IOU-Purpose object and to show on grid list
 *  For details go through Use-Case doc named 'CreateAccIouPurposeActionService'
 */
class CreateAccIouPurposeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String INVALID_AMOUNT = "Please enter digits in amount field"
    private static final String SUCCESS_MESSAGE = "IOU purpose has been saved successfully"
    private static final String FAILURE_MESSAGE = "Can not saved IOU purpose"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String SLIP_NOT_FOUND_MESSAGE = "IOU slip not found"
    private static final String ALREADY_APPROVED_MESSAGE = "Can not add IOU purpose at approved IOU slip"
    private static final String ERROR_INDENT_ITEM = "Indents item not found"
    private static final String ITEM_DESCRIPTION = "itemDescription"
    private static final String ACC_IOU_SLIP = "accIouSlip"
    private static final String ACC_IOU_PURPOSE_OBJ = "accIouPurposeObj"
    private static final String ITEM_LIST = "itemList"

    AccIouPurposeService accIouPurposeService
    AccIouSlipService accIouSlipService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    /**
     * Check different criteria for creating new accIouPurpose object
     *      1) Check validity of given amount
     *      2) Check existence of required parameter
     *      3) Checking existing of Parent(AccIouSlip)
     *      4) Check approval of Parent(AccIouSlip)
     *      5) Checking existing of related IndentDetails object
     * @param params -parameters send from UI
     * @param obj -N/A
     * @return -a map containing accIouSlip, accIouPurpose & itemDescription of indentDetails object for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            try {//Check validity of given amount
                double amount = Double.parseDouble(params.amount.toString())
            } catch (Exception e) {
                result.put(Tools.MESSAGE, INVALID_AMOUNT)
                return result
            }

            long accIouSlipId = Long.parseLong(params.slipId.toString())
            if (!accIouSlipId) {//Check existence of required parameter
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            //Checking existing of Parent(AccIouSlip) object
            AccIouSlip accIouSlip = accIouSlipService.read(accIouSlipId)
            if (!accIouSlip) {
                result.put(Tools.MESSAGE, SLIP_NOT_FOUND_MESSAGE)
                return result
            }

            //Checking approval of of Parent(AccIouSlip) object
            if (accIouSlip.approvedBy > 0) {
                result.put(Tools.MESSAGE, ALREADY_APPROVED_MESSAGE)
                return result
            }

            //Checking existing of related IndentDetails object
            long indentDetailsId = Long.parseLong(params.indentDetailsId.toString())
            Object indentDetails = procurementImplService.readIndentDetails(indentDetailsId)
            if (!indentDetails) {
                result.put(Tools.MESSAGE, ERROR_INDENT_ITEM)
                return result
            }

            //build iouPurpose object to create
            AccIouPurpose accIouPurpose = buildAccIouPurposeObject(params, indentDetailsId)

            //increase totalPurposeAmount & purposeCount of Parent(AccIouSlip) object
            accIouSlip.totalPurposeAmount = accIouSlip.totalPurposeAmount + accIouPurpose.amount
            accIouSlip.purposeCount = accIouSlip.purposeCount + 1

            result.put(ITEM_DESCRIPTION, indentDetails.itemDescription)
            result.put(ACC_IOU_SLIP, accIouSlip)
            result.put(ACC_IOU_PURPOSE_OBJ, accIouPurpose)
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
     * Save AccIouPurpose(Child) object; also update PurposeCount & PurposeAmount of AccIouSlip(Parent)
     * @param parameters -N/A
     * @param obj -a map containing accIouSlip, accIouPurpose & itemDescription of indentDetails object send from executePreCondition
     * @return -newly created AccIouPurpose object & itemDescription for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            AccIouSlip accIouSlip = (AccIouSlip) preResult.get(ACC_IOU_SLIP)
            AccIouPurpose accIouPurpose = (AccIouPurpose) preResult.get(ACC_IOU_PURPOSE_OBJ)

            //Save AccIouPurpose(Child) object
            AccIouPurpose newAccIouPurpose = accIouPurposeService.create(accIouPurpose)

            //update PurposeCount & PurposeAmount of AccIouSlip(Parent) object
            updatePurposeCountAndAmount(accIouSlip)

            result.put(ITEM_DESCRIPTION, preResult.get(ITEM_DESCRIPTION))
            result.put(ACC_IOU_PURPOSE_OBJ, newAccIouPurpose)
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
     * Wrap newly created accIouPurpose to show on grid
     * @param obj -newly created accIouPurpose object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            AccIouPurpose accIouPurpose = (AccIouPurpose) receiveResult.get(ACC_IOU_PURPOSE_OBJ)

            GridEntity object = new GridEntity()
            String strAmount = Tools.makeAmountWithThousandSeparator(accIouPurpose.amount)
            AppUser appUser = (AppUser) appUserCacheUtility.read(accIouPurpose.createdBy)

            String description = receiveResult.get(ITEM_DESCRIPTION)
            // if itemDescription is too long - make it short to display in grid
            description = Tools.makeDetailsShort(description, Tools.DEFAULT_LENGTH_DETAILS_OF_INDENT)

            object.id = accIouPurpose.id
            object.cell = [Tools.LABEL_NEW,
                    accIouPurpose.id,
                    description,
                    strAmount,
                    appUser.username,
                    Tools.EMPTY_SPACE
            ]

            //get list of indentDetails for create again
            List itemList = getItemList(accIouPurpose.accIouSlipId)
            itemList = Tools.listForKendoDropdown(itemList, null, null)
            result.put(ITEM_LIST, itemList)
            result.put(Tools.ENTITY, object)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build accIouPurpose object to create
     * @param parameterMap -GrailsParameterMap
     * @param indentDetailsId -ProcIndentDetails.id
     * @return -AccIouPurpose
     */
    private AccIouPurpose buildAccIouPurposeObject(GrailsParameterMap parameterMap, long indentDetailsId) {
        AppUser user = accSessionUtil.appSessionUtil.getAppUser()
        AccIouPurpose accIouPurpose = new AccIouPurpose()
        accIouPurpose.version = 0
        accIouPurpose.accIouSlipId = Long.parseLong(parameterMap.slipId.toString())
        accIouPurpose.indentDetailsId = indentDetailsId
        accIouPurpose.amount = Double.parseDouble(parameterMap.amount.toString())
        accIouPurpose.createdBy = user.id
        accIouPurpose.createdOn = new Date()
        accIouPurpose.updatedBy = 0L
        accIouPurpose.updatedOn = null
        accIouPurpose.comments = parameterMap.comments.toString()
        return accIouPurpose
    }

    private static final String QUERY_UPDATE = """
              UPDATE acc_iou_slip
                SET purpose_count =:purposeCount,
                    total_purpose_amount =:totalPurposeAmount
              WHERE
                  id =:id
            """
    /**
     * update(increase) PurposeCount & PurposeAmount of AccIouSlip object
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
            throw new RuntimeException(FAILURE_MESSAGE)
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
            FROM proc_indent_details idls
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
