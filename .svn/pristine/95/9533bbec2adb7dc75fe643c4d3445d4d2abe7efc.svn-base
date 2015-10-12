/**
 * Module Name - AccFinancialYear
 * Purpose - Entity of Financial-Year
 **/

package com.athena.mis.accounting.entity

class AccFinancialYear {

    Date startDate        // Start date of Financial Year
    Date endDate          // End date of Financial Year
    boolean isCurrent     // is Current financial year or Not
    Date createdOn        // Object creation DateTime
    long createdBy        // AppUser.id
    Date updatedOn        // Object updated DateTime
    long updatedBy        // AppUser.id
    long companyId        // Company.id
    int contentCount      // number of content per financial year

    static constraints = {
        startDate(nullable: false)
        endDate(nullable: false)
        isCurrent(nullable: false)
        createdOn(nullable: false)
        createdBy(nullable: false)
        updatedOn(nullable: true)
        updatedBy(nullable: false)
        companyId(nullable: false)
        contentCount(nullable: false)
    }

    static mapping = {
        startDate type: 'date'
        endDate type: 'date'
        id generator: 'sequence', params: [sequence: 'acc_financial_year_id_seq']

        createdBy index: 'acc_financial_year_created_by_idx'
        updatedBy index: 'acc_financial_year_updated_by_idx'
        companyId index: 'acc_financial_year_company_id_idx'
    }
}
