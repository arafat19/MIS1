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
 *  Update appUser object and grid data
 *  For details go through Use-Case doc named 'UpdateAppUserActionService'
 */
class UpdateAppUserActionService extends BaseService implements ActionIntf {

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
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility

    private static final String APP_USER_UPDATE_SUCCESS_MESSAGE = "User has been updated successfully"
    private static final String APP_USER_UPDATE_FAILURE_MESSAGE = "User could not be updated"
    private static final String APP_USER_NOT_FOUND = "User not found or might has been changed"
    private static final String IMAGE_FILE = "imageFile"
    private static final String NEW_SIGNATURE_IMAGE = "newSignatureImage"
    private static final String EXISTING_SIGNATURE_IMAGE = "existingSignatureImage"
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
     * Check pre conditions before updating AppUser
     *      1. check if user has access to update AppUser or not
     *      2. get appUser object and check uniqueness of login id
     * Get existing AppUser from cache utility by appUser.id
     * Keep the previous signature image if not newly updated
     * If signature image is updated then
     *      1. validate and resize image file
     *      2. convert image to bytes
     *      3. if user already has signature image then update existing image
     *      4. or build new EntityContent object with image file
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
            // only development role type user can update AppUSer
            if (appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            } else {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            // get appUser object
            AppUser appUser = (AppUser) obj // validate appUser object
            // get existing appUser from cache utility by appUser.id
            AppUser existingAppUser = (AppUser) appUserCacheUtility.read(appUser.id)
            // check if appUser exists or not
            if ((!existingAppUser) || (existingAppUser.version != appUser.version)) {
                result.put(Tools.MESSAGE, APP_USER_NOT_FOUND)
                return result
            }
            // only development role type user can change these properties
            if (!appSessionUtil.getAppUser().isConfigManager) {
                appUser.isPowerUser = existingAppUser.isPowerUser
                appUser.isConfigManager = existingAppUser.isConfigManager
                appUser.isDisablePasswordExpiration = existingAppUser.isDisablePasswordExpiration
            }
            appUser.hasSignature = existingAppUser.hasSignature
            appUser.updatedBy = appSessionUtil.getAppUser().id
            appUser.updatedOn = new Date()
            AppUser sameAppUser = (AppUser) appUserCacheUtility.readByLoginIdAndIdNotEqual(appUser.loginId, appUser.id)
            if (sameAppUser) {
                result.put(Tools.MESSAGE, LOGIN_ID_EXISTS_MSG)
                return result
            }
            // keep the previous signature image if not newly updated
            // check if signature image is updated or not
            GrailsParameterMap parameters = (GrailsParameterMap) params
            CommonsMultipartFile imageFile = parameters.signatureImage ? parameters.signatureImage : null
            // if signature image is updated then validate image file
            if (imageFile && (!imageFile.isEmpty())) {
                appUser.hasSignature = true
                ContentCategory imageTypeSignature = (ContentCategory) contentCategoryCacheUtility.readBySystemContentCategory(contentCategoryCacheUtility.IMAGE_TYPE_SIGNATURE)
                ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(imageTypeSignature.id)
                String validateImageMsg = validateContent(imageFile, contentCategory)
                if (validateImageMsg) {
                    result.put(Tools.MESSAGE, validateImageMsg)
                    return result
                }
                // resize image and convert to bytes
                byte[] byteImage = resizeImage(imageFile, contentCategory)
                // pull system entity type(AppUser) object
                SystemEntity contentEntityTypeAppUser = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_APPUSER, appSessionUtil.getCompanyId())

                long contentTypeId = contentCategory.contentTypeId
                long entityTypeId = contentEntityTypeAppUser.id
                // get previous signature image of user if exists
                EntityContent existingSignImage = entityContentService.findByEntityTypeIdAndEntityIdAndContentTypeId(entityTypeId, existingAppUser.id, contentTypeId)
                if (existingSignImage) {    // update existing EntityContent (signature image) object
                    existingSignImage.content = byteImage
                    existingSignImage.extension = getImageExtension(imageFile)
                    existingSignImage.updatedBy = appSessionUtil.getAppUser().id
                    existingSignImage.updatedOn = new Date()
                    result.put(EXISTING_SIGNATURE_IMAGE, existingSignImage)
                } else {    // build new EntityContent object with image file
                    EntityContent newSignImage = buildEntityImageObject(byteImage, contentCategory, imageFile, appUser, contentEntityTypeAppUser.id)
                    result.put(NEW_SIGNATURE_IMAGE, newSignImage)
                }
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
     * Update AppUser object in DB & update cache utility accordingly
     * If signature image is updated then
     *      1. if user already has signature image then update existing image
     *      2. or create new EntityContent object in DB with image file
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -updated company user(appUser)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            Map preResult = (Map) obj   // cast map returned from executePreCondition method
            AppUser appUser = (AppUser) preResult.get(Tools.ENTITY)
            AppUser savedAppUser = appUserService.update(appUser)   // update company user(appUser) object in DB
            // check if signature image is updated or not
            CommonsMultipartFile imageFile = (CommonsMultipartFile) preResult.get(IMAGE_FILE)
            if (imageFile && (!imageFile.isEmpty())) {
                // get old signature image if exists
                EntityContent existingSignatureImage = (EntityContent) preResult.get(EXISTING_SIGNATURE_IMAGE)
                if (existingSignatureImage) {   // update existing signature image
                    entityContentService.updateWithContent(existingSignatureImage)
                } else {    // save new EntityContent (signature image) object in DB
                    EntityContent newSignatureImage = (EntityContent) preResult.get(NEW_SIGNATURE_IMAGE)
                    entityContentService.create(newSignatureImage)
                }
            }
            // update cache utility accordingly and keep the data sorted
            appUserCacheUtility.update(savedAppUser, appUserCacheUtility.SORT_ON_NAME, appUserCacheUtility.SORT_ORDER_ASCENDING)
            return savedAppUser
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(APP_USER_UPDATE_FAILURE_MESSAGE)
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
     * Show updated AppUser object in grid
     * Show success message
     * @param obj -object of appUser
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            AppUser appUser = (AppUser) obj // company user(appUser) returned from execute method
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
            result.put(Tools.MESSAGE, APP_USER_UPDATE_SUCCESS_MESSAGE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.GRID_ENTITY, null)
            result.put(Tools.MESSAGE, APP_USER_UPDATE_FAILURE_MESSAGE)
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
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, APP_USER_UPDATE_FAILURE_MESSAGE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APP_USER_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build EntityContent object with signature image file
     * @param signImage -signature image in byte form
     * @param contentCategory -object of ContentCategory
     * @param imageFile -signature image file
     * @param appUser -object of AppUser
     * @return -EntityContent object
     */
    private EntityContent buildEntityImageObject(byte[] signImage, ContentCategory contentCategory, CommonsMultipartFile imageFile,
                                                 AppUser appUser, long contentAppUserTypeId) {
        EntityContent entityContent = new EntityContent()

        entityContent.contentCategoryId = contentCategory.id
        entityContent.contentTypeId = contentCategory.contentTypeId
        entityContent.entityTypeId = contentAppUserTypeId
        entityContent.entityId = appUser.id
        entityContent.companyId = appUser.companyId

        entityContent.content = signImage
        entityContent.caption = null
        entityContent.extension = getImageExtension(imageFile)  // get extension of image file

        entityContent.createdBy = appUser.id
        entityContent.createdOn = new Date()

        entityContent.updatedBy = 0L
        entityContent.updatedOn = null

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
