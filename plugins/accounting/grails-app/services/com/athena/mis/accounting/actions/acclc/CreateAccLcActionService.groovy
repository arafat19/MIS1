package com.athena.mis.accounting.actions.acclc

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccLc
import com.athena.mis.accounting.service.AccLcService
import com.athena.mis.accounting.utility.AccLcCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.AppUser
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
 *  Class to create AccLc object and to show on grid list
 *  For details go through Use-Case doc named 'CreateAccLcActionService'
 */
class CreateAccLcActionService extends BaseService implements ActionIntf {

    AccLcService accLcService
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    AccLcCacheUtility accLcCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String CREATE_FAILURE_MSG = "LC has not been saved"
    private static final String CREATE_SUCCESS_MSG = "LC has been successfully saved"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to create LC"
    private static final String LC_NO_ALREADY_EXISTS = "Same LC already exists"
    private static final String ACC_LC = "accLc"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check existence of same AccLc object for creating new AccLc object
     * @param params -parameters send from UI
     * @param obj -N/A
     * @return -a map containing AccLc object & isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            //build AccLc object to create
            AccLc accLc = buildAccLcObject(parameterMap)

            int duplicateLcNo = AccLc.countByLcNoIlikeAndCompanyId(accLc.lcNo, accLc.companyId)
            if (duplicateLcNo > 0) {//Check existence of same AccLc object
                result.put(Tools.MESSAGE, LC_NO_ALREADY_EXISTS)
                return result
            }

            result.put(ACC_LC, accLc)
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
     * Save AccLc object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -a map containing AccLc object send from executePreCondition
     * @return -newly created AccLc object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccLc accLc = (AccLc) preResult.get(ACC_LC)
            AccLc newAccLcObject = accLcService.create(accLc)   // save in DB
            // add to cache utility and keep the data sorted
            accLcCacheUtility.add(newAccLcObject, accLcCacheUtility.SORT_ON_ID, accLcCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_LC, newAccLcObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Wrap newly created AccLc to show on grid
     * @param obj -newly created AccLc object from execute method
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
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
     * build accLc object to create
     * @param params -GrailsParameterMap
     * @return -AccLc
     */
    private AccLc buildAccLcObject(GrailsParameterMap params) {
        AccLc accLc = new AccLc(params)
        AppUser user = accSessionUtil.appSessionUtil.getAppUser()
        accLc.createdOn = new Date()
        accLc.createdBy = user.id
        accLc.updatedBy = 0L
        accLc.updatedOn = null
        accLc.companyId = user.companyId
        return accLc
    }

}
