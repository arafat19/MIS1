package com.athena.mis.exchangehouse.service

import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhRegularFee
import org.springframework.transaction.annotation.Transactional

class ExhRegularFeeService extends BaseService {
    static transactional = false

    public List list() {
        return ExhRegularFee.list(readOnly: true)
    }

    @Transactional(readOnly = true)
    public ExhRegularFee read(long id) {
        ExhRegularFee regularFee = ExhRegularFee.read(id)
        return regularFee
    }

    public ExhRegularFee update(ExhRegularFee exhRegularFee){
        String query = """
                    UPDATE exh_regular_fee
                    SET
                          version=:newVersion,
                          logic=:logic
                    WHERE
                          id=:id AND
                          version=:version
                          """
        Map queryParams = [
                id: exhRegularFee.id,
                version: exhRegularFee.version,
                newVersion: exhRegularFee.version + 1,
                logic: exhRegularFee.logic
        ]

        int updateCount = executeUpdateSql(query, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating regular fee information')
        }
        exhRegularFee.version = exhRegularFee.version + 1
        return exhRegularFee;
    }

    @Transactional
    public ExhRegularFee createDefaultData(long companyId) {
        String logic = """
        return amount * 0.10;
        """
        ExhRegularFee exhRegularFee = new ExhRegularFee(logic: logic, companyId: companyId, createdBy: companyId, createdOn: new Date(), updatedBy:0L, updatedOn: new Date())
        ExhRegularFee newExhRegularFee = exhRegularFee.save(flush: true)
        return newExhRegularFee
    }
}
