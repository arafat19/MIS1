package com.athena.mis.exchangehouse.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Country
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.entity.ExhPhotoIdType
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 * build html for show customer for admin
 * for details go through use-case "GetExhCustomerDetailsTagLibActionService"
 */
class GetExhCustomerDetailsTagLibActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ID = "id"
    private static final String PROPERTY_NAME = "property_name"
    private static final String PROPERTY_VALUE = "property_value"
    private static final String FROM_DATE = "from_date"
    private static final String TO_DATE = "to_date"
    private static final String URL = "url"

    ExhCustomerService exhCustomerService
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility

    @Override
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    @Override
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * check if propertyName and propertyValue exists
     * get exhCustomer and build customer details
     * @param parameters - attr of taglib
     * @param obj - N/A
     * @return - customerDetails
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        Map preResult = new LinkedHashMap()
        try {
            Map attr = (Map) parameters
            String strId = attr.get(ID)
            String strUrl = attr.get(URL)
            String propertyName = attr.get(PROPERTY_NAME)
            def propertyValue = attr.get(PROPERTY_VALUE)
            Date fromDate = DateUtility.parseMaskedFromDate(attr.get(FROM_DATE).toString())
            Date toDate = DateUtility.parseMaskedFromDate(attr.get(TO_DATE).toString())
            preResult.put(ID, strId)
            preResult.put(URL, strUrl)
            ExhCustomer customer
            if (propertyName && propertyValue) {
                customer = getExhCustomer(fromDate, toDate, propertyName, propertyValue)
            }
            String customerDetails = buildCustomerDetails(customer, preResult)
            return customerDetails
        } catch (Exception e) {
            log.error(e.getMessage())
            return Boolean.FALSE
        }
    }

    @Override
    Object buildSuccessResultForUI(Object obj) {
        return null
    }

    @Override
    Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * get ExhCustomer
     * @param fromDate
     * @param toDate
     * @param propertyName
     * @param propertyValue
     * @return ExhCustomer obj
     */
    private ExhCustomer getExhCustomer(Date fromDate, Date toDate, String propertyName, def propertyValue) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        List<ExhCustomer> lstCustomer = ExhCustomer.withCriteria {
            if (propertyName.equals(ID)) {
                long propertyVal = propertyValue ? Tools.parseLongInput(propertyValue.toString()) : 0L
                eq(propertyName, propertyVal)
            } else {
                ilike(propertyName, propertyValue)
            }
            if (fromDate && toDate) {
                between('createdOn', fromDate, toDate)
            }
            eq("companyId", companyId)
            setReadOnly(true)
        }
        return (lstCustomer.size()) ? lstCustomer[0] : null
    }

    /**
     * build customer details html
     * @param customer
     * @param preResult
     * @return html
     */
    private String buildCustomerDetails(ExhCustomer customer, Map preResult) {
        String strId = preResult.get(ID) ? "id=${preResult.get(ID)}" : Tools.EMPTY_SPACE
        String strUrl = preResult.get(URL) ? "url=${preResult.get(URL)}" : Tools.EMPTY_SPACE
        Country country
        ExhPhotoIdType photoIdType
        String systemCurrencyName
        if (customer) {
            country = (Country) countryCacheUtility.read(customer.countryId)
            photoIdType = (ExhPhotoIdType) exhPhotoIdTypeCacheUtility.read(customer.photoIdTypeId)
            systemCurrencyName = currencyCacheUtility.getLocalCurrency().symbol
        }

        String html = """
        <div ${strId}  ${strUrl}>
            <table class="table table-condensed table-bordered" style="margin:0">
                <tbody>
                    <tr>
                        <td class="active">Name:</td>
                        <td>${customer ? customer.name : Tools.EMPTY_SPACE}</td>
                        <td class="active">Declaration amount:</td>
                        <td>${
            customer?.declarationAmount ? (customer.declarationAmount + Tools.SINGLE_SPACE + systemCurrencyName) : Tools.EMPTY_SPACE
        }</td>
                    </tr>
                    <tr>
                        <td class="active">Customer A/C no:</td>
                        <td>${customer ? customer.code : Tools.EMPTY_SPACE}</td>
                        <td class="active">Address Verified:</td>
                        <td style='color:green'>${customer?.addressVerifiedStatus ? Tools.YES : Tools.EMPTY_SPACE}</td>
                    </tr>
                    <tr>
                        <td class="active">Nationality:</td>
                        <td>${country ? country.nationality : Tools.EMPTY_SPACE}</td>
                        <td class="active">Phone:</td>
                        <td>${customer ? customer.phone : Tools.EMPTY_SPACE}</td>
                    </tr>
                    <tr>
                        <td class="active">Date Of Birth:</td>
                        <td>${customer ? customer.dateOfBirth : Tools.EMPTY_SPACE}</td>
                        <td class="active">Email:</td>
                        <td>${customer?.email ? customer.email : Tools.EMPTY_SPACE}</td>
                    </tr>
                    <tr>
                        <td class="active">Post Code:</td>
                        <td>${customer?.postCode ? customer.postCode : Tools.EMPTY_SPACE}</td>
                        <td class="active">Photo ID Type:</td>
                        <td>${photoIdType ? photoIdType.name : Tools.EMPTY_SPACE}</td>
                    </tr>
                    <tr>
                        <td class="active">Source of Fund:</td>
                        <td>${customer ? customer.sourceOfFund : Tools.EMPTY_SPACE}</td>
                        <td class="active">Photo ID Expiry Date:</td>
                        <td>${customer?.photoIdExpiryDate ? customer.photoIdExpiryDate : Tools.EMPTY_SPACE}</td>
                    </tr>
                    <tr>
                        <td class="active">Address:</td>
                        <td>${customer ? customer.address : Tools.EMPTY_SPACE}</td>
                        <td class="active">Photo ID No:</td>
                        <td>${customer?.photoIdNo ? customer.photoIdNo : Tools.EMPTY_SPACE}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        """
        return html
    }
}
