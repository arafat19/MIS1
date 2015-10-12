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

class ListRmsPurchaseInstrumentMappingActionService extends BaseService implements ActionIntf {

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

    private static final String FAILURE_MESSAGE = "Failed to load purchase instrument mapping page"
    private static final String LST_INSTRUMENT_MAPPING = "lstInstrumentMapping"

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
     * 1. Get RmsPurchaseInstrumentMapping list
     * 2. Get count of total RmsPurchaseInstrumentMapping
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameterMap)
            List<RmsPurchaseInstrumentMapping> rmsPurchaseInstrumentMappingList = rmsPurchaseInstrumentMappingService.list(this)
            int count = rmsPurchaseInstrumentMappingService.count()
            result.put(LST_INSTRUMENT_MAPPING, rmsPurchaseInstrumentMappingList)
            result.put(Tools.COUNT, count.toInteger())
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
     * Wrap RmsPurchaseInstrumentMapping list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     */
    Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<RmsPurchaseInstrumentMapping> rmsPurchaseInstrumentMappingList = (List<RmsPurchaseInstrumentMapping>) executeResult.get(LST_INSTRUMENT_MAPPING)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedObj = wrapRmsPurchaseInstrumentMapping(rmsPurchaseInstrumentMappingList, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedObj]
            return output
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
     * @return -a map containing isError = true & relevant error message
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
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of RmsPurchaseInstrumentMapping in grid entity
     * @param lstRmsPurchaseInstrumentMapping -list of RmsPurchaseInstrumentMapping object
     * @param start -starting index of the page
     * @return -list of wrapped RmsPurchaseInstrumentMapping
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
