package com.athena.mis.sarb.service

import com.athena.mis.BaseService
import com.athena.mis.sarb.utility.SarbSessionUtil
import org.springframework.beans.factory.annotation.Autowired

class SarbCustomerDetailsTraceService extends BaseService {
    @Autowired
    SarbSessionUtil sarbSessionUtil
	private static final String INSERT_QUERY =
		"""
        INSERT INTO sarb_customer_details_trace(
        id, customer_trace_id, customer_id, suburb, city,
        province_id,contact_surname,contact_name,
        company_id)
        VALUES (
        NEXTVAL('sarb_customer_details_trace_id_seq'),
        :customerTraceId, :customerId, :suburb, :city, :provinceId,
        :contactSurname, :contactName, :companyId);
    """
	/**
	 * Save SarbCustomerDetailsTrace object into DB
	 * @param params - ParameterMap containing all SarbCustomerTrace attr
	 * @return true/throw exception
	 */
	public boolean create(Map params) {
		Map queryParams = [
				customerTraceId: params.customerTraceId,
				customerId: Long.parseLong(params.customerId),
				suburb: params.suburb,
				city: params.city,
				provinceId: Long.parseLong(params.provinceId),
				contactSurname: params.contactSurname,
				contactName: params.contactName,
				companyId: sarbSessionUtil.appSessionUtil.getCompanyId()
		]
		List result = executeInsertSql(INSERT_QUERY, queryParams)

		if (result.size() <= 0) {
			throw new RuntimeException('Error occurred while insert SarbCustomerDetailsTrace information.')
		}
		return true
	}
}
