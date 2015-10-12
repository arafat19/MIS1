package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.entity.EntityContent
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.ContentCategoryCacheUtility
import com.athena.mis.application.utility.ContentEntityTypeCacheUtility
import com.athena.mis.application.utility.ContentTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
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
 *  Create new entityContent and show in grid
 *  For details go through Use-Case doc named 'CreateExhCustomerContentActionService'
 */
class CreateExhCustomerContentActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String SAVE_SUCCESS_MESSAGE = "Attachment has been saved successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Can not save attachment content"
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

    EntityContentService entityContentService

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility

    /**
     * Get parameters from UI and build EntityContent object
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)                  // set default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            if ((!parameterMap.contentObj) || (!parameterMap.caption)) {     // check required params
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            if (!parameterMap.customerId) {              // check required parameter
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long customerId = Tools.parseLongInput(parameterMap.customerId.toString())
            if (customerId == 0) {                                      // check parse exception
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long contentCategoryId = Long.parseLong(parameterMap.contentCategoryId.toString())
            ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(contentCategoryId)     // get content category ie signature, photo etc form cache

            CommonsMultipartFile contentFile = parameterMap.contentObj
            String validateFileMsg = validateContent(contentFile, contentCategory)     // validate content category
            if (validateFileMsg != null) {
                result.put(Tools.MESSAGE, validateFileMsg)
                return result
            }

            EntityContent newEntityContent = buildEntityContentObject(parameterMap, contentFile)          // build entityContent object

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
     * Save entityContent object in DB
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            EntityContent entityContent = (EntityContent) preResult.get(ENTITY_CONTENT)
            EntityContent newEntityContent = entityContentService.create(entityContent)

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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show newly created entityContent object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj        // cast map returned from execute method
            EntityContent entityContent = (EntityContent) receiveResult.get(ENTITY_CONTENT)

            ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(entityContent.contentCategoryId) // get content category ie signature, photo etc form cache
            GridEntity object = new GridEntity()   //build grid object
            object.id = entityContent.id
            object.cell = [
                    Tools.LABEL_NEW,
                    entityContent.id,
                    contentCategory.name,
                    Tools.makeDetailsShort(entityContent.caption, 100),       // make caption short if more than 100 characters
                    entityContent.extension,
                    DateUtility.getDateFormatAsString(entityContent.expirationDate)
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
     * Build EntityContent object
     * @param parameterMap -serialized parameters from UI
     * @param contentFile -contentFile from UI
     * @return -new entityContent object
     */
    private EntityContent buildEntityContentObject(GrailsParameterMap parameterMap, CommonsMultipartFile contentFile) {
        EntityContent entityContent = new EntityContent(parameterMap)            // initialize  entityContent by parameterMap
        long customerId = Long.parseLong(parameterMap.customerId.toString())
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        // pull system entity type(Image) object
        SystemEntity contentTypeImage = (SystemEntity) contentTypeCacheUtility.readByReservedAndCompany(contentTypeCacheUtility.CONTENT_TYPE_IMAGE_ID, companyId)

        entityContent.content = contentFile.bytes


        long contentTypeId = Long.parseLong(parameterMap.contentTypeId.toString())
        if (contentTypeId == contentTypeImage.id) {
            long contentCategoryId = Long.parseLong(parameterMap.contentCategoryId.toString())
            ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(contentCategoryId)
            byte[] byteContent = resizeImage(contentFile, contentCategory)         // image resize given contentCategory property width, height
            entityContent.content = byteContent
        }

        //Set Extension of uploaded file
        String originalContentFileName = contentFile.properties.originalFilename.toString()
        entityContent.fileName = originalContentFileName

        int i = originalContentFileName.lastIndexOf(Tools.SINGLE_DOT);
        String fileExtension = originalContentFileName.substring(i + 1)
        entityContent.extension = fileExtension

        AppUser systemUser = exhSessionUtil.appSessionUtil.getAppUser()
        // pull system entity type(Customer) object
        SystemEntity contentEntityTypeCustomer = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_EXH_CUSTOMER, systemUser.companyId)

        entityContent.entityTypeId = contentEntityTypeCustomer.id
        entityContent.entityId = customerId
        entityContent.createdOn = new Date()
        entityContent.createdBy = systemUser.id
        entityContent.companyId = systemUser.companyId
        entityContent.updatedOn = null
        entityContent.updatedBy = 0L
        entityContent.expirationDate = parameterMap.expirationDate ? DateUtility.parseMaskedDate(parameterMap.expirationDate) : null

        return entityContent
    }

    // Validating uploaded image type, extension, size etc.
    private String validateContent(CommonsMultipartFile contentFile, ContentCategory contentCategory) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity contentTypeImage = (SystemEntity) contentTypeCacheUtility.readByReservedAndCompany(contentTypeCacheUtility.CONTENT_TYPE_IMAGE_ID, companyId)
        String returnMsg = null
        long contentFileSize = contentFile.size.longValue()
        String uploadedFileExtension = null

        //Get the extension of the uploaded file
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

    // Check if uploaded file exceeds content pre-defined size
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

    private String getContentExtension(CommonsMultipartFile contentFile) {
        String contentFileName = contentFile.properties.originalFilename.toString()
        int i = contentFileName.lastIndexOf(Tools.SINGLE_DOT)
        String uploadedFileExtension = contentFileName.substring(i + 1)
        return uploadedFileExtension
    }

    // resize image and convert to bytes
    private byte[] resizeImage(CommonsMultipartFile imageFile, ContentCategory contentCategory) {
        if ((contentCategory.width <= 0) || (contentCategory.height <= 0)) {
            return imageFile.bytes        // return without resizing
        }
        BufferedImage tempImg = ImageIO.read(imageFile.getInputStream())
        BufferedImage scaledImg = Scalr.resize(tempImg, Scalr.Mode.FIT_EXACT, contentCategory.width, contentCategory.height)     // change the image size
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        String imageExt = getContentExtension(imageFile)
        ImageIO.write(scaledImg, imageExt, buffer)
        byte[] imageBytes = buffer.toByteArray()
        return imageBytes
    }

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
