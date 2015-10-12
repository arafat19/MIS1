package com.athena.mis.arms.service

import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 * RmsExchangeHouseService is used to handle only CRUD related object manipulation (e.g. read, create, update, delete)
 */
class RmsExchangeHouseService extends BaseService {

    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility

    /**
     * @return - list of ExchangeHouse
     */
    public List list() {
        return RmsExchangeHouse.list(sort: rmsExchangeHouseCacheUtility.SORT_ON_NAME, order: rmsExchangeHouseCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * Get ExchangeHouse object by id
     * @param id
     * @return -exchangeHouse object
     */
    public RmsExchangeHouse read(long id) {
        RmsExchangeHouse exchangeHouse = RmsExchangeHouse.read(id)
        return exchangeHouse
    }

    /**
     * Save ExchangeHouse object into DB
     * @param ExchangeHouse -ExchangeHouse object
     * @return -saved ExchangeHouse object
     */
    public RmsExchangeHouse create(RmsExchangeHouse exchangeHouse) {
        RmsExchangeHouse savedExchangeHouse = exchangeHouse.save()
        return savedExchangeHouse
    }

    private static final String UPDATE_QUERY = """
        UPDATE rms_exchange_house SET
            version=:newVersion,
            name=:name,
            code=:code,
            country_id=:countryId,
            company_id=:companyId
        WHERE
            id=:id AND
            version=:version
    """
    /**
     * Update ExchangeHouse object into DB
     * @param ExchangeHouse -ExchangeHouse object
     * @return -an integer containing the value of update count
     */
    public int update(RmsExchangeHouse exchangeHouse) {
        Map queryParams = [
            id: exchangeHouse.id,
            newVersion: exchangeHouse.version + 1,
            version: exchangeHouse.version,
            name: exchangeHouse.name,
            code: exchangeHouse.code,
            countryId: exchangeHouse.countryId,
            companyId: exchangeHouse.companyId
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update ExchangeHouse information')
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
        DELETE FROM rms_exchange_house
        WHERE
            id=:id
    """
    /**
     * Delete ExchangeHouse object from DB
     * @param id -id of ExchangeHouse object
     * @return -an integer containing the value of delete count
     */
    public int delete(long id) {
        Map queryParams = [
                id: id
        ]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete ExchangeHouse information')
        }
        return deleteCount;
    }

    private static final String QUERY_UPDATE_BALANCE = """
        UPDATE rms_exchange_house SET
        balance = :balance
        WHERE id = :exhHouseId
    """
    public int updateBalance(long exhHouseId, double amount){
        Map queryParams=[
                balance :amount,
                exhHouseId :exhHouseId
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE_BALANCE, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update ExchangeHouse information')
        }
        return updateCount
    }


    public void createDefaultDataForRmsExchangeHouse(long companyId) {
        new RmsExchangeHouse(version: 0, name: 'SouthEast Financial Services(UK)', code: 'SFSL', countryId: 1, companyId: companyId, balance: 10000).save()
        new RmsExchangeHouse(version: 0, name: 'Southeast Financial Services Australia PTY Ltd.', code: 'SFSA', countryId: 1, companyId: companyId, balance: 15000).save()
        new RmsExchangeHouse(version: 0, name: 'SouthEast Exchange Company Pty Ltd', code: 'SECL', countryId: 1, companyId: companyId, balance: 10000).save()
    }
}
