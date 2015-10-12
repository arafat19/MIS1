package com.athena.mis.application.actions.bankbranch

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.District
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class ListBankBranchActionService extends BaseService implements ActionIntf {

    private static final String HAS_ACCESS = "hasAccess"
    private static final String NAME = 'name'
    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

/**
 * List bankBranch has no pre-condition
 */
    public Object executePreCondition(Object parameters, Object obj) {
        try {
            Map outputMap = new HashMap();
            boolean hasAccess = appSessionUtil.getAppUser().isPowerUser
            outputMap.put(HAS_ACCESS, hasAccess)
            return outputMap;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Retrieving a list of bankBranches, it may sort and paginate the resulting
     * bankBranch list if requested from the browser.
     *
     * Pagination request has been revealed by invoking super's initPager method
     *
     * @param params request parameters
     * @param obj additional parameters
     * @return list of bankBranchs with count and page number for pagination
     */
    Object execute(Object params, Object obj) {
        LinkedHashMap result
        try {

            if (!params.rp) {
                params.rp = DEFAULT_RESULT_PER_PAGE;
            }

            if ((!params.sortname) || (params.sortname.toString().equals(Tools.ID))) {
                // if no sort name then sort by name/asc
                params.sortname = NAME
                params.sortorder = ASCENDING_SORT_ORDER
            }

            initSearch(params) // initSearch will call initPager()
            List<BankBranch> bankBranchList = []

            List<BankBranch> tempBankBranchs = bankBranchCacheUtility.list()
            tempBankBranchs.each { BankBranch bankBranch ->
                bankBranchList.add(bankBranch)
            }


            if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {    // if desc
                bankBranchList.sort { a, b -> b.properties.get(sortColumn)<=>a.properties.get(sortColumn) }
            } else {
                bankBranchList.sort { b, a -> b.properties.get(sortColumn)<=>a.properties.get(sortColumn) }
            }

            int count = bankBranchList.size()
            int end = start + resultPerPage > count ? count : start + resultPerPage
            bankBranchList = bankBranchList.subList(start, end)

            result = [bankBranchList: bankBranchList, count: count]
            return result
        } catch (Exception e) {
            log.error(e.getMessage());
            result = [bankBranchList: null, count: 0]
            return result
        }
    }

    /**
     * Wrapping each BankBranch entity in GridEntity object (required representation of object
     * for Flexigrid), create a list of GridEntity, and then return
     *
     * @param bankBranchList List of all BankBranchs
     * @param start start offset, required to set counter
     * @return List of GridEntity
     */
    private List<BankBranch> wrapBankBranchListInGridEntityList(List<BankBranch> bankBranchList, int start) {
        List bankBranches = [] as List
        try {
            int counter = start + 1
            bankBranchList.each { BankBranch bankBranch ->
                GridEntity obj = new GridEntity()
                obj.id = bankBranch.id
                Bank bank = (Bank) bankCacheUtility.read(bankBranch.bankId)
                District district = (District) districtCacheUtility.read(bankBranch.districtId)
                obj.cell = [counter, bankBranch.id, bank.name,
                        district.name, bankBranch.name,
                        bankBranch.code ? bankBranch.code : Tools.EMPTY_SPACE,
                        bankBranch.address ? bankBranch.address : Tools.EMPTY_SPACE,
                        bankBranch.isPrincipleBranch ? Tools.YES : Tools.NO,
                        bankBranch.isSmeServiceCenter ? Tools.YES : Tools.NO
                ]
                bankBranches.add(obj)
                counter++
            };
            return bankBranches;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return bankBranches;
        }
    }

    /**
     * List bankBranch has no post-condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null;
    }

    /**
     * Wrapping bankBranch list into FlexiGrid equivalent row representation
     * with page number and total
     *
     * @param obj bankBranch list to wrap in GridEntity collection
     * @return Collection of GridEntity, total and page number in a map
     */
    public Object buildSuccessResultForUI(Object bankBranchResult) {
        LinkedHashMap output
        try {
            List<BankBranch> bankBranchList = (List<BankBranch>) bankBranchResult.bankBranchList
            int count = (int) bankBranchResult.count
            List<BankBranch> bankBranches = wrapBankBranchListInGridEntityList(bankBranchList, start)
            output = [page: pageNumber, total: count, rows: bankBranches]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage());
            output = [page: pageNumber, total: 0, rows: null]
            return output
        }

    }

    /**
     * Builds UI specific object on failure;
     *
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}

