package com.athena.mis.accounting.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.entity.AccTier1
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.service.AccTier1Service
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('accTier1CacheUtility')
class AccTier1CacheUtility extends ExtendedCacheUtility {

    @Autowired
    AccTier1Service accTier1Service
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    static final String SORT_BY_NAME = 'name'
    private static final String QUERY_TYPE_ACC_TYPE = "accType"

    public void init() {
        List list = accTier1Service.list()
        super.setList(list)
    }

    public Map searchByField(String queryType, String query, List<AccTier1> accTier1List, BaseService baseService) {
        AccType accType
        query = Tools.escapeForRegularExpression(query)
        accTier1List = accTier1List.findAll {
            if (queryType.equals(QUERY_TYPE_ACC_TYPE)) {
                accType = (AccType) accTypeCacheUtility.read(it.accTypeId)
                String accTypeName = accType.name
                accTypeName ==~ /(?i).*${query}.*/
            } else {
                it.properties.get(queryType) ==~ /(?i).*${query}.*/
            }
        }
        int end = accTier1List.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : accTier1List.size()
        List lstResult = accTier1List.subList(baseService.start, end)
        return [list: lstResult, count: accTier1List.size()]
    }

    public List<AccTier1> listByAccTypeId(long accTypeId) {
        List<AccTier1> newAccTier1List = []
        List<AccTier1> accTier1List = super.list()
        for (int i = 0; i < accTier1List.size(); i++) {
            if ((accTier1List[i].accTypeId == accTypeId) && (accTier1List[i].isActive)) {
                newAccTier1List << accTier1List[i]
            }
        }
        return newAccTier1List
    }

    public List<AccTier1> listByAccTypeIdForEdit(long accTypeId, int accTier1Id) {
        List<AccTier1> newAccTier1List = []
        List<AccTier1> accTier1List = super.list()
        for (int i = 0; i < accTier1List.size(); i++) {
            if ((accTier1List[i].accTypeId == accTypeId) && (accTier1List[i].isActive || accTier1List[i].id == accTier1Id)) {
                newAccTier1List << accTier1List[i]
            }
        }
        return newAccTier1List
    }

    public AccTier1 readByName(String accTier1Name) {
        accTier1Name = accTier1Name.trim()
        String existingAccTier1Name
        List<AccTier1> accTier1List = super.list()
        for (int i = 0; i < accTier1List.size(); i++) {
            existingAccTier1Name = accTier1List[i].name
            if (existingAccTier1Name.equalsIgnoreCase(accTier1Name)) {
                return accTier1List[i]
            }
        }
        return null
    }
}
