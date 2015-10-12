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

class SearchBankBranchActionService extends BaseService implements ActionIntf {

    private static final String NAME = 'name'
    private final Logger log = Logger.getLogger(getClass());

    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        try {
            boolean hasAccess = appSessionUtil.getAppUser().isPowerUser
            Map outputMap = [hasAccess: hasAccess]
            return outputMap;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    Object execute(Object params, Object obj = null) {
        LinkedHashMap result
        try {
            // redundant logic?
            if ((!params.sortname) || (params.sortname.toString().equals(Tools.ID))) {
                // if no sort name then sort by name/asc
                params.sortname = NAME
                params.sortorder = ASCENDING_SORT_ORDER
            }
            initSearch(params)
            List<BankBranch> bankBranchList = []
            List<BankBranch> tempBankBranches = bankBranchCacheUtility.list()
            tempBankBranches.each { BankBranch bankBranch ->
                bankBranchList.add(bankBranch)
            }

            // check to see if it's a search by field (single)
            String query = Tools.escapeForRegularExpression(params.query.toString()) // escaping special char for regX
            String qtype = params.qtype.toString();

            Map searchResult = bankBranchCacheUtility.searchByField(qtype, query, bankBranchList, this)
            result = [bankBranchList: searchResult.list, count: searchResult.count]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
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
            if (!bankBranchList) return bankBranches
            int counter = start + 1
            //remove each closure
            bankBranchList.each { BankBranch bankBranch ->
                GridEntity obj = new GridEntity()
                obj.id = bankBranch.id
                Bank bank = (Bank) bankCacheUtility.read(bankBranch.bankId)
                District district = (District) districtCacheUtility.read(bankBranch.districtId)
                obj.cell = ["${counter}", bankBranch.id, bank.name,
                        district.name, bankBranch.name,
                        bankBranch.code ? bankBranch.code : Tools.EMPTY_SPACE,
                        bankBranch.address ? bankBranch.address : Tools.EMPTY_SPACE,
                        bankBranch.isPrincipleBranch ? Tools.YES : Tools.NO,
                        bankBranch.isSmeServiceCenter ? Tools.YES : Tools.NO
                ]
                bankBranches << obj
                counter++
            };
            return bankBranches;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return bankBranches;
        }
    }

    /**
     * List bankBranch has not post-condition
     * @param paramters
     * @param obj
     * @return nothing
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null;
    }

    /**
     * Wrapping bankBranch list retrieved from search into FlexiGrid equivalent
     * row representation with page number and total
     *
     * @param obj bankBranch list to wrap in GridEntity collection
     * @return Collection of GridEntity, total and page number in a map
     */
    public Object buildSuccessResultForUI(Object bankBranchResult) {
        LinkedHashMap output
        try {
            List<BankBranch> bankBranchList = (List<BankBranch>) bankBranchResult.bankBranchList
            int count = (int) bankBranchResult.count
            def bankBranchs = this.wrapBankBranchListInGridEntityList(bankBranchList, start)
            output = [page: pageNumber, total: count, rows: bankBranchs]
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
