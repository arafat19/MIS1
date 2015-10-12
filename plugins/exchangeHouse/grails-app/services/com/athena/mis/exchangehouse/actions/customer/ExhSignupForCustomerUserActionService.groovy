package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppSysConfigurationCacheUtility
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.*
import com.athena.mis.application.utility.*
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.entity.ExhPhotoIdType
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.service.ExhCustomerTraceService
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhUserCustomerCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.grails.plugin.jcaptcha.JcaptchaService
import org.imgscalr.Scalr
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *  Customer sign up
 *  For details go through Use-Case doc named 'ExhSignupForCustomerUserActionService'
 */
class ExhSignupForCustomerUserActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    SpringSecurityService springSecurityService
    AppUserService appUserService
    UserRoleService userRoleService
    AppUserEntityService appUserEntityService
    ExhCustomerTraceService exhCustomerTraceService
    ExhCustomerService exhCustomerService
    AppMailService appMailService
    JcaptchaService jcaptchaService
    LinkGenerator grailsLinkGenerator
    EntityContentService entityContentService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ExhUserCustomerCacheUtility exhUserCustomerCacheUtility
    @Autowired
    RoleCacheUtility roleCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility
    @Autowired
    AppSysConfigurationCacheUtility appSysConfigurationCacheUtility

    private static final String CUSTOMER_USER_SAVE_SUCCESS_MESSAGE = "Customer User has been saved successfully"
    private static final String CUSTOMER_USER_SAVE_FAILURE_MESSAGE = "Customer User could not be saved"
    private static final String SUCCESS_MAIL_CONFIRMATION = "Please check your mail."
    private static final String INVALID_INPUT = ", login id  is not available."
    private static final String CAPTCHA_INVALID_ERROR = "Invalid security code, please refresh the page."
    private static final String CAPTCHA_NOT_MATCHED_ERROR = "Security code did not match. Try again."
    private static final String COMPANY_NOT_FOUND = "Company not found"
    private static final String INVALID_PHONE_NO_MSG = "Invalid phone number"
    private static final String CONTENT_CATEGORY_NOT_FOUND = "'Photo ID' content category not found"

    private static final String APP_USER_OBJ = "appUser"
    private static final String CUSTOMER_OBJ = "customer"
    private static final String IMAGE = "image"
    private static final String EXH_CUSTOMER = "exhCustomer"
    private static final String ACTIVATION = "activation"
    private static final String USECASE_SEND_MAIL_CUSTOMER = "ExhSignupForCustomerUserActionService"

    private static final String OBJ_COMPANY = "objCompany"
    private static final String COUNTRY_lIST = "countryList"
    private static final String COMPANY_ID = "companyId"
    private static final String PHOTO_ID_TYPE_LIST = "photoIdTypeList"

    private static final String PHOTO_ID_IMAGE = "photoIdImage"
    private static final String ON = "on"
    private static final double DEFAULT_DECLARATION_AMOUNT = 10000.0d
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
     * Get params from UI and build appUser and customer object
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long companyId = Long.parseLong(parameterMap.companyId.toString())
            result.put(COMPANY_ID,companyId)

            if (!(parameterMap.captcha) || parameterMap.captcha.toString().toString().equals(Tools.EMPTY_SPACE)) {    // check captcha
                result.put(Tools.MESSAGE, CAPTCHA_INVALID_ERROR)
                return result
            }
            boolean matched = jcaptchaService.validateResponse(IMAGE, obj.id, parameterMap.captcha);
            if (!matched) {
                result.put(Tools.MESSAGE, CAPTCHA_NOT_MATCHED_ERROR)
                return result
            }


            Company company = (Company) companyCacheUtility.read(companyId)
            if (!company) {                                              // check required company
                result.put(Tools.MESSAGE, COMPANY_NOT_FOUND)
                return result
            }

            boolean isValidPhoneNo = validatePhoneNumber(parameterMap, company)      // phone number validation
            if (!isValidPhoneNo) {                                        // check required phone number
                result.put(Tools.MESSAGE, INVALID_PHONE_NO_MSG)
                return result
            }

            CommonsMultipartFile imageFile = parameterMap.photoIdImage ? parameterMap.photoIdImage : null
            if (!((parameterMap.sendByEmail) && (parameterMap.sendByEmail.toString().equals(ON)))) {
                if (imageFile && !imageFile.isEmpty()) {
                    ContentCategory contentCategory = readBySystemContentCategory(contentCategoryCacheUtility.IMAGE_TYPE_PHOTO_ID, companyId)
                    if (!contentCategory) {
                        result.put(Tools.MESSAGE, CONTENT_CATEGORY_NOT_FOUND)
                        return result
                    }
                    String validateImageMsg = validateContent(imageFile, contentCategory)      // content validate
                    if (validateImageMsg) {
                        result.put(Tools.MESSAGE, validateImageMsg)
                        return result
                    }
                    EntityContent photoIdImage = buildEntityContent(contentCategory, imageFile, parameterMap)    // build entity content ie
                    result.put(PHOTO_ID_IMAGE, photoIdImage)
                }
            }

            AppUser appUser = buildAppUser(parameterMap)         // build appUser
            AppUser duplicateAppUser = appUserCacheUtility.readByLoginId(appUser.loginId,company.id)
            if (duplicateAppUser) {                                   // check user login id duplicate
                String duplicateErrorMsg = appUser.loginId + INVALID_INPUT
                result.put(Tools.MESSAGE, duplicateErrorMsg)
                return result
            }

            ExhCustomer customer = buildCustomerObject(parameterMap)    // now buildCustomer
            if (customer.hasErrors()) {
                result.put(Tools.IS_VALID, Boolean.FALSE)
            } else {
                result.put(Tools.IS_VALID, Boolean.TRUE)
            }

            result.put(CUSTOMER_OBJ, customer)
            result.put(APP_USER_OBJ, appUser)

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_USER_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Save appUser and userRole, appUserEntity object in DB accordingly
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for executePostCondition
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj     // cast map returned from executePreCondition method

            AppUser appUser = (AppUser) preResult.get(APP_USER_OBJ)
            AppUser newAppUser = appUserService.create(appUser)        // save appUser object in DB

            // Save User Role Mapping
            Role role = (Role) roleCacheUtility.read(roleTypeCacheUtility.ROLE_TYPE_EXH_CUSTOMER,appUser.companyId)
            UserRole newUserRole = new UserRole(user: newAppUser, role: role)
            userRoleService.create(newUserRole)        // save user role in DB

            // Save Customer
            ExhCustomer customer = (ExhCustomer) preResult.get(CUSTOMER_OBJ)
            customer.userId = appUser.id
            exhCustomerService.create(customer)                 // save customer object in DB
            exhCustomerTraceService.create(customer, new Date(), Tools.ACTION_CREATE)    // save customer trace

            EntityContent photoIdImage = (EntityContent) preResult.get(PHOTO_ID_IMAGE)
            if (photoIdImage) {
                photoIdImage.entityId = customer.id             //set customerId as entityId
                photoIdImage.createdBy = customer.id            //set customerId as createdBy
                entityContentService.create(photoIdImage)        // save entity content object in DB
            }

            AppUserEntity userCustomer = new AppUserEntity()
            userCustomer.appUserId = newAppUser.id
            userCustomer.entityId = customer.id
            SystemEntity appUserSysEntityObject = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.CUSTOMER, newAppUser.companyId)
            userCustomer.entityTypeId = appUserSysEntityObject.id
            appUserEntityService.create(userCustomer,appUser.companyId)        // save new appUserEntity object in DB

            // push in the Cache and keep the data sorted
            appUserCacheUtility.add(newAppUser, appUserCacheUtility.SORT_ON_NAME, appUserCacheUtility.SORT_ORDER_ASCENDING)
            exhUserCustomerCacheUtility.add(userCustomer, exhUserCustomerCacheUtility.SORT_ON_ID, exhUserCustomerCacheUtility.SORT_ORDER_ASCENDING)

            result.put(APP_USER_OBJ, newAppUser)
            result.put(CUSTOMER_OBJ, customer)
            result.put(COMPANY_ID, appUser.companyId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, CUSTOMER_USER_SAVE_SUCCESS_MESSAGE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(CUSTOMER_USER_SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_USER_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * @param parameters -N/A
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for UI
     * map contains isError(true/false) depending on method success
     */
    public Object executePostCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap executeResult = (LinkedHashMap) obj       // cast map returned from execute method
            AppUser appUser = (AppUser) executeResult.get(APP_USER_OBJ)
            sendActivationMail(appUser)              // sent mail for activation
            result.put(APP_USER_OBJ, appUser)
            result.put(COMPANY_ID, appUser.companyId)
            result.put(Tools.MESSAGE, SUCCESS_MAIL_CONFIRMATION)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_USER_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for build Success Result For UI
     */
    public Object buildSuccessResultForUI(Object obj) {
       return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message  and country, company, photoIdType list
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj;     // cast map returned from previous method
            Long companyId = (Long)preResult.get(COMPANY_ID)
            List<Country> countryList = countryCacheUtility.list(companyId.longValue())
            List<ExhPhotoIdType> photoIdTypeList = exhPhotoIdTypeCacheUtility.list(companyId.longValue())
            Company company = (Company)companyCacheUtility.read(companyId.longValue())
            result.put(OBJ_COMPANY, company)
            result.put(COUNTRY_lIST, countryList)
            result.put(PHOTO_ID_TYPE_LIST, photoIdTypeList)
            AppUser appUser = (AppUser)preResult.get(APP_USER_OBJ)
            result.put(Tools.ENTITY, appUser)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, preResult ? preResult.get(Tools.MESSAGE) : CAPTCHA_NOT_MATCHED_ERROR)
            return result;
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_USER_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build appUser object
     * @param params -serialized parameters from UI
     * @return -new appUser object
     */
    private AppUser buildAppUser(GrailsParameterMap params) {
        long companyId = Long.parseLong(params.companyId.toString())
        AppUser appUser = new AppUser(params)
        appUser.username = params.name
        SysConfiguration sysConfig = (SysConfiguration) appSysConfigurationCacheUtility.readByKeyAndCompanyId(appSysConfigurationCacheUtility.DEFAULT_PASSWORD_EXPIRE_DURATION, companyId)
        if (sysConfig) {
            appUser.nextExpireDate = new Date() + Integer.parseInt(sysConfig.value)
        } else {
            appUser.nextExpireDate = new Date()
        }
        appUser.enabled = Boolean.FALSE
        appUser.companyId = companyId
        appUser.password = springSecurityService.encodePassword(params.password)
        appUser.activationLink = springSecurityService.encodePassword(appUser.loginId + new Date().toString())
        appUser.createdOn= new Date()
        return appUser
    }

    /**
     * Build customer object
     * @param params -serialized parameters from UI
     * @return -new customer object
     */
    private ExhCustomer buildCustomerObject(GrailsParameterMap params) {
        ExhCustomer customer = new ExhCustomer(params)
        customer.address = params.address.toString().isEmpty() ? null : params.address
        customer.phone = params.phone.toString().isEmpty() ? null : params.phone
        customer.postCode = params.postCode.toString().isEmpty() ? null : params.postCode
        customer.email = params.loginId
        customer.addressVerifiedStatus = Tools.CUSTOMER_ADDRESS_NOT_VERIFIED;
        customer.agentId = 0L
        customer.companyId = Long.parseLong(params.companyId.toString())
        customer.dateOfBirth = DateUtility.parseMaskedDate(params.customerDateOfBirth)
        customer.photoIdExpiryDate = DateUtility.parseMaskedDate(params.customerPhotoIdExpiryDate)
        customer.declarationAmount = DEFAULT_DECLARATION_AMOUNT      // hard code value : 10000
        customer.declarationStart = new Date()
        customer.declarationEnd = new Date() + 365

        if (params.visaExpireDate) {
            customer.visaExpireDate = DateUtility.parseMaskedDate(params.visaExpireDate)
        } else {
            customer.visaExpireDate = null
        }

        if (params.isCorporate.toString().equals(ON)) {
            customer.companyRegNo = params.companyRegNo
            customer.dateOfIncorporation = params.dateOfIncorporation
        } else {
            customer.companyRegNo = null
            customer.dateOfIncorporation = null
        }

        if (params.smsSubscription.toString().equals(ON)) {
            customer.smsSubscription = true
        }

        if (params.mailSubscription.toString().equals(ON)) {
            customer.mailSubscription = true
        }

        customer.isSanctionException = Boolean.FALSE
        customer.createdOn= new Date()

        return customer
    }

    /**
     * a mail sent to newly created user with activation link
     */
    private void sendActivationMail(AppUser appUser) {
        String link = grailsLinkGenerator.link(controller: EXH_CUSTOMER, action: ACTIVATION, absolute: true, params: [link: appUser.activationLink])    // activation link generate
        AppMail appMail = appMailService.findByTransactionCode(USECASE_SEND_MAIL_CUSTOMER)
        Map parameters = [email: appUser.loginId, name: appUser.username, link: link]
        appMailService.checkAndSendMailForExchangeHouse(appMail, appUser.loginId, parameters)      // sent mail for activation
    }

    /**
     * Build entityContent object
     * @param params -serialized parameters from UI
     * @param imageFile -imageFile from UI
     * @return -new entityContent object
     */
    private EntityContent buildEntityContent(ContentCategory contentCategory, CommonsMultipartFile imageFile, GrailsParameterMap params) {
        // pull system entity type(Customer) object
        SystemEntity contentEntityTypeCustomer = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_EXH_CUSTOMER, contentCategory.companyId)

        EntityContent entityContent = new EntityContent()
        entityContent.contentCategoryId = contentCategory.id
        entityContent.contentTypeId = contentCategory.contentTypeId
        entityContent.entityTypeId = contentEntityTypeCustomer.id
        entityContent.entityId = 0L

        entityContent.content = resizeImage(imageFile, contentCategory)
        entityContent.caption = PHOTO_ID_IMAGE + Tools.UNDERSCORE + params.name.toString().replace(Tools.SINGLE_SPACE, Tools.UNDERSCORE)      // set a common caption with customer name
//        entityContent.fileName = imageFile.originalFilename
        entityContent.fileName = imageFile.properties.originalFilename.toString()
        entityContent.extension = getImageExtension(imageFile)    // get image extension ie jpg, jpeg, gif

        entityContent.createdBy = 0L
        entityContent.createdOn = new Date()

        entityContent.updatedBy = 0L
        entityContent.updatedOn = null

        entityContent.companyId = contentCategory.companyId

        return entityContent
    }

    /**
     * get phone number validation
     * @param parameterMap -serialized parameters from UI
     * @param company -company from executePreCondition
     * @return -true or false
     */
    private boolean validatePhoneNumber(GrailsParameterMap parameterMap, Company company) {
        Country country = (Country) countryCacheUtility.read(company.countryId, company.id)
        Pattern pattern = Pattern.compile(country.phoneNumberPattern)
        Matcher matcher = pattern.matcher(parameterMap.phone)
        if (matcher.matches()) return true
        return false
    }

    // Validating uploaded image type, extension, size etc.
    private String validateContent(CommonsMultipartFile contentFile, ContentCategory contentCategory) {
        SystemEntity contentTypeImage = (SystemEntity) contentTypeCacheUtility.readByReservedAndCompany(contentTypeCacheUtility.CONTENT_TYPE_IMAGE_ID, contentCategory.companyId)
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

    // Following method will return a object instance from app scope for systemContentCategory
    private ContentCategory readBySystemContentCategory(String systemContentCategory, long companyId) {
        List<ContentCategory> lstAll = (List<ContentCategory>) contentCategoryCacheUtility.list(companyId)
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].systemContentCategory.equals(systemContentCategory)) {
                return lstAll[i]
            }
        }
        return null
    }

}

