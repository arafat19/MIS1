package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ContentCategoryCacheUtility
import com.athena.mis.application.utility.ContentTypeCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 * ContentCategoryService is used to handle only CRUD related object manipulation (e.g. list, read, create, delete etc.)
 */
class ContentCategoryService extends BaseService {

    static transactional = false

    SystemEntityService systemEntityService
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility

    /**
     * @return - list of ContentCategory
     */
    public List list() {
        return ContentCategory.list(sort: contentCategoryCacheUtility.SORT_ON_NAME, order: contentCategoryCacheUtility.SORT_ORDER_ASCENDING, readOnly: true)
    }

    private static final String INSERT_QUERY = """
            INSERT INTO content_category(id, version, name, content_type_id, max_size, system_content_category,
                          width, height, extension, updated_by, updated_on, company_id ,is_reserved,
                          created_by, created_on)
            VALUES (:id,0,:name, :contentTypeId, :maxSize, :systemContentCategory,
                          :width,:height, :extension, :updatedBy, :updatedOn, :companyId, false,
                          :createdBy, :createdOn)
    """

    /**
     * Save ContentCategory object into DB
     * @param contentCategory -object of ContentCategory
     * @return -saved ContentCategory object
     */
    public ContentCategory create(ContentCategory contentCategory) {
        Map queryParams = [
                id: contentCategory.id,
                name: contentCategory.name,
                contentTypeId: contentCategory.contentTypeId,
                systemContentCategory: contentCategory.systemContentCategory,
                maxSize: contentCategory.maxSize,
                width: contentCategory.width,
                height: contentCategory.height,
                extension: contentCategory.extension,
                createdBy: contentCategory.createdBy,
                updatedBy: contentCategory.updatedBy,
                createdOn: DateUtility.getSqlDateWithSeconds(contentCategory.createdOn),
                updatedOn: contentCategory.updatedOn,
                companyId: contentCategory.companyId
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException("error occurred at contentCategoryService.create")

        }
        return contentCategory
    }

    private static final String SELECT_NEXT_VAL_CONTENT_CATEGORY = "SELECT nextval('content_category_id_seq') as id"

    /**
     * Get id from dedicated content category id sequence
     * @return - a long variable containing the value of id
     */
    public long getContentCategoryId() {
        List results = executeSelectSql(SELECT_NEXT_VAL_CONTENT_CATEGORY)
        long contentCategoryId = results[0].id
        return contentCategoryId
    }

    /**
     * Update ContentCategory object in DB
     * @param contentCategory -object of ContentCategory
     * @return -an integer containing the value of update count
     */
    public int update(ContentCategory contentCategory) {
        String query = """
                    UPDATE content_category SET
                          version=version+1,
                          name=:name,
                          content_type_id=:contentTypeId,
                          max_size=:maxSize,
                          width=:width,
                          height=:height,
                          extension=:extension,
                          updated_by=:updatedBy,
                          updated_on=:updatedOn
                        WHERE id=:id AND
                              version=:version
        """
        Map queryParams = [
                id: contentCategory.id,
                version: contentCategory.version,
                name: contentCategory.name,
                contentTypeId: contentCategory.contentTypeId,
                maxSize: contentCategory.maxSize,
                width: contentCategory.width,
                height: contentCategory.height,
                extension: contentCategory.extension,
                updatedBy: contentCategory.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(contentCategory.updatedOn)
        ]

        int updateCount = executeUpdateSql(query, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating content_category')
        }
        contentCategory.version = contentCategory.version + 1
        return updateCount
    }

    /**
     * Delete ContentCategory object from DB
     * @param id -id of ContentCategory object
     * @return -a boolean value(true/false) depending on method success
     */
    public Boolean delete(long id) {
        String query = """
                        DELETE FROM content_category
                          WHERE id=:id
        """
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(query, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("Error occurred while delete content_category information")
        }
        return Boolean.TRUE
    }

    /**
     * Create default data for ContentCategory
     */
    public void createDefaultData(long companyId) {
        SystemEntity contentTypeImage = systemEntityService.findByReservedIdAndCompanyId(contentTypeCacheUtility.CONTENT_TYPE_IMAGE_ID, companyId)
        Date currDate = new Date()
        ContentCategory contentTypePhoto = new ContentCategory(version: 0, name: 'Photo', width: 379, height: 485, maxSize: 1048576, extension: null, updatedOn: null, updatedBy: 0, companyId: companyId, contentTypeId: contentTypeImage.id, systemContentCategory: contentCategoryCacheUtility.IMAGE_TYPE_PHOTO, createdOn: currDate, createdBy: 1, isReserved: true)
        contentTypePhoto.save()

        ContentCategory contentTypeSignature = new ContentCategory(version: 0, name: 'Signature', width: 200, height: 50, maxSize: 1048576, extension: null, updatedOn: null, updatedBy: 0, companyId: companyId, contentTypeId: contentTypeImage.id, systemContentCategory: contentCategoryCacheUtility.IMAGE_TYPE_SIGNATURE, createdOn: currDate, createdBy: 1, isReserved: true)
        contentTypeSignature.save()

        ContentCategory contentTypeCompanyLogo = new ContentCategory(version: 0, name: 'Company Logo', width: 500, height: 500, maxSize: 1048576, extension: null, updatedOn: null, updatedBy: 0, companyId: companyId, contentTypeId: contentTypeImage.id, systemContentCategory: contentCategoryCacheUtility.IMAGE_TYPE_LOGO, createdOn: currDate, createdBy: 1, isReserved: true)
        contentTypeCompanyLogo.save()

        ContentCategory contentTypePhotoId = new ContentCategory(version: 0, name: 'Photo ID', width: 500, height: 500, maxSize: 1048576, extension: null, updatedOn: null, updatedBy: 0, companyId: companyId, contentTypeId: contentTypeImage.id, systemContentCategory: contentCategoryCacheUtility.IMAGE_TYPE_PHOTO_ID, createdOn: currDate, createdBy: 1, isReserved: true)
        contentTypePhotoId.save()

        if(PluginConnector.isPluginInstalled(PluginConnector.PROJECT_TRACK)){
            ContentCategory contentTypeScreenShot = new ContentCategory(version: 0, name: 'Screen Shot', width: 0, height: 0, maxSize: 2097152, extension: null, updatedOn: null, updatedBy: 0, companyId: companyId, contentTypeId: contentTypeImage.id, systemContentCategory: contentCategoryCacheUtility.IMAGE_TYPE_SCREEN_SHOT, createdOn: currDate, createdBy: 1, isReserved: true)
            contentTypeScreenShot.save()
        }
    }
}
