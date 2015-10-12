package com.athena.mis.application.actions.company

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.CompanyService
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
 *  Class to create company CRUD and to show on grid list
 *  For details go through Use-Case doc named 'CreateCompanyActionService'
 */
class CreateCompanyActionService extends BaseService implements ActionIntf {

    CompanyService companyService
    EntityContentService entityContentService
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility

    private static final String COMPANY_SAVE_SUCCESS_MESSAGE = "Company has been saved successfully"
    private static final String COMPANY_SAVE_FAILURE_MESSAGE = "Company could not be saved"
    private static final String COMPANY_ALREADY_EXISTS = "Same company name already exists"
    private static final String CODE_ALREADY_EXISTS = "Same company code already exists"
    private static final String EMAIL_ALREADY_EXISTS = "Same company email already exists"
    private static final String LOGO_NOT_FOUND_MSG = "Please select a logo"
    private static final String COUNTRY_NOT_EXIST = "The selected country does not exist, please refresh the page"
    private static final String COMPANY = "company"
    private static final String IMAGE_FILE = "imageFile"
    private static final String COMPANY_LOGO = "companyLogo"
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

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria for creating new company
     *      1) Check access permission to create company
     *      2) Check existence of selected country
     *      3) Check duplicate company name and code
     *      4) Validate company logo
     * @param params -N/A
     * @param obj -Company object send from Controller
     * @return -a map containing company object and companyLogo for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            Company company = (Company) obj

            //Only developmentUser can create company object
            if (!appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            //validate company object
            company.validate()
            if (company.hasErrors()) {
                return result
            }

            // check existence of country
            Country country = (Country) countryCacheUtility.read(company.countryId)
            if (!country) {
                result.put(Tools.MESSAGE, COUNTRY_NOT_EXIST)
                return result
            }

            // check unique company name
            int duplicateNameCount = companyCacheUtility.countByNameIlike(company.name)
            if (duplicateNameCount > 0) {
                result.put(Tools.MESSAGE, COMPANY_ALREADY_EXISTS)
                return result
            }

            // check unique Company Code
            int duplicateCodeCount = companyCacheUtility.countByCodeIlike(company.code)
            if (duplicateCodeCount > 0) {
                result.put(Tools.MESSAGE, CODE_ALREADY_EXISTS)
                return result
            }

            // check unique Company email
            if (company.email != null) {
                int duplicateEmailCount = companyCacheUtility.countByEmailIlike(company.email)
                if (duplicateEmailCount > 0) {
                    result.put(Tools.MESSAGE, EMAIL_ALREADY_EXISTS)
                    return result
                }
            }

            // validate company logo
            GrailsParameterMap parameters = (GrailsParameterMap) params
            CommonsMultipartFile imageFile = parameters.companyLogo ? parameters.companyLogo : null
            if (!imageFile.isEmpty()) {
                ContentCategory imageTypeLogo = (ContentCategory) contentCategoryCacheUtility.readBySystemContentCategory(contentCategoryCacheUtility.IMAGE_TYPE_LOGO)
                ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(imageTypeLogo.id)
                //Validate companyLogo(e.g: check if image extension is valid or not)
                String validateImageMsg = validateContent(imageFile, contentCategory)
                if (validateImageMsg) {
                    result.put(Tools.MESSAGE, validateImageMsg)
                    return result
                }

                // resize image and convert to bytes
                byte[] byteImage = resizeImage(imageFile, contentCategory)

                // Build EntityContent object
                EntityContent companyLogo = buildEntityImageObject(byteImage, contentCategory, imageFile)
                result.put(COMPANY_LOGO, companyLogo)
            } else {//if companyLogo not found then return with message
                result.put(Tools.MESSAGE, LOGO_NOT_FOUND_MSG)
                return result
            }

            result.put(Tools.ENTITY, company)
            result.put(IMAGE_FILE, imageFile)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * Save company object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -companyObject & companyLogo from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            Map preResult = (Map) obj
            Company company = (Company) preResult.get(Tools.ENTITY)
            //Save company in DB
            Company newCompany = companyService.create(company)

            //Save company logo in entityContent domain
            CommonsMultipartFile imageFile = (CommonsMultipartFile) preResult.get(IMAGE_FILE)
            if (!imageFile.isEmpty()) {
                EntityContent companyLogo = (EntityContent) preResult.get(COMPANY_LOGO)
                companyLogo.entityId = newCompany.id //set CompanyId as entityId
                entityContentService.create(companyLogo)
            }

            //Add newly created company in cache and keep the data sorted
            companyCacheUtility.add(newCompany, companyCacheUtility.SORT_ON_NAME, companyCacheUtility.SORT_ORDER_ASCENDING)
            return newCompany
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(COMPANY_SAVE_FAILURE_MESSAGE)
            return null
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap newly created company to show on grid
     * @param obj -newly created Company object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Company company = (Company) obj
            GridEntity object = new GridEntity()
            Country country = (Country) countryCacheUtility.read(company.countryId)
            object.id = company.id
            object.cell = [
                    Tools.LABEL_NEW,
                    company.id,
                    company.name,
                    company.code,
                    company.webUrl,
                    company.email,
                    country.name
            ]
            Map resultMap = [entity: object, version: company.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, COMPANY_SAVE_SUCCESS_MESSAGE)
            result.put(COMPANY, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, COMPANY_SAVE_FAILURE_MESSAGE)
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
            Map receiveResult = (Map) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, COMPANY_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, COMPANY_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build companyLogo(EntityContent) object
     * @param logoImage -byte array of companyLogo
     * @param contentCategory -ContentCategory
     * @param imageFile -CommonsMultipartFile of companyLogo
     * @return -entityContent object
     */
    private EntityContent buildEntityImageObject(byte[] logoImage, ContentCategory contentCategory, CommonsMultipartFile imageFile) {
        long companyId = appSessionUtil.getCompanyId()
        // pull system entity type(Company) object
        SystemEntity contentEntityTypeCompany = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_COMPANY, companyId)

        EntityContent entityContent = new EntityContent()

        AppUser appUser = appSessionUtil.getAppUser()

        entityContent.contentCategoryId = contentCategory.id
        entityContent.contentTypeId = contentCategory.contentTypeId
        entityContent.entityTypeId = contentEntityTypeCompany.id
        entityContent.entityId = 0L  //Set entityContent.entityId=company.Id after creating New company

        entityContent.content = logoImage
        entityContent.caption = null
        entityContent.fileName = null

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
