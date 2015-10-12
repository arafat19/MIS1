package com.athena.mis.application.actions.appcompanyuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.*
import com.athena.mis.application.utility.*
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.integration.application.ApplicationBootStrapService
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.fixedasset.FixedAssetPluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.integration.qsmeasurement.QsMeasurementPluginConnector
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
 *  Create new company user(appUser) object and show in grid
 *  For details go through Use-Case doc named 'CreateAppCompanyUserActionService'
 */
class CreateAppCompanyUserActionService extends BaseService implements ActionIntf {

    RoleFeatureMappingService roleFeatureMappingService
    EntityContentService entityContentService
    AppUserService appUserService
    RoleService roleService
    ReservedSystemEntityService reservedSystemEntityService
    SystemEntityService systemEntityService
    ApplicationBootStrapService applicationBootStrapService
    ThemeService themeService
    ContentCategoryService contentCategoryService
    AppMailService appMailService
    SysConfigurationService sysConfigurationService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired(required = false)
    QsMeasurementPluginConnector qsMeasurementImplService
    @Autowired(required = false)
    FixedAssetPluginConnector fixedAssetImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility

    private static final String APP_USER_SAVE_SUCCESS_MESSAGE = "Company user has been saved successfully"
    private static final String APP_USER_SAVE_FAILURE_MESSAGE = "Company user could not be saved"
    private static final String IMAGE_FILE = "imageFile"
    private static final String SIGNATURE_IMAGE = "signatureImage"
    private static final String HAS_USER = "The selected company already has a company user"
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
     * Check pre conditions before creating company user
     *      1. check if user has access to create company user or not
     *      2. check if the company already has a company user or not
     *      3. get appUser object and validate the object
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
            if (appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            } else {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            GrailsParameterMap parameters = (GrailsParameterMap) params
            // check if the company already has a company user or not
            long companyId = Long.parseLong(parameters.companyId.toString())
            int companyUser = appUserService.countByCompanyId(companyId)
            if (companyUser > 0) {
                result.put(Tools.MESSAGE, HAS_USER)
                return result
            }
            // get appUser object
            AppUser appUser = (AppUser) obj
            appUser.isCompanyUser = true    // hard coded true for this use-case only
            appUser.isPowerUser = true  // hard coded true for this use-case only
            appUser.isConfigManager = true  // hard coded true for this use-case only
            appUser.createdOn = new Date()
            appUser.createdBy = appSessionUtil.getAppUser().id
            appUser.updatedOn = null
            appUser.updatedBy = 0
            appUser.validate()  // validate appUser object
            if (appUser.hasErrors()) {
                return result
            }
            // check if user has signature image or not
            CommonsMultipartFile imageFile = parameters.signatureImage ? parameters.signatureImage : null
            // if user has signature image then validate image file
            if (imageFile && (!imageFile.isEmpty())) {
                appUser.hasSignature = true
                // get ContentCategory object for signature of AppUser
                ContentCategory contentCategorySignature = (ContentCategory) contentCategoryCacheUtility.readBySystemContentCategory(contentCategoryCacheUtility.IMAGE_TYPE_SIGNATURE)
                ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(contentCategorySignature.id)
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
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * Save company user(appUser) object in DB
     * Save signature image (entityContent object) in DB
     * Check if company user already has default roles or not, if not then
     *      1. assign default roles for the created company user
     *      2. append default roles in request map
     * Assign admin role for the created company user
     * Create reserved system entity for the company
     * Create default theme for the company
     * Create default mail objects for the company
     * Create default system configuration
     * If account plugin is installed then-
     *      -create default account type (Asset, Liabilities, Income, Expense)
     *      -create default acc group
     * Init all cache utility
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
            AppUser savedAppUser = appUserService.create(appUser)   // save new company user(appUser) object in DB

            CommonsMultipartFile imageFile = (CommonsMultipartFile) preResult.get(IMAGE_FILE)
            if (imageFile && (!imageFile.isEmpty())) {
                EntityContent signatureImage = (EntityContent) preResult.get(SIGNATURE_IMAGE)
                signatureImage.entityId = savedAppUser.id   // set appUserId as entityId
                entityContentService.create(signatureImage) // save signature image (entityContent object) in DB
            }
            // check if company user already has default roles or not
            Role role = roleService.findByCompanyIdAndRoleTypeId(savedAppUser.companyId, roleTypeCacheUtility.ROLE_TYPE_ADMIN)
            if (!role) {
                // default role assign for the created company user
                List<Role> lstRole = assignDefaultRoleForCompanyUser(savedAppUser.companyId)
                // append default roles in request map
                appendRoleInRequestMap(lstRole)
            }
            // admin role assign for the created company user
            assignAdminRoleForCompanyUser(savedAppUser)
            createReservedSystemEntity(savedAppUser.companyId)  // create reserved system entity for the company
            themeService.createDefaultData(savedAppUser.companyId)  // create default theme for the company
            createDefaultMailObjects(savedAppUser.companyId)  // create default mail objects for the company
            contentCategoryService.createDefaultData(savedAppUser.companyId)    // create default systemContentCategory(Photo, Signature) for the company
            createSystemConfiguration(savedAppUser.companyId)   // create default system configuration
            if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
                accountingImplService?.createDefaultData(savedAppUser.companyId)    // create default account type
                accountingImplService?.createDefaultDataForAccGroup(savedAppUser.companyId) // create default acc group
            }
            initAllCacheUtility()    // init all cache utility
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
     * Show newly created company user(appUser) object in grid
     * Show success message
     * @param obj -object of appUser
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            AppUser appUser = (AppUser) obj // company user(appUser) returned from execute method
            Company company = (Company) companyCacheUtility.read(appUser.companyId)
            GridEntity gridObject = new GridEntity()    //build grid object
            gridObject.id = appUser.id
            gridObject.cell = [
                    Tools.LABEL_NEW,
                    appUser.id,
                    appUser.username,
                    appUser.loginId,
                    appUser.enabled ? Tools.YES : Tools.NO,
                    appUser.accountLocked ? Tools.YES : Tools.NO,
                    appUser.accountExpired ? Tools.YES : Tools.NO,
                    company.name
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
        // pull system entity type(AppUser) object
        long companyId = appSessionUtil.getCompanyId()
        SystemEntity contentEntityTypeAppUser = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_APPUSER, companyId)

        EntityContent entityContent = new EntityContent()
        AppUser appUser = appSessionUtil.getAppUser()

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

    private static final String QUERY_SELECT_NEXTVAL_SEQUENCE = "SELECT NEXTVAL('role_id_seq') as id"

    /**
     * Get id from dedicated role id sequence to create role
     * @return - a long variable containing the value of id
     */
    private long getRoleId() {
        List results = executeSelectSql(QUERY_SELECT_NEXTVAL_SEQUENCE)
        long roleId = results[0].id
        return roleId
    }

    /**
     * Assign default roles for the created company user
     * @param companyId -id of company
     * @return -a list of newly created roles
     */
    private List assignDefaultRoleForCompanyUser(long companyId) {
        List<RoleType> lstRoleType = roleTypeCacheUtility.list()    // get list of default role types
        List lstRole = []
        for (int i = 0; i < lstRoleType.size(); i++) {
            RoleType roleType = lstRoleType[i]
            Role role = new Role()
            role.id = getRoleId()
            role.authority = roleType.authority + Tools.UNDERSCORE + companyId.toString()   // ex: ROLE_-3_1
            role.version = 0
            role.name = roleType.name
            role.companyId = companyId
            role.roleTypeId = roleType.id

            roleService.create(role)    // save new role object in DB
            lstRole << role
        }
        return lstRole
    }

    private static final String INSERT_QUERY = """
        INSERT INTO user_role(role_id,user_id)
        VALUES(:roleId,:userId)
    """

    /**
     * Assign admin role for the created company user
     * @param appUser -object of AppUser
     * @return -boolean value true
     */
    private boolean assignAdminRoleForCompanyUser(AppUser appUser) {
        Role role = roleService.findByCompanyIdAndRoleTypeId(appUser.companyId, roleTypeCacheUtility.ROLE_TYPE_ADMIN)
        Map queryParams = [
                userId: appUser.id,
                roleId: role.id
        ]
        executeInsertSql(INSERT_QUERY, queryParams)
        return true
    }

    private static final String APPEND_ROLE_IN_REQUEST_MAP = """
        UPDATE request_map
        SET
        config_attribute = config_attribute || :roleAuthority
        WHERE
        transaction_code = :transactionCode;
    """

    /**
     * Append default roles in common features of request map
     * Append default roles in request map according to role feature mapping
     * @param lstRole -list of default roles
     * @return -boolean value true
     */
    private boolean appendRoleInRequestMap(List<Role> lstRole) {
        for (int i = 0; i < lstRole.size(); i++) {
            Role role = lstRole[i]
            createApplicationRequestMap(role.authority) // append default role in common features of request map

            List<RoleFeatureMapping> lstRoleFeatureMapping
            RoleFeatureMapping roleFeatureMapping

            lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.roleTypeId, PluginConnector.APPLICATION_ID)
            if (lstRoleFeatureMapping.size() > 0) {
                for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                    roleFeatureMapping = lstRoleFeatureMapping[j]
                    String roleAuthority = ',' + role.authority
                    Map queryParams = [roleAuthority: roleAuthority, transactionCode: roleFeatureMapping.transactionCode]
                    executeUpdateSql(APPEND_ROLE_IN_REQUEST_MAP, queryParams)
                }
            }
            if (PluginConnector.isPluginInstalled(PluginConnector.BUDGET)) {
                lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.roleTypeId, PluginConnector.BUDGET_ID)
                if (lstRoleFeatureMapping.size() > 0) {
                    for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                        roleFeatureMapping = lstRoleFeatureMapping[j]
                        String roleAuthority = ',' + role.authority
                        Map queryParams = [roleAuthority: roleAuthority, transactionCode: roleFeatureMapping.transactionCode]
                        executeUpdateSql(APPEND_ROLE_IN_REQUEST_MAP, queryParams)
                    }
                }
            }
            if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
                lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.roleTypeId, PluginConnector.ACCOUNTING_ID)
                if (lstRoleFeatureMapping.size() > 0) {
                    for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                        roleFeatureMapping = lstRoleFeatureMapping[j]
                        String roleAuthority = ',' + role.authority
                        Map queryParams = [roleAuthority: roleAuthority, transactionCode: roleFeatureMapping.transactionCode]
                        executeUpdateSql(APPEND_ROLE_IN_REQUEST_MAP, queryParams)
                    }
                }
            }
            if (PluginConnector.isPluginInstalled(PluginConnector.FIXED_ASSET)) {
                lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.roleTypeId, PluginConnector.FIXED_ASSET_ID)
                if (lstRoleFeatureMapping.size() > 0) {
                    for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                        roleFeatureMapping = lstRoleFeatureMapping[j]
                        String roleAuthority = ',' + role.authority
                        Map queryParams = [roleAuthority: roleAuthority, transactionCode: roleFeatureMapping.transactionCode]
                        executeUpdateSql(APPEND_ROLE_IN_REQUEST_MAP, queryParams)
                    }
                }
            }
            if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
                lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.roleTypeId, PluginConnector.INVENTORY_ID)
                if (lstRoleFeatureMapping.size() > 0) {
                    for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                        roleFeatureMapping = lstRoleFeatureMapping[j]
                        String roleAuthority = ',' + role.authority
                        Map queryParams = [roleAuthority: roleAuthority, transactionCode: roleFeatureMapping.transactionCode]
                        executeUpdateSql(APPEND_ROLE_IN_REQUEST_MAP, queryParams)
                    }
                }
            }
            if (PluginConnector.isPluginInstalled(PluginConnector.PROCUREMENT)) {
                lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.roleTypeId, PluginConnector.PROCUREMENT_ID)
                if (lstRoleFeatureMapping.size() > 0) {
                    for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                        roleFeatureMapping = lstRoleFeatureMapping[j]
                        String roleAuthority = ',' + role.authority
                        Map queryParams = [roleAuthority: roleAuthority, transactionCode: roleFeatureMapping.transactionCode]
                        executeUpdateSql(APPEND_ROLE_IN_REQUEST_MAP, queryParams)
                    }
                }
            }
            if (PluginConnector.isPluginInstalled(PluginConnector.QS)) {
                lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.roleTypeId, PluginConnector.QS_ID)
                if (lstRoleFeatureMapping.size() > 0) {
                    for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                        roleFeatureMapping = lstRoleFeatureMapping[j]
                        String roleAuthority = ',' + role.authority
                        Map queryParams = [roleAuthority: roleAuthority, transactionCode: roleFeatureMapping.transactionCode]
                        executeUpdateSql(APPEND_ROLE_IN_REQUEST_MAP, queryParams)
                    }
                }
            }
            if (PluginConnector.isPluginInstalled(PluginConnector.EXCHANGE_HOUSE)) {
                lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.roleTypeId, PluginConnector.EXCHANGE_HOUSE_ID)
                if (lstRoleFeatureMapping.size() > 0) {
                    for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                        roleFeatureMapping = lstRoleFeatureMapping[j]
                        String roleAuthority = ',' + role.authority
                        Map queryParams = [roleAuthority: roleAuthority, transactionCode: roleFeatureMapping.transactionCode]
                        executeUpdateSql(APPEND_ROLE_IN_REQUEST_MAP, queryParams)
                    }
                }
            }
        }
        return true
    }

    private static final String APPEND_ROLE_IN_COMMON_FEATURES = """
        UPDATE request_map
        SET
        config_attribute = config_attribute || :roleAuthority
        WHERE
        is_common = true;
    """

    /**
     * Append default roles in common features of request map (root, logout, manage password, & change password)
     * @param roleAuthority -authority of role
     * @return -boolean value true
     */
    private boolean createApplicationRequestMap(String roleAuthority) {
        roleAuthority = ',' + roleAuthority
        Map queryParams = [roleAuthority: roleAuthority]
        executeUpdateSql(APPEND_ROLE_IN_COMMON_FEATURES, queryParams)
        return true
    }

    /**
     * Create reserved system entity for the company
     * @param companyId -id of company
     * @return -a boolean value
     */
    private boolean createReservedSystemEntity(long companyId) {
        List<ReservedSystemEntity> lstReservedSystemEntity
        ReservedSystemEntity reservedSystemEntity
        SystemEntity systemEntity

        lstReservedSystemEntity = reservedSystemEntityService.findAllByPluginId(PluginConnector.APPLICATION_ID)
        if (lstReservedSystemEntity.size() > 0) {
            for (int i = 0; i < lstReservedSystemEntity.size(); i++) {
                reservedSystemEntity = lstReservedSystemEntity[i]
                systemEntity = buildSystemEntityObject(reservedSystemEntity, companyId)
                systemEntityService.create(systemEntity)    // save SystemEntity object in DB
            }
        }
        if (PluginConnector.isPluginInstalled(PluginConnector.BUDGET)) {
            lstReservedSystemEntity = reservedSystemEntityService.findAllByPluginId(PluginConnector.BUDGET_ID)
            if (lstReservedSystemEntity.size() > 0) {
                for (int i = 0; i < lstReservedSystemEntity.size(); i++) {
                    reservedSystemEntity = lstReservedSystemEntity[i]
                    systemEntity = buildSystemEntityObject(reservedSystemEntity, companyId)
                    systemEntityService.create(systemEntity)    // save SystemEntity object in DB
                }
            }
        }
        if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
            lstReservedSystemEntity = reservedSystemEntityService.findAllByPluginId(PluginConnector.ACCOUNTING_ID)
            if (lstReservedSystemEntity.size() > 0) {
                for (int i = 0; i < lstReservedSystemEntity.size(); i++) {
                    reservedSystemEntity = lstReservedSystemEntity[i]
                    systemEntity = buildSystemEntityObject(reservedSystemEntity, companyId)
                    systemEntityService.create(systemEntity)    // save SystemEntity object in DB
                }
            }
        }
        if (PluginConnector.isPluginInstalled(PluginConnector.FIXED_ASSET)) {
            lstReservedSystemEntity = reservedSystemEntityService.findAllByPluginId(PluginConnector.FIXED_ASSET_ID)
            if (lstReservedSystemEntity.size() > 0) {
                for (int i = 0; i < lstReservedSystemEntity.size(); i++) {
                    reservedSystemEntity = lstReservedSystemEntity[i]
                    systemEntity = buildSystemEntityObject(reservedSystemEntity, companyId)
                    systemEntityService.create(systemEntity)    // save SystemEntity object in DB
                }
            }
        }
        if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
            lstReservedSystemEntity = reservedSystemEntityService.findAllByPluginId(PluginConnector.INVENTORY_ID)
            if (lstReservedSystemEntity.size() > 0) {
                for (int i = 0; i < lstReservedSystemEntity.size(); i++) {
                    reservedSystemEntity = lstReservedSystemEntity[i]
                    systemEntity = buildSystemEntityObject(reservedSystemEntity, companyId)
                    systemEntityService.create(systemEntity)    // save SystemEntity object in DB
                }
            }
        }
        if (PluginConnector.isPluginInstalled(PluginConnector.PROCUREMENT)) {
            lstReservedSystemEntity = reservedSystemEntityService.findAllByPluginId(PluginConnector.PROCUREMENT_ID)
            if (lstReservedSystemEntity.size() > 0) {
                for (int i = 0; i < lstReservedSystemEntity.size(); i++) {
                    reservedSystemEntity = lstReservedSystemEntity[i]
                    systemEntity = buildSystemEntityObject(reservedSystemEntity, companyId)
                    systemEntityService.create(systemEntity)    // save SystemEntity object in DB
                }
            }
        }
        if (PluginConnector.isPluginInstalled(PluginConnector.QS)) {
            lstReservedSystemEntity = reservedSystemEntityService.findAllByPluginId(PluginConnector.QS_ID)
            if (lstReservedSystemEntity.size() > 0) {
                for (int i = 0; i < lstReservedSystemEntity.size(); i++) {
                    reservedSystemEntity = lstReservedSystemEntity[i]
                    systemEntity = buildSystemEntityObject(reservedSystemEntity, companyId)
                    systemEntityService.create(systemEntity)    // save SystemEntity object in DB
                }
            }
        }
        if (PluginConnector.isPluginInstalled(PluginConnector.EXCHANGE_HOUSE)) {
            lstReservedSystemEntity = reservedSystemEntityService.findAllByPluginId(PluginConnector.EXCHANGE_HOUSE_ID)
            if (lstReservedSystemEntity.size() > 0) {
                for (int i = 0; i < lstReservedSystemEntity.size(); i++) {
                    reservedSystemEntity = lstReservedSystemEntity[i]
                    systemEntity = buildSystemEntityObject(reservedSystemEntity, companyId)
                    systemEntityService.create(systemEntity)    // save SystemEntity object in DB
                }
            }
        }
        return true
    }

    private static final String SELECT_NEXT_VAL_ID_SEQ = "SELECT NEXTVAL('system_entity_id_seq') as id"

    /**
     * Get id from dedicated systemEntity id sequence
     * @return - a long variable containing the value of id
     */
    public long getSystemEntityId() {
        List results = executeSelectSql(SELECT_NEXT_VAL_ID_SEQ)
        long systemEntityId = results[0].id
        return systemEntityId
    }

    /**
     * Build system entity object
     * @param reservedSystemEntity -object of ReservedSystemEntity
     * @param companyId -id of company
     * @return -a boolean value
     */
    private SystemEntity buildSystemEntityObject(ReservedSystemEntity reservedSystemEntity, long companyId) {
        SystemEntity systemEntity = new SystemEntity()
        String strCompanyId = companyId
        String strPluginId = reservedSystemEntity.pluginId
        String strNextId = getSystemEntityId()
        String strId = strCompanyId + strPluginId + strNextId
        long systemEntityId = Long.parseLong(strId)
        systemEntity.id = systemEntityId
        systemEntity.version = 0
        systemEntity.key = reservedSystemEntity.key
        systemEntity.value = reservedSystemEntity.value
        systemEntity.type = reservedSystemEntity.type
        systemEntity.isActive = true
        systemEntity.companyId = companyId
        systemEntity.reservedId = reservedSystemEntity.id
        systemEntity.pluginId = reservedSystemEntity.pluginId
        return systemEntity
    }

    /**
     * Create default mail objects for the company
     * @param companyId -id of company
     */
    private void createDefaultMailObjects(long companyId) {
        appMailService.createDefaultDataForApplication(companyId)

        if (PluginConnector.isPluginInstalled(PluginConnector.PROCUREMENT)) {
            appMailService.createDefaultDataForProcurement(companyId)
        }
        if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
            appMailService.createDefaultDataForInventory(companyId)
        }
        if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
            appMailService.createDefaultDataForAccounting(companyId)
        }
    }

    /**
     * Create default system configuration
     * @param companyId -id of company
     */
    private void createSystemConfiguration(long companyId) {
        sysConfigurationService.createDefaultAppSysConfig(companyId)

        if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
            sysConfigurationService.createDefaultAccSysConfig(companyId)
        }
        if (PluginConnector.isPluginInstalled(PluginConnector.EXCHANGE_HOUSE)) {
            sysConfigurationService.createDefaultDataForExh(companyId)
        }
    }

    /**
     * Init all cache utility
     * @return - a boolean value (true)
     */
    private boolean initAllCacheUtility() {
        applicationBootStrapService.init()
        if (budgetImplService) budgetImplService.bootStrap(false, false)
        if (procurementImplService) procurementImplService.bootStrap(false, false)
        if (inventoryImplService) inventoryImplService.bootStrap(false, false)
        if (accountingImplService) accountingImplService.bootStrap(false, false)
        if (qsMeasurementImplService) qsMeasurementImplService.bootStrap(false, false)
        if (fixedAssetImplService) fixedAssetImplService.bootStrap(false, false)
        if (exchangeHouseImplService) exchangeHouseImplService.bootStrap(false, false)
        return true
    }
}
