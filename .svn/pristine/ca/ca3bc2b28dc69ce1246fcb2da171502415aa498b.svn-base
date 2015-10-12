package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.model.*

class AccountingModelService extends BaseService {

    public void createDefaultSchema() {
        executeSql(AccVoucherModel.SQL_ACC_VOUCHER_MODEL)
        executeSql(AccVoucherTypeModel.SQL_ACC_VOUCHER_TYPE_MODEL)
        executeSql(AccCashFlowDetailsModel.SQL_CASH_FLOW_DETAILS_MODEL)
        executeSql(AccIouDetailsModel.SQL_ACC_IOU_DETAILS_MODEL)
        executeSql(AccSourceBalanceModel.SQL_ACC_SOURCE_BALANCE_MODEL)
        executeSql(AccSupplierPaymentModel.SQL_ACC_SUPPLIER_PAYMENT_MODEL)
    }
}
