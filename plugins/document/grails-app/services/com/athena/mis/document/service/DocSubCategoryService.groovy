package com.athena.mis.document.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppUserService
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

class DocSubCategoryService extends BaseService {

    AppUserService appUserService
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility

    public List<DocSubCategory> list() {
        List<DocSubCategory> subCategoryList = DocSubCategory.list(
                sort: docSubCategoryCacheUtility.DEFAULT_SORT_ORDER,
                order: docSubCategoryCacheUtility.SORT_ORDER_ASCENDING,
                readOnly: true
        )

        return subCategoryList
    }

    /**
     * @params id - SubCategory id
     * @return - A subCategory object
     * */

    public List<DocSubCategory> findAllByIdInList(List<Long> lstSubCategoryId) {
        List<DocSubCategory> lstSubCategory = DocSubCategory.findAllByIdInList(lstSubCategoryId)
        return lstSubCategory
    }

    /**
     * @params id - SubCategory id
     * @return - A subCategory object
     * */

    public DocSubCategory read(long id) {
        DocSubCategory subCategory = DocSubCategory.read(id)
        return subCategory
    }
    /**
     * @param categoryId - category id
     * @return - count of category object
     */
    public int countByCategoryId(long categoryId) {
        int count = DocSubCategory.countByCategoryId(categoryId)
        return count
    }

    private static final String INSERT_QUERY = """
            INSERT INTO doc_sub_category(id, version, category_id, name, description, url, is_active, is_email_notification,
                                        company_id, created_by, created_on, updated_by)
            VALUES (:id, 0, :categoryId, :name, :description, :url, :isActive, :isEmailNotification,
                    :companyId, :createdBy, :createdOn, :updatedBy)
    """

    /**
     * Save new sub category to DB
     * @params subcategory - sub category object for save
     * @return saved new sub category object
     * */
    public DocSubCategory create(DocSubCategory subCategory) {
        Map queryParams = [
                id: subCategory.id,
                categoryId: subCategory.categoryId,
                name: subCategory.name,
                description: subCategory.description,
                url: subCategory.url,
                isActive: subCategory.isActive,
                isEmailNotification: subCategory.isEmailNotification,
                companyId: subCategory.companyId,
                createdBy: subCategory.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(subCategory.createdOn),
                updatedBy: subCategory.updatedBy
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)
        int subCategoryId = (int) result[0][0]
        subCategory.id = subCategoryId
        if (result.size() <= 0) {
            throw new RuntimeException("error occurred at Sub Category Create")
        }
        return subCategory
    }

    public static final String UPDATE_QUERY = """
        UPDATE doc_sub_category SET
            version=version+1,
            name=:name,
            description=:description,
            url=:url,
            is_active=:isActive,
            is_email_notification=:isEmailNotification,
            updated_on=:updatedOn,
            updated_by=:updatedBy
        WHERE
            id=:id AND
            version=:oldVersion
    """

    /**
     * Update Sub Category object in DB
     * @param sub category - sub Category object
     * @return - an int containing the value of update count
     */

    public int update(DocSubCategory subCategory) {
        Map queryParams = [
                name: subCategory.name,
                description: subCategory.description,
                url: subCategory.url,
                isActive: subCategory.isActive,
                isEmailNotification: subCategory.isEmailNotification,
                id: subCategory.id,
                oldVersion: subCategory.version,
                updatedOn: DateUtility.getSqlDateWithSeconds(subCategory.updatedOn),
                updatedBy: subCategory.updatedBy
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Sub Category")
        }

        subCategory.version = subCategory.version + 1
        return updateCount
    }

    public static final String UPDATE_QUERY_FOR_IS_ACTIVE_FALSE = """
        UPDATE doc_sub_category SET
            version=version+1,
            is_active=false
        WHERE
            category_id=:categoryId
      """

    /**
     * Update Sub Category object(s) in DB if category isActive=false
     * @return - an int containing the value of update count
     */

    public int updateForIsActiveFalse(DocCategory category) {

        Map queryParams = [
                categoryId: category.id
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY_FOR_IS_ACTIVE_FALSE, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Sub Category")
        }
        return updateCount
    }


    private static final String DELETE_QUERY = """
                    DELETE FROM doc_sub_category WHERE id=:id
                 """

    /**
     * Delete Sub Category object from DB
     * @param id -id of Sub Category object
     * @return -an integer containing the value of update count
     */
    public int delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete sub category information')
        }
        return deleteCount;
    }

    /**
     * Create default data for DocCategory
     */
    public void createDefaultDataForDocSubCategory(Company company) {
        Date currDate = new Date()
        AppUser appUser = appUserService.findByLoginId('admin@athena.com')
        String webUrl = company.webUrl
        DocSubCategory subCategory = new DocSubCategory(version: 0, categoryId: 1, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, description: 'Description for Sub Category_11', url: webUrl + '/docSubCategory/showSubCategory?category=1&sub=1', isActive: true, isEmailNotification: false, name: 'Sub Category_11', updatedBy: 0, updatedOn: null)
        subCategory.save()
        DocSubCategory subCategory1 = new DocSubCategory(version: 0, categoryId: 1, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, description: 'Description for Sub Category_12', url: webUrl + '/docSubCategory/showSubCategory?category=1&sub=2', isActive: true, isEmailNotification: false, name: 'Sub Category_12', updatedBy: 0, updatedOn: null)
        subCategory1.save()
        DocSubCategory subCategory2 = new DocSubCategory(version: 0, categoryId: 1, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, description: 'Description for Sub Category_13', url: webUrl + '/docSubCategory/showSubCategory?category=1&sub=3', isActive: true, isEmailNotification: false, name: 'Sub Category_13', updatedBy: 0, updatedOn: null)
        subCategory2.save()
        DocSubCategory subCategory3 = new DocSubCategory(version: 0, categoryId: 2, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, description: 'Description for Sub Category_21', url: webUrl + '/docSubCategory/showSubCategory?category=2&sub=4', isActive: true, isEmailNotification: false, name: 'Sub Category_21', updatedBy: 0, updatedOn: null)
        subCategory3.save()
        DocSubCategory subCategory4 = new DocSubCategory(version: 0, categoryId: 2, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, description: 'Description for Sub Category_22', url: webUrl + '/docSubCategory/showSubCategory?category=2&sub=5', isActive: true, isEmailNotification: false, name: 'Sub Category_22', updatedBy: 0, updatedOn: null)
        subCategory4.save()
        DocSubCategory subCategory5 = new DocSubCategory(version: 0, categoryId: 3, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, description: 'Description for Sub Category_31', url: webUrl + '/docSubCategory/showSubCategory?category=3&sub=6', isActive: true, isEmailNotification: false, name: 'Sub Category_31', updatedBy: 0, updatedOn: null)
        subCategory5.save()
        DocSubCategory subCategory6 = new DocSubCategory(version: 0, categoryId: 3, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, description: 'Description for Sub Category_32', url: webUrl + '/docSubCategory/showSubCategory?category=3&sub=7', isActive: true, isEmailNotification: false, name: 'Sub Category_32', updatedBy: 0, updatedOn: null)
        subCategory6.save()
        DocSubCategory subCategory7 = new DocSubCategory(version: 0, categoryId: 3, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, description: 'Description for Sub Category_33', url: webUrl + '/docSubCategory/showSubCategory?category=3&sub=8', isActive: true, isEmailNotification: false, name: 'Sub Category_33', updatedBy: 0, updatedOn: null)
        subCategory7.save()
        DocSubCategory subCategory8 = new DocSubCategory(version: 0, categoryId: 3, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, description: 'Description for Sub Category_34', url: webUrl + '/docSubCategory/showSubCategory?category=3&sub=9', isActive: true, isEmailNotification: false, name: 'Sub Category_34', updatedBy: 0, updatedOn: null)
        subCategory8.save()
        DocSubCategory subCategory9 = new DocSubCategory(version: 0, categoryId: 3, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, description: 'Description for Sub Category_35', url: webUrl + '/docSubCategory/showSubCategory?category=3&sub=10', isActive: true, isEmailNotification: false, name: 'Sub Category_35', updatedBy: 0, updatedOn: null)
        subCategory9.save()

    }

}
