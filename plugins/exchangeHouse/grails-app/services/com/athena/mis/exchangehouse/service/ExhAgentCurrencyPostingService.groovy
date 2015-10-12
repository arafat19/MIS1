package com.athena.mis.exchangehouse.service

import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhAgentCurrencyPosting
import com.athena.mis.utility.DateUtility
import org.springframework.transaction.annotation.Transactional

class ExhAgentCurrencyPostingService extends BaseService {
    private static final String SORT_ORDER_DESCENDING = 'desc'

    static transactional = false

    @Transactional(readOnly = true)
    public List list() {
        return ExhAgentCurrencyPosting.list(readOnly: Boolean.TRUE, sort: ID, order: SORT_ORDER_DESCENDING);
    }

    public ExhAgentCurrencyPosting read(long id) {
        return ExhAgentCurrencyPosting.read(id)
    }

    /**
     * Save agentCurrencyPosting into DB
     * @param agentCurrencyPosting
     * @return savedCurrencyPosting -ExhAgentCurrencyPosting object
     */
    public ExhAgentCurrencyPosting create(ExhAgentCurrencyPosting agentCurrencyPosting) {
        ExhAgentCurrencyPosting savedCurrencyPosting = agentCurrencyPosting.save(flush: true)
        if (savedCurrencyPosting) {
            return savedCurrencyPosting
        }
        return null
    }

    public int delete(long id) {
        String query = """
                    DELETE FROM exh_agent_currency_posting
                      WHERE
                          id=:id
                          """
        Map queryParams = [
                id: id
        ]
        int deleteCount = executeUpdateSql(query, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("error occurred while deleting agent currency posting information")
        }
        return deleteCount;
    }

    public ExhAgentCurrencyPosting update(ExhAgentCurrencyPosting exhAgentCurrencyPosting) {

        String query = """
                    UPDATE exh_agent_currency_posting
                    SET
                          version=:newVersion,
                          amount=:amount,
                          updated_on='${DateUtility.getDBDateFormatWithSecond(exhAgentCurrencyPosting.updatedOn)}',
                          updated_by=:updatedBy

                    WHERE
                          id=:id AND
                          version=:version
                          """
        Map queryParams = [
                id: exhAgentCurrencyPosting.id,
                version: exhAgentCurrencyPosting.version,
                newVersion: exhAgentCurrencyPosting.version + 1,
                amount: exhAgentCurrencyPosting.amount,
                updatedBy: exhAgentCurrencyPosting.updatedBy
        ]

        int updateCount = executeUpdateSql(query, queryParams)
        if (updateCount < 0) {
            throw new RuntimeException('Failed to update agent currency posting')
        }
        exhAgentCurrencyPosting.version = exhAgentCurrencyPosting.version+1
        return  exhAgentCurrencyPosting
    }


}

