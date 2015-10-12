package com.athena.mis.fixedasset.actions.fixedassettrace

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.fixedasset.entity.FxdFixedAssetDetails
import com.athena.mis.fixedasset.entity.FxdFixedAssetTrace
import com.athena.mis.fixedasset.service.FixedAssetTraceService
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Create Fixed Asset Trace(Move fixed asset).
 * For details go through Use-Case doc named 'CreateForFixedAssetTraceActionService'
 */
class CreateForFixedAssetTraceActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    FixedAssetTraceService fixedAssetTraceService
    @Autowired
    FxdSessionUtil fxdSessionUtil

    private static final String SAVE_SUCCESS_MESSAGE = "Fixed Asset Has Been Moved Successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Can Not Move Fixed Asset"
    private static final String SERVER_ERROR_MESSAGE = "Failed To Move Fixed Asset"
    private static final String INPUT_VALIDATION_ERROR = "Error Occurred Due To Invalid Input"
    private static final String FIXED_ASSET_TRACE = "fixedAssetTrace"
    private static final String MODEL_NOT_FOUND = "Model/Serial Not Found"
    private static final String SAME_INVENTORY_FOUND = "Fixed Asset Can't Move To Same Inventory"
    private static final String FUTURE_DATE_FOUND = "Transaction date can not be future date"

    /**
     * 1. check required fields
     * 2. pull purchase order details object by po details id
     * 3. check fxd details model/serial
     * 4. check same inventory existence
     * 5. input date can not be future date
     * 6. build Fixed-Asset-Trace Object
     * 7. checks input validation
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing fixed asset trace object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if ((!parameterMap.categoryId.toString()) || (!parameterMap.fixedAssetDetailsId.toString()) ||
                    !(parameterMap.inventoryId.toString()) || (!parameterMap.transactionDate.toString())) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            long fixedAssetDetailsId = Long.parseLong(parameterMap.fixedAssetDetailsId.toString())
            FxdFixedAssetDetails fixedAssetDetails = FxdFixedAssetDetails.read(fixedAssetDetailsId)
            if (!fixedAssetDetails) {
                result.put(Tools.MESSAGE, MODEL_NOT_FOUND)
                return result
            }

            long oldInventoryId = Long.parseLong(parameterMap.currentInventoryId.toString())
            long newInventoryId = Long.parseLong(parameterMap.inventoryId.toString())
            if (oldInventoryId == newInventoryId) {
                result.put(Tools.MESSAGE, SAME_INVENTORY_FOUND)
                return result
            }
            Date transactionDate = DateUtility.parseMaskedDate(parameterMap.transactionDate.toString())
            if (transactionDate > new Date()) {
                result.put(Tools.MESSAGE, FUTURE_DATE_FOUND)
                return result
            }

            //Build Fixed-Asset-Trace Object
            FxdFixedAssetTrace fixedAssetTrace = buildFixedAssetTraceObject(parameterMap)

            // checks input validation
            fixedAssetTrace.validate()
            if (fixedAssetTrace.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            result.put(FIXED_ASSET_TRACE, fixedAssetTrace)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * 1. receive objects from pre execute method
     * 2. create new fixed asset trace as well as update fixed asset details
     * 3. check action success
     * @param parameters -N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing isError(true/false)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            FxdFixedAssetTrace fixedAssetTrace = (FxdFixedAssetTrace) preResult.get(FIXED_ASSET_TRACE)
            FxdFixedAssetTrace newFixedAssetTrace = (FxdFixedAssetTrace) createWithUpdateFixedAssetDetails(fixedAssetTrace)
            if (!newFixedAssetTrace) {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Error to create  Fixed-Asset-Trace")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. fixed asset trace receive from execute method
     * 2. wrap object for grid entity
     * @param obj - object receive from execute method
     * @return - item list and fixed asset details
     */
    @Transactional(readOnly = true)
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            Map serviceReturn = list()
            List<GroovyRowResult> fixedAssetTraceList = serviceReturn.fixedAssetTraceList
            int count = (int) serviceReturn.count

            List fixedAssetTraceListWrap = wrapFixedAssetTraceGridEntityList(fixedAssetTraceList, start)

            result = [page: pageNumber, total: count, rows: fixedAssetTraceListWrap]

            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            log.error(ex.getMessage())
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }
    /**
     * Build Fixed Asset trace object
     * @param params - serialize parameters from UI
     * @return Fixed Asset trace object
     */
    private FxdFixedAssetTrace buildFixedAssetTraceObject(GrailsParameterMap parameterMap) {
        FxdFixedAssetTrace fixedAssetTrace = new FxdFixedAssetTrace()

        fixedAssetTrace.version = 0
        fixedAssetTrace.fixedAssetDetailsId = Long.parseLong(parameterMap.fixedAssetDetailsId.toString())
        fixedAssetTrace.itemId = Long.parseLong(parameterMap.categoryId.toString())
        fixedAssetTrace.inventoryId = Long.parseLong(parameterMap.inventoryId.toString())
        fixedAssetTrace.transactionDate = DateUtility.parseMaskedDate(parameterMap.transactionDate.toString())
        fixedAssetTrace.createdBy = fxdSessionUtil.appSessionUtil.getAppUser().id
        fixedAssetTrace.createdOn = new Date()
        fixedAssetTrace.companyId = fxdSessionUtil.appSessionUtil.getCompanyId()
        fixedAssetTrace.comments = parameterMap.comments ? parameterMap.comments : null
        fixedAssetTrace.isCurrent = Boolean.TRUE

        return fixedAssetTrace
    }
    /**
     * @param fixedAssetTraceList - fixed asset trace list
     * @param start - starting point of index
     * @return - list of fixed asset trace(move) list
     */
    private wrapFixedAssetTraceGridEntityList(List<GroovyRowResult> fixedAssetTraceList, int start) {
        List lstFixedAssetTrace = []
        int counter = start + 1
        GroovyRowResult eachRow
        for (int i = 0; i < fixedAssetTraceList.size(); i++) {
            eachRow = fixedAssetTraceList[i]
            GridEntity gridEntity = new GridEntity()
            gridEntity.id = eachRow.id
            gridEntity.cell = [
                    counter,
                    eachRow.id,
                    eachRow.category_name,
                    eachRow.model_name,
                    eachRow.inventory_name,
                    eachRow.current ? Tools.YES : Tools.NO,
                    eachRow.transaction_date
            ]
            lstFixedAssetTrace << gridEntity
            counter++
        }
        return lstFixedAssetTrace

    }

    //create FxdFixedAssetTrace with update FxdFixedAssetDetails.currentInventoryId
    private FxdFixedAssetTrace createWithUpdateFixedAssetDetails(FxdFixedAssetTrace fixedAssetTrace) {
        //First : update Previous FxdFixedAssetDetails (IsCurrent=FALSE)
        updateIsCurrent(fixedAssetTrace)
        //Then : Create new FxdFixedAssetDetails
        FxdFixedAssetTrace newFixedAssetTrace = fixedAssetTraceService.create(fixedAssetTrace)

        updateFixedAssetDetails(fixedAssetTrace)
        return newFixedAssetTrace
    }

    //update  fixed_asset_trace (SET is_current  = FALSE)
    private static final String QUERY_UPDATE_FADT = """
                    UPDATE fxd_fixed_asset_trace
                    SET is_current = FALSE
                    WHERE fixed_asset_details_id = :fixedAssetDetailsId
                      AND is_current = TRUE
                """
    private int updateIsCurrent(FxdFixedAssetTrace fixedAssetTrace) {
        Map queryParams = [
                fixedAssetDetailsId: fixedAssetTrace.fixedAssetDetailsId
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE_FADT, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException("Exception occurred at FixedAssetTraceService.updateIsCurrent")
        }
        return updateCount
    }

    //Update Fixed-Asset-Details(Change current_inventory_id when Fixed-Asset-Trace create)
    private static final String QUERY_UPDATE_FAD = """
            UPDATE fxd_fixed_asset_details
               SET current_inventory_id = :inventoryId,
                   version = version + 1
            WHERE
                id = :fixedAssetDetailsId
        """
    private int updateFixedAssetDetails(FxdFixedAssetTrace fixedAssetTrace) {
        Map queryParams = [
                inventoryId: fixedAssetTrace.inventoryId,
                fixedAssetDetailsId: fixedAssetTrace.fixedAssetDetailsId
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE_FAD, queryParams)

        if (updateCount > 0) {
            return updateCount
        } else {
            throw new RuntimeException("Exception occurred at Fixed-Asset-Details Update")
        }
    }

    // List of Fixed Asset Trace for grid
    private LinkedHashMap list() {
        //@todo:model adjust using FixedAssetTraceModel.list()
        String queryStr = """
             SELECT fat.id, item.name AS category_name, fad.name AS model_name,
                    to_char(fat.transaction_date, 'dd-Mon-YYYY') AS transaction_date,
                    inventory.name AS inventory_name, fat.is_current AS current
            FROM fxd_fixed_asset_trace fat
                LEFT JOIN item ON item.id = fat.item_id
                LEFT JOIN fxd_fixed_asset_details fad ON fad.id = fat.fixed_asset_details_id
                LEFT JOIN inv_inventory inventory ON inventory.id = fat.inventory_id
            ORDER BY fat.id desc
            LIMIT ${resultPerPage}  OFFSET ${start}
        """

        String queryCount = """
            SELECT COUNT(fat.id) count FROM fxd_fixed_asset_trace fat
        """

        List<GroovyRowResult> result = executeSelectSql(queryStr)
        List<GroovyRowResult> countResult = executeSelectSql(queryCount)

        int total = (int) countResult[0].count
        return [fixedAssetTraceList: result, count: total]
    }
}

