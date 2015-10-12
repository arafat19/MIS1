package com.athena.mis.accounting.model
// AccVoucherTypeModel is the model for database view vw_acc_voucher_with_type,


class AccVoucherTypeModel implements Serializable {

    public static final String SQL_ACC_VOUCHER_TYPE_MODEL = """
    DROP TABLE IF EXISTS vw_acc_voucher_with_type;
    DROP VIEW IF EXISTS vw_acc_voucher_with_type;
    CREATE OR REPLACE view vw_acc_voucher_with_type AS
    SELECT av.id AS voucher_id, av.trace_no,av.voucher_date,to_char(av.voucher_date, 'dd-MON-YYYY') AS str_voucher_date,
    av.created_on, av.voucher_type_id, avt.key AS voucher_type_name,
    av.instrument_type_id,av.instrument_id,av.cheque_no,
    av.amount, (to_char(av.amount,'FM৳ 99,99,999,99,99,990.00')) AS str_amount,
    av.is_voucher_posted, av.dr_count,av.cr_count, av.company_id
    FROM acc_voucher av
    LEFT JOIN system_entity avt ON avt.id = av.voucher_type_id
    """

    long voucherId              // accVoucher.id
    String traceNo              // accVoucher.traceNo
    Date voucherDate            // accVoucher.voucherDate
    String strVoucherDate       // formatted date for accVoucher.voucherDate (dd-MMM-yyyy)
    Date createdOn              // accVoucher.createdOn
    long voucherTypeId          // accVoucher.voucherTypeId
    String voucherTypeName      // systemEntity.key
    long instrumentTypeId       // accVoucher.instrumentTypeId
    long instrumentId           // accVoucher.instrumentId
    String chequeNo             // accVoucher.chequeNo

    double amount               // accVoucher.amount
    String strAmount            // formatted amount for accVoucher.amount
    boolean isVoucherPosted     // accVoucher.isVoucherPosted
    int drCount                 // accVoucher.drCount
    int crCount                 // accVoucher.crCount
    long companyId             // accVoucher.companyId

    static mapping = {
        table 'vw_acc_voucher_with_type'  //database view
        version false
        id composite: ['voucherId', 'voucherTypeId']
        cache usage: 'read-only'
    }

    static namedQueries = {

        listByVoucherTypeAndDateWithOrder { long voucherTypeId, Date fromDate, Date toDate, long companyId ->
            eq('voucherTypeId', voucherTypeId)
            between('createdOn', fromDate, toDate)
            eq('companyId', companyId)
            order('voucherDate')
            order('traceNo')
            setReadOnly(true)
        }

        listByVoucherTypeAndDate { long voucherTypeId, Date fromDate, Date toDate, long companyId ->
            eq('voucherTypeId', voucherTypeId)
            between('createdOn', fromDate, toDate)
            eq('companyId', companyId)
            setReadOnly(true)
        }

        listByVoucherTypeAndDateAndPostedWithOrder { boolean isPosted, long voucherTypeId, Date fromDate, Date toDate, long companyId ->
            eq('voucherTypeId', voucherTypeId)
            eq('isVoucherPosted', isPosted)
            between('createdOn', fromDate, toDate)
            eq('companyId', companyId)
            order('voucherDate')
            order('traceNo')
            setReadOnly(true)
        }

        listByVoucherTypeAndDateAndPosted { boolean isPosted, long voucherTypeId, Date fromDate, Date toDate, long companyId ->
            eq('voucherTypeId', voucherTypeId)
            eq('isVoucherPosted', isPosted)
            between('createdOn', fromDate, toDate)
            eq('companyId', companyId)
            setReadOnly(true)
        }
    }
}
