package com.athena.mis

import com.athena.mis.utility.Tools

class CacheUtility {

    private List lstAll
    private List lstAllActive

    public final String SORT_ORDER_ASCENDING = 'asc'
    public final String SORT_ORDER_DESCENDING = 'desc'

    public void setList(List list) {
        lstAll = list
    }

    public List list() {
        return lstAll
    }

    public List list(BaseService baseService) {
        int end = lstAll.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstAll.size();
        return lstAll.subList(baseService.start, end);
    }

    public int count() {
        return lstAll.size()
    }

    public List search(String queryType, String query) {
        query = Tools.escapeForRegularExpression(query)
        return lstAll.findAll { it.properties.get(queryType) ==~ /(?i).*${query}.*/ }
    }

    public Map search(String queryType, String query, BaseService baseService) {
        query = Tools.escapeForRegularExpression(query)
        List lstSearchResult = lstAll.findAll { it.properties.get(queryType) ==~ /(?i).*${query}.*/ }
        int end = lstSearchResult.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstSearchResult.size()
        List lstResult = lstSearchResult.subList(baseService.start, end)
        return [list: lstResult, count: lstSearchResult.size()]
    }

    public boolean add(Object object, String fieldName, String sortOrder) {
        List tmpList = []
        if (lstAll == null) {
            lstAll = tmpList
        }
        lstAll.add(object)
        sort(fieldName, sortOrder)
        return true
    }

    public boolean update(Object object, String fieldName, String sortOrder) {
        long objectId = object.id
        delete(objectId)
        lstAll.add(object)
        sort(fieldName, sortOrder)
        return true
    }

    public boolean delete(long id) {
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].id == id) {
                lstAll.remove(lstAll[i])
                return true
            }
        }
        return false
    }

    public Object read(long id) {
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].id == id)
                return lstAll[i]
        }
        return null
    }

    public Object readByReservedAndCompany(long reservedId, long companyId) {
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].reservedId == reservedId && lstAll[i].companyId == companyId)
                return lstAll[i]
        }
        return null
    }

    public List listByIsActive() {
        if (lstAllActive) return lstAllActive
        List lstTemp = list()
        lstAllActive = lstTemp.findAll { it.isActive == true }
        return lstAllActive
    }

    private void sort(String fieldName, String sortOrder) {
        if (sortOrder.equalsIgnoreCase(SORT_ORDER_DESCENDING)) {
            lstAll.sort { a, b -> b.properties.get(fieldName) <=> a.properties.get(fieldName) }
        } else {
            lstAll.sort { b, a -> b.properties.get(fieldName) <=> a.properties.get(fieldName) }
        }
    }
}
