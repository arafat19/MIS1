package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccCancelledVoucher

class AccCancelledVoucherService extends BaseService {

    static transactional = true

    public AccCancelledVoucher get(long voucherId) {
        AccCancelledVoucher accCancelledVoucher = AccCancelledVoucher.get(voucherId)
        return accCancelledVoucher
    }

    public AccCancelledVoucher create(AccCancelledVoucher cancelledVoucher) {
        AccCancelledVoucher accCancelledVoucher = cancelledVoucher.save(false)
        return accCancelledVoucher
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM acc_voucher
                      WHERE
                          id=:id
                          """

    public int delete(long voucherId) {
        int deleteCount = executeUpdateSql(QUERY_DELETE, [id: voucherId])
        if (deleteCount <= 0) {
            throw new RuntimeException('Failed to delete voucher')
        }
        return deleteCount
    }

}
