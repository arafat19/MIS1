package com.athena.mis.accounting.actions.acclc

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccLc
import com.athena.mis.accounting.service.AccLcService
import com.athena.mis.accounting.utility.AccLcCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update AccLc object and to show on grid list
 *  For details go through Use-Case doc named 'UpdateAccLcActionService'
 */
class UpdateAccLcActionService extends BaseService implements ActionIntf {

    AccLcService accLcService
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccLcCacheUtility accLcCacheUtility

    private static final String UPDATE_FAILURE_MESSAGE = "LC could not be updated"
    private static final String UPDATE_SUCCESS_MESSAGE = "LC has been updated successfully"
    private static final String ACC_LC = "accLc"
    private static final String NOT_FOUND_MASSAGE = "Selected LC is not found"
    private static final String LC_ALREADY_EXISTS = "Same LC already exists"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to update accLc object
     *      1) Check existence of required parameter
     *      2) Check existence of selected(old) accLc object
     *      3) check unique LC NO
     * @param params -N/A
     * @param obj -AccLc object send from controller
     * @return -a map containing accLc object for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            // Check existence of required parameter
            if ((!parameterMap.lcNo) || (!parameterMap.bank) || (!parameterMap.amount)
                    || (!parameterMap.id) || (!parameterMap.version)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long accLcId = Long.parseLong(parameterMap.id.toString())
            AccLc oldAccLcObject = (AccLc) accLcCacheUtility.read(accLcId)
            if (!oldAccLcObject) { //Check existence of selected(old) accLc object
                result.put(Tools.MESSAGE, NOT_FOUND_MASSAGE)
                return result
            }

            //build AccLc object to update
            AccLc accLc = buildAccLcObject(parameterMap, oldAccLcObject)

            int duplicateCount = AccLc.countByLcNoIlikeAndIdNotEqualAndCompanyId(accLc.lcNo, accLc.id, accLc.companyId)
            if (duplicateCount > 0) {//check unique LC NO
                result.put(Tools.MESSAGE, LC_ALREADY_EXISTS)
                return result
            }

            result.put(ACC_LC, accLc)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update AccLc object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -a map containing AccLc object send from executePreCondition
     * @return -updated AccLc object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccLc accLc = (AccLc) preResult.get(ACC_LC)
            //update in DB
            accLcService.update(accLc)
            //update in cache and keep the data sorted
            accLcCacheUtility.update(accLc, accLcCacheUtility.SORT_ON_ID, accLcCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_LC, accLc)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
     * Wrap updated AccLc to show on grid
     * @param obj -updated AccLc object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            AccLc accLc = (AccLc) receiveResult.get(ACC_LC)
            Item item = (Item) itemCacheUtility.read(accLc.itemId)
            Supplier supplier = (Supplier) supplierCacheUtility.read(accLc.supplierId)
            GridEntity object = new GridEntity()
            object.id = accLc.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accLc.id,
                    accLc.lcNo,
                    Tools.formatAmountWithoutCurrency(accLc.amount),
                    accLc.bank,
                    item.name,
                    supplier.name
            ]
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build accLc object to create
     * @param params -GrailsParameterMap
     * @param oldAccLcObject -AccLc
     * @return -AccLc
     */
    private AccLc buildAccLcObject(GrailsParameterMap parameterMap, AccLc oldAccLcObject) {
        AccLc accLc = new AccLc(parameterMap)
        accLc.id = oldAccLcObject.id
        accLc.version = oldAccLcObject.version
        accLc.createdBy = oldAccLcObject.createdBy
        accLc.createdOn = oldAccLcObject.createdOn
        accLc.companyId = oldAccLcObject.companyId
        accLc.updatedBy = accSessionUtil.appSessionUtil.getAppUser().id
        accLc.updatedOn = new Date()
        return accLc
    }
}
