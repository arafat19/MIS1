package com.athena.mis.accounting.model
// AccSupplierPaymentModel is the model for database view vw_acc_supplier_payment,
// which is primarily used for supplier wise payment report

class AccSupplierPaymentModel implements Serializable {

    public static final String SQL_ACC_SUPPLIER_PAYMENT_MODEL = """
    DROP TABLE IF EXISTS vw_acc_supplier_payment;
    DROP VIEW IF EXISTS vw_acc_supplier_payment;
    CREATE OR REPLACE view vw_acc_supplier_payment AS
    SELECT v.instrument_id AS id, v.posted_by,
    v.voucher_date, vd.amount_dr total_paid, COALESCE(po.total_price,0) total_po,
    (COALESCE(po.total_price,0)-vd.amount_dr) remaining, COALESCE(po.project_id,0) po_project_id,
    vd.source_id, v.company_id, vd.project_id, v.trace_no
    FROM acc_voucher_details vd
    LEFT JOIN acc_voucher v ON v.id = vd.voucher_id
    LEFT JOIN proc_purchase_order po ON po.id=v.instrument_id
    WHERE vd.source_type_id IN (SELECT id from system_entity WHERE reserved_id IN (25))
    AND v.instrument_type_id IN (0, (SELECT id from system_entity WHERE reserved_id IN (235) AND company_id = v.company_id))
    ORDER BY v.instrument_id;
    """

    long id                     // accVoucherDetails.instrumentId(it is poId)
    long postedBy              // accVoucherDetails.postedBy
    Date voucherDate            // accVoucher.voucherDate
    double totalPaid            // accVoucherDetails.amountDr (But it will be total amount dr of voucherDetails of a po
    double totalPo              // purchaseOrder.totalPrice of a poId
    double remaining            // totalPO- totalPaid
    long sourceId               // accVoucherDetails.sourceId is supplierId
    long companyId               // accVoucherDetails.companyId
    long projectId               // accVoucherDetails.projectId
    long poProjectId             // po.projectId
    String traceNo               // accVoucher.traceNO

    static mapping = {
        table 'vw_acc_supplier_payment'  //database view
        version false
        cache usage: 'read-only'
    }

    static transients = ['voucherDate','postedBy','companyId','traceNo','projectId']
}
