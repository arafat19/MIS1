package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccVoucher
import com.athena.mis.utility.DateUtility

class AccVoucherService extends BaseService {

    static transactional = false

    public AccVoucher create(AccVoucher accVoucher) {
        AccVoucher newAccVoucher = accVoucher.save(false)
        return newAccVoucher
    }

    private static final String UPDATE_QUERY = """
            UPDATE acc_voucher SET
            version=:version,
            project_id=:projectId,
            note=:note,
            updated_by=:updatedBy,
            updated_on=:updatedOn,
            voucher_date=:voucherDate,
            voucher_type_id=:voucherTypeId,
            financial_month =:financialMonth,
            financial_year=:financialYear,
            amount=:amount,
            dr_count=:drCount,
            cr_count=:crCount,
            instrument_type_id=:instrumentTypeId,
            instrument_id=:instrumentId,
            cheque_no=:chequeNo,
            cheque_date =:chequeDate
            WHERE id=:id AND
                  version=:oldVersion
            """

    public AccVoucher update(AccVoucher accVoucher) {

        Map queryParams = [
                version: accVoucher.version + 1,
                projectId: accVoucher.projectId,
                note: accVoucher.note,
                updatedBy: accVoucher.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(new Date()),
                voucherDate: DateUtility.getSqlDate(accVoucher.voucherDate),
                voucherTypeId: accVoucher.voucherTypeId,
                financialMonth: accVoucher.financialMonth,
                financialYear: accVoucher.financialYear,
                amount: accVoucher.amount,
                drCount: accVoucher.drCount,
                crCount: accVoucher.crCount,
                id: accVoucher.id,
                oldVersion: accVoucher.version,
                instrumentTypeId: accVoucher.instrumentTypeId,
                instrumentId: accVoucher.instrumentId,
                chequeNo: accVoucher.chequeNo,
                chequeDate: DateUtility.getSqlDate(accVoucher.chequeDate)
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update voucher')
        }
        // voucher version increased in actionService
        accVoucher.version = accVoucher.version + 1
        return accVoucher
    }

    public AccVoucher readByTraceNo(String traceNo) {
        AccVoucher accVoucher = AccVoucher.findByTraceNo(traceNo, [readOnly: true])
        return accVoucher
    }

    public AccVoucher read(long voucherId) {
        AccVoucher accVoucher = AccVoucher.read(voucherId)
        return accVoucher
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM acc_voucher
                      WHERE
                          id=:id
                          """

    public boolean delete(long voucherId) {
        int deleteCount = executeUpdateSql(QUERY_DELETE, [id: voucherId])
        if (deleteCount <= 0) {
            throw new RuntimeException('Failed to delete voucher')
        }
        return true
    }

    public boolean delete(AccVoucher accVoucher) {
        accVoucher.delete()
        return true
    }
}
