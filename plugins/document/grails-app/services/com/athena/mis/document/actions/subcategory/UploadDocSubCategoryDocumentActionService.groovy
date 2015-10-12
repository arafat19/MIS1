package com.athena.mis.document.actions.subcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.multipart.commons.CommonsMultipartFile

class UploadDocSubCategoryDocumentActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String UPLOAD_SUCCESS_MESSAGE = 'Document has been successfully uploaded'
    private static final String UPLOAD_FAILURE_MESSAGE = 'Failed to upload document'
    private static final String UPLOADED_FILE_NOT_FOUND = 'Please select file for upload'
    private static final String UNSUPPORTED_EXTENSION = 'Unsupported file type'
    private static final String DOCUMENT_DIR = 'docDir'
    private static final String DOCUMENT_FILE = 'docFile'
    private static final String DESTINATION_PATH_NOT_FOUND = 'Sub category destination path not found'


    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility

    /*
    * @params parameters - serialize parameters form UI
    * @params obj - N/A
    *   1.get Supported extension from system configuration
    *   2.Check uploading document for supported extensions
    * @return - A map of containing Document file and directory or error message
    * */

    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            long companyId = docSessionUtil.appSessionUtil.getCompanyId()
            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUPPORTED_EXTENSIONS, companyId)
            if (!sysConfiguration) {
                result.put(Tools.MESSAGE, UPLOAD_FAILURE_MESSAGE)
                return result
            }

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            CommonsMultipartFile document = parameterMap.uploadDocument ? parameterMap.uploadDocument : null
            if (!document) {
                result.put(Tools.MESSAGE, UPLOADED_FILE_NOT_FOUND)
                return result
            }
            Map checkExtAndDir = checkExtAndCreateDir(document, parameterMap, sysConfiguration)
            Boolean err = checkExtAndDir.isError
            if (err.booleanValue()) {
                result.put(Tools.MESSAGE, checkExtAndDir.message)
                return result
            }
            result.put(DOCUMENT_DIR, checkExtAndDir.get(DOCUMENT_DIR))
            result.put(DOCUMENT_FILE, document)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPLOAD_FAILURE_MESSAGE)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /*
    * Upload Document to Storage
    * @params parameters - N/A
    * @params obj - a map from executePreCondition
    * @return - A map of success or error messages
    * */

    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            String documentDir = preResult.get(DOCUMENT_DIR)
            CommonsMultipartFile uploadedFile = (CommonsMultipartFile) preResult.get(DOCUMENT_FILE)
            uploadFile(uploadedFile, documentDir)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(UPLOAD_FAILURE_MESSAGE)

            result.put(Tools.MESSAGE, UPLOAD_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * @params obj - map from execute method
    * Show success message
    * @return - A map containing all necessary object for show
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()

        result.put(Tools.MESSAGE, UPLOAD_SUCCESS_MESSAGE)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
    }

    /*
    * Build Failure result in case of any error
    * @params obj - A map from execute method
    * @return - A map containing all necessary message for show
    * */

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            if (obj) {
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                } else {
                    result.put(Tools.MESSAGE, UPLOAD_FAILURE_MESSAGE)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, UPLOAD_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * @params file - document file
    * @params parameterMap - GrailsParameter Map from UI
    * @params sysConfiguration - System configuration object for supported extension
    * Check Document directory or create directory
    * Check supported extensions
    * @return result -  a map containing uploading document info
    * */

    private Map checkExtAndCreateDir(CommonsMultipartFile uploadedFile, GrailsParameterMap parameterMap, SysConfiguration sysConfiguration) {
        Map result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        String defaultPath = null
        SysConfiguration sysConfiguration1 = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUB_CATEGORY_DOCUMENT_PATH, docSessionUtil.appSessionUtil.getCompanyId())
        if (!sysConfiguration1) {
            result.put(Tools.MESSAGE, DESTINATION_PATH_NOT_FOUND)
            return result
        }
        defaultPath = sysConfiguration1.value
        long categoryId = Long.parseLong(parameterMap.categoryId.toString())
        long subCategoryId = Long.parseLong(parameterMap.subCategoryId.toString())
        DocCategory category = (DocCategory) docCategoryCacheUtility.read(categoryId)
        DocSubCategory subCategory = (DocSubCategory) docSubCategoryCacheUtility.read(subCategoryId)
        String subCategoryDirs = defaultPath + "\\" + category.name + "\\" + subCategory.name + "\\"
        File destinationPath = new File(subCategoryDirs)
        if (!destinationPath.exists()) {
            destinationPath.mkdirs()
        }

        String sysConfigExtensions = sysConfiguration.value
        String getDocumentExtension = getDocumentExtension(uploadedFile)
        List getSysConfigExtensions = getSysConfigExtensions(sysConfigExtensions)
        for (int i = 0; i < getSysConfigExtensions.size(); i++) {
            if (getDocumentExtension.equalsIgnoreCase(getSysConfigExtensions[i].toString())) {
                result.put(DOCUMENT_DIR, subCategoryDirs)
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                return result
            }
        }
        result.put(Tools.MESSAGE, UNSUPPORTED_EXTENSION)
        return result
    }

    /*
    * Upload document to storage
    * @params file - CommonsMultipartFile object
    * @params docDir - document directory
    * */

    private void uploadFile(CommonsMultipartFile uploadedFile, String docDir) {
        String fileName = uploadedFile.getOriginalFilename()

        File destinationPath = new File(docDir + fileName)
        if (!destinationPath.exists()) {
            uploadedFile.transferTo(destinationPath)
        }
    }

    /**
     * Get uploading file extension
     * @param multipartFile - object of CommonsMultipartFile
     * @return - file extension string
     */
    private String getDocumentExtension(CommonsMultipartFile multipartFile) {
        String documentFileName = multipartFile.properties.originalFilename.toString()
        int i = documentFileName.lastIndexOf(Tools.SINGLE_DOT)
        String uploadedFileExtension = documentFileName.substring(i + 1)
        return uploadedFileExtension
    }

    /**
     * Get System Configuration file extension List
     * @param sysConfigValue - System Configuration Value for supported extensions
     * @return sysConfigExtensions - List of supported file extension
     */
    private List getSysConfigExtensions(String sysConfigValue) {
        List sysConfigExtensions = []
        String[] sysConfigExtValue = sysConfigValue.split(Tools.COMA)
        for (int i = 0; i < sysConfigExtValue.length; i++) {
            sysConfigExtensions << sysConfigExtValue[i].trim()
        }
        return sysConfigExtensions
    }
}
