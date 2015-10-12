package com.athena.mis.document.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.Company
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.service.DocCategoryService
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

/**
 * Created by Rezaul on 4/30/14.
 */
@Component('docCategoryCacheUtility')
class DocCategoryCacheUtility extends ExtendedCacheUtility {

    public static final String DEFAULT_SORT_ORDER = 'name'

    @Autowired
    DocCategoryService docCategoryService

    @Transactional(readOnly = true)
    public void init() {
        List categoryList = docCategoryService.list()
        super.setList(categoryList)
    }

    // Following method will return a object instance from app scope for any valid urlInName
    public DocCategory findByUrlInName(String urlInName, long companyId) {
        List lstAll = list(companyId)
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].urlInName.equalsIgnoreCase(urlInName))
                return lstAll[i]
        }
        return null
    }

    // Following method will return a object instance from app scope for any valid id
    public Object readByIdAndCompanyId(long id, HttpServletRequest request) {
        Long companyId = getCompanyId(request)
        List lstAll = list(companyId)
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].id == id)
                return lstAll[i]
        }
        return null
    }

    /**
     * retrieve companyUser from request
     * @param request
     * @return company ID
     */

    private long getCompanyId(HttpServletRequest request) {
        String fullUrl = Tools.getFullUrl(request, false)    // retrieve url with www
        Company company = companyCacheUtility.readByWebUrl(fullUrl) // compare with www
        if (company) {
            return company.id
        }
        // if company not found try to retrieve url without www
        fullUrl = Tools.getFullUrl(request, true)
        company = companyCacheUtility.readByWebUrlWithoutWWW(fullUrl)     // compare without www
        if (company) {
            return company.id
        } else {
            return 0L
        }
    }
}
