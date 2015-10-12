package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.*
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
 *  Create new appUser object and show in grid
 *  For details go through Use-Case doc named 'CreateAppUserActionService'
 */
class CreateAppUserActionService extends BaseService implements ActionIntf {

    EntityContentService entityContentService
    AppUserService appUserService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility

    private static final String APP_USER_SAVE_SUCCESS_MESSAGE = "User has been saved successfully"
    private static final String APP_USER_SAVE_FAILURE_MESSAGE = "User could not be saved"
    private static final String IMAGE_FILE = "imageFile"
    private static final String SIGNATURE_IMAGE = "signatureImage"
    private static final String LOGIN_ID_EXISTS_MSG = "Same login id already exists"
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

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check pre conditions before creating user
     *      1. check if user has access to create company user or not
     *      2. get appUser object and check uniqueness of login id
     * If user has signature image then
     *      1. validate and resize image file
     *      2. convert image to bytes and build EntityContent object with image file
     * @param params -serialized parameters from UI
     * @param obj -object of appUser
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) and hasAccess(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // only development role type user can create company user
            if (appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            } else {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            // get appUser object
            AppUser appUser = (AppUser) obj
            appUser.isCompanyUser = false   // hard coded false for this use-case only
            appUser.createdOn = new Date()
            appUser.createdBy = appSessionUtil.getAppUser().id
            appUser.updatedOn = null
            appUser.updatedBy = 0
            AppUser sameAppUser = (AppUser) appUserCacheUtility.readByLoginId(appUser.loginId)
            if (sameAppUser) {
                result.put(Tools.MESSAGE, LOGIN_ID_EXISTS_MSG)
                return result
            }
            // check if user has signature image or not
            GrailsParameterMap parameters = (GrailsParameterMap) params
            CommonsMultipartFile imageFile = parameters.signatureImage ? parameters.signatureImage : null
            // if user has signature image then validate image file
            if (imageFile && (!imageFile.isEmpty())) {
                appUser.hasSignature = true
                // get ContentCategory object for signature of AppUser
                ContentCategory imageTypeSignature = (ContentCategory) contentCategoryCacheUtility.readBySystemContentCategory(contentCategoryCacheUtility.IMAGE_TYPE_SIGNATURE)
                ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(imageTypeSignature.id)
                String validateImageMsg = validateContent(imageFile, contentCategory)
                if (validateImageMsg) {
                    result.put(Tools.MESSAGE, validateImageMsg)
                    return result
                }
                // resize image and convert to bytes
                byte[] byteImage = resizeImage(imageFile, contentCategory)
                // build EntityContent object with image file
                EntityContent signatureImage = buildEntityImageObject(byteImage, contentCategory, imageFile)
                result.put(SIGNATURE_IMAGE, signatureImage)
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, appUser)
            result.put(IMAGE_FILE, imageFile)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * Save appUser object in DB and update cache utility accordingly
     * Save signature image (entityContent object) in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -saved company user(appUser)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            Map preResult = (Map) obj   // cast map returned from executePreCondition method
            AppUser appUser = (AppUser) preResult.get(Tools.ENTITY)
            AppUser savedAppUser = appUserService.create(appUser)   // save new appUser object in DB

            CommonsMultipartFile imageFile = (CommonsMultipartFile) preResult.get(IMAGE_FILE)
            if (imageFile && (!imageFile.isEmpty())) {
                EntityContent signatureImage = (EntityContent) preResult.get(SIGNATURE_IMAGE)
                signatureImage.entityId = savedAppUser.id   //set appUserId as entityId
                entityContentService.create(signatureImage) // save signature image (entityContent object) in DB
            }
            // add new appUser object in cache utility and keep the data sorted
            appUserCacheUtility.add(savedAppUser, appUserCacheUtility.SORT_ON_NAME, appUserCacheUtility.SORT_ORDER_ASCENDING)
            return savedAppUser
        } catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException(APP_USER_SAVE_FAILURE_MESSAGE)
            return null
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show newly created appUser object in grid
     * Show success message
     * @param obj -object of appUser
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            AppUser appUser = (AppUser) obj // appUser returned from execute method
            Employee employee = (Employee) employeeCacheUtility.read(appUser.employeeId)
            GridEntity gridObject = new GridEntity()    // build grid object
            gridObject.id = appUser.id
            gridObject.cell = [
                    Tools.LABEL_NEW,
                    appUser.id,
                    appUser.username,
                    appUser.loginId,
                    appUser.enabled ? Tools.YES : Tools.NO,
                    appUser.accountLocked ? Tools.YES : Tools.NO,
                    appUser.accountExpired ? Tools.YES : Tools.NO,
                    employee ? employee.fullName : Tools.EMPTY_SPACE
            ]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.GRID_ENTITY, gridObject)
            result.put(Tools.VERSION, appUser.version)
            result.put(Tools.MESSAGE, APP_USER_SAVE_SUCCESS_MESSAGE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.GRID_ENTITY, null)
            result.put(Tools.MESSAGE, APP_USER_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map previousResult = (Map) obj  // cast map returned from previous method
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, APP_USER_SAVE_FAILURE_MESSAGE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APP_USER_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build EntityContent object with signature image file
     * @param signImage -signature image in byte form
     * @param contentCategory -object of ContentCategory
     * @param imageFile -signature image file
     * @return -EntityContent object
     */
    private EntityContent buildEntityImageObject(byte[] signImage, ContentCategory contentCategory, CommonsMultipartFile imageFile) {

        EntityContent entityContent = new EntityContent()
        AppUser appUser = appSessionUtil.getAppUser()
        // pull system entity type(AppUser) object
        SystemEntity contentEntityTypeAppUser = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_APPUSER, appSessionUtil.getCompanyId())

        entityContent.contentCategoryId = contentCategory.id
        entityContent.contentTypeId = contentCategory.contentTypeId
        entityContent.entityTypeId = contentEntityTypeAppUser.id
        entityContent.entityId = 0L  // set entityContent.entityId = appUser.Id after creating new AppUser

        entityContent.content = signImage
        entityContent.caption = null
        entityContent.extension = getImageExtension(imageFile)  // get extension of image file

        entityContent.createdBy = appUser.id
        entityContent.createdOn = new Date()

        entityContent.updatedBy = 0L
        entityContent.updatedOn = null

        entityContent.companyId = appUser.companyId

        return entityContent
    }

    // Validating uploaded image type, extension, size etc.
    private String validateContent(CommonsMultipartFile contentFile, ContentCategory contentCategory) {
        long companyId = appSessionUtil.getCompanyId()
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
        BufferedImage scaledImg = Scalr.resize(tempImg, Scalr.Mode.FIT_EXACT, contentCategory.width, contentCategory.height)
        // change the image size
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
