package com.athena.mis.document.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.service.DocSubCategoryService
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

/**
 * Created by Rezaul on 5/22/14.
 */
@Component("docSubCategoryCacheUtility")
class DocSubCategoryCacheUtility extends ExtendedCacheUtility {
    public static final String DEFAULT_SORT_ORDER = 'name'

    @Autowired
    DocSubCategoryService docSubCategoryService

    @Transactional(readOnly = true)
    public void init() {
        List lstSubCategory = docSubCategoryService.list()
        super.setList(lstSubCategory)
    }

    // Following method will return a object instance from app scope for any valid id
    public Object readByIdAndCompanyId(long id, long companyId) {
        List lstAll = list(companyId)
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].id == id)
                return lstAll[i]
        }
        return null
    }

    // Following method will return a object instance from app scope for any valid urlInName
    public DocSubCategory findByUrlInSubCategoryName(String urlInName, long companyId) {
        List lstAll = list(companyId)
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].urlInName.equalsIgnoreCase(urlInName))
                return lstAll[i]
        }
        return null
    }
    /**
     * Get List of Sub Category by category Id
     * @param categoryId - Category Id
     * @return lstByCategoryId - list by category id
     * */

    public List<DocSubCategory> listByCategoryId(long categoryId) {
        List<DocSubCategory> lstAll = list()
        List<DocSubCategory> lstByCategoryId = []
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].categoryId == categoryId) {
                lstByCategoryId << lstAll[i]
            }
        }

        return lstByCategoryId
    }
    /**
     * Get List of Sub Category by category Id & isActive = true
     * @param categoryId - Category Id
     * @return lstByCategoryId - list by category id
     * */

    public List<DocSubCategory> listByCategoryIdAndActive(long categoryId) {
        List<DocSubCategory> lstAll = list()
        List<DocSubCategory> lstByCategoryId = []
        for (int i = 0; i < lstAll.size(); i++) {
            DocSubCategory subCategory = lstAll[i]
            if ((subCategory.categoryId == categoryId) && subCategory.isActive) {
                lstByCategoryId << subCategory
            }
        }
        return lstByCategoryId
    }
    /**
     * Get Count of Sub Category by category Id
     * @param categoryId - Category Id
     * @return size of lstByCategoryId
     * */

    public int countByCategoryIdAndIsActive(long categoryId) {
        List<DocSubCategory> lstAll = list()
        List<DocSubCategory> lstByCategoryId = []
        for (int i = 0; i < lstAll.size(); i++) {
            if ((lstAll[i].categoryId == categoryId) && lstAll[i].isActive) {
                lstByCategoryId << lstAll[i]
            }
        }

        return lstByCategoryId.size()
    }
    /**
     * Get List of Sub Category by category Id & BaseService
     * @param categoryId - Category Id
     * @param baseService - BaseService object for pagination
     * @return subList of subcategory by baseService
     * */

    public List<DocSubCategory> listByCategoryId(long categoryId, BaseService baseService) {
        List<DocSubCategory> lstAll = listByCategoryId(categoryId)
        int end = lstAll.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstAll.size();
        return lstAll.subList(baseService.start, end);
    }
    /**
     * Get Count of Sub Category by category Id
     * @param categoryId - Category Id
     * @return size of lstByCategoryId
     * */

    public int countByCategoryId(long categoryId) {
        List<DocSubCategory> lstAll = list()
        List<DocSubCategory> lstByCategoryId = []
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].categoryId == categoryId) {
                lstByCategoryId << lstAll[i]
            }
        }

        return lstByCategoryId.size()
    }
    /**
     * Get Count of Sub Category by name & category Id for duplicate count in create
     * @param name
     * @param categoryId - Category Id
     * @return size of lstByCategoryId
     * */

    public int countByNameIlikeAndCategoryId(String name, long categoryId) {
        List<DocSubCategory> lstAll = list()
        List<DocSubCategory> lstByCategoryId = []
        for (int i = 0; i < lstAll.size(); i++) {
            if ((lstAll[i].categoryId == categoryId) && (lstAll[i].name.equalsIgnoreCase(name))) {
                lstByCategoryId << lstAll[i]
            }
        }

        return lstByCategoryId.size()
    }
    /**
     * Get Count of Sub Category by name & category Id for duplicate count in update
     * @param subCategory - Sub Category Object
     * @return size of lstByCategoryId
     * */

    public int countByNameIlikeAndCategoryIdAndIdNotEqual(DocSubCategory subCategory) {
        List<DocSubCategory> lstAll = list()
        List<DocSubCategory> lstByCategoryId = []
        for (int i = 0; i < lstAll.size(); i++) {
            if ((lstAll[i].categoryId == subCategory.categoryId) && (lstAll[i].name.equalsIgnoreCase(subCategory.name)) && (lstAll[i].id != subCategory.id)) {
                lstByCategoryId << lstAll[i]
            }
        }

        return lstByCategoryId.size()
    }
    /**
     * Update Subcategory isActive for category isActive = false
     * @param categoryId - Category Id
     * @return true
     * */
    public boolean updateSubCategoryForIsActiveFalse(long categoryId) {
        List<DocSubCategory> lstAll = listByCategoryId(categoryId)
        for (int i = 0; i < lstAll.size(); i++) {
            lstAll[i].isActive = false
        }
        return true
    }

    /**
     * Get SubCategory List & Count of Sub Category by category Id, queryTpe, query, baseService for search
     * @param categoryId - Category Id
     * @return - a map of lstResult and count for Sub Category Search
     * */
    public Map searchByCategoryId(long categoryId, String queryType, String query, BaseService baseService) {
        List lstAll = listByCategoryId(categoryId)
        query = Tools.escapeForRegularExpression(query)
        List lstSearchResult = lstAll.findAll { it.properties.get(queryType) ==~ /(?i).*${query}.*/ }
        int end = lstSearchResult.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstSearchResult.size()
        List lstResult = lstSearchResult.subList(baseService.start, end)
        return [list: lstResult, count: lstSearchResult.size()]
    }

}
