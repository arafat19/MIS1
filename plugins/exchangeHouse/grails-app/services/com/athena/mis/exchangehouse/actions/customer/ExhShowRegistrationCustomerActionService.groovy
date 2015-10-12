package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.Country
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.exchangehouse.entity.ExhPhotoIdType
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

/**
 *  Show UI for customer registration
 *  For details go through Use-Case doc named 'ExhShowRegistrationCustomerActionService'
 */
class ExhShowRegistrationCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = "Fail to load customer registration page"
    private static final String COUNTRY_lIST = "countryList"
    private static final String PHOTO_ID_TYPE_LIST = "photoIdTypeList"
    private static final String OBJ_COMPANY = "objCompany"
    private static final String ISD_CODE = "isdCode"
    private static final String NATIONALITY = "nationality"

    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get list of country, photo ID type & company for dropDown
     * @param params -N/A
     * @param obj -an object of HttpServletRequest
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            HttpServletRequest request = (HttpServletRequest) obj
            Company company = (Company) getCompany(request)
            Country nativeCountry = (Country) countryCacheUtility.read(company.countryId, company.id)
            List<Country> countryList = countryCacheUtility.list(company.id)
            // get country list from cache for dropDown

            List<ExhPhotoIdType> photoIdTypeList = exhPhotoIdTypeCacheUtility.list(company.id)
            // get photo ID type  list from cache for dropDown
            List dropDownNationality = Tools.listForKendoDropdown(countryList, NATIONALITY, null)
            result.put(OBJ_COMPANY, company)
            result.put(ISD_CODE, nativeCountry.isdCode)
            result.put(COUNTRY_lIST, dropDownNationality)
            result.put(PHOTO_ID_TYPE_LIST, Tools.listForKendoDropdown(photoIdTypeList, null, null))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for buildSuccessResultForUI operation
     */
    public Object buildSuccessResultForUI(Object customerResult) {
        return null
    }

    /**
     * do nothing for buildFailureResultForUI operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private Company getCompany(HttpServletRequest request) {
        String fullUrl = Tools.getFullUrl(request, false)    // retrieve url with www
        Company company = companyCacheUtility.readByWebUrl(fullUrl)
        if (company) {
            return company
        }
        // if company not found try to retrieve url without www
        fullUrl = Tools.getFullUrl(request, true)
        company = companyCacheUtility.readByWebUrlWithoutWWW(fullUrl)
        return company
    }

}
