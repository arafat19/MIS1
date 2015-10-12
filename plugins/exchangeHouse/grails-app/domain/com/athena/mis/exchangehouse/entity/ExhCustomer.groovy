package com.athena.mis.exchangehouse.entity

import com.athena.mis.utility.Tools

class ExhCustomer {

    Integer version
    String name
	String surname
    String code
    Long countryId
    String phone
    Long photoIdTypeId
    Date photoIdExpiryDate      // newly added
    String photoIdNo
    Date dateOfBirth
	long genderId				//SystemEntity.id
    String email
    String address
    Integer addressVerifiedStatus     // newly added (currently 0= unverified,1=verified)
    String postCode
    Long companyId
    long agentId
    Long userId                 // should be removed when trace is implemented
    String sourceOfFund         // newly added
    boolean isSanctionException
    double declarationAmount    // customer target amount for one year
    Date declarationStart
    Date declarationEnd
    Date createdOn
    String companyRegNo
    String dateOfIncorporation
    Date visaExpireDate
    String profession
    boolean smsSubscription         // sms activation or not
    boolean mailSubscription        // mail activation or not
    boolean isBlocked               //if customer is blocked

    static mapping = {
        id generator: 'sequence', params: [sequence: 'exh_customer_id_seq']
        declarationAmount sqlType: "numeric(16,4)"
        dateOfBirth type: 'date'
        photoIdExpiryDate type: 'date'
        declarationStart type: 'date'
        declarationEnd type: 'date'
        visaExpireDate type: 'date'

        // indexing
        code index: 'exh_customer_code_idx'
        photoIdTypeId index: 'exh_customer_photo_id_type_id_idx'
        companyId index: 'exh_customer_company_id_idx'
        countryId index: 'exh_customer_country_id_idx'
        agentId index: 'exh_customer_agent_id_idx'
        userId index: 'exh_customer_user_id_idx'
    }
    static constraints = {
        name(nullable: false)
		surname(nullable: true)
        code(nullable: false)
        countryId(nullable: false)
        phone(nullable: false)
        photoIdTypeId(nullable: false)
        photoIdNo(nullable: true)
        dateOfBirth(nullable: false)
        email(nullable: true)
        address(nullable: false)
        postCode(nullable: false)
        userId(nullable: false)
        addressVerifiedStatus(nullable: false)
        photoIdExpiryDate(nullable: true)
        declarationStart(nullable: true)
        declarationEnd(nullable: true)
        visaExpireDate(nullable: true)
        sourceOfFund(nullable: false)
        isSanctionException(nullable: false)
        declarationAmount(nullable: true)
        companyRegNo(nullable: true)
        dateOfIncorporation(nullable: true)
        profession(nullable: true)
        smsSubscription(nullable: true)
        mailSubscription(nullable: true)
    }

	public String getFullName() {
		return this.name +
				(this.surname ? Tools.SINGLE_SPACE + this.surname : Tools.EMPTY_SPACE)
	}
}
