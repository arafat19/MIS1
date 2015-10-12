package com.athena.mis.application.actions.bank

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of bank for grid
 *  For details go through Use-Case doc named 'ListBankActionService'
 */
class ListBankActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass());

    private static final String DEFAULT_FAILURE_MSG_SHOW_BANK = "Failed to load bank information page"
    private static final String NAME = 'name'
    private static final String GRID_OBJ = "gridObj"
    private static final String LST_BANKS = "lstBanks"

    @Autowired
    BankCacheUtility bankCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get bank list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()

        try {
            if (!params.rp) {
                params.rp = 15;
            }

            if ((!params.sortname) || (params.sortname.toString().equals(Tools.ID))) {
                // if no sort name then sort by name/asc
                params.sortname = NAME
                params.sortorder = ASCENDING_SORT_ORDER
            }
            initSearch(params)                                      // initSearch will call initPager()

            List<Bank> lstBanks = bankCacheUtility.list()
            int count = lstBanks.size()                                                 // get count total bank
            int end = (start + resultPerPage) > count ? count : (start + resultPerPage)
            lstBanks = lstBanks.subList(start, end)

            result.put(LST_BANKS, lstBanks)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_BANK)
            return null
        }
    }

    /**
     * Wrapping each Bank entity in GridEntity object (required representation of object
     * for flexgrid), create a list of GridEntity, and then return
     * @param bankList List of all Banks
     * @param start start offset, required to set counter
     * @return List of GridEntity
     */
    private List<GridEntity> wrapBanks(List<Bank> bankList, int start) {

        List<GridEntity> banks = []
        try {
            if (!bankList) return banks
            int counter = start + 1
            for (int i = 0; i < bankList.size(); i++) {
                Bank bank = bankList[i]
                GridEntity obj = new GridEntity()
                obj.id = bank.id
                obj.cell = [counter, "${bank.id}", "${bank.name}", "${bank.code}"]
                banks.add(obj)
                counter = counter + 1
            };
            return banks;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return banks;
        }
    }

    /**
     * do nothing for ost operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }

    /**
     * Wrap bank list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj    // cast map returned from execute method
            List lstBanks = (List) executeResult.get(LST_BANKS)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrapBanks = wrapBanks(lstBanks, start)

            Map gridOutput = [page: pageNumber, total: count, rows: lstWrapBanks]
            result.put(GRID_OBJ, gridOutput)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_BANK)
            return result
        }
    }

    /**
     * Builds UI specific object on failure;
     * @param obj -Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public Object buildFailureResultForUI(Object obj) {
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
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_BANK)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_BANK)
            return result
        }
    }
}
