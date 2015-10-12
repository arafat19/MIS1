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
 *  Search bank and show specific list of bank(s) for grid
 *  For details go through Use-Case doc named 'ExhSearchBankActionService'
 */
class SearchBankActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass());

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load Bank List"
    private static final String GRID_OBJ = "gridObj"
    private static final String NAME = 'name'
    private static final String LST_BANK = "lstBank"

    @Autowired
    BankCacheUtility bankCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        // do nothing for pre operation
        return null
    }

    /**
     * Get bank list for grid through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            if ((!params.sortname) || (params.sortname.toString().equals(Tools.ID))) {
                // if no sort name then sort by name/asc
                params.sortname = NAME
                params.sortorder = ASCENDING_SORT_ORDER
            }
            initSearch(params)
            Map searchResult = bankCacheUtility.search(queryType, query, this)   // get sub list of bank by search key word
            result.put(Tools.COUNT, searchResult.count)
            result.put(LST_BANK, searchResult.list)
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
     * Wrap list of banks in grid entity
     * @param lstBanks -list of bank object(s)
     * @param start -starting index of the page
     * @return -list of wrapped banks
     */
    private List<GridEntity> wrapBanks(List<Bank> lstBanks, int start) {

        List<GridEntity> banks = []
        int counter = start + 1
        lstBanks.each { Bank bank ->
            GridEntity obj = new GridEntity()
            obj.id = bank.id
            obj.cell = [
                    counter,
                    "${bank.id ? bank.id : Tools.EMPTY_SPACE}",
                    "${bank.name ? bank.name : Tools.EMPTY_SPACE}",
                    "${bank.code ? bank.code : Tools.EMPTY_SPACE}"
            ]
            banks.add(obj)
            counter = counter + 1
        };
        return banks;
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }

    /**
     * Wrap bank list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj                                    // cast map returned from execute method
            List<Bank> lstBanks = (List<Bank>) executeResult.get(LST_BANK)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedBanks = wrapBanks(lstBanks, start)
            Map gridOutput = [page: pageNumber, total: count, rows: lstWrappedBanks]
            result.put(GRID_OBJ, gridOutput)
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
     * @return -a map containing isError = true & relevant error message
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

}
