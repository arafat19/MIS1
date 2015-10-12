package com.athena.mis.arms.service

import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsExchangeHouseCurrencyPosting
import com.athena.mis.utility.DateUtility

/**
 * RmsExchangeHouseCurrencyPostingService is used to handle only CRUD related object manipulation (e.g. read, create, update, delete)
 */
class RmsExchangeHouseCurrencyPostingService extends BaseService {

    /**
     * get count of total ExchangeHouseCurrencyPosting
     * @return int -count of total ExchangeHouseCurrencyPosting
     */
    public int countByCompanyIdAndAmountGreaterThanAndTaskId(long companyId) {
        int count = RmsExchangeHouseCurrencyPosting.countByCompanyIdAndAmountGreaterThanAndTaskId(companyId, 0, 0L)
        return count
    }

    /**
     * Get ExchangeHouseCurrencyPosting object by Id
     * @param id -Id of ExchangeHouseCurrencyPosting
     * @return -object of ExchangeHouseCurrencyPosting
     */
    public RmsExchangeHouseCurrencyPosting read(long id) {
        RmsExchangeHouseCurrencyPosting exchangeHouseCurrencyPosting = RmsExchangeHouseCurrencyPosting.read(id)
        return exchangeHouseCurrencyPosting
    }

    private static final String QUERY_AMOUNT = """
        SELECT COALESCE(SUM(amount),0) amount
        FROM rms_exchange_house_currency_posting
        WHERE exchange_house_id = :exchangeHouseId
    """
    public double getBalanceAmount(long exchangeHouseId) {
        Map queryParams = [
                exchangeHouseId: exchangeHouseId
        ]
        List amount = executeSelectSql(QUERY_AMOUNT, queryParams)
        return amount[0].amount
    }

    /**
     * Save exchangeHouseCurrencyPosting object into DB
     * @param exchangeHouseCurrencyPosting -exchangeHouseCurrencyPosting object
     * @return -saved exchangeHouseCurrencyPosting object
     */
    public RmsExchangeHouseCurrencyPosting save(RmsExchangeHouseCurrencyPosting exhHouseCurPosting) {
        RmsExchangeHouseCurrencyPosting savedExhHouseCurPosting = exhHouseCurPosting.save(flush: true)
        return savedExhHouseCurPosting
    }

    /**
     * Save ExchangeHouseCurrencyPosting object into DB
     * @param ExchangeHouseCurrencyPosting -ExchangeHouseCurrencyPosting object
     * @return -saved ExchangeHouseCurrencyPosting object
     */
    public RmsExchangeHouseCurrencyPosting create(RmsExchangeHouseCurrencyPosting exhHouseCurPosting) {
        RmsExchangeHouseCurrencyPosting savedExhHouseCurPosting = exhHouseCurPosting.save(flush: true)
        return savedExhHouseCurPosting
    }

    private static final String UPDATE_QUERY = """
        UPDATE rms_exchange_house_currency_posting SET
            version=:newVersion,
            exchange_house_id=:exchangeHouseId,
            task_id=:taskId,
            amount=:amount,
            updated_on=:updatedOn,
            updated_by=:updatedBy
        WHERE
            id=:id AND
            version=:version
    """
    /**
     * Update ExchangeHouseCurrencyPosting object into DB
     * @param ExchangeHouseCurrencyPosting -ExchangeHouseCurrencyPosting object
     * @return -an integer containing the value of update count
     */
    public int update(RmsExchangeHouseCurrencyPosting exhHouseCurPosting) {
        Map queryParams = [
            id: exhHouseCurPosting.id,
            newVersion: exhHouseCurPosting.version + 1,
            version: exhHouseCurPosting.version,
            exchangeHouseId: exhHouseCurPosting.exchangeHouseId,
            taskId: exhHouseCurPosting.taskId,
            amount: exhHouseCurPosting.amount,
            updatedOn: DateUtility.getSqlDateWithSeconds(exhHouseCurPosting.updatedOn),
            updatedBy: exhHouseCurPosting.updatedBy
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update ExchangeHouseCurrencyPosting information')
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
        DELETE FROM rms_exchange_house_currency_posting
        WHERE
            id=:id
    """
    /**
     * Delete ExchangeHouseCurrencyPosting object from DB
     * @param id -id of ExchangeHouseCurrencyPosting object
     * @return -an integer containing the value of delete count
     */
    public int delete(long id) {
        Map queryParams = [
            id: id
        ]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete ExchangeHouseCurrencyPosting information')
        }
        return deleteCount
    }

    public void createDefaultDataForRmsExchangeHouseCurrencyPosting(long companyId) {
        new RmsExchangeHouseCurrencyPosting(version: 0, exchangeHouseId: 1, taskId: 0, amount: 10000, companyId: companyId, createdBy: 2, createdOn: new Date(), updatedBy: 0).save()
        new RmsExchangeHouseCurrencyPosting(version: 0, exchangeHouseId: 2, taskId: 0, amount: 15000, companyId: companyId, createdBy: 2, createdOn: new Date(), updatedBy: 0).save()
        new RmsExchangeHouseCurrencyPosting(version: 0, exchangeHouseId: 3, taskId: 0, amount: 10000, companyId: companyId, createdBy: 2, createdOn: new Date(), updatedBy: 0).save()
    }

    public int countByExchangeHouseId(long exhHouseId){
        int count= RmsExchangeHouseCurrencyPosting.countByExchangeHouseId(exhHouseId)
        return count
    }
}
