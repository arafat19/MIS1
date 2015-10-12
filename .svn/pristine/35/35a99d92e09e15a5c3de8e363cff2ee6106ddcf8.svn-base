package com.athena.mis.application.actions.entitycontent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.entity.EntityContent
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.service.ProjectService
import com.athena.mis.application.utility.*
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.imgscalr.Scalr
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 *  Class to create (upload) new entityContent(Project attachment) object and show on grid list
 *  For details go through Use-Case doc named 'CreateEntityContentActionService'
 */
class CreateEntityContentActionService extends BaseService implements ActionIntf {

    EntityContentService entityContentService
    ProjectService projectService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String SAVE_SUCCESS_MESSAGE = "Attachment has been successfully saved"
    private static final String SAVE_FAILURE_MESSAGE = "Can not save attachment"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"
    private static final String ENTITY_CONTENT = "entityContent"
    //constants for image validations
    public static final String UNRECOGNIZED_IMAGE = "Unrecognized image file"
    public static final String UNRECOGNIZED_FILE = "Unrecognized file"
    public static final String SELECT_ONLY = "Select only "
    public static final String TYPE = " type for "
    //constants for image validations
    private static final String MB = " MB"
    private static final String KB = " KB"
    private static final String BYTES = " bytes"
    private static final String PNG_EXTENSION = "png"
    private static final String JPG_EXTENSION = "jpg"
    private static final String BMP_EXTENSION = "bmp"
    private static final String JPEG_EXTENSION = "jpeg"
    private static final String GIF_EXTENSION = "gif"
    private static final String FILE_SIZE_MAX_OF = " size can be maximum of "

    /**
     * Check different criteria for creating(Uploading) new entityContent(project attachment)
     *      1) Check existence of required parameters
     *      2) Validate uploaded attachment file (EntityContent object)
     * @param params -parameter send from UI
     * @param obj -N/A
     * @return -a map containing entityContent(project attachment) object for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            // check here for required params are present
            if ((!parameterMap.contentObj) || (!parameterMap.entityTypeId) || (!parameterMap.entityId)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            long contentCategoryId = Long.parseLong(parameterMap.contentCategoryId.toString())

            //validate uploaded attachment file(EntityContent)
            CommonsMultipartFile contentFile = parameterMap.contentObj
            String validateFileMsg = validateContent(contentFile, contentCategoryId)
            if (validateFileMsg != null) {//if validation failed then return with message
                result.put(Tools.MESSAGE, validateFileMsg)
                return result
            }

            //build entityContent object to create
            EntityContent newEntityContent = buildEntityContentObject(parameterMap, contentFile)

            result.put(ENTITY_CONTENT, newEntityContent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Save entityContent object (project attachment) in DB
     * @param parameters -N/A
     * @param obj -entityContentObject send from executePreCondition
     * @return -newly created entityContent object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            EntityContent entityContent = (EntityContent) preResult.get(ENTITY_CONTENT)
            EntityContent newEntityContent = entityContentService.create(entityContent)
            updateContentCount(entityContent)
            result.put(ENTITY_CONTENT, newEntityContent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap newly created entityContent object to show on grid
     * @param obj -newly created entityContent object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            EntityContent entityContent = (EntityContent) receiveResult.get(ENTITY_CONTENT)

            ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(entityContent.contentCategoryId)
            SystemEntity contentType = (SystemEntity) contentTypeCacheUtility.read(entityContent.contentTypeId)
            GridEntity object = new GridEntity()
            object.id = entityContent.id
            object.cell = [
                    Tools.LABEL_NEW,
                    entityContent.id,
                    contentType.key,
                    contentCategory.name,
                    entityContent.extension,
                    entityContent.caption
            ]

            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build entityContent object to save in DB
     * @param parameterMap -GrailsParameterMap
     * @param contentFile -Commons Multi-part File
     * @return -EntityContent object
     */
    private EntityContent buildEntityContentObject(GrailsParameterMap parameterMap, CommonsMultipartFile contentFile) {
        EntityContent entityContent = new EntityContent(parameterMap)

        entityContent.content = contentFile.bytes
        long companyId = appSessionUtil.getCompanyId()
        // pull system entity object(Image)
        SystemEntity contentType = (SystemEntity) contentTypeCacheUtility.readByReservedAndCompany(contentTypeCacheUtility.CONTENT_TYPE_IMAGE_ID, companyId)

        // resize content for image content type
        long contentTypeId = Long.parseLong(parameterMap.contentTypeId.toString())
        if (contentTypeId == contentType.id) {
            long contentCategoryId = Long.parseLong(parameterMap.contentCategoryId.toString())
            ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(contentCategoryId)
            byte[] byteContent = resizeImage(contentFile, contentCategory)
            entityContent.content = byteContent
        }

        //Set Extension of uploaded file
        String originalContentFileName = contentFile.properties.originalFilename.toString()
        entityContent.fileName = originalContentFileName

        int i = originalContentFileName.lastIndexOf('.');
        String fileExtension = originalContentFileName.substring(i + 1);
        entityContent.extension = fileExtension
        entityContent.expirationDate = parameterMap.expirationDate ? DateUtility.parseMaskedDate(parameterMap.expirationDate.toString()) : null
        entityContent.createdOn = new Date()
        entityContent.createdBy = appSessionUtil.getAppUser().id
        entityContent.companyId = companyId
        entityContent.updatedOn = null
        entityContent.updatedBy = 0L
        return entityContent
    }

    /**
     * Validating uploaded image type, extension, size etc.
     * @param contentFile - object of CommonsMultipartFile
     * @param contentCategoryId - ContentCategory.id
     * @return - a string of message containing uploaded file size
     */
    public String validateContent(CommonsMultipartFile contentFile, long contentCategoryId) {
        long companyId = appSessionUtil.getCompanyId()
        SystemEntity contentTypeImage = (SystemEntity) contentTypeCacheUtility.readByReservedAndCompany(contentTypeCacheUtility.CONTENT_TYPE_IMAGE_ID, companyId)
        String returnMsg = null
        long contentFileSize = contentFile.size.longValue()
        String uploadedFileExtension = null
        //Get the extension of the uploaded file
        ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(contentCategoryId)

        if (contentCategory.contentTypeId == contentTypeImage.id) {
            uploadedFileExtension = getImageExtension(contentFile)
            if (!uploadedFileExtension) return UNRECOGNIZED_IMAGE
        } else {
            uploadedFileExtension = getContentExtension(contentFile)
            if (!uploadedFileExtension) return UNRECOGNIZED_FILE
        }

        if (contentCategory.extension) {
            boolean extensionMatched = false
            String validExtensions = contentCategory.extension
            for (String currentExtension : validExtensions.split(Tools.COMA)) {
                extensionMatched = currentExtension.trim().equalsIgnoreCase(uploadedFileExtension)
                if (extensionMatched) break
            }
            if (!extensionMatched) {
                returnMsg = SELECT_ONLY + contentCategory.extension + TYPE + contentCategory.name
                return returnMsg
            }
        }
        returnMsg = checkMaxSize(contentFileSize, contentCategory)
        return returnMsg
    }

    /**
     * Check if uploaded file exceeds content pre-defined size
     * @param contentFileSize - long value of content file size
     * @param contentCategory - object of ContentCategory
     * @return - a message string of file size
     */
    private String checkMaxSize(long contentFileSize, ContentCategory contentCategory) {
        String returnMsg = null
        if (contentFileSize <= contentCategory.maxSize) return returnMsg
        long kbSize = 1024L
        long mbSize = 1048576L  // 1024 * 1024
        if (contentCategory.maxSize < kbSize) {
            returnMsg = contentCategory.name + FILE_SIZE_MAX_OF + contentCategory.maxSize + BYTES
        } else if (contentCategory.maxSize < mbSize) {
            long inKb = (contentCategory.maxSize) / kbSize
            returnMsg = contentCategory.name + FILE_SIZE_MAX_OF + inKb + KB
        } else {
            long inMb = (contentCategory.maxSize) / mbSize
            returnMsg = contentCategory.name + FILE_SIZE_MAX_OF + inMb + MB
        }
        return returnMsg
    }

    /**
     * Get uploaded file extension
     * @param contentFile - object of CommonsMultipartFile
     * @return - file extension string
     */
    private String getContentExtension(CommonsMultipartFile contentFile) {
        String contentFileName = contentFile.properties.originalFilename.toString()
        int i = contentFileName.lastIndexOf(Tools.SINGLE_DOT)
        String uploadedFileExtension = contentFileName.substring(i + 1)
        return uploadedFileExtension
    }

    /**
     * Resize image and convert to bytes
     * @param imageFile - object of CommonsMultipartFile
     * @param contentCategory - object of ContentCategory
     * @return - byte array of image
     */
    private byte[] resizeImage(CommonsMultipartFile imageFile, ContentCategory contentCategory) {
        if ((contentCategory.width <= 0) || (contentCategory.height <= 0)) {
            return imageFile.bytes        // return without resizing
        }
        BufferedImage tempImg = ImageIO.read(imageFile.getInputStream())
        BufferedImage scaledImg = Scalr.resize(tempImg, Scalr.Mode.FIT_EXACT, contentCategory.width, contentCategory.height)
        // change the image size
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        String imageExt = getContentExtension(imageFile)
        ImageIO.write(scaledImg, imageExt, buffer)
        byte[] imageBytes = buffer.toByteArray()
        return imageBytes
    }

    /**
     * Give image extension
     * @param imageFile - object of CommonsMultipartFile
     * @return - name string of image extension
     */
    private String getImageExtension(CommonsMultipartFile imageFile) {
        String imageFileName = imageFile.properties.originalFilename.toString()
        String temp = imageFileName.toLowerCase()
        String imageExtension = null

        if (temp.endsWith(PNG_EXTENSION))
            return imageExtension = PNG_EXTENSION
        if (temp.endsWith(GIF_EXTENSION))
            return imageExtension = GIF_EXTENSION
        if (temp.endsWith(BMP_EXTENSION))
            return imageExtension = BMP_EXTENSION
        if (temp.endsWith(JPEG_EXTENSION))
            return imageExtension = JPEG_EXTENSION
        if (temp.endsWith(JPG_EXTENSION))
            return imageExtension = JPG_EXTENSION
        return imageExtension
    }

    /**
     * Update content count counter as content entity type
     * @param entityContent - object of EntityContent
     */
    private void updateContentCount(EntityContent entityContent) {
        SystemEntity entityContentType = (SystemEntity) contentEntityTypeCacheUtility.read(entityContent.entityTypeId)
        int count = 1
        int updateCount = 1
        switch (entityContentType.reservedId) {
            case contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_BUDGET:
                updateCount = (Integer) budgetImplService.updateContentCountForBudget(entityContent.entityId, count)
                break
            case contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_PROJECT:
                updateCount = projectService.updateContentCountForProject(entityContent.entityId, count)
                Project project = projectService.read(entityContent.entityId)
                projectCacheUtility.update(project, projectCacheUtility.SORT_ON_NAME, projectCacheUtility.SORT_ORDER_ASCENDING)
                break
            case contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_FINANCIAL_YEAR:
                updateCount = (Integer) accountingImplService.updateContentCountForFinancialYear(entityContent.entityId, count)
                break
            default:
                break
        }
        if (updateCount <= 0) {
            throw new RuntimeException("Error occurred updating content count")
        }
    }
}
