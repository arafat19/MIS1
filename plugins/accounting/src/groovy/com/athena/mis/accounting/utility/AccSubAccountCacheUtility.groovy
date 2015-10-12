package com.athena.mis.accounting.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.entity.AccSubAccount
import com.athena.mis.accounting.service.AccSubAccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *  For details go through Use-Case doc named 'AccSubAccountCacheUtility'
 */
@Component('accSubAccountCacheUtility')
class AccSubAccountCacheUtility extends ExtendedCacheUtility {
    @Autowired
    AccSubAccountService accSubAccountService

    public static final String SORT_ON_DESCRIPTION = "description"

    /**
     * pull all list of AccSubAccount objects and store list in cache
     */
    public void init() {
        List list = accSubAccountService.list()
        super.setList(list)
    }

    /**
     * get list of AccSubAccount list by specific CoaId
     * @param coaId -AccChartOfAccount.id
     * @return -list of accSubAccount objects
     */
    public List<AccSubAccount> searchByCoaIdAndCompany(long coaId) {
        List<AccSubAccount> lstAccSubAccount = this.list()
        return lstAccSubAccount.findAll { (it.coaId == coaId) && (it.isActive) }
    }

    /**
     * get AccSubAccount object by description & specific CoaId
     * @param description -AccSubAccount.description
     * @param coaId -AccChartOfAccount.id
     * @return -if specific accSubAccount object found then return accSubAccount; otherWise return null
     */
    public AccSubAccount readByDescriptionAndCoaId(String description, long coaId) {
        List<AccSubAccount> lstAccSubAccount = this.list()
        for (int i = 0; i < lstAccSubAccount.size(); i++) {
            if (lstAccSubAccount[i].coaId == coaId && lstAccSubAccount[i].description.equalsIgnoreCase(description)) {
                return lstAccSubAccount[i]
            }
        }
        return null
    }

    /**
     * get AccSubAccount object by id, description & specific CoaId
     * @param subAccountId -AccSubAccount.id
     * @param description -AccSubAccount.description
     * @param coaId -AccChartOfAccount.id
     * @return -if specific accSubAccount object found then return accSubAccount; otherWise return null
     */
    public AccSubAccount readByDescriptionAndCoaIdForUpdate(long subAccountId, String description, long coaId) {
        List<AccSubAccount> lstAccSubAccount = this.list()
        for (int i = 0; i < lstAccSubAccount.size(); i++) {
            if (lstAccSubAccount[i].id != subAccountId && lstAccSubAccount[i].coaId == coaId && lstAccSubAccount[i].description.equalsIgnoreCase(description)) {
                return lstAccSubAccount[i]
            }
        }
        return null
    }
}
