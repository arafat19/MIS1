package com.athena.mis.exchangehouse.entity

import com.athena.mis.utility.Tools

class ExhBeneficiary {

    String firstName
    String middleName
    String lastName
    String accountNo
    String bank
    String bankBranch
    String district          // outlet district
    String thana             // outlet thana
    String photoIdType
    String photoIdNo
    String phone
    String email
    String address
    String relation
    Long companyId
    long countryId          // country.id
    long createdBy          // appUser.id
    Date createdOn          // Object created DateTime
    long approvedBy
    Date approvedOn          // Object updated DateTime
    long updatedBy          // AppUser.id
    Date updatedOn          // Object updated DateTime

    boolean isSanctionException

    static mapping = {
        id generator: 'sequence', params: [sequence: 'exh_beneficiary_id_seq']

        // indexing
        companyId index: 'exh_beneficiary_company_id_idx'
        countryId index: 'exh_beneficiary_country_id_idx'
        createdBy index: 'exh_beneficiary_created_by_idx'
        updatedBy index: 'exh_beneficiary_updated_by_idx'
    }

    static constraints = {
        firstName(nullable: false)
        middleName(nullable: true)
        lastName(nullable: true)
        accountNo(nullable: true)
        countryId(nullable: false)
        bank(nullable: true)
        bankBranch(nullable: true)
        district(nullable: true)
        thana(nullable: true)
        photoIdType(nullable: true)
        photoIdNo(nullable: true)
        phone(nullable: true)
        email(nullable: true)
        address(nullable: true)
        relation(nullable: true)
        createdBy(nullable: false)
        createdOn(nullable: false)
        approvedBy(nullable: false)
        approvedOn(nullable: true)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
    }

    public boolean hasBeneficiary(long customerId) {
        return (ExhCustomerBeneficiaryMapping.countByBeneficiaryIdAndCustomerId(this.id, customerId) > 0)
    }

    public String getFullName() {
        return this.firstName +
                (this.middleName ? Tools.SINGLE_SPACE + this.middleName : Tools.EMPTY_SPACE) +
                (this.lastName ? Tools.SINGLE_SPACE + this.lastName : Tools.EMPTY_SPACE)
    }
}
