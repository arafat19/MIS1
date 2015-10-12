package com.athena.mis.accounting.entity

class AccCancelledVoucher {

    // these for preserving voucher information
    long id
    long companyId              // Company.id
    long voucherTypeId          // AccVoucherType.id
    int financialYear           // 2000, 2012
    int financialMonth          // 4 , 11
    boolean isVoucherPosted     // flag to post/Unpost the object
    String note                 // Mandatory
    String traceNo              // AccType Prefix + ddMMyyyy + counter(3 digit) ex: A13062012007 ; counter will reset for each day

    Date voucherDate            // user defined date
    long createdBy              // AppUser.id
    Date createdOn              // Object creation DateTime
    long updatedBy              // AppUser.id
    Date updatedOn              // Object updated DateTime
    long postedBy               // AppUser.id

    double amount               // redundant (sum of Debit/Credit)
    int drCount                 // no of debit entry
    int crCount                 // no of credit entry

    long instrumentTypeId       // SystemEntity.id (e.g. PurchaseOrder, IOU)
    long instrumentId           // PO.id or IOU.id etc.
    String chequeNo             // mandatory for payment or receive voucher to bank
    Date chequeDate             // mandatory if payment or receive voucher to bank

    long projectId              // Project.id

    // these for cancellation
    long cancelledBy                    // AppUser.id
    Date cancelledOn                    // Object cancelled DateTime
    String cancelReason                 // reason of cancellation

    int voucherCount            // Count of voucher within same date & type

    static mapping = {
        id generator: 'assigned'
        voucherDate type: 'date'
        chequeDate type: 'date'
        amount sqlType: "numeric(16,4)"
    }
    static constraints = {
        updatedOn(nullable: true)
        note(nullable: true)
        chequeNo(nullable: true)
        chequeDate(nullable: true)
    }
}
