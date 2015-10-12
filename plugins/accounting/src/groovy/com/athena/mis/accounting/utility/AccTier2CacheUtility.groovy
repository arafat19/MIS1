package com.athena.mis.accounting.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.entity.AccTier2
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.service.AccTier2Service
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('accTier2CacheUtility')
class AccTier2CacheUtility extends ExtendedCacheUtility {
    @Autowired
    AccTier2Service accTier2Service
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    static final String SORT_BY_NAME = 'name'
    private static final String QUERY_TYPE_ACC_TYPE = "accType"
    private static final String ACC_TIER1_ID = "accTier1Id"

    public void init() {
        List list = accTier2Service.list()
        super.setList(list)
    }

    public Map searchByField(String queryType, String query, List<AccTier2> accTier2List, BaseService baseService) {

        AccType accType
        query = Tools.escapeForRegularExpression(query)
        accTier2List = accTier2List.findAll {
            if (queryType.equals(QUERY_TYPE_ACC_TYPE)) {
                accType = (AccType) accTypeCacheUtility.read(it.accTypeId)
                String accTypeName = accType.name
                accTypeName ==~ /(?i).*${query}.*/
            } else {
                it.properties.get(queryType) ==~ /(?i).*${query}.*/
            }
        }
        int end = accTier2List.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : accTier2List.size()
        List lstResult = accTier2List.subList(baseService.start, end)
        return [list: lstResult, count: accTier2List.size()]
    }

    public List<AccTier2> listByAccTier1Id(int accTier1Id) {
        List<AccTier2> newAccTier2List = []
        List<AccTier2> accTier2List = super.list()
        for (int i = 0; i < accTier2List.size(); i++) {
            if ((accTier2List[i].accTier1Id == accTier1Id) && (accTier2List[i].isActive)) {
                newAccTier2List << accTier2List[i]
            }
        }
        return newAccTier2List
    }

    public List<AccTier2> listByAccTier1IdForEdit(int accTier1Id, int accTier2Id) {
        List<AccTier2> newAccTier2List = []
        List<AccTier2> accTier2List = super.list()
        for (int i = 0; i < accTier2List.size(); i++) {
            if ((accTier2List[i].accTier1Id == accTier1Id) && ((accTier2List[i].isActive) || accTier2List[i].id == accTier2Id)) {
                newAccTier2List << accTier2List[i]
            }
        }
        return newAccTier2List
    }

    public AccTier2 readByName(String accTier2Name) {
        accTier2Name = accTier2Name.trim()
        String existingAccTier2Name
        List<AccTier2> accTier2List = super.list()
        for (int i = 0; i < accTier2List.size(); i++) {
            existingAccTier2Name = accTier2List[i].name
            if (existingAccTier2Name.equalsIgnoreCase(accTier2Name)) {
                return accTier2List[i]
            }
        }
        return null
    }
}
