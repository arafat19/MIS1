package com.athena.mis.accounting.entity

class AccCancelledVoucherDetails {

    long id
    long projectId          // Project.id    (optional)
    long voucherId          // AccVoucher.id(Parent Id)
    int rowId               // determine hierarchy of VoucherDetails for same voucher (First Debit then Credit)
    long coaId              // AccChartOfAccount.id
    long groupId            // AccGroup.id
    long sourceId           // AccSource.id
    long sourceTypeId       // SystemEntity.id eg: None,employee,supplier etc.
    long sourceCategoryId   // e.g. Item.itemTypeId, Employee.designationId etc
    long divisionId         // AccDivision.id
    double amountDr         // Amount debited
    double amountCr         // Amount credited
    String particulars      // Optional

    long createdBy          // AppUser.id
    Date createdOn

    static constraints = {
        particulars(nullable: true)
    }
}
