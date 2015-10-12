package com.athena.mis.exchangehouse.service

import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhAgent
import com.athena.mis.utility.DateUtility

class ExhAgentService extends BaseService {

    private static final String SORT_ON_NAME = "name"
    private static final String SORT_ORDER_ASCENDING = 'asc'

    static transactional = false

    public List list() {
        return ExhAgent.list(readOnly: true, sort: SORT_ON_NAME, order: SORT_ORDER_ASCENDING);
    }

    public ExhAgent create(ExhAgent exhAgent) {
        ExhAgent newExhAgent = exhAgent.save(false)
        return newExhAgent

    }

    public ExhAgent read(long id) {
        return ExhAgent.read(id)
    }

    public ExhAgent get(long id) {
        return ExhAgent.get(id)
    }

    public int update(ExhAgent exhAgent) {
        String query = """
                    UPDATE exh_agent
                    SET
                          version=:newVersion,
                          name=:name,
                          address=:address,
                          city=:city,
                          phone=:phone,
                          credit_limit=:creditLimit,
                          commission_logic=:commissionLogic,
                          updated_on='${DateUtility.getDBDateFormatWithSecond(exhAgent.updatedOn)}',
                          updated_by=:updatedBy

                    WHERE
                          id=:id AND
                          version=:version
                          """
        Map queryParams = [
                id: exhAgent.id,
                version: exhAgent.version,
                newVersion: exhAgent.version + 1,
                name: exhAgent.name,
                address: exhAgent.address,
                city: exhAgent.city,
                phone: exhAgent.phone,
                updatedBy: exhAgent.updatedBy,
                creditLimit: exhAgent.creditLimit,
                commissionLogic: exhAgent.commissionLogic
        ]

        int updateCount = executeUpdateSql(query, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating agent information')
        }
        exhAgent.version = exhAgent.version + 1
        return updateCount;
    }

    public int delete(long id) {
        String query = """
                    DELETE FROM exh_agent
                      WHERE
                          id=:id
                          """
        Map queryParams = [
                id: id
        ]
        int deleteCount = executeUpdateSql(query, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("error occurred while deleting agent information")
        }
        return deleteCount;
    }
}
