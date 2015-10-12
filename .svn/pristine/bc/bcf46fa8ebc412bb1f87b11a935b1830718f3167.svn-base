package com.athena.mis.accounting.model

// AccCashFlowDetailsModel is the model for database view vw_acc_cash_flow_details,

class AccCashFlowDetailsModel implements Serializable {

    public static final String SQL_CASH_FLOW_DETAILS_MODEL = """
            DROP TABLE IF EXISTS vw_acc_cash_flow_details;
            DROP VIEW IF EXISTS vw_acc_cash_flow_details;
            CREATE OR REPLACE VIEW vw_acc_cash_flow_details AS
            SELECT coa_id AS id, coa_code,source.key source_type,
                    coa_description, group_id AS acc_group_id,
                    project_id , voucher_date,
                   (amount_dr-amount_cr) balance_activities
            FROM vw_acc_voucher_with_details
            LEFT JOIN system_entity source ON source.id=vw_acc_voucher_with_details.source_type_id;
   """

    long id                               // acc_chart_of_account.id ( is coaId)
    String coaCode                           // acc_chart_of_account.code
    String coaDescription                   // acc_chart_of_account.description
    long accGroupId                          // acc_chart_of_account.acc_group_id
    long projectId                           // vw_acc_voucher_with_details.project_id
    Date voucherDate                         // vw_acc_voucher_with_details.voucher_date
    double balanceActivities                // SUM(vw_acc_voucher_with_details.amount_dr-vw_acc_voucher_with_details.amount_cr) balance_activities
    String sourceType                       // SystemEntity.key ( e.g. supplier, sub-account etc.)

    static mapping = {
        table 'vw_acc_cash_flow_details'  //database view
        version false
//        id composite: ['coaId', 'projectId']
        cache usage: 'read-only'
    }

    static transients = ['accGroupId', 'projectId', 'voucherDate']

}
