package com.athena.mis.projecttrack.actions.ptbug

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
 *  Create orphan bug object and show newly created object on grid
 *  For details go through Use-Case doc named 'CreateOrphanBugActionService'
 */
class CreateOrphanBugActionService extends BaseService implements ActionIntf {

    PtBugService ptBugService
    EntityContentService entityContentService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtBugSeverityCacheUtility ptBugSeverityCacheUtility
    @Autowired
    PtBugTypeCacheUtility ptBugTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String BUG_OBJ = "ptBug"
    private static final String BUG_CREATE_FAILURE_MSG = "Bug has not been saved"
    private static final String BUG_CREATE_SUCCESS_MSG = "Bug has been successfully saved"
    private static final String ENTITY_CONTENT = "entityContent"
    private static final String IMAGE_FILE = "imageFile"
    //constants for image validations
    public static final String UNRECOGNIZED_IMAGE = "Unrecognized image file"
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
     * Get parameters from UI and build PtBug object
     * 1. check contentObj(attachment) for bug
     * 2. if bug has a image file(attachment) than create entity content for that bug
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            PtBug bug = buildBugObject(parameterMap)    // build ptBug object
            // create & validate bug attachment(if given)
            CommonsMultipartFile imageFile = parameterMap.contentObj ? parameterMap.contentObj : null
            if (imageFile && !imageFile.isEmpty()) {
                bug.hasAttachment = true
                ContentCategory imageTypePhoto = (ContentCategory) contentCategoryCacheUtility.readBySystemContentCategory(contentCategoryCacheUtility.IMAGE_TYPE_SCREEN_SHOT)
                ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(imageTypePhoto.id)

                // Validate Bug image(e.g: check if image extension is valid or not)
                String validateImageMsg = validateContent(imageFile, contentCategory.id)
                if (validateImageMsg) {
                    result.put(Tools.MESSAGE, validateImageMsg)
                    return result
                }

                // resize image and convert to bytes
                byte[] byteImage = resizeImage(imageFile, contentCategory)

                //build entityContent object to create
                EntityContent newEntityContent = buildEntityContentObject(byteImage, contentCategory, imageFile)
                result.put(ENTITY_CONTENT, newEntityContent)
            }
            result.put(IMAGE_FILE, imageFile)
            result.put(BUG_OBJ, bug)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_CREATE_FAILURE_MSG)
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
     * 1. Save ptBug object in DB
     * 2. Create entity content if bug has image file.
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
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from executePreCondition method
            PtBug bug = (PtBug) preResult.get(BUG_OBJ)
            CommonsMultipartFile imageFile = (CommonsMultipartFile) preResult.get(IMAGE_FILE)
            PtBug savedBugObj = ptBugService.create(bug)    // save new ptBug object in DB
            if (imageFile && !imageFile.isEmpty()) {
                EntityContent entityContent = (EntityContent) preResult.get(ENTITY_CONTENT)
                entityContent.entityId = savedBugObj.id
                entityContentService.create(entityContent) // save attachment to entity content
            }
            result.put(BUG_OBJ, savedBugObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ex.message)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build grid object to show a single row (newly created object) in grid
     * 1. Get severity key by id(severity)
     * 2. Get type key by id(type)
     * 3. Get appUser by id
     * 4. build success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj               // cast map returned from execute method
            PtBug bug = (PtBug) executeResult.get(BUG_OBJ)
            SystemEntity status = (SystemEntity) ptBugStatusCacheUtility.read(bug.status)
            SystemEntity severity = (SystemEntity) ptBugSeverityCacheUtility.read(bug.severity)
            SystemEntity type = (SystemEntity) ptBugTypeCacheUtility.read(bug.type)
            GridEntity object = new GridEntity()    // build grid object
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
                    ptSessionUtil.appSessionUtil.getAppUser().username,
                    bug.hasAttachment ? Tools.YES : Tools.NO
            ]
            result.put(Tools.MESSAGE, BUG_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_CREATE_FAILURE_MSG)
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
                LinkedHashMap preResult = (LinkedHashMap) obj               // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, BUG_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build new PtBug object
     * @param parameterMap -serialized parameters from UI
     * @return -new PtBug object
     */
    private PtBug buildBugObject(GrailsParameterMap parameterMap) {
        PtBug newBug = new PtBug(parameterMap)
        newBug.companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        newBug.createdOn = new Date()
        newBug.createdBy = ptSessionUtil.appSessionUtil.getAppUser().id
        return newBug
    }

    /**
     * build entityContent object to save in DB
     * @param parameterMap -GrailsParameterMap
     * @param contentFile -CommonsMultiPartFile
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
    public String validateContent(CommonsMultipartFile contentFile, long contentCategoryId) {
        String returnMsg = null
        long contentFileSize = contentFile.size.longValue()
        String uploadedFileExtension = null
        //Get the extension of the uploaded file
        ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(contentCategoryId)

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
