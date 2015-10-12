package com.athena.mis.accounting.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.entity.AccTier1
import com.athena.mis.accounting.entity.AccTier2
import com.athena.mis.accounting.entity.AccTier3
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.service.AccTier3Service
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *  For details go through Use-Case doc named 'AccTier3CacheUtility'
 */
@Component('accTier3CacheUtility')
class AccTier3CacheUtility extends ExtendedCacheUtility {
    @Autowired
    AccTier3Service accTier3Service
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility
    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTier2CacheUtility accTier2CacheUtility

    static final String SORT_BY_NAME = 'name'

    private static final String QUERY_TYPE_ACC_TYPE = "accType"
    private static final String QUERY_TYPE_ACC_TIER1 = "accTier1"
    private static final String QUERY_TYPE_ACC_TIER2 = "accTier2"

    /**
     * pull all list of accTier3 objects and store list in cache
     */
    public void init() {
        List list = accTier3Service.list()
        super.setList(list)
    }

    /**
     * get list of specific accTier3 objects from given accTier3 objects
     * @param queryType -queryType
     * @param query -query
     * @param accTier3List -list of accTier3 objects
     * @return -list of specific accTier3 objects
     */
    public Map searchByField(String queryType, String query, List<AccTier3> accTier3List, BaseService baseService) {
        AccType accType
        AccTier1 accTier1
        AccTier2 accTier2
        query = Tools.escapeForRegularExpression(query)
        accTier3List = accTier3List.findAll {
            if (queryType.equals(QUERY_TYPE_ACC_TYPE)) {
                accType = (AccType) accTypeCacheUtility.read(it.accTypeId)
                String accTypeName = accType.name
                accTypeName ==~ /(?i).*${query}.*/
            } else if (queryType.equals(QUERY_TYPE_ACC_TIER1)) {
                accTier1 = (AccTier1) accTier1CacheUtility.read(it.accTier1Id)
                String accTier1Name = accTier1.name
                accTier1Name ==~ /(?i).*${query}.*/
            } else if (queryType.equals(QUERY_TYPE_ACC_TIER2)) {
                accTier2 = (AccTier2) accTier2CacheUtility.read(it.accTier2Id)
                String accTier2Name = accTier2.name
                accTier2Name ==~ /(?i).*${query}.*/
            } else {
                it.properties.get(queryType) ==~ /(?i).*${query}.*/
            }
        }
        int end = accTier3List.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : accTier3List.size()
        List lstResult = accTier3List.subList(baseService.start, end)
        return [list: lstResult, count: accTier3List.size()]
    }

    /**
     * get list of active accTier3 objects by specific accTier2Id
     * @param accTier2Id -accTier2.Id
     * @return -list of active accTier3 objects
     */
    public List<AccTier3> listByAccTier2Id(int accTier2Id) {
        List<AccTier3> newAccTier3List = []
        List<AccTier3> accTier3List = super.list()
        for (int i = 0; i < accTier3List.size(); i++) {
            AccTier3 accTier3 = accTier3List[i]
            if ((accTier3.accTier2Id == accTier2Id) && (accTier3.isActive)) {
                newAccTier3List << accTier3
            }
        }
        return newAccTier3List
    }

    /**
     * get specific accTier3 object by specific accTier3 name
     * @param accTier3Name -accTier3.name
     * @return -if specific accTier3 object found then return that object; otherWise return null
     */
    public AccTier3 readByName(String accTier3Name) {
        accTier3Name = accTier3Name.trim()
        String existingAccTier3Name
        List<AccTier3> accTier3List = super.list()
        for (int i = 0; i < accTier3List.size(); i++) {
            existingAccTier3Name = accTier3List[i].name
            if (existingAccTier3Name.equalsIgnoreCase(accTier3Name)) {
                return accTier3List[i]
            }
        }
        return null
    }

    /**
     * get specific accTier3 object by accTier3Name & accTier3Id
     * @param accTier3Name -AccTier3.name
     * @param accTier3Id -AccTier3.id
     * @return -if specific accTier3 object found then return that object; otherWise return null
     */
    public AccTier3 readByNameForUpdate(String accTier3Name, int accTier3Id) {
        accTier3Name = accTier3Name.trim()
        String existingAccTier3Name
        List<AccTier3> accTier3List = super.list()
        for (int i = 0; i < accTier3List.size(); i++) {
            existingAccTier3Name = accTier3List[i].name
            if (existingAccTier3Name.equalsIgnoreCase(accTier3Name)
                    && accTier3List[i].id != accTier3Id) {
                return accTier3List[i]
            }
        }
        return null
    }
}