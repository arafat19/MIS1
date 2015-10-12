package com.athena.mis.application.entity

class BankBranch {
    Integer version
    String code
    String name
    String address
    Long bankId
    Long districtId
    boolean isSmeServiceCenter
    boolean isPrincipleBranch
    long companyId      // Company.id
    boolean isGlobal    //determine if ANY BRANCH(for ARMS plugin)

    long createdBy          // AppUser.id
    Date createdOn          // Object creation DateTime
    long updatedBy = 0L     // AppUser.id
    Date updatedOn          // Object Updated DateTime

    static mapping = {
        id generator: 'sequence', params: [sequence: 'bank_branch_id_seq']

        // indexing
        bankId index: 'bank_branch_bank_id_idx'
        districtId index: 'bank_branch_district_id_idx'
        createdBy index: 'bank_branch_created_by_idx'
        updatedBy index: 'bank_branch_updated_by_idx'
        companyId index: 'bank_branch_company_id_idx'
    }

    static constraints = {
        name(blank: false, nullable: false)
        code(blank: false, nullable: true,unique: true)
        bankId(nullable: false)
        districtId(nullable: false)
        address(nullable: true)
        companyId(nullable: false)
        isGlobal(nullable: true)

        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
    }

    public String toString() {
        return name
    }
}
