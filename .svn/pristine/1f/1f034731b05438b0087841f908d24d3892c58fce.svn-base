package com.athena.mis.accounting.actions.accbankstatement

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccBankStatement
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.service.AccBankStatementService
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.WrapAccBankStatement
import com.athena.mis.application.entity.AppUser
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

/**
 *  Create new bank statement object and if the bank statement object contains error(s) then it will be shown in grid
 *  For details go through Use-Case doc named 'ImportAccBankStatementActionService'
 */
class ImportAccBankStatementActionService extends BaseService implements ActionIntf {

    AccBankStatementService accBankStatementService
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility

    private final Logger log = Logger.getLogger(getClass());

    private static final String GENERAL_ERROR = "generalError"
    private static final String HAS_ACCESS = "hasAccess"
    private static final String FILE_ID = "bankStatementFile"
    private static final String NO_CSV_FILE_UPLOADED = "No CSV file has been uploaded"
    private static final String INVALID_FILE_EXTENSION = "Invalid file extension (.csv expected)"
    private static final String FOLLOWING_BANK_STATEMENT_HAVE_ERROR = "Following Bank Statement contain error"
    private static final String UPLOAD_FAILED_ERROR = "Failed to import Bank Statement list"
    private static final String INVALID_TEMPLATE = "Invalid template"
    private static final String NO_BANK_STATEMENT_FOUND_IN_CSV = "No Bank Statement found in the CSV file"
    private static final String ERRORS = "errors"
    private static final String LST_WRAPPED_BANK_STATEMENT = "lstWrappedBankStatement"
    private static final String SUCCESS_MESSAGE = " Bank Statement(s) imported successfully"
    private static final String BANK_ACC_OBJ = "bankObj"
    private static final String BANK_NOT_FOUND = "Bank not found"
    private static final String CHARSET_UTF_8 = "UTF-8"
    private static final String SEPARATOR_CHAR = ","
    private static final String SKIP_LINES = "1"

    /**
     * Get parameters from UI and build bank statement object
     * @param parameters - serialized parameters from UI
     * @param obj - contain HttpServletRequest object
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap preResult = new LinkedHashMap()
        try {
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)
            preResult.put(GENERAL_ERROR, Boolean.TRUE)


            HttpServletRequest request = (HttpServletRequest) obj // Cast input stream 'request' sent from AccBankStatementController

            // Check if csv file is uploaded
            def uploadedFile = request.getFile(FILE_ID)
            if (!uploadedFile) {
                preResult.put(Tools.MESSAGE, NO_CSV_FILE_UPLOADED)
                return preResult
            }
            // Check file extension using isCsvFile method
            String strFileName = uploadedFile.properties.originalFilename.toString()
            if (!isCsvFile(strFileName)) {
                preResult.put(Tools.MESSAGE, INVALID_FILE_EXTENSION)
                return preResult
            }

            // Parse the stream (skip first line: column name)
            def csvReader = uploadedFile.inputStream.toCsvReader(['charset': CHARSET_UTF_8,
                    'separatorChar': SEPARATOR_CHAR, 'skipLines': SKIP_LINES])

            List<WrapAccBankStatement> lstWrappedBankStatement = []
            boolean isInvalidTemplate = false  // flag for required template validation check
            long rowCount = 0
            csvReader.eachLine { tokens ->
                if (tokens.size() != 9) {   // if token(columns).size != 9 then the template is invalid
                    isInvalidTemplate = true
                } else {
                    rowCount++
                    lstWrappedBankStatement << new WrapAccBankStatement(tokens, rowCount) // constructor method of WrapAccBankStatement class which is used for wrapping the bank statement
                }
            }

            // Check required template validity
            if (isInvalidTemplate) {
                preResult.put(Tools.MESSAGE, INVALID_TEMPLATE)
                return preResult
            }

            // Check the wrapped bank statement list size; if lstWrappedBankStatement.size() == 0 then there is no bank statement in the CSV file
            if (lstWrappedBankStatement.size() == 0) {
                preResult.put(Tools.MESSAGE, NO_BANK_STATEMENT_FOUND_IN_CSV)
                return preResult
            }

            // Check the list of wrapped bank statement using validateFile method and assigning in another list named errorBankStatement
            List<WrapAccBankStatement> errorBankStatement = validateFile(lstWrappedBankStatement)

            // if errorBankStatement.size() > 0 then the error list is shown in the UI
            if (errorBankStatement.size() > 0) {
                preResult.put(GENERAL_ERROR, Boolean.FALSE)
                preResult.put(Tools.MESSAGE, FOLLOWING_BANK_STATEMENT_HAVE_ERROR)
                preResult.put(ERRORS, errorBankStatement)
                return preResult
            }

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long bankAccId = Long.parseLong(parameterMap.bankAccId.toString())
            AccChartOfAccount bankAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(bankAccId) // get bankAccount from accChartOfAccountCacheUtility
            if (!bankAccount) {
                preResult.put(Tools.MESSAGE, BANK_NOT_FOUND)
                return preResult
            }

            preResult.put(BANK_ACC_OBJ, bankAccount)
            preResult.put(Tools.IS_ERROR, Boolean.FALSE)
            preResult.put(LST_WRAPPED_BANK_STATEMENT, lstWrappedBankStatement)
            return preResult;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)
            preResult.put(HAS_ACCESS, Boolean.TRUE)
            preResult.put(Tools.MESSAGE, UPLOAD_FAILED_ERROR)
            return preResult
        }
    }

    /**
     * Build errorBankStatement list
     * @param lstWrappedBankStatement -list of wrapped bank statement from executePreCondition method
     * @return - error list of bank statement
     */
    private List<WrapAccBankStatement> validateFile(List<WrapAccBankStatement> lstWrappedBankStatement) {
        List<WrapAccBankStatement> errorBankStatement = []

        for (int i = 0; i < lstWrappedBankStatement.size(); i++) {
            WrapAccBankStatement currentBankStatement = lstWrappedBankStatement[i]
            currentBankStatement.validateFile()  // each row of bank statement is validated by validateFile method which is in WrapAccBankStatement class
            if (currentBankStatement.errors.size() > 0) {
                errorBankStatement << currentBankStatement
            }
        }
        return errorBankStatement
    }

    private static final String CSV_EXTENSION = ".csv"

    /**
     * Build errorBankStatement list
     * @param fileName -string (file name) from executePreCondition method
     * @return - boolean value that is the file has the extension .csv or not
     */
    private boolean isCsvFile(String fileName) {
        String lowerFileName = fileName.toLowerCase()
        return lowerFileName.endsWith(CSV_EXTENSION)
    }

    /**
     * Save bank statement object in DB
     * This method is in transactional boundary and will roll back in case of any exception
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

            LinkedHashMap preResult = (LinkedHashMap) obj     // cast map returned from executePreCondition method
            AccChartOfAccount bankAccount = (AccChartOfAccount) preResult.get(BANK_ACC_OBJ)
            List<WrapAccBankStatement> lstWrappedBankStatement = (List<WrapAccBankStatement>) preResult.get(LST_WRAPPED_BANK_STATEMENT)
            int saveCount = 0;
            for (int i = 0; i < lstWrappedBankStatement.size(); i++) {
                WrapAccBankStatement wrapAccBankStatement = lstWrappedBankStatement[i]
                AccBankStatement accBankStatement = buildAccBankStatement(wrapAccBankStatement, bankAccount)  // build bank statement object

                //Check duplicate entry of bank statement
                int duplicateCount = checkDuplicateEntry(wrapAccBankStatement, bankAccount)
                if (duplicateCount == 0) {
                    accBankStatementService.create(accBankStatement)  // save new bank statement object in DB
                    saveCount++
                }
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.COUNT, saveCount)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            //@todo:rollback
            throw new RuntimeException(UPLOAD_FAILED_ERROR)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(GENERAL_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPLOAD_FAILED_ERROR)
            return result
        }
    }

    /**
     * Build bank statement object
     * @param wrapAccBankStatement -object from execute method  which contains single row of list of wrapped bank statement
     * @param bankAccount - object of AccChartOfAccount from execute method
     * @return -new accBankStatement object
     */
    private AccBankStatement buildAccBankStatement(WrapAccBankStatement wrapAccBankStatement, AccChartOfAccount bankAccount) {
        AppUser user = accSessionUtil.appSessionUtil.getAppUser()
        AccBankStatement accBankStatement = new AccBankStatement()
        accBankStatement.transactionDate = wrapAccBankStatement.transactionDate
        accBankStatement.transactionType = wrapAccBankStatement.transactionType
        accBankStatement.companyRef = wrapAccBankStatement.companyRef
        accBankStatement.narrative = wrapAccBankStatement.narrative
        accBankStatement.bankRef = wrapAccBankStatement.bankRef
        accBankStatement.transactionRef = wrapAccBankStatement.transactionRef
        accBankStatement.debit = wrapAccBankStatement.debit
        accBankStatement.credit = wrapAccBankStatement.credit
        accBankStatement.balance = wrapAccBankStatement.balance
        accBankStatement.bankAccId = bankAccount.id
        accBankStatement.companyId = user.companyId
        accBankStatement.chequeNo = wrapAccBankStatement.chequeNo
        accBankStatement.amount = wrapAccBankStatement.amount
        accBankStatement.createdBy = user.id
        accBankStatement.createdOn = new Date()
        return accBankStatement
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for success message view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap successResult = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj     // cast map returned from execute method
            int count = (int) executeResult.get(Tools.COUNT)
            String successMessage = count + SUCCESS_MESSAGE
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

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap failureResult = new LinkedHashMap()
        try {
            if (obj) {
                failureResult = (LinkedHashMap) obj  // cast map returned from previous method
                return failureResult
            } else {
                failureResult.put(HAS_ACCESS, Boolean.TRUE)
                failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
                failureResult.put(GENERAL_ERROR, Boolean.TRUE)
                failureResult.put(Tools.MESSAGE, UPLOAD_FAILED_ERROR)
                return failureResult
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            failureResult.put(HAS_ACCESS, Boolean.TRUE)
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            failureResult.put(GENERAL_ERROR, Boolean.TRUE)
            failureResult.put(Tools.MESSAGE, UPLOAD_FAILED_ERROR)
            return failureResult
        }
    }

    /**
     * Check Duplicate Entry of  bank statement
     * @param wrapAccBankStatement -object from execute method  which contains single row of list of wrapped bank statement
     * @param bankAccount - object of AccChartOfAccount from execute method
     * @return -integer value of select query count
     */
    private int checkDuplicateEntry(WrapAccBankStatement wrapAccBankStatement, AccChartOfAccount bankAccount) {
        String strQuery = """SELECT count(id) FROM acc_bank_statement
            WHERE transaction_date =:transactionDate"""

        if (wrapAccBankStatement.transactionType) {
            strQuery = strQuery + """
                AND transaction_type =:transactionType """
        } else if (wrapAccBankStatement.transactionType == null) {
            strQuery = strQuery + """
                AND transaction_type IS NULL """
        }

        if (wrapAccBankStatement.companyRef) {
            strQuery = strQuery + """
                AND company_ref =:companyRef """
        } else if (wrapAccBankStatement.companyRef == null) {
            strQuery = strQuery + """
                AND company_ref IS NULL """
        }

        if (wrapAccBankStatement.narrative) {
            strQuery = strQuery + """
                AND narrative =:narrative """
        } else if (wrapAccBankStatement.narrative == null) {
            strQuery = strQuery + """
                AND narrative IS NULL """
        }

        if (wrapAccBankStatement.bankRef) {
            strQuery = strQuery + """
                AND bank_ref =:bankRef """
        } else if (wrapAccBankStatement.bankRef == null) {
            strQuery = strQuery + """
                AND bank_ref IS NULL """
        }

        if (wrapAccBankStatement.transactionRef) {
            strQuery = strQuery + """
                AND transaction_ref =:transactionRef """
        } else if (wrapAccBankStatement.transactionRef == null) {
            strQuery = strQuery + """
                AND transaction_ref IS NULL """
        }

        strQuery = strQuery + """
                AND debit = ${wrapAccBankStatement.debit}
                AND credit = ${wrapAccBankStatement.credit}
                AND balance = ${wrapAccBankStatement.balance}
                AND bank_acc_id = ${bankAccount.id}
                """

        Map queryParams = [
                transactionType: wrapAccBankStatement.transactionType,
                transactionDate: DateUtility.getSqlDate(wrapAccBankStatement.transactionDate),
                companyRef: wrapAccBankStatement.companyRef,
                narrative: wrapAccBankStatement.narrative,
                bankRef: wrapAccBankStatement.bankRef,
                transactionRef: wrapAccBankStatement.transactionRef
        ]
        List result = executeSelectSql(strQuery, queryParams)

        return result[0].count
    }

}

