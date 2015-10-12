package com.athena.mis.accounting.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.service.AccChartOfAccountService
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("accChartOfAccountCacheUtility")
class AccChartOfAccountCacheUtility extends ExtendedCacheUtility {

    @Autowired
    AccChartOfAccountService accChartOfAccountService
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    static final String SORT_BY_ID = "id";
    static final String SEARCH_BY_CODE = "code";

    public void init() {
        List list = accChartOfAccountService.list();
        super.setList(list)
    }

    public AccChartOfAccount readByCode(String code) {
        code = code.toUpperCase()
        List<AccChartOfAccount> lstMain = (List<AccChartOfAccount>) super.list()
        List lstCoa = lstMain.findAll { it.code.equals(code) }
        if (lstCoa.size() > 0) {
            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) lstCoa[0]
            return accChartOfAccount
        }
        return null
    }

    public Map searchByCodeAndDescription(String query, BaseService baseService) {
        query = Tools.escapeForRegularExpression(query)
        List<AccChartOfAccount> lstMain = (List<AccChartOfAccount>) super.list()
        List<AccChartOfAccount> lstCodeMatch = lstMain.findAll { it.code ==~ /(?i).*${query}.*/ }
        List<AccChartOfAccount> lstDescriptionMatch = lstMain.findAll { it.description ==~ /(?i).*${query}.*/ }
        List<AccChartOfAccount> lstReturn = (lstCodeMatch + lstDescriptionMatch).unique()
        int end = lstReturn.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstReturn.size()
        List lstResult = lstReturn.subList(baseService.start, end)
        return [list: lstResult, count: lstReturn.size()]
    }

    //Get List-For-Bank-Cash-Group by companyID
    public List listForBankCashGroup() {
        List lstAll = super.list()
        List subList = []
        AccGroup accGroupBank = accGroupCacheUtility.readBySystemAccGroup(accGroupCacheUtility.ACC_GROUP_BANK)
        AccGroup accGroupCash = accGroupCacheUtility.readBySystemAccGroup(accGroupCacheUtility.ACC_GROUP_CASH)

        if (lstAll.size() <= 0) {
            return subList
        }
        for (int i = 0; i < lstAll.size(); i++) {
            if ((lstAll[i].accGroupId == accGroupBank.id) || (lstAll[i].accGroupId == accGroupCash.id)) {
                subList.add(lstAll[i])
            }
        }
        return subList
    }

    public Map listForBankCashGroup(BaseService baseService) {
        List lstAll = super.list()
        List lstSearchResult = []
        AccGroup accGroupBank = accGroupCacheUtility.readBySystemAccGroup(accGroupCacheUtility.ACC_GROUP_BANK)
        AccGroup accGroupCash = accGroupCacheUtility.readBySystemAccGroup(accGroupCacheUtility.ACC_GROUP_CASH)

        if (lstAll.size() <= 0) {
            return [list: lstSearchResult, count: 0]
        }
        for (int i = 0; i < lstAll.size(); i++) {
            if ((lstAll[i].accGroupId == accGroupBank.id) || (lstAll[i].accGroupId == accGroupCash.id)) {
                lstSearchResult.add(lstAll[i])
            }
        }
        int end = lstSearchResult.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstSearchResult.size()
        List lstResult = lstSearchResult.subList(baseService.start, end)
        return [list: lstResult, count: lstSearchResult.size()]
    }

    public Map searchForBankCashGroup(String query, BaseService baseService) {
        query = Tools.escapeForRegularExpression(query)
        List<AccChartOfAccount> lstMainList = (List<AccChartOfAccount>) listForBankCashGroup()
        List<AccChartOfAccount> lstCodeMatch = lstMainList.findAll { (it.code ==~ /(?i).*${query}.*/) }
        List<AccChartOfAccount> lstDescriptionMatch = lstMainList.findAll { (it.description ==~ /(?i).*${query}.*/) }
        List<AccChartOfAccount> lstReturn = (lstCodeMatch + lstDescriptionMatch).unique()
        int end = lstReturn.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstReturn.size()
        List lstResult = lstReturn.subList(baseService.start, end)
        return [list: lstResult, count: lstReturn.size()]
    }

    //Get List-For-Bank-Cash-Group by companyID
    public List listByAccGroupId(long accGroupId) {
        List lstAll = (List<AccChartOfAccount>) super.list()
        List subList = []
        if (lstAll.size() <= 0) {
            return subList
        }

        for (int i = 0; i < lstAll.size(); i++) {
            if ((lstAll[i].accGroupId == accGroupId)) {
                subList.add(lstAll[i])
            }
        }
        return subList
    }

    //Get ID List-For-Bank-Cash-Group by companyID
    public List listIdsByAccGroupId(long accGroupId) {
        List lstAll = (List<AccChartOfAccount>) super.list()
        List listIds = []
        if (lstAll.size() <= 0) {
            return listIds
        }

        for (int i = 0; i < lstAll.size(); i++) {
            if ((lstAll[i].accGroupId == accGroupId)) {
                listIds.add(lstAll[i].id)
            }
        }
        return listIds
    }

    //Get ID List-For-Bank-Cash-Group by companyID
    public List listIdsByAccGroupIds(List accGroupIds) {
        List lstAll = (List<AccChartOfAccount>) super.list()
        List listIds = []
        if (lstAll.size() <= 0) {
            return listIds
        }

        for (int i = 0; i < lstAll.size(); i++) {
            for (int j = 0; j <= i; j++) {
                if ((lstAll[i].accGroupId == accGroupIds[j])) {
                    listIds.add(lstAll[i].id)
                }
            }
        }
        return listIds
    }
}