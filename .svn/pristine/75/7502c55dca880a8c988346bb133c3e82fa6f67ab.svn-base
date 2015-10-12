package com.athena.mis.exchangehouse.entity


class ExhCustomerTrace {

    char action  // create, update, delete
    Date actionDate
    Long createdBy   // action performed by
    long customerId  // Customer.id
    String name
    String surname
    //String code   // no need to trace code since it will never be updated
    Long countryId
    String phone
    Long photoIdTypeId
    Date photoIdExpiryDate      // newly added
    String photoIdNo

    long genderId
    Date dateOfBirth
    String email
    String address
    Integer addressVerifiedStatus     // newly added (currently 0= unverified,1=verified)
    boolean isSanctionException
    String postCode
    Long companyId
    long agentId
    String sourceOfFund         // newly added

    double declarationAmount    // customer target amount for one year
    Date declarationStart
    Date declarationEnd

    String companyRegNo
    String dateOfIncorporation

    Date visaExpireDate
    String profession

    boolean smsSubscription         // sms activation or not
    boolean mailSubscription        // mail activation or not

    static mapping = {
        version false
        id generator: 'sequence', params: [sequence: 'exh_customer_trace_id_seq']
        declarationAmount sqlType: "numeric(16,4)"
        dateOfBirth type: 'date'
        photoIdExpiryDate type: 'date'
        declarationStart type: 'date'
        declarationEnd type: 'date'
        visaExpireDate type: 'date'

        // not indexing of countryId, companyId, userId etc , if necessary
    }
    static constraints = {
        name(nullable: false)
        surname(nullable: true)
        countryId(nullable: false)
        phone(nullable: false)
        photoIdTypeId(nullable: false)
		photoIdNo(nullable: true)
        dateOfBirth(nullable: false)
        email(nullable: true)
        address(nullable: false)
        postCode(nullable: false)
        addressVerifiedStatus(nullable: false)
        photoIdExpiryDate(nullable: true)
        declarationStart(nullable: true)
        declarationEnd(nullable: true)
        visaExpireDate(nullable: true)
        sourceOfFund(nullable: false)
        declarationAmount(nullable: true)
        companyRegNo(nullable: true)
        dateOfIncorporation(nullable: true)
        profession(nullable: true)
    }
}
