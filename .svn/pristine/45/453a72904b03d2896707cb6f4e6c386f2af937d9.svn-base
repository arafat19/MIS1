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
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ShowBankBranchActionService extends BaseService implements ActionIntf {

    private static final String NAME = 'name'

    private final Logger log = Logger.getLogger(getClass())
    private static final String BANK_ID="bankId"
    private static final String BRANCH_LIST="bankBranchList"

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
            boolean hasAccess = appSessionUtil.getAppUser().isPowerUser
            Map outputMap = [hasAccess: hasAccess]
            return outputMap;
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null;
        }
    }

    /**
     * Retrieving a list of bankBranchs, it may sort and pagingate the resulting
     * bankBranch list if requested from the browser.
     *
     * Pagination request has been revealed by invoking super's initPager method
     *
     * @param params request parameters
     * @param obj additional parameters
     * @return list of bankBranchs with count and page number for pagination
     */
    Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap=(GrailsParameterMap)params
            if (!parameterMap.rp) {
                parameterMap.rp = 15;
            }
            initPager(parameterMap)
            sortColumn = NAME  // no usage?
            sortOrder = ASCENDING_SORT_ORDER // no usage?

            List<BankBranch> bankBranchList = getSortedBranches()
            int count = bankBranchList.size()
            //bank id- required att of district taglib
            long bankId=0                                       //bank id-0 to populate all districts
            result.put(BANK_ID,bankId)
            result.put(BRANCH_LIST,bankBranchList)
            result.put(Tools.COUNT,count)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result = [bankBranchList: null, count: 0]
            return result
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
        Map output
        try {
            output = [bankBranchListJSON: getBranchListJSON(bankBranchResult),
                    allBanks: bankCacheUtility.list(),
                    allDistricts: districtCacheUtility.list()]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage());
            output = [page: pageNumber, total: 0, rows: null]
            return [bankBranchListJSON: output, allBanks: [], allDistricts: []]
        }
    }

    private LinkedHashMap<String, Object> getBranchListJSON(bankBranchResult) {
        List<BankBranch> bankBranchList = (List<BankBranch>) bankBranchResult.bankBranchList
        int count = (int) bankBranchResult.count
        List bankBranches = (List) wrapBankBranchListInGridEntityList(bankBranchList)
        return [page: pageNumber, total: count, rows: bankBranches]
    }

    /**
     * Builds UI specific object on failure;
     *
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public Object buildFailureResultForUI(Object obj) {
        // do nothing
        return null
    }

    private List<BankBranch> getSortedBranches() {
        List<BankBranch> bankBranchList = []
        List<BankBranch> tempBankBranches = bankBranchCacheUtility.list() // why not sorted list?
        for (BankBranch bankBranch in tempBankBranches) {
            bankBranchList.add(bankBranch) // why not clone?
        }
        int count = bankBranchList.size()
        int resultCount = start + resultPerPage
        int end = (resultCount > count) ? count : resultCount
        bankBranchList = bankBranchList.subList(start, end)
        return bankBranchList
    }

    /**
     * Wrapping each BankBranch entity in GridEntity object (required representation of object
     * for Flexigrid), create a list of GridEntity, and then return
     *
     * @param bankBranchList List of all BankBranchs
     * @param start start offset, required to set counter
     * @return List of GridEntity
     */
    private List<BankBranch> wrapBankBranchListInGridEntityList(List<BankBranch> bankBranchList) {
        List bankBranches = [] as List
        try {
            if (!bankBranchList) return bankBranches
            int counter = start + 1
            for (BankBranch bankBranch in bankBranchList) {
                GridEntity obj = new GridEntity()
                obj.id = bankBranch.id
                Bank bank = (Bank) bankCacheUtility.read(bankBranch.bankId)
                District district = (District) districtCacheUtility.read(bankBranch.districtId)
                obj.cell = [counter, bankBranch.id, bank.name,
                        district.name, bankBranch.name,
                        bankBranch.code ? bankBranch.code : Tools.EMPTY_SPACE,
                        bankBranch.address ? bankBranch.address : Tools.EMPTY_SPACE]
                bankBranches.add(obj)
                counter = counter + 1
            }
            return bankBranches;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return bankBranches;
        }
    }
}