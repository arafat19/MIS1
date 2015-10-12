package com.athena.mis.exchangehouse.actions.sanction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.common.WrapSanction
import com.athena.mis.exchangehouse.entity.ExhSanction
import com.athena.mis.exchangehouse.service.ExhSanctionService
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

class ExhImportSanctionFileActionService extends BaseService implements ActionIntf {

    ExhSanctionService exhSanctionService
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String GENERAL_ERROR = "generalError"
    private static final String HAS_ACCESS = "hasAccess"
    private static final String FILE_ID = "sanctionFile"
    private static final String NO_CSV_FILE_UPLOADED = "No CSV file has been uploaded"
    private static final String INVALID_FILE_EXTENSION = "Invalid file extension (.csv expected)"
    private static final String FOLLOWING_SANCTIONS_HAVE_ERROR = "Following sanctions contain error"
    private static final String UPLOAD_FAILED_ERROR = "Failed to import sanction list"
    private static final String SANCTION_SAVE_FAILED = "Failed to save sanction list"
    private static final String INVALID_TEMPLATE = "Invalid template"
    private static final String NO_SANCTION_FOUND_IN_CSV = "No sanction found in the CSV file"
    private static final String ERRORS = "errors"
    private static final String LST_WRAPPED_SANCTIONS = "lstWrappedSanctions"

    @Transactional
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap preResult = new LinkedHashMap()
        try {
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)
            preResult.put(GENERAL_ERROR, Boolean.TRUE)

            if (exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                preResult.put(HAS_ACCESS, Boolean.TRUE)
            } else {
                preResult.put(HAS_ACCESS, Boolean.FALSE)
                return preResult                             // return instantly
            }

            HttpServletRequest request = (HttpServletRequest) obj

            // check if csv file uploaded
            def uploadedFile = request.getFile(FILE_ID)
            if (!uploadedFile) {
                preResult.put(Tools.MESSAGE, NO_CSV_FILE_UPLOADED)
                return preResult
            }
            // check file extension
            String strFileName = uploadedFile.properties.originalFilename.toString()
            if (!isCsvFile(strFileName)) {
                preResult.put(Tools.MESSAGE, INVALID_FILE_EXTENSION)
                return preResult
            }

            // parse the stream (skip first two lines: lastUpdate date and column name)
            def csvReader = uploadedFile.inputStream.toCsvReader(['charset': 'UTF-8',
                    'separatorChar': ',', 'skipLines': 2])

            List<WrapSanction> lstWrappedSanctions = []
            boolean isInvalidTemplate = false
            long rowCount = 0
            csvReader.eachLine { tokens ->
                if (tokens.size() != 29) {
                    isInvalidTemplate = true
                } else {
                    rowCount++
                    lstWrappedSanctions << new WrapSanction(tokens, rowCount)
                }
            }

            if (isInvalidTemplate) {
                preResult.put(Tools.MESSAGE, INVALID_TEMPLATE)
                return preResult
            }

            if (lstWrappedSanctions.size() == 0) {
                preResult.put(Tools.MESSAGE, NO_SANCTION_FOUND_IN_CSV)
                return preResult
            }

            List<WrapSanction> errorSanctions = this.validateFile(lstWrappedSanctions)

            if (errorSanctions.size() > 0) {
                preResult.put(GENERAL_ERROR, Boolean.FALSE)
                preResult.put(Tools.MESSAGE, FOLLOWING_SANCTIONS_HAVE_ERROR)
                preResult.put(ERRORS, errorSanctions)
                return preResult
            }

            preResult.put(Tools.IS_ERROR, Boolean.FALSE)
            preResult.put(LST_WRAPPED_SANCTIONS, lstWrappedSanctions)
            return preResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)
            preResult.put(HAS_ACCESS, Boolean.TRUE)
            preResult.put(Tools.MESSAGE, UPLOAD_FAILED_ERROR)
            return preResult
        }
    }

    private List<WrapSanction> validateFile(List<WrapSanction> lstWrappedSanctions) {
        List<WrapSanction> errorSanctions = []

        for (int i = 0; i < lstWrappedSanctions.size(); i++) {
            WrapSanction currentSanction = lstWrappedSanctions[i]
            currentSanction.validateFile()
            if (currentSanction.errors.size() > 0) {
                errorSanctions << currentSanction
            }
        }
        return errorSanctions
    }

    private static final String CSV_EXTENSION = ".csv"

    private boolean isCsvFile(String fileName) {
        String lowerFileName = fileName.toLowerCase()
        return lowerFileName.endsWith(CSV_EXTENSION)
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap executeResult = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            List<WrapSanction> lstWrappedSanctions = (List<WrapSanction>) preResult.get(LST_WRAPPED_SANCTIONS)
            List<ExhSanction> lstSanctions = buildListOfSanctions(lstWrappedSanctions)
            boolean success = exhSanctionService.create(lstSanctions)
            if (!success) {
                executeResult.put(Tools.IS_ERROR, Boolean.TRUE)
                executeResult.put(GENERAL_ERROR, Boolean.TRUE)
                executeResult.put(Tools.MESSAGE, SANCTION_SAVE_FAILED)
            } else {
                executeResult.put(Tools.IS_ERROR, Boolean.FALSE)
                executeResult.put(Tools.COUNT, lstSanctions.size())
            }
            return executeResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to import Sanction File')
            executeResult.put(Tools.IS_ERROR, Boolean.TRUE)
            executeResult.put(GENERAL_ERROR, Boolean.TRUE)
            executeResult.put(Tools.MESSAGE, UPLOAD_FAILED_ERROR)
            return executeResult
        }
    }

    private List<ExhSanction> buildListOfSanctions(List<WrapSanction> lstWrappedSanctions) {
        List<ExhSanction> lstSanctions = []

        for (int i = 0; i < lstWrappedSanctions.size(); i++) {
            ExhSanction sanction = new ExhSanction()
            WrapSanction wrapSanction = lstWrappedSanctions[i]
            sanction.name = wrapSanction.name
            sanction.title = wrapSanction.title
            sanction.dob = wrapSanction.dob
            sanction.townOfBirth = wrapSanction.townOfBirth
            sanction.countryOfBirth = wrapSanction.countryOfBirth
            sanction.nationality = wrapSanction.nationality
            sanction.passportDetails = wrapSanction.passportDetails
            sanction.niNumber = wrapSanction.niNumber
            sanction.position = wrapSanction.position
            sanction.address1 = wrapSanction.address1
            sanction.address2 = wrapSanction.address2
            sanction.address3 = wrapSanction.address3
            sanction.address4 = wrapSanction.address4
            sanction.address5 = wrapSanction.address5
            sanction.address6 = wrapSanction.address6
            sanction.postOrZip = wrapSanction.postOrZip
            sanction.country = wrapSanction.country
            sanction.otherInformation = wrapSanction.otherInformation
            sanction.groupType = wrapSanction.groupType
            sanction.aliasType = wrapSanction.aliasType
            sanction.regime = wrapSanction.regime
            sanction.createdOn = wrapSanction.createdOn
            sanction.lastUpdate = wrapSanction.lastUpdate
            sanction.groupId = wrapSanction.groupId
            lstSanctions << sanction
        }
        return lstSanctions
    }


    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }


    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap successResult = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            int count = (int) executeResult.get(Tools.COUNT)
            String successMessage = count + ' Sanction(s) imported successfully'
            successResult.put(Tools.IS_ERROR, Boolean.FALSE)
            successResult.put(Tools.MESSAGE, successMessage)
            return successResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            successResult.put(Tools.IS_ERROR, Boolean.TRUE)
            successResult.put(GENERAL_ERROR, Boolean.TRUE)
            successResult.put(Tools.MESSAGE, UPLOAD_FAILED_ERROR)
            return successResult
        }
    }


    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap failureResult = new LinkedHashMap()
        try {
            if (obj) {
                failureResult = (LinkedHashMap) obj
                return failureResult
            } else {
                failureResult.put(HAS_ACCESS, Boolean.TRUE)
                failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
                failureResult.put(GENERAL_ERROR, Boolean.TRUE)
                failureResult.put(Tools.MESSAGE, UPLOAD_FAILED_ERROR)
                return failureResult
            }
        } catch (Exception ex) {
            log.error(ex.getMessage())
            failureResult.put(HAS_ACCESS, Boolean.TRUE)
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            failureResult.put(GENERAL_ERROR, Boolean.TRUE)
            failureResult.put(Tools.MESSAGE, UPLOAD_FAILED_ERROR)
            return failureResult
        }
    }

}
