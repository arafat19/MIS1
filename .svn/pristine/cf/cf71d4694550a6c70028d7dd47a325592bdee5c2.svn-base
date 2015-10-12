package com.athena.mis.arms.actions.rmspurchaseinstrumentmapping

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.District
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.arms.entity.RmsPurchaseInstrumentMapping
import com.athena.mis.arms.service.RmsPurchaseInstrumentMappingService
import com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * show process instrument mapping page with grid populated
 * for details go through use-case named "ShowRmsPurchaseInstrumentMappingActionService"
 */
class ShowRmsPurchaseInstrumentMappingActionService extends BaseService implements ActionIntf {

    RmsPurchaseInstrumentMappingService rmsPurchaseInstrumentMappingService
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String LST_PURCHASE_INSTRUMENT = "lstPurchaseInstrument"
    private static final String DEFAULT_FAILURE_MSG = "Failed to load purchase instrument mapping page"
    private static final String GRID_OBJ = "gridObj"

    /**
     * Do nothing for pre operation
     */
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * 1. Get RmsPurchaseInstrumentMapping list for grid
     * 2. Get count of total RmsPurchaseInstrumentMapping
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap();
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameterMap)
            List<RmsPurchaseInstrumentMapping> rmsPurchaseInstrumentMappingList = rmsPurchaseInstrumentMappingService.list(this)
            int count = rmsPurchaseInstrumentMappingService.count()
            result.put(LST_PURCHASE_INSTRUMENT, rmsPurchaseInstrumentMappingList)
            result.put(Tools.COUNT, count.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * Wrap RmsPurchaseInstrumentMapping list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<RmsPurchaseInstrumentMapping> rmsPurchaseInstrumentMappingList = (List<RmsPurchaseInstrumentMapping>) executeResult.get(LST_PURCHASE_INSTRUMENT)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrapped = wrapRmsPurchaseInstrumentMapping(rmsPurchaseInstrumentMappingList, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrapped]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    Object buildFailureResultForUI(Object obj) {
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
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * Wrap list of RmsPurchaseInstrumentMapping in grid entity
     * @param lstRmsPurchaseInstrumentMapping -list of RmsPurchaseInstrumentMapping object(s)
     * @param start - starting index of the page
     * @return - list of wrapped RmsPurchaseInstrumentMapping
     */
    private List wrapRmsPurchaseInstrumentMapping(List<RmsPurchaseInstrumentMapping> lstRmsPurchaseInstrumentMapping, int start) {
        List lstWrappedObj = []
        int counter = start + 1
        for (int i = 0; i < lstRmsPurchaseInstrumentMapping.size(); i++) {
            RmsPurchaseInstrumentMapping purchaseInstrumentMapping = lstRmsPurchaseInstrumentMapping[i]
            SystemEntity instrument = (SystemEntity) rmsInstrumentTypeCacheUtility.read(purchaseInstrumentMapping.instrumentTypeId)
            Bank bank = (Bank) bankCacheUtility.read(purchaseInstrumentMapping.bankId)
            BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(purchaseInstrumentMapping.bankBranchId)
            District district = (District) districtCacheUtility.read(purchaseInstrumentMapping.districtId)
            GridEntity obj = new GridEntity()
            obj.id = purchaseInstrumentMapping.id
            obj.cell = [
                    counter,
                    instrument ? instrument.key : Tools.EMPTY_SPACE,
                    bank? bank.name : Tools.EMPTY_SPACE,
                    bankBranch? bankBranch.name : Tools.EMPTY_SPACE,
                    district? district.name : Tools.EMPTY_SPACE
            ]
            lstWrappedObj << obj
            counter++
        }
        return lstWrappedObj
    }
}
