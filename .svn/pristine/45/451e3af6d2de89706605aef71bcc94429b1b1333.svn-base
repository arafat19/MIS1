package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.application.utility.VehicleCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 * VehicleService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class VehicleService extends BaseService {

    static transactional = false

    @Autowired
    VehicleCacheUtility vehicleCacheUtility
    /**
     * Pull vehicle object
     * @return - list of vehicle
     */
    public List list() {
        return Vehicle.list(sort: vehicleCacheUtility.SORT_ON_NAME, order: vehicleCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * Method to count vehicle
     * @param vehicleName - vehicle name
     * @param companyId - company id
     * @return - an integer value of vehicle count
     */
    public int countByNameIlikeAndCompanyId(String vehicleName, long companyId) {
        int count = Vehicle.countByNameIlikeAndCompanyId(vehicleName, companyId)
        return count
    }

    /**
     * Method to count vehicle
     * @param vehicleName - vehicle name
     * @param companyId - company id
     * @param vehicleId - vehicle id
     * @return - an integer value of vehicle count
     */
    public int countByNameIlikeAndCompanyIdAndIdNotEqual(String vehicleName, long companyId, long vehicleId) {
        int vehicleCount = Vehicle.countByNameIlikeAndCompanyIdAndIdNotEqual(vehicleName, companyId, vehicleId)
        return vehicleCount
    }
    private static final String QUERY_CREATE = """
            INSERT INTO vehicle(id,version,name,description,company_id,created_on,created_by,updated_by,updated_on)
            VALUES (NEXTVAL('vehicle_id_seq'),:version,:name,:description,:companyId,:createdOn,:createdBy,:updatedBy,null);
            """
    /**
     * Create new vehicle
     * @param vehicle -vehicle  object
     * @return -create vehicle if saved successfully, otherwise throw RuntimeException
     */
    public Vehicle create(Vehicle vehicle) {

        Map queryParams = [
                version: vehicle.version,
                name: vehicle.name,
                description: vehicle.description,
                companyId: vehicle.companyId,
                createdBy: vehicle.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(vehicle.createdOn),
                updatedBy: vehicle.updatedBy
        ]

        List result = executeInsertSql(QUERY_CREATE, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert vehicle information')
        }

        int vehicleId = (int) result[0][0]
        vehicle.id = vehicleId
        return vehicle
    }

    private static final String QUERY_UPDATE = """
                    UPDATE vehicle SET
                          version= :newVersion,
                          name=:name,
                          description=:description,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy
                      WHERE
                          id=:id AND
                          version=:version
                          """
    /**
     * Updates supplied vehicle
     * @param vehicle -vehicle  object
     * @return -updated vehicle if saved successfully, otherwise throw RuntimeException
     */
    public int update(Vehicle vehicle) {

        Map queryParams = [
                newVersion: vehicle.version + 1,
                name: vehicle.name,
                description: vehicle.description,
                id: vehicle.id,
                version: vehicle.version,
                updatedBy: vehicle.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(vehicle.updatedOn)
        ]

        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('error occurred at vehicleService.update')
        }
        vehicle.version = vehicle.version + 1
        return updateCount;
    }
    /**
     * Delete supplied vehicle
     * @param id -vehicle  id
     * @return -boolean value true for success or throw exception for failure
     */
    public Boolean delete(long id) {
        String queryStr = """
                    DELETE FROM vehicle
                      WHERE
                          id=:id
                          """
        int deleteCount = executeUpdateSql(queryStr, [id: id])
        if (deleteCount <= 0) {
            throw new RuntimeException('Failed to delete vehicle')
        }
        return Boolean.TRUE;
    }
    /**
     * applicable only for create default vehicle
     */
    public void createDefaultData(long companyId) {
        Vehicle vehicle1 = new Vehicle(name: 'Car', description: 'Its a car', companyId: companyId, createdBy: 1, createdOn: new Date())
        vehicle1.save()
        Vehicle vehicle2 = new Vehicle(name: 'Covered van', description: 'Its a van', companyId: companyId, createdBy: 1, createdOn: new Date())
        vehicle2.save()
        Vehicle vehicle3 = new Vehicle(name: 'General Van', description: 'Covered van', companyId: companyId, createdBy: 1, createdOn: new Date())
        vehicle3.save()
        Vehicle vehicle4 = new Vehicle(name: 'Heavy Truck', description: 'Its Heavy Duty Car', companyId: companyId, createdBy: 1, createdOn: new Date())
        vehicle4.save()
        Vehicle vehicle5 = new Vehicle(name: 'Toyota Jeep', description: 'Jeep description', companyId: companyId, createdBy: 1, createdOn: new Date())
        vehicle5.save()
        Vehicle vehicle6 = new Vehicle(name: 'Truck', description: 'Truck Description', companyId: companyId, createdBy: 1, createdOn: new Date())
        vehicle6.save()
    }
}
