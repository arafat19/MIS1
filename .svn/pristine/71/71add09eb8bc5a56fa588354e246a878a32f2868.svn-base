package com.athena.mis.procurement.service

import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcTransportCost
import groovy.sql.GroovyRowResult

class TransportCostService extends BaseService {

    static transactional = false
    PurchaseOrderService purchaseOrderService

    /**
     * Method to get the list of ProcTransportCost and it's count
     * @param baseService - object of BaseService
     * @return - a map containing transportCostList and it's count
     */
    public Map list(BaseService baseService) {
        List<ProcTransportCost> transportCostList = ProcTransportCost.list(order: baseService.sortOrder, sort: baseService.sortColumn, offset: baseService.start, max: baseService.resultPerPage, readOnly: true);
        int count = ProcTransportCost.count();
        return [transportCostList: transportCostList, count: count];
    }

    /**
     * Method to search transport cost list and it's total
     * @param baseService - object of BaseService
     * @return - a map containing transportCostList and it's total
     */
    public Map search(BaseService baseService) {
        long poId = Long.parseLong(baseService.query)
        List<ProcTransportCost> transportCostList = ProcTransportCost.withCriteria {
            eq(baseService.queryType, poId)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
        } as List;
        List counts = ProcTransportCost.withCriteria {
            eq(baseService.queryType, poId)
            projections { rowCount() }
        } as List
        int total = counts[0] as int
        return [transportCostList: transportCostList, count: total]

    }

    private static final String INSERT_QUERY = """
                    INSERT INTO proc_transport_cost(id, version, amount, comments, purchase_order_id, quantity, rate)
                        VALUES (NEXTVAL('proc_transport_cost_id_seq'), :version, :amount, :comments,
                              :purchaseOrderId, :quantity, :rate );
             """

    /**
     * Method to save ProcTransportCost object
     * @param transportCost - object of ProcTransportCost
     * @return - newly created ProcTransportCost object
     */
    public ProcTransportCost create(ProcTransportCost transportCost) {
        Map queryParams = [
                version: transportCost.version,
                amount: transportCost.amount,
                comments: transportCost.comments,
                purchaseOrderId: transportCost.purchaseOrderId,
                quantity: transportCost.quantity,
                rate: transportCost.rate
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException("Fail to create Transport Cost")
        }
        int transportCostId = (int) result[0][0]
        transportCost.id = transportCostId
        return transportCost
    }

    /**
     * Method to read ProcTransportCost object by id
     * @param id - ProcTransportCost.id
     * @return - an object of ProcTransportCost
     */
    public ProcTransportCost read(long id) {
        return (ProcTransportCost) ProcTransportCost.read(id);
    }

    private static final String UPDATE_QUERY = """
                    UPDATE proc_transport_cost SET
                          version=:newVersion,
                          amount=:amount,
                          quantity=:quantity,
                          rate=:rate ,
                          comments=:comments
                      WHERE
                          id=:id AND
                          version=:version
                          """

    /**
     * Method to update ProcTransportCost object
     * @param transportCost - object of ProcTransportCost
     * @return - updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(ProcTransportCost transportCost) {
        Map queryParams = [
                id: transportCost.id,
                version: transportCost.version,
                newVersion: transportCost.version + 1,
                amount: transportCost.amount,
                quantity: transportCost.quantity,
                rate: transportCost.rate,
                comments: transportCost.comments
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update Transport Cost")
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
                                DELETE FROM proc_transport_cost
                                   WHERE
                                      id=:id
                             """

    /**
     * Method to delete ProcTransportCost object
     * @param transportCost - object of ProcTransportCost
     * @return - if deleteCount <= 0 then throw exception to rollback transaction
     */
    public int delete(ProcTransportCost transportCost) {
        int deleteCount = executeUpdateSql(DELETE_QUERY, [id: transportCost.id])
        if (deleteCount <= 0) {
            throw new RuntimeException("Fail to delete Transport cost")
        }
        return deleteCount
    }

    private static final String SELECT_QUERY = """
                    SELECT * FROM proc_transport_cost
                    WHERE id = :id
                         AND version = :version
            """

    /**
     * Method to read ProcTransportCost object by id and version
     * 1. Get transportCost object
     * @param id - ProcTransportCost.id
     * @param version - ProcTransportCost.version
     * @return - an object of ProcTransportCost
     */
    public ProcTransportCost readByIdAndVersion(long id, int version) {
        ProcTransportCost transportCost = null
        List<GroovyRowResult> resultList = executeSelectSql(SELECT_QUERY, [id: id, version: version])
        if (resultList.size() > 0) {
            transportCost = buildTransportCost(resultList[0])
        }
        return transportCost
    }

    /**
     * Build ProcTransportCost object
     * @param result - GroovyRowResult from the caller method
     * @return - new object of ProcTransportCost
     */
    private ProcTransportCost buildTransportCost(GroovyRowResult result) {
        ProcTransportCost transportCost = new ProcTransportCost()
        transportCost.id = result.id
        transportCost.version = result.version
        transportCost.amount = result.amount
        transportCost.comments = result.comments
        transportCost.purchaseOrderId = result.purchase_order_id
        transportCost.quantity = result.quantity
        transportCost.rate = result.rate

        return transportCost
    }
}
