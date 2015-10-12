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
 *  Show UI for bank CRUD and list of bank for grid
 *  For details go through Use-Case doc named 'ExhShowBankActionService'
 */
class ShowBankActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass());

    private static final String DEFAULT_FAILURE_MSG_SHOW_BANK = "Failed to load bank information page"
    private static final String NAME = 'name'
    private static final String GRID_OBJ = "gridObj"
    private static final String LST_BANKS = "lstBanks"

    @Autowired
    BankCacheUtility bankCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        // do nothing for pre operation
        return null
    }

    /**
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            initPager(params)                                               // initialize params for flexGrid
            sortColumn = NAME                                               // set sort column by name
            sortOrder = ASCENDING_SORT_ORDER                                // set sort order by asc

            List<Bank> lstBanks = bankCacheUtility.list()             // get bank form cache utility
            int count = lstBanks.size()                                      // get count total Bank
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
            return result
        }
    }

    /**
     * Wrap list of bank in grid entity
     * @param bankList -list of bank object(s)
     * @param start -starting index of the page
     * @return -list of wrapped banks
     */

    private List wrapBanks(List<Bank> bankList, int start) {
        List banks = []
        try {
            int counter = start + 1
            for (int i = 0; i < bankList.size(); i++) {
                GridEntity obj = new GridEntity()
                Bank bank = bankList[i]
                obj.id = bank.id
                obj.cell = [
                        counter,
                        bank.id ? bank.id : Tools.EMPTY_SPACE,
                        bank.name ? bank.name : Tools.EMPTY_SPACE,
                        bank.code ? bank.code : Tools.EMPTY_SPACE
                ]
                banks << obj
                counter++
            }

            return banks;
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return banks;
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null;
    }

    /**
     * Wrap list of banks for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj                           // cast map returned from execute method
            List<Bank> lstBanks = (List<Bank>) executeResult.get(LST_BANKS)
            int total = (int) executeResult.get(Tools.COUNT)
            List<GridEntity> banks = (List<GridEntity>) wrapBanks(lstBanks, start)
            Map gridOutput = [page: pageNumber, total: total, rows: banks]

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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj                    // cast map returned from previous method
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