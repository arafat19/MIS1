package com.athena.mis.application.utility

import com.athena.mis.CacheUtility
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.CompanyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *  For details go through Use-Case doc named 'CompanyCacheUtility'
 */
@Component('companyCacheUtility')
class CompanyCacheUtility extends CacheUtility {
    @Autowired
    CompanyService companyService

    public final String SORT_ON_NAME = "name";

    /**
     * pull all list of company and store list in cache
     */
    public void init() {
        List list = companyService.list();
        super.setList(list)
    }

    /**
     * read company from cache by webUrl property
     * @return -matching company with given url otherwise return null
     */
    public Company readByWebUrl(String webUrl) {
        List<Company> lstCompany = (List<Company>) list()
        for (int i = 0; i < lstCompany.size(); i++) {
            if (lstCompany[i].webUrl.equals(webUrl)) {
                return lstCompany[i]
            }
        }
        return null
    }

    /**
     * read company from cache by webUrl property But exclude www
     * @return -matching company with given url otherwise return null
     */
    private static final String OPENING_WWW = '//www.'
    private static final String DOUBLE_SLASH = '//'

    public Company readByWebUrlWithoutWWW(String webUrl) {
        List<Company> lstCompany = (List<Company>) list()
        for (int i = 0; i < lstCompany.size(); i++) {
            String companyUrl = lstCompany[i].webUrl
            companyUrl = companyUrl.replace(OPENING_WWW, DOUBLE_SLASH)
            if (companyUrl.equals(webUrl)) {
                return lstCompany[i]
            }
        }
        return null
    }

    public int countByNameIlikeAndIdNotEqual(String name, long id) {
        int count = 0
        List<Company> lstAll = (List<Company>) list()
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].name.equalsIgnoreCase(name) && lstAll[i].id != id) {
                count++
            }
        }
        return count
    }

    public int countByCodeIlikeAndIdNotEqual(String code, long id) {
        int count = 0
        List<Company> lstAll = (List<Company>) list()
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].code.equalsIgnoreCase(code) && lstAll[i].id != id) {
                count++
            }
        }
        return count
    }

    public int countByEmailIlikeAndIdNotEqual(String email, long id) {
        int count = 0
        List<Company> lstAll = (List<Company>) list()
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].email.equals(email) && lstAll[i].id != id) {
                count++
            }
        }
        return count
    }

    public int countByNameIlike(String name) {
        int count = 0
        List<Company> lstAll = (List<Company>) list()
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].name.equalsIgnoreCase(name)) {
                count++
            }
        }
        return count
    }

    public int countByCodeIlike(String code) {
        int count = 0
        List<Company> lstAll = (List<Company>) list()
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].code.equalsIgnoreCase(code)) {
                count++
            }
        }
        return count
    }

    public int countByEmailIlike(String email) {
        int count = 0
        List<Company> lstAll = (List<Company>) list()
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].email.equalsIgnoreCase(email)) {
                count++
            }
        }
        return count
    }

    public int countByWebUrlIlikeAndIdNotEqual(String url, long id) {
        int count = 0
        List<Company> lstAll = (List<Company>) list()
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].webUrl.equals(url) && lstAll[i].id != id) {
                count++
            }
        }
        return count
    }
}
