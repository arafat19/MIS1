package com.athena.mis.sarb.service

import com.athena.mis.BaseService
import com.athena.mis.sarb.entity.SarbCustomerDetails
import com.athena.mis.utility.DateUtility

class SarbCustomerDetailsService extends BaseService{


	private static final String INSERT_QUERY =
		"""
        INSERT INTO sarb_customer_details(
        id, version, customer_id, suburb, city,
        province_id,contact_surname,contact_name,
        company_id, updated_by, updated_on)
        VALUES (
        NEXTVAL('sarb_customer_details_id_seq'),
        :version, :customerId, :suburb, :city, :provinceId,
        :contactSurname, :contactName, :companyId,
        :updatedBy, :updatedOn);
    """
	/**
	 * Save SarbCustomer object into DB
	 * @param SarbCustomer -SarbCustomer object
	 * @return -saved SarbCustomer object
	 */
	public SarbCustomerDetails create(SarbCustomerDetails customerDetails) {
		Map queryParams = [
				version: customerDetails.version,
				customerId: customerDetails.customerId,
				suburb: customerDetails.suburb,
				city: customerDetails.city,
				provinceId: customerDetails.provinceId,
				contactSurname: customerDetails.contactSurname,
				contactName: customerDetails.contactName,
				companyId: customerDetails.companyId,
				updatedBy: customerDetails.updatedBy,
				updatedOn: customerDetails.updatedOn
		]
		List result = executeInsertSql(INSERT_QUERY, queryParams)

		if (result.size() <= 0) {
			throw new RuntimeException('Error occurred while insert SarbCustomerDetails information')
		}
		int sarbCustomerDetailsId = (int) result[0][0]
		customerDetails.id = sarbCustomerDetailsId
		return customerDetails
	}

	/**
	 * Read SarbCustomerDetails by customerId
	 * @param customerId
	 * @return SarbCustomerDetails obj
	 */
	public SarbCustomerDetails readByCustomerId(long customerId) {
		SarbCustomerDetails sarbCustomerDetails = SarbCustomerDetails.findByCustomerId(customerId, [readOnly: true])
		return sarbCustomerDetails
	}

	private static final String UPDATE_QUERY =
		"""
        UPDATE sarb_customer_details SET
            version=:newVersion,
            suburb=:suburb,
            province_id=:provinceId,
            city=:city,
            contact_name=:contactName,
            contact_surname=:contactSurname,
            updated_by=:updatedBy,
            updated_on=:updatedOn
        WHERE
            id=:id AND
            version=:version
    """
	/**
	 * Update SarbCustomerDetails in DB
	 * @param customerDetails
	 * @return int updateCount
	 */
	public int update(SarbCustomerDetails customerDetails) {
		Map queryParams = [
				id: customerDetails.id,
				newVersion: customerDetails.version + 1,
				version: customerDetails.version,
				suburb: customerDetails.suburb,
				provinceId: customerDetails.provinceId,
				city: customerDetails.city,
				contactName: customerDetails.contactName,
				contactSurname: customerDetails.contactSurname,
				updatedBy: customerDetails.updatedBy,
				updatedOn: DateUtility.getSqlDateWithSeconds(customerDetails.updatedOn)
		]
		int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);

		if (updateCount <= 0) {
			throw new RuntimeException('Error occurred while update SarbCustomerDetails information')
		}
		return updateCount
	}
}
