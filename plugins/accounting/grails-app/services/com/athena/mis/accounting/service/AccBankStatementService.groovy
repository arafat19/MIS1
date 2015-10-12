package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccBankStatement

class AccBankStatementService extends BaseService {

    static transactional = false

    public AccBankStatement create(AccBankStatement accBankStatement) {
        AccBankStatement newAccBankStatement = accBankStatement.save(validate: false,flush: true)
        return newAccBankStatement
    }
}

