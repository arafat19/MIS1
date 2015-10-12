package com.athena.mis.projecttrack.actions.ptbug

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.entity.EntityContent
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.*
import com.athena.mis.projecttrack.entity.PtBug
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.projecttrack.utility.PtBugSeverityCacheUtility
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtBugTypeCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.imgscalr.Scalr
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 *  Update ptBug object  & entity content for bug
 *  For details go through Use-Case doc named 'UpdatePtBugActionService'
 */
class UpdatePtBugActionService extends BaseService implements ActionIntf {

    PtBugService ptBugService
    EntityContentService entityContentService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility
    @Autowired
    PtBugSeverityCacheUtility ptBugSeverityCacheUtility
    @Autowired
    PtBugTypeCacheUtility ptBugTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String NOT_AUTHORIZED = "Not Authorized to edit."
    private static final String OBJ_NOT_FOUND = "Selected bug not found"
    private static final String BUG_OBJ = "ptBug"
    private static final String BUG_UPDATE_FAILURE_MESSAGE = "Bug could not be updated"
    private static final String BUG_UPDATE_SUCCESS_MESSAGE = "Bug has been updated successfully"
    private static final String EXISTING_IMAGE = "ExistingImage"
    private static final String IMAGE_FILE = "imageFile"
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
     * Get parameters from UI and build ptBug object for update
     * 1. Check validity for input
     * 2. Check existence of PtBug object
     * 3. Build ptBug object with new parameters
     * 4. check contentObj(attachment) for bug
     * 5. if bug already has a image file(attachment) than update entity content,
     *    otherwise create entity content for that bug.
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long bugId = Long.parseLong(parameterMap.id.toString())
            PtBug oldBug = ptBugService.read(bugId)                         // Get ptBug object from DB
            if (!oldBug) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            long userId = ptSessionUtil.appSessionUtil.getAppUser().id
            boolean isAdmin = ptSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_SOFTWARE_PROJECT_ADMIN)
            if (userId != oldBug.createdBy && !isAdmin) {
                result.put(Tools.MESSAGE, NOT_AUTHORIZED)
                return result
            }

            long oldStatus = oldBug.status
            PtBug bug = buildBugObject(parameterMap, oldBug)              // Build ptBug object
            if (bug.status != oldStatus) {
                bug.statusUpdatedBy = bug.updatedBy
                bug.statusUpdatedOn = new Date()
            }

            // validate company logo(if given)
            CommonsMultipartFile imageFile = parameterMap.contentObj ? parameterMap.contentObj : null
            long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
            // pull system entity type(Bug) object
            SystemEntity contentEntityTypeBug = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_PT_BUG, companyId)

            if (imageFile && !imageFile.isEmpty()) {
                bug.hasAttachment = true
                ContentCategory imageTypePhoto = (ContentCategory) contentCategoryCacheUtility.readBySystemContentCategory(contentCategoryCacheUtility.IMAGE_TYPE_SCREEN_SHOT)
                ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(imageTypePhoto.id)
                long entityTypeId = contentEntityTypeBug.id
                EntityContent existingBugImage = entityContentService.findByEntityTypeIdAndEntityIdAndContentTypeId(entityTypeId, bugId, contentCategory.contentTypeId)
                result.put(EXISTING_IMAGE, existingBugImage)
                // Validate Bug image(e.g: check if image extension is valid or not)
                String validateImageMsg = validateContent(imageFile, contentCategory)
                if (validateImageMsg) {
                    result.put(Tools.MESSAGE, validateImageMsg)
                    return result
                }

                // resize image and convert to bytes
                byte[] byteImage = resizeImage(imageFile, contentCategory)

                if (!existingBugImage) { //if image of bug(entityContent) not found then create
                    // Build EntityContent object
                    EntityContent newEntityContent = buildEntityContentObject(byteImage, contentCategory, imageFile)
                    result.put(ENTITY_CONTENT, newEntityContent)
                } else {
                    // Update EntityContent object
                    EntityContent updateContentImage = buildBugImageObjectForUpdate(byteImage, imageFile, existingBugImage)
                    result.put(ENTITY_CONTENT, updateContentImage)
                }
            }

            result.put(BUG_OBJ, bug)
            result.put(IMAGE_FILE, imageFile)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * 1. Update ptBug object in DB
     * 2. Create/Update entity content
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            // cast map returned from executePreCondition method
            CommonsMultipartFile imageFile = (CommonsMultipartFile) preResult.get(IMAGE_FILE)
            PtBug bug = (PtBug) preResult.get(BUG_OBJ)
            ptBugService.update(bug)                                  // update new ptBug object in DB

            if (imageFile && !imageFile.isEmpty()) {
                EntityContent entityContent = (EntityContent) preResult.get(ENTITY_CONTENT)
                EntityContent existingBugImage = (EntityContent) preResult.get(EXISTING_IMAGE)
                entityContent.entityId = bug.id  // set bug id as entity id in entity content
                if (!existingBugImage) {
                    entityContentService.create(entityContent)
                } else {
                    entityContentService.updateWithContent(entityContent)
                }
            }

            result.put(BUG_OBJ, bug)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build grid object to show a single row (updated object) in grid
     * 1. Get status key by id(status)
     * 2. Get severity key by id(severity)
     * 3. Get type key by id(type)
     * 4. Get appUser by id
     * 5. build success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj                   // cast map returned from execute method
            PtBug bug = (PtBug) executeResult.get(BUG_OBJ)
            long statusId = bug.status
            long severityId = bug.severity
            long typeId = bug.type
            long createdById = bug.createdBy
            SystemEntity status = (SystemEntity) ptBugStatusCacheUtility.read(statusId)
            // Pull SystemEntity object by id for status
            SystemEntity severity = (SystemEntity) ptBugSeverityCacheUtility.read(severityId)
            // Pull SystemEntity object by id for severity
            SystemEntity type = (SystemEntity) ptBugTypeCacheUtility.read(typeId)
            // Pull SystemEntity object by id for type
            AppUser appUser = (AppUser) appUserCacheUtility.read(createdById)
            GridEntity object = new GridEntity()                                // build grid object
            object.id = bug.id
            object.cell = [
                    Tools.LABEL_NEW,
                    bug.id,
                    bug.title,
                    bug.stepToReproduce,
                    status.key,
                    severity.key,
                    type.key,
                    DateUtility.getLongDateForUI(bug.createdOn),
                    appUser.username,
                    bug.hasAttachment ? Tools.YES : Tools.NO
            ]
            result.put(Tools.MESSAGE, BUG_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, bug.version.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, BUG_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Make existing/old object Up-to-date with new parameters
     * @param parameterMap -serialized parameters from UI
     * @param oldBug -old ptBug object
     * @return -updated ptBug object
     */
    private PtBug buildBugObject(GrailsParameterMap parameterMap, PtBug oldBug) {
        PtBug newBug = new PtBug(parameterMap)
        oldBug.title = newBug.title
        oldBug.stepToReproduce = newBug.stepToReproduce
        oldBug.note = newBug.note
        oldBug.status = newBug.status
        oldBug.severity = newBug.severity
        oldBug.type = newBug.type
        oldBug.updatedOn = new Date()
        oldBug.updatedBy = ptSessionUtil.appSessionUtil.getAppUser().id
        return oldBug
    }

    /**
     * build companyLogo(EntityContent) object
     * @param logoImage -byte array of companyLogo
     * @param contentCategory -ContentCategory
     * @param imageFile -CommonsMultipartFile of companyLogo
     * @param existingBugImage -EntityContent
     * @return -entityContent object
     */
    private EntityContent buildBugImageObjectForUpdate(byte[] logoImage, CommonsMultipartFile imageFile, EntityContent existingBugImage) {
        EntityContent newBugImage = new EntityContent()

        AppUser appUser = ptSessionUtil.appSessionUtil.getAppUser()

        newBugImage.id = existingBugImage.id
        newBugImage.version = existingBugImage.version
        newBugImage.contentCategoryId = existingBugImage.contentCategoryId
        newBugImage.contentTypeId = existingBugImage.contentTypeId
        newBugImage.entityTypeId = existingBugImage.entityTypeId
        newBugImage.entityId = existingBugImage.entityId
        newBugImage.companyId = existingBugImage.companyId

        newBugImage.content = logoImage
        newBugImage.caption = null
        newBugImage.fileName = imageFile.properties.originalFilename.toString()

        //Get extension of uploaded imageFile(companyLogo) and set extension
        newBugImage.extension = getImageExtension(imageFile)

        newBugImage.createdBy = existingBugImage.createdBy
        newBugImage.createdOn = existingBugImage.createdOn

        newBugImage.updatedBy = appUser.id
        newBugImage.updatedOn = new Date()

        return newBugImage
    }

    /**
     * build entityContent object to save in DB
     * @param parameterMap -GrailsParameterMap
     * @param contentFile -CommonsMultipartFile
     * @return -EntityContent object
     */
    private EntityContent buildEntityContentObject(byte[] bugImage, ContentCategory contentCategory, CommonsMultipartFile imageFile) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        // pull system entity type(Bug) object
        SystemEntity contentEntityTypeBug = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_PT_BUG, companyId)

        EntityContent entityContent = new EntityContent()
        AppUser appUser = ptSessionUtil.appSessionUtil.getAppUser()

        entityContent.contentCategoryId = contentCategory.id
        entityContent.contentTypeId = contentCategory.contentTypeId
        entityContent.entityTypeId = contentEntityTypeBug.id

        entityContent.content = bugImage
        entityContent.caption = null
        entityContent.fileName = imageFile.properties.originalFilename.toString()

        //Get extension of uploaded imageFile(companyLogo) and set extension
        entityContent.extension = getImageExtension(imageFile)

        entityContent.createdBy = appUser.id
        entityContent.createdOn = new Date()
        entityContent.updatedBy = 0L
        entityContent.updatedOn = null
        entityContent.companyId = appUser.companyId
        return entityContent
    }

    // Validating uploaded image type, extension, size etc.
    private String validateContent(CommonsMultipartFile contentFile, ContentCategory contentCategory) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity contentTypeImage = (SystemEntity) contentTypeCacheUtility.readByReservedAndCompany(contentTypeCacheUtility.CONTENT_TYPE_IMAGE_ID, companyId)
        String returnMsg = null
        long contentFileSize = contentFile.size.longValue()
        String uploadedFileExtension = null

        //Get the extension of the uploaded file
        uploadedFileExtension = getImageExtension(contentFile)
        if (!uploadedFileExtension) return UNRECOGNIZED_IMAGE

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
