package com.athena.mis.application.actions.bankbranch

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.District
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search bankBranch and show specific list of bankBranch(s) for grid on Dash Board
 *  For details go through Use-Case doc named 'SearchDistributionPointActionService'
 */
class SearchDistributionPointActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass());

    private static final String NAME = 'name'
    private static final String GRID_OBJ = "gridObj"
    private static final String DEFAULT_FAILURE_MSG = "Failed to load distribution point information page"
    private static final String LST_DISTRIBUTION_POINT = "lstDistributionPoint"

    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get bankBranch list for grid through specific search
     * 1. pull bankBranch from cache
     * 2. search by field
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    Object execute(Object params, Object obj = null) {
        Map result = new LinkedHashMap()
        try {
            if ((!params.sortname) || (params.sortname.toString().equals(Tools.ID))) {
                params.sortname = NAME
                params.sortorder = ASCENDING_SORT_ORDER
            }
            initSearch(params)
            List<BankBranch> lstDistributionPoint
            lstDistributionPoint = (List<BankBranch>) bankBranchCacheUtility.list()
            Map searchResult = bankBranchCacheUtility.searchByField(queryType, query, lstDistributionPoint, this)
            result.put(Tools.COUNT, searchResult.count)
            result.put(LST_DISTRIBUTION_POINT, searchResult.list)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * Wrap list of bankBranch(s) in grid entity
     * @param lstDistributionPoint -list of bankBranch object(s)
     * @param start -starting index of the page
     * @return -list of wrapped distributionPoints
     */
    private List<GridEntity> wrapDistributionPoint(List<BankBranch> lstDistributionPoint, int start) {
        List distributionPoints = []
        try {
            int counter = start + 1
            for (int i = 0; i < lstDistributionPoint.size(); i++) {
                BankBranch distributionPoint = lstDistributionPoint[i]
                GridEntity obj = new GridEntity()
                obj.id = distributionPoint.id
                Bank bank = (Bank) bankCacheUtility.read(distributionPoint.bankId)
                District district = (District) districtCacheUtility.read(distributionPoint.districtId)
                obj.cell = [
                        counter,
                        "${distributionPoint.id}",
                        "${distributionPoint.name}",
                        "${district.name}",
                        "${bank.name}"
                ]
                distributionPoints.add(obj)
                counter = counter + 1
            };
            return distributionPoints;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return distributionPoints;
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }

    /**
     * Wrap bankBranch list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj    // cast map returned from execute method
            List lstDistributionPoint = (List) executeResult.get(LST_DISTRIBUTION_POINT)
            int count = ((Integer) executeResult.get(Tools.COUNT)).intValue()
            List<GridEntity> wrappedDistributionPoint = wrapDistributionPoint(lstDistributionPoint, start)
            Map gridOutput = [page: pageNumber, total: count, rows: wrappedDistributionPoint]
            result.put(GRID_OBJ, gridOutput)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
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
     * @return -a map containing isError = true & relevant error message
     */
    Object buildFailureResultForUI(Object obj) {
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
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

}
