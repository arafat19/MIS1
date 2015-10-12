package com.athena.mis.application.actions.entitycontent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.entity.EntityContent
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.*
import com.athena.mis.integration.budget.BudgetPluginConnector
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
 *  Class to update entityContent(attachment) object and show on grid list
 *  For details go through Use-Case doc named 'UpdateEntityContentActionService'
 */
class UpdateEntityContentActionService extends BaseService implements ActionIntf {

    EntityContentService entityContentService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_SUCCESS_MESSAGE = "Entity content has been updated successfully"
    private static final String UPDATE_FAILURE_MESSAGE = "Could not update entity content"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"
    private static final String NOT_FOUND_MESSAGE = "Selected entity content not found"
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
     * Check different criteria for updating entityContent(attachment) object
     *      1) Check existence of required parameters
     *      2) Check existence of old entityContent (attachment file)
     *      3) Validate uploaded attachment file (EntityContent object)
     * @param params -parameter send from UI
     * @param obj -N/A
     * @return -a map containing entityContent(attachment) object for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            // check here for required params are present
            if ((!parameterMap.id) || (!parameterMap.caption)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            long id = Long.parseLong(parameterMap.id.toString())
            EntityContent oldEntityContent = entityContentService.read(id)
            if (!oldEntityContent) {//Check existence of old entityContent (attachment file)
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }
            long contentCategoryId = Long.parseLong(parameterMap.contentCategoryId.toString())

            EntityContent newEntityContent
            //validate uploaded attachment file(EntityContent)
            CommonsMultipartFile contentFile = parameterMap.contentObj ? parameterMap.contentObj : null
            if (contentFile && (!contentFile.isEmpty())) {
                String validateFileMsg = validateContent(contentFile, contentCategoryId)
                if (validateFileMsg != null) {
                    result.put(Tools.MESSAGE, validateFileMsg)
                    return result
                }
                newEntityContent = buildEntityContentObjectWithContentFile(parameterMap, contentFile, oldEntityContent)

            } else {
                newEntityContent = buildEntityContentObjectWithoutContentFile(parameterMap, oldEntityContent)
            }

            result.put(ENTITY_CONTENT, newEntityContent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  1) if new entityContent(attachment) is given then update all fields of entityContent object
     *           otherwise update fields of entityContent object except content field
     *  2) if budget lineItem changed then update content_count field of Budget domain
     *
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
            EntityContent existingEntityContent = entityContentService.read(entityContent.id)

            EntityContent newEntityContent
            if (entityContent.content != null) {//update all fields of entityContent object
                newEntityContent = entityContentService.updateWithContent(entityContent)
            } else { //update fields of entityContent object except content
                newEntityContent = entityContentService.updateWithoutContent(entityContent)
            }
            result.put(ENTITY_CONTENT, newEntityContent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
     * Wrap updated entityContent object to show on grid
     * @param obj -updated entityContent object from execute method
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

            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build entityContent object to update in DB
     * @param parameterMap -GrailsParameterMap
     * @param contentFile -CommonsMultipartFile
     * @param oldEntityContent -EntityContent
     * @return -EntityContent object
     */
    private EntityContent buildEntityContentObjectWithContentFile(GrailsParameterMap parameterMap, CommonsMultipartFile contentFile, EntityContent oldEntityContent) {
        EntityContent entityContent = new EntityContent(parameterMap)

        entityContent.extension = oldEntityContent.extension
        entityContent.fileName = oldEntityContent.fileName

        //Set Extension of uploaded file
        if (contentFile.size.longValue() > 0) {
            entityContent.content = contentFile.bytes
            String originalContentFileName = contentFile.properties.originalFilename.toString()
            entityContent.fileName = originalContentFileName

            int i = originalContentFileName.lastIndexOf('.');
            String fileExtension = originalContentFileName.substring(i + 1);
            entityContent.extension = fileExtension

            // pull system entity object
            SystemEntity contentTypeImage = (SystemEntity) contentTypeCacheUtility.readByReservedAndCompany(contentTypeCacheUtility.CONTENT_TYPE_IMAGE_ID, appSessionUtil.getCompanyId())

            // resize content for image content type
            if (oldEntityContent.contentTypeId == contentTypeImage.id) {
                long contentCategoryId = Long.parseLong(parameterMap.contentCategoryId.toString())
                ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(contentCategoryId)
                byte[] byteContent = resizeImage(contentFile, contentCategory)
                entityContent.content = byteContent
            }
        }

        entityContent.contentTypeId = oldEntityContent.contentTypeId
        entityContent.entityTypeId = oldEntityContent.entityTypeId
        entityContent.id = oldEntityContent.id
        entityContent.version = oldEntityContent.version
        entityContent.createdOn = oldEntityContent.createdOn
        entityContent.createdBy = oldEntityContent.createdBy
        entityContent.companyId = oldEntityContent.companyId
        entityContent.updatedOn = new Date()
        entityContent.updatedBy = appSessionUtil.getAppUser().id
        entityContent.expirationDate = oldEntityContent.expirationDate
        entityContent.expirationDate = parameterMap.expirationDate ? DateUtility.parseMaskedDate(parameterMap.expirationDate.toString()) : null

        return entityContent
    }

    /**
     * build entityContent object to update in DB
     * @param parameterMap -GrailsParameterMap
     * @param oldEntityContent -EntityContent
     * @return -EntityContent object
     */
    private EntityContent buildEntityContentObjectWithoutContentFile(GrailsParameterMap parameterMap, EntityContent oldEntityContent) {
        EntityContent entityContent = new EntityContent(parameterMap)
        entityContent.extension = oldEntityContent.extension
        entityContent.fileName = oldEntityContent.fileName

        entityContent.contentTypeId = oldEntityContent.contentTypeId
        entityContent.entityTypeId = oldEntityContent.entityTypeId
        entityContent.id = oldEntityContent.id
        entityContent.version = oldEntityContent.version
        entityContent.createdOn = oldEntityContent.createdOn
        entityContent.createdBy = oldEntityContent.createdBy
        entityContent.companyId = oldEntityContent.companyId
        entityContent.updatedOn = new Date()
        entityContent.updatedBy = appSessionUtil.getAppUser().id
        entityContent.expirationDate = oldEntityContent.expirationDate
        entityContent.expirationDate = parameterMap.expirationDate ? DateUtility.parseMaskedDate(parameterMap.expirationDate.toString()) : null

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
}
