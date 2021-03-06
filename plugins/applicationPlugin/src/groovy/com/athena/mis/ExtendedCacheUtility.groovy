package com.athena.mis

import com.athena.mis.application.entity.Company
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired

class ExtendedCacheUtility {

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    CompanyCacheUtility companyCacheUtility

    private Map<Long, List> lstMain = new HashMap<Long, List>()

    public final String SORT_ORDER_ASCENDING = 'asc'
    public final String SORT_ORDER_DESCENDING = 'desc'

    public void setList(List lstObj) {
        initMain()
        for (int i = 0; i < lstObj.size(); i++) {
            Object obj = lstObj[i]
            Long objCompanyId = new Long(obj.companyId)
            List lstCompany = (List) lstMain.get(objCompanyId)
            lstCompany << obj
            lstMain.put(objCompanyId, lstCompany)
        }
    }

    private void initMain() {
        List<Company> lstCompany = (List<Company>) companyCacheUtility.list()
        for (int i = 0; i < lstCompany.size(); i++) {
            lstMain.put(lstCompany[i].id, [])
        }
    }

    public List list() {
        Long companyId = new Long(appSessionUtil.getCompanyId())
        List lstAll = lstMain.get(companyId)
        return lstAll
    }

    public List list(long companyId) {
        Long id = new Long(companyId)
        List lstAll = lstMain.get(id)
        return lstAll
    }

    public List list(BaseService baseService) {
        Long companyId = new Long(appSessionUtil.getCompanyId())
        List lstAll = new ArrayList()
        lstAll = lstMain.get(companyId)
        sort(lstAll,baseService.sortColumn,baseService.sortOrder)
        int end = lstAll.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstAll.size();
        return lstAll.subList(baseService.start, end);
    }

    public int count() {
        Long companyId = new Long(appSessionUtil.getCompanyId())
        List lstAll = lstMain.get(companyId)
        return lstAll.size()
    }

    public List search(String queryType, String query) {
        Long companyId = new Long(appSessionUtil.getCompanyId())
        List lstAll = lstMain.get(companyId)
        query = Tools.escapeForRegularExpression(query)
        return lstAll.findAll { it.properties.get(queryType) ==~ /(?i).*${query}.*/ }
    }

    public Map search(String queryType, String query, BaseService baseService) {
        Long companyId = new Long(appSessionUtil.getCompanyId())
        List lstAll = lstMain.get(companyId)
        query = Tools.escapeForRegularExpression(query)
        List lstSearchResult = lstAll.findAll { it.properties.get(queryType) ==~ /(?i).*${query}.*/ }
        sort(lstSearchResult,baseService.sortColumn,baseService.sortOrder)
        int end = lstSearchResult.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstSearchResult.size()
        List lstResult = lstSearchResult.subList(baseService.start, end)
        return [list: lstResult, count: lstSearchResult.size()]
    }

    public boolean add(Object object, String fieldName, String sortOrder) {
        Long companyId = new Long(object.companyId)
        List lstAll = lstMain.get(companyId)
        lstAll.add(object)
        sort(lstAll, fieldName, sortOrder)
        return true
    }

    // Following method will update the specific Object in lstAll
    public boolean update(Object object, String fieldName, String sortOrder) {
        Long companyId = new Long(object.companyId)
        List lstAll = lstMain.get(companyId)
        long objectId = object.id
        delete(objectId, companyId)
        lstAll.add(object)
        sort(lstAll, fieldName, sortOrder)
        return true
    }

    // Following method will delete the specific Object from lstAll
    public boolean delete(long id) {
        Long companyId = new Long(appSessionUtil.getCompanyId())
        List lstAll = lstMain.get(companyId)
        int i = 0
        int list_size = lstAll.size()
        for (i = 0; i < list_size; i++) {
            if (lstAll[i].id == id) {
                lstAll.remove(lstAll[i])
                return true
            }
        }
        return false
    }

    // Following method will delete the specific Object from lstAll with given companyId
    // used by update() method
    public boolean delete(long id, long companyId) {
        List lstAll = lstMain.get(companyId)
        int i = 0
        int list_size = lstAll.size()
        for (i = 0; i < list_size; i++) {
            if (lstAll[i].id == id) {
                lstAll.remove(lstAll[i])
                return true
            }
        }
        return false
    }

    // Following method will return a object instance from app scope for any valid id
    public Object read(long id) {
        Long companyId = new Long(appSessionUtil.getCompanyId())
        List lstAll = lstMain.get(companyId)
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].id == id)
                return lstAll[i]
        }
        return null
    }

    public Object read(long id, long companyId) {
        List lstAll = lstMain.get(new Long(companyId))
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].id == id)
                return lstAll[i]
        }
        return null
    }

    // Following method will return a object instance from app scope for any reserved id
    public Object readByReservedAndCompany(long reservedId, long companyId) {
        List lstAll = lstMain.get(new Long(companyId))
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].reservedId == reservedId)
                return lstAll[i]
        }
        return null
    }

    // lstAllActive contains List of active objects where lstAll holds both active & inactive objects
    public List listByIsActive() {
        Long companyId = new Long(appSessionUtil.getCompanyId())
        List lstAll = lstMain.get(companyId)
        List lstAllActive = lstAll.findAll { it.isActive == true }
        return lstAllActive
    }

    protected void sort(List lstAll, String fieldName, String sortOrder) {
        if (sortOrder.equalsIgnoreCase(SORT_ORDER_DESCENDING)) {
            lstAll.sort { a, b -> b.properties.get(fieldName)<=>a.properties.get(fieldName) }
        } else {
            lstAll.sort { b, a -> b.properties.get(fieldName)<=>a.properties.get(fieldName) }
        }
    }

    public int countByNameIlikeAndIdNotEqual(String name, long id) {
        int count = 0
        List lstAll = list()
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].name.equalsIgnoreCase(name) && lstAll[i].id != id) {
                count++
            }
        }
        return count
    }

    public int countByNameIlike(String name) {
        int count = 0
        List lstAll = list()
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].name.equalsIgnoreCase(name)) {
                count++
            }
        }
        return count
    }
}

