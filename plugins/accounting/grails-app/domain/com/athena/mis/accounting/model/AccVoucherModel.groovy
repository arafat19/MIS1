package com.athena.mis.accounting.model
// AccLedgerModel is the model for database view vw_acc_voucher_with_details,
// which is primarily used for ledger report (and other ledger-dependent use-cases)

class AccVoucherModel implements Serializable {

    public static final String SQL_ACC_VOUCHER_MODEL = """
    DROP TABLE IF EXISTS vw_acc_voucher_with_details;
    DROP VIEW IF EXISTS vw_acc_voucher_with_details;
    CREATE OR REPLACE view vw_acc_voucher_with_details AS
    SELECT avd.id AS voucher_details_id,av.id AS voucher_id, av.voucher_type_id,
    av.trace_no, avd.coa_id, avd.project_id, avd.particulars, av.voucher_date,
    to_char(av.voucher_date, 'dd-MON-YYYY') AS str_voucher_date,
    av.cheque_no, avd.amount_dr, to_char(avd.amount_dr, 'FM৳ 99,99,999,99,99,990.00'::text) AS str_amount_dr, avd.amount_cr,
    to_char(avd.amount_cr, 'FM৳ 99,99,999,99,99,990.00'::text) AS str_amount_cr,
    (avd.amount_dr-avd.amount_cr) AS dr_balance, (avd.amount_cr-avd.amount_dr) AS cr_balance,
    coa.code AS coa_code, coa.description AS coa_description, coa.acc_type_id, avd.source_type_id,
    avd.source_category_id, avd.source_id, coa.acc_group_id AS group_id,
    project.name AS project_name, avd.row_id, avd.division_id, av.company_id, av.posted_by
    FROM acc_voucher_details avd
    LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
    LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
    LEFT JOIN project ON project.id = avd.project_id;
    """

    long voucherDetailsId   // accVoucherDetails.id
    long voucherId          // accVoucher.id
    long voucherTypeId     // accVoucher.voucherTypeId
    String traceNo          // accVoucher.traceNo
    long coaId              // accVoucherDetails.coaId
    String coaCode          // accChartOfAccount.code
    String coaDescription   // accChartOfAccount.description
    long projectId          // accVoucherDetails.projectId
    String projectName      // project.name
    String particulars      // accVoucherDetails.particulars
    Date voucherDate        // accVoucher.voucherDate
    String strVoucherDate   // accVoucher.voucherDate
    String chequeNo         // accVoucher.chequeNo
    double amountDr         // accVoucherDetails.amountDr
    String strAmountDr      // formatted amount for amountDr
    double amountCr         // accVoucherDetails.amountCr
    String strAmountCr      // formatted amount for amountCr
    double drBalance        // amountDr - amountCr
    double crBalance        // amountCr - amountDr
    long accTypeId           // accChartOfAccount.accTypeId
    long sourceTypeId       // accVoucherDetails.sourceTypeId
    long sourceCategoryId   // accVoucherDetails.sourceCategoryId
    long sourceId           // accVoucherDetails.sourceId
    long groupId            // accVoucherDetails.groupId
    long rowId              // accVoucherDetails.rowId
    long divisionId         // accVoucherDetails.divisionId
    long companyId         // accVoucher.companyId
    long postedBy          // accVoucher.postedBy

    static constraints = {
    }

    static mapping = {
        table 'vw_acc_voucher_with_details'  //database view
        version false
        id composite: ['voucherDetailsId', 'coaId']
        cache usage: 'read-only'
    }

    static namedQueries = {

        listLedgerByCoaAndProject { long coaId, Date fromDate, Date toDate, List<Long> lstProjectIds, long postedByParam, long companyId ->
            eq('coaId', coaId)
            between('voucherDate', fromDate, toDate)
            'in'('projectId', lstProjectIds)
            eq('companyId', companyId)
            gt('postedBy', postedByParam)
        }

        // Get the previous balance of a Chart of Account
        previousBalance { long coaId, Date fromDate, List<Long> lstProjectIds, long postedByParam, long companyId ->
            eq('coaId', coaId)
            lt('voucherDate', fromDate)
            'in'('projectId', lstProjectIds)
            eq('companyId', companyId)
            gt('postedBy', postedByParam)
            projections {
                sum('drBalance')
            }
        }

        previousBalanceByGroup { long groupId, Date fromDate, List<Long> lstProjectIds, long postedByParam, long companyId ->
            eq('groupId', groupId)
            lt('voucherDate', fromDate)
            'in'('projectId', lstProjectIds)
            eq('companyId', companyId)
            gt('postedBy', postedByParam)
            projections {
                sum('drBalance')
            }
        }

        previousBalanceBySourceTypeAndSource { long sourceTypeId, List<Long> lstSourceId, Date fromDate, List<Long> lstProjectIds, long postedByParam, long companyId ->
            eq('sourceTypeId', sourceTypeId)
            'in'('sourceId', lstSourceId)
            lt('voucherDate', fromDate)
            'in'('projectId', lstProjectIds)
            eq('companyId', companyId)
            gt('postedBy', postedByParam)
            projections {
                sum('drBalance')
            }
        }

        listByVoucherIdWithOrder { long voucherId, long companyId ->
            eq('voucherId', voucherId)
            eq('companyId', companyId)
            order('voucherDate')
            order('traceNo')
        }

        listByVoucherId { long voucherId, long companyId ->
            eq('voucherId', voucherId)
            eq('companyId', companyId)
            order('voucherDetailsId')
        }

        ledgerByGroupWithOrderWithOrder { long groupId, Date fromDate, Date toDate, List<Long> lstProjectIds, long postedByParam, long companyId ->
            eq('groupId', groupId)
            between('voucherDate', fromDate, toDate)
            'in'('projectId', lstProjectIds)
            eq('companyId', companyId)
            gt('postedBy', postedByParam)
            order('voucherDate')
            order('voucherDetailsId')
        }

        ledgerByGroupWithOrder { long groupId, Date fromDate, Date toDate, List<Long> lstProjectIds ->
            eq('groupId', groupId)
            between('voucherDate', fromDate, toDate)
            'in'('projectId', lstProjectIds)
        }

        ledgerByGroup { long groupId, Date fromDate, Date toDate, List<Long> lstProjectIds, long postedByParam, long companyId ->
            eq('groupId', groupId)
            between('voucherDate', fromDate, toDate)
            'in'('projectId', lstProjectIds)
            eq('companyId', companyId)
            gt('postedBy', postedByParam)
        }

        ledgerBySourceTypeAndSourceWithOrder { long sourceTypeId, List<Long> lstSourceId, Date fromDate, Date toDate, List<Long> lstProjectIds, long postedByParam, long companyId ->
            eq('sourceTypeId', sourceTypeId)
            'in'('sourceId', lstSourceId)
            between('voucherDate', fromDate, toDate)
            'in'('projectId', lstProjectIds)
            eq('companyId', companyId)
            gt('postedBy', postedByParam)
            order('voucherDate')
            order('voucherDetailsId')
        }

        ledgerBySourceTypeAndSource { long sourceTypeId, List<Long> lstSourceId, Date fromDate, Date toDate, List<Long> lstProjectIds, long postedByParam, long companyId ->
            eq('sourceTypeId', sourceTypeId)
            'in'('sourceId', lstSourceId)
            between('voucherDate', fromDate, toDate)
            'in'('projectId', lstProjectIds)
            eq('companyId', companyId)
            gt('postedBy', postedByParam)
        }
    }
}
