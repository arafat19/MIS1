package com.athena.mis.accounting.model

// AccSourceBalanceModel is the model for database view vw_acc_source_balance,
// which is primarily used for source wise balance report

class AccSourceBalanceModel implements Serializable {

    public static final String SQL_ACC_SOURCE_BALANCE_MODEL = """
    DROP TABLE IF EXISTS vw_acc_source_balance;
    DROP VIEW IF EXISTS vw_acc_source_balance;
    CREATE OR REPLACE view vw_acc_source_balance AS
    SELECT customer.full_name AS customer_full_name, employee.full_name AS employee_full_name,
    acc_sub_account.description AS sub_account_description, supplier.name AS supplier_name,
    item.name AS item_name, fad.name AS fad_name,
    avd.source_type_id,
    CASE
    WHEN SUM(avd.amount_dr-avd.amount_cr) > 0
    THEN ABS(SUM(avd.amount_dr-avd.amount_cr))
    ELSE  0.00
    END AS dr_balance,
    CASE
    WHEN SUM(avd.amount_dr-avd.amount_cr) > 0
    THEN  (to_char(ABS(SUM(avd.amount_dr-avd.amount_cr)),'FM৳ 99,99,999,99,99,990.00'))
    ELSE  ' 0.00'
    END AS str_dr_balance,
    CASE
    WHEN SUM(avd.amount_dr-avd.amount_cr) <0
    THEN ABS(SUM(avd.amount_dr-avd.amount_cr))
    ELSE 0.00
    END AS cr_balance,
    CASE
    WHEN SUM(avd.amount_dr-avd.amount_cr) <0
    THEN  (to_char(ABS(SUM(avd.amount_dr-avd.amount_cr)),'FM৳ 99,99,999,99,99,990.00'))
    ELSE  ' 0.00'
    END AS str_cr_balance
    FROM acc_voucher_details avd
    LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
    LEFT JOIN supplier ON avd.source_id = supplier.id
    LEFT JOIN customer ON avd.source_id = customer.id
    LEFT JOIN employee ON avd.source_id = employee.id
    LEFT JOIN acc_sub_account ON avd.source_id = acc_sub_account.id
    LEFT JOIN item ON item.id = avd.source_id
    LEFT JOIN fxd_fixed_asset_details fad ON fad.id = avd.source_id
    GROUP BY acc_sub_account.description, customer.full_name, employee.full_name, supplier.name, avd.source_type_id, item.name, fad.name
    ORDER BY customer.full_name,employee.full_name,acc_sub_account.description,supplier.name
    """

    String customerFullName             // customer.fullName
    String employeeFullName             // employee.fullName
    String subAccountDescription        // accSubAccount.description
    String supplierName                 // supplier.name
    long sourceTypeId                   // accVoucherDetails.sourceTypeId

    double drBalance                    // debit balance of accVoucherDetails
    String strDrBalance                 // formatted amount of drBalance
    double crBalance                    // credit balance of accVoucherDetails
    String strCrBalance                 // formatted amount of crBalance
//    Date voucherDate                    // accVoucher.voucherDate



    static mapping = {
        table 'vw_acc_source_balance'  //database view
        version false
        id composite: ['sourceTypeId', 'drBalance']
        cache usage: 'read-only'
    }

/*    static namedQueries = {

        sourceWiseBalance { long sourceTypeId , Date toVoucherDate ->
            eq('sourceTypeId',sourceTypeId)
            le('voucherDate',toVoucherDate)    // av.voucher_date
        }
    }*/





}
