package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.EntityNote
import com.athena.mis.utility.DateUtility

class EntityNoteService extends BaseService {
    static transactional = false

    /**
     * Save note object into DB
     * @param note -entityNote object
     * @return -saved entityNote object
     */
    public EntityNote create(EntityNote note) {
        EntityNote entityNote = note.save(false)
        return entityNote
    }

    /**
     * Get EntityNote object by id
     * @param id -id of object
     * @return entityNote object
     */
    public EntityNote read(long id) {
        return EntityNote.read(id)
    }

    private static final String UPDATE_QUERY = """
        UPDATE entity_note SET
            version=:newVersion,
            note=:note,
            updated_on=:updatedOn,
            updated_by=:updatedBy
        WHERE
            id=:id AND
            version=:version
    """

    /**
     * Update EntityNote object in DB
     * @param entityNote -EntityNote object
     * @return -saved EntityNote object
     */
    public EntityNote update(EntityNote entityNote) {
        Map queryParams = [
                id: entityNote.id,
                newVersion: entityNote.version + 1,
                version: entityNote.version,
                note: entityNote.note,
                updatedBy: entityNote.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(entityNote.updatedOn)
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred at update EntityNote information')
        }
        entityNote.version = entityNote.version + 1
        return entityNote
    }

    private static final DELETE_ENTITY_NOTE_QUERY = """ DELETE FROM entity_note WHERE id=:id """

    /**
     * Delete note object from DB
     * @param id -id of note object
     * @return -an integer containing the value of update count
     */
    public int delete(long id) {
        Map queryParams = [
                id: id
        ]
        int deleteCount = executeUpdateSql(DELETE_ENTITY_NOTE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("error occurred while deleting entity note information")
        }
        return deleteCount;
    }

    /**
     * Get count of EntityNote by entityTypeId and companyId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param companyId -id of company
     * @return -an integer containing the value of count
     */
    public int countByEntityTypeIdAndCompanyId(long entityTypeId, long companyId) {
        int count = EntityNote.countByEntityTypeIdAndCompanyId(entityTypeId, companyId)
        return count
    }

    /**
     * Get list of EntityNote by companyId, entityTypeId and entityId
     * @param companyId -id of company
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param entityId -id of entity (Customer.id, Task.id etc.)
     * @return -list of EntityNote
     */
    public List<EntityNote> findAllByCompanyIdAndEntityTypeIdAndEntityId(long companyId, long entityTypeId, long entityId) {
        List<EntityNote> lstEntityNote = EntityNote.findAllByCompanyIdAndEntityTypeIdAndEntityId(companyId, entityTypeId, entityId, [readOnly: true])
        return lstEntityNote
    }

    /**
     * Get list of EntityNote by companyId, entityTypeId and entityId and sort by order
     * @param companyId -id of company
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param entityId -id of entity (Customer.id, Task.id etc.)
     * @return -list of EntityNote
     */
    public List<EntityNote> findAllByCompanyIdAndEntityTypeIdAndEntityIdOrdered(long companyId, long entityTypeId, long entityId, String order) {
        List<EntityNote> lstEntityNote = EntityNote.findAllByCompanyIdAndEntityTypeIdAndEntityId(companyId, entityTypeId, entityId, [readOnly: true, sort: ID, order: order])
        return lstEntityNote
    }

    /**
     * Get list of EntityNote by companyId, entityTypeId and entityId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param entityId -id of entity (Customer.id, Task.id etc.)
     * @param companyId -id of company
     * @param baseService -object of BaseService
     * @return -list of EntityNote
     */
    public List<EntityNote> findAllByEntityTypeIdAndEntityIdAndCompanyId(long entityTypeId, long entityId, long companyId, BaseService baseService) {
        List<EntityNote> lstEntityNote = EntityNote.findAllByEntityTypeIdAndEntityIdAndCompanyId(entityTypeId, entityId, companyId, [max: baseService.resultPerPage, offset: baseService.start, sort: baseService.sortColumn, order: baseService.sortOrder, readOnly: true])
        return lstEntityNote
    }

    /**
     * Get count of EntityNote by companyId, entityTypeId and entityId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param entityId -id of entity (Customer.id, Task.id etc.)
     * @param companyId -id of company
     * @return -an integer containing the value of count
     */
    public int countByEntityTypeIdAndEntityIdAndCompanyId(long entityTypeId, long entityId, long companyId) {
        int count = EntityNote.countByEntityTypeIdAndEntityIdAndCompanyId(entityTypeId, entityId, companyId)
        return count
    }

    /**
     * Get list of EntityNote by companyId, entityTypeId and entityId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param entityId -id of entity (Customer.id, Task.id etc.)
     * @param companyId -id of company
     * @param note -String value note (search key word)
     * @param baseService -object of BaseService
     * @return -list of EntityNote
     */
    public List<EntityNote> findAllByEntityTypeIdAndEntityIdAndCompanyIdAndNoteIlike(long entityTypeId, long entityId, long companyId, String note, BaseService baseService) {
        List<EntityNote> lstEntityNote = EntityNote.findAllByEntityTypeIdAndEntityIdAndCompanyIdAndNoteIlike(entityTypeId, entityId, companyId, note, [max: baseService.resultPerPage, offset: baseService.start, sort: baseService.sortColumn, order: baseService.sortOrder, readOnly: true])
        return lstEntityNote
    }

    /**
     * Get count of EntityNote by companyId, entityTypeId and entityId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param entityId -id of entity (Customer.id, Task.id etc.)
     * @param companyId -id of company
     * @param note -String value note (search key word)
     * @return -an integer containing the value of count
     */
    public int countByEntityTypeIdAndEntityIdAndCompanyIdAndNoteIlike(long entityTypeId, long entityId, long companyId, String note) {
        int count = EntityNote.countByEntityTypeIdAndEntityIdAndCompanyIdAndNoteIlike(entityTypeId, entityId, companyId, note)
        return count
    }
}
