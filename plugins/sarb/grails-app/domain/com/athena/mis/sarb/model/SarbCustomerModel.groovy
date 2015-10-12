package com.athena.mis.sarb.model

/**
 * SarbCustomerModel is the model for database view vw_sarb_customer_model,
 * which is primarily used to combine ExhCustomer & SarbCustomer (and for integration with SARB)
 */

class SarbCustomerModel {

	public static final String SQL_SARB_CUSTOMER_MODEL = """
	DROP TABLE IF EXISTS vw_sarb_customer_model;
	DROP VIEW IF EXISTS vw_sarb_customer_model;
	CREATE OR REPLACE VIEW vw_sarb_customer_model AS
	SELECT
	customer.id AS id,
	customer.name,
	customer.surname,
	customer.code,
	country.code AS country_code,
	customer.phone,
	customer.photo_id_no,
	customer.date_of_birth,
	customer.email,
	customer.address,
	customer.post_code,
	customer.created_on,
	system_entity.value AS gender_code,
	photo_type.code AS photo_id_code,
	customer.company_id,
	sarb_customer.suburb,
	sarb_customer.city,
	province.name AS province_name,
	sarb_customer.contact_name,
	sarb_customer.contact_surname
	FROM exh_customer customer
	LEFT JOIN sarb_customer_details sarb_customer ON sarb_customer.customer_id = customer.id
	LEFT JOIN country ON country.id = customer.country_id
	LEFT JOIN system_entity ON system_entity.id = customer.gender_id
	LEFT JOIN sarb_province province ON province.id = sarb_customer.province_id
	LEFT JOIN exh_photo_id_type photo_type ON photo_type.id = customer.photo_id_type_id;
	"""

	long id					//ExhCustomer.id
	String name				//ExhCustomer.name
	String surname			//ExhCustomer.surname
	String code				//ExhCustomer.code
	String countryCode		//Country.code
	String phone			//ExhCustomer.phone
	String photoIdNo		//ExhCustomer.photoIdNo
	Date dateOfBirth		//ExhCustomer.dateOfBirth
	String email			//ExhCustomer.email
	String address      	//ExhCustomer.address
	String postCode			//ExhCustomer.postCode
	Date createdOn			//ExhCustomer.createdOn
	String genderCode		//SystemEntity.value
	long companyId			//ExhCustomer.companyId
    String photoIdCode      //ExhCustomer.photoIdTypeId.code

	String suburb			//SarbCustomerDetails.suburb
	String city				//SarbCustomerDetails.city
	String provinceName		//SarbProvince.name
	String contactName		//SarbCustomerDetails.contactName
	String contactSurname   //SarbCustomerDetails.contactSurname

	static mapping = {
		table "vw_sarb_customer_model"
		version false
		cache usage: "read-only"
	}
}
