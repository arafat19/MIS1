package com.athena.mis.exchangehouse.service

import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired

class ExhCustomerTraceService extends BaseService {

    @Autowired
    ExhSessionUtil exhSessionUtil

    private static final String CUSTOMER_TRACE_FIELD_NAMES =
        """
            id,
            action,
            action_date,
            created_by,
            name,
            surname,
            country_id,
            phone,
            photo_id_type_id,
            photo_id_expiry_date,
            photo_id_no,
            date_of_birth,
            email,
            address,
            address_verified_status,
            is_sanction_exception,
            post_code,
            company_id,
            source_of_fund,
            declaration_amount,
            declaration_start,
            declaration_end,
            company_reg_no,
            date_of_incorporation,
            visa_expire_date,
            agent_id,
            profession,
            customer_id,
            sms_subscription,
            mail_subscription,
            gender_id
        """

    /**
     * Save customer trace object into DB
     * @param customer -ExhCustomer object
     * @param executionDate -Date object
     * @param paramAction -Character object
     * @return -true
     */
    public long create(ExhCustomer customer, Date executionDate, Character paramAction) {
        char action = Tools.ACTION_UPDATE // Default action
        if (paramAction) action = paramAction
        if (customer.userId < 1) {

            customer.userId = exhSessionUtil.appSessionUtil.getAppUser().id
        }
        Long traceId = getNextVal()
        String queryTrace =
            """
                INSERT INTO exh_customer_trace (${CUSTOMER_TRACE_FIELD_NAMES})
                VALUES
                ( ${traceId},
                '${action}',
                ${"'" + DateUtility.getDBDateFormatWithSecond(executionDate) + "'"},
                ${customer.userId},
                :name,
                :surname,
                ${customer.countryId},
                :phone,
                ${customer.photoIdTypeId},
                ${customer.photoIdExpiryDate ? "'" + DateUtility.getDBDateFormatWithSecond(customer.photoIdExpiryDate) + "'" : null},
                :photoIdNo,
                ${"'" + DateUtility.getDBDateFormatWithSecond(customer.dateOfBirth) + "'"},
                :email,
                :address,
                ${customer.addressVerifiedStatus},
                ${customer.isSanctionException},
                :postCode,
                ${customer.companyId},
                :sourceOfFund,
                ${customer.declarationAmount},
                ${customer.declarationStart ? "'" + DateUtility.getDBDateFormat(customer.declarationStart) + "'" : null},
                ${customer.declarationEnd ? "'" + DateUtility.getDBDateFormat(customer.declarationEnd) + "'" : null},
                :companyRegNo,
                :dateOfIncorporation,
                ${customer.visaExpireDate ? "'" + DateUtility.getDBDateFormat(customer.visaExpireDate) + "'" : null},
                :agentId,
                :profession,
                :customerId,
                ${customer.smsSubscription},
                ${customer.mailSubscription},
                ${customer.genderId}
                )
            """

        Map queryParams = [
                name: customer.name,
                surname: customer.surname,
                phone: customer.phone,
                photoIdNo: customer.photoIdNo,
                email: customer.email,
                address: customer.address,
                postCode: customer.postCode,
                sourceOfFund: customer.sourceOfFund,
                companyRegNo:customer.companyRegNo,
                dateOfIncorporation: customer.dateOfIncorporation,
                agentId: customer.agentId,
                profession: customer.profession,
                customerId: customer.id
        ]
        List result = executeInsertSql(queryTrace, queryParams)

		if(result.size() <=0 ) {
			throw new RuntimeException("Failed to insert CustomerTrace information.")
		}

        return traceId.longValue()
    }

    // get the next id sequence for customerTrace
    private static final String QUERY_CUSTOMER_TRACE_NEXT_VAL = "select nextval('exh_customer_trace_id_seq')"

    private Long getNextVal() {
        List result = executeSelectSql(QUERY_CUSTOMER_TRACE_NEXT_VAL)
        Long traceID = (Long) result[0][0]
        return traceID
    }
}
