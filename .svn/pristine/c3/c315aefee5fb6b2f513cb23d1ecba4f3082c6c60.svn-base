package com.athena.mis.arms.model

import com.athena.mis.application.entity.Country
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.actions.rmstask.UploadBankDepositTaskActionService
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.utility.RmsPaymentMethodCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger

import java.text.DecimalFormat

/**
 * Wrapper class for BankDepositTask
 */
class WrapBankDepositTask {

    def uploadBankDepositTaskActionService

	private Logger log = Logger.getLogger(getClass());

	private static final int SERIAL_INDEX = 0
	private static final int DATE_INDEX = 1
	private static final int TRANSACTION_REF_No_INDEX = 2
	private static final int BENEFICIARY_NAME_INDEX = 3
	private static final int BENEFICIARY_ACC_NUM_INDEX = 4
	private static final int BENEFICIARY_BANK_INDEX = 5
	private static final int BENEFICIARY_BRANCH_INDEX = 6
	private static final int BENEFICIARY_DISTRICT_INDEX = 7
	private static final int BENEFICIARY_TELEPHONE_INDEX = 8
	private static final int REMITTER_NAME_INDEX = 9
	private static final int AMOUNT_INDEX = 10
	private static final int CURRENCY_INDEX = 11

	// String constants
	private static final String INVALID_TEMPLATE = "Invalid Template"
	private static final String VALUE_DATE_REQUIRED = "Value date is required"
	private static final String VALUE_DATE_CANT_ADV_DATE = "Value date can not be set in advanced date"
	private static final String VALUE_DATE_INVALID = "Invalid value date"
	private static final String REF_NO_REQUIRED = "Transaction Ref No is required"
	private static final String REF_NO_INVALID = "Invalid Ref No. Example: AB012 , 123-aBc"
	private static final String BEN_NAME_REQUIRED = "Beneficiary name is required"
	private static final String ACCOUNT_NO_REQUIRED = "Account number is required"
	private static final String ACCOUNT_NO_MUST_HAVE_MIN_CHAR = "Account number must have 3 characters or more"
	private static final String AMOUNT_REQUIRED = "Amount is required"
	private static final String AMOUNT_MUST_BE_NON_ZERO = "Amount must be greater than zero"
	private static final String AMOUNT_EXCEED_LIMIT = "Amount exceeds the amount limit. LIMIT: "
	private static final String AMOUNT_INVALID = "Invalid amount"
	private static final String CURRENCY_REQUIRED = "Currency name is required"
	private static final String CURRENCY_NOT_SUPPORTED = "Currency is not supported"
	private static final String SERIAL_NO_REQUIRED = "Serial number is required"
	private static final String BANK_NAME_REQUIRED = "Bank Name is required"
	private static final String BANK_BR_NAME_REQUIRED = "Bank Branch name is required"
	private static final String DISTRICT_NAME_REQUIRED = "District name is required"

	static final double TASK_AMOUNT_LIMIT = 99999999.99;
	// check the pattern of transaction Ref no to avoid special characters
	private static final String REF_NO_PATTERN = """^[a-zA-Z0-9]+(-[a-zA-Z0-9]+)*\$"""
	// Transaction Ref no Prefix will be added only if it is numeric
	private static final String REF_NO_PATTERN_FOR_PREFIX = "^\\d*\$"


	String serial
	String transactionRefNo
	String beneficiaryName
	String beneficiaryPhone
	String accountNumber
	String outletName
	String outletBranchName
	String outletDistrictName
	String senderName
	String currency
	Long currencyId
	String benAmount
	Double amount
	Long exHouseId
	Long countryId
	List errors = []
	String taskValueDate
	Long paymentMethod

	WrapBankDepositTask(UploadBankDepositTaskActionService bankDepositActionServiceObj) {
		uploadBankDepositTaskActionService = bankDepositActionServiceObj
	}

    /**
     * Parse property from tokens
     * @param tokens - single token from csv file
     * @param rmsExchangeHouse
     * @return
     */
	public void parseTokens(tokens, RmsExchangeHouse rmsExchangeHouse) {
		SystemEntity paymentMethodObj = (SystemEntity) uploadBankDepositTaskActionService.rmsPaymentMethodCacheUtility.readByReservedAndCompany(RmsPaymentMethodCacheUtility.BANK_DEPOSIT_ID, uploadBankDepositTaskActionService.rmsSessionUtil.appSessionUtil.getCompanyId())
		this.paymentMethod = paymentMethodObj.id
		this.exHouseId = rmsExchangeHouse.id
		Country country = (Country) uploadBankDepositTaskActionService.countryCacheUtility.read(rmsExchangeHouse.countryId)
		this.countryId = country.id
		try {
			this.serial = tokens[SERIAL_INDEX]                                     	// Index = 0
			this.taskValueDate = tokens[DATE_INDEX]                                	// Index = 1
			this.transactionRefNo = tokens[TRANSACTION_REF_No_INDEX]               	// Index = 2
			this.beneficiaryName = tokens[BENEFICIARY_NAME_INDEX]                  	// Index = 3
			this.accountNumber = tokens[BENEFICIARY_ACC_NUM_INDEX]                  // Index = 4
			this.outletName = tokens[BENEFICIARY_BANK_INDEX]               			// Index = 5
			this.outletBranchName = tokens[BENEFICIARY_BRANCH_INDEX]           		// Index = 6
			this.outletDistrictName = tokens[BENEFICIARY_DISTRICT_INDEX]      		// Index = 7
			this.beneficiaryPhone = tokens[BENEFICIARY_TELEPHONE_INDEX]            	// Index = 8
			this.senderName = tokens[REMITTER_NAME_INDEX]                          	// Index = 9
			this.benAmount = tokens[AMOUNT_INDEX]                                  	// Index = 10
			this.currency = tokens[CURRENCY_INDEX]                                 	// Index = 11
		} catch (Exception exp) {
			this.errors << INVALID_TEMPLATE
		}
	}

    /**
     * validate bankDeposit task
     * @return TRUE/FALSE
     */
	public boolean validate() {
		// Index=0 ;Serial
		if (this.serial.trim().equals(Tools.EMPTY_SPACE)) {
			this.errors << SERIAL_NO_REQUIRED
		}

		// Index=1 ;Date
		validateValueDate()

		// Index=2 ;TransactionRefNo
		validateTransactionRefNo()

		// Index=3 ;Beneficiary Name
        if (this.beneficiaryName.trim().equals(Tools.EMPTY_SPACE)) {
            this.errors << BEN_NAME_REQUIRED
        }

		// Index=4 ;BeneficiaryAccNo
		validateAccountNumber()

		// Index=5 ;BenBank

		if (this.outletName.trim().equals(Tools.EMPTY_SPACE)) {
            this.errors << BANK_NAME_REQUIRED
		}

		// Index=6 ;BenBranch
		if (this.outletBranchName.trim().equals(Tools.EMPTY_SPACE)) {
            this.errors << BANK_BR_NAME_REQUIRED
		}

		// Index=7 ;BenDistrict
		if (this.outletDistrictName.trim().equals(Tools.EMPTY_SPACE)) {
            this.errors << DISTRICT_NAME_REQUIRED
		}

		// Index=10 ;Amount
		validateAmount()

		// Index=11 ;Currency
		validateCurrency()

		//*********************** Now all validation check is done!
		if (errors.size() == 0) {
			setPrefix()   // set the Prefix of Transaction Reference No
            uploadBankDepositTaskActionService = null
			return true
		} else {
            uploadBankDepositTaskActionService = null
			return false
		}
	}

    /**
     * validate valueDate
     */
	private void validateValueDate() {
		if (this.taskValueDate.trim().equals(Tools.EMPTY_SPACE)) {
            this.errors << VALUE_DATE_REQUIRED
		} else {
			try {
				Date valueDate = DateUtility.parseMaskedDate(this.taskValueDate)
				if (valueDate.after(new Date())) {
                    this.errors << VALUE_DATE_CANT_ADV_DATE
				}
			} catch (Exception ex) {
                this.errors << VALUE_DATE_INVALID
			}
		}
	}

    /**
     * validate transactionRefNo
     */
	private void validateTransactionRefNo() {
		if (this.transactionRefNo.trim().equals(Tools.EMPTY_SPACE)) {
			this.errors << REF_NO_REQUIRED
		} else if (!checkRefNoPattern(this.transactionRefNo)) {
			this.errors << REF_NO_INVALID
		}
	}

    /**
     * validate account number
     */
	private void validateAccountNumber() {
		if (this.accountNumber.trim().equals(Tools.EMPTY_SPACE)) {
			this.errors << ACCOUNT_NO_REQUIRED
		} else if (this.accountNumber.length() < 3) {
			this.errors << ACCOUNT_NO_MUST_HAVE_MIN_CHAR
		}
	}

    /**
     * validate amount
     */
	private void validateAmount() {
		if (this.benAmount.trim().equals(Tools.EMPTY_SPACE)) {
			this.errors << AMOUNT_REQUIRED
		} else {
			try {
				this.benAmount = this.benAmount.replace(Tools.COMA, Tools.EMPTY_SPACE)
				this.amount = Double.parseDouble(this.benAmount).round(2)
				if (this.amount <= 0) {
                    this.errors << AMOUNT_MUST_BE_NON_ZERO
				} else if (this.amount > TASK_AMOUNT_LIMIT) {
                    this.errors << AMOUNT_EXCEED_LIMIT + getArmsTaskAmountLimit()
				}
			} catch (Exception ex) {
				log.error ex.getMessage()
                this.errors << AMOUNT_INVALID
			}
		}
	}

    /**
     * validate currency
     */
	private void validateCurrency() {

		if ((this.currency.trim().equals(Tools.EMPTY_SPACE)) || (this.currency == null)) {
            this.errors << CURRENCY_REQUIRED
		}
		else{
			Currency currency = uploadBankDepositTaskActionService.currencyCacheUtility.getLocalCurrency()
			if ((this.currency.equalsIgnoreCase(currency.symbol))) {
				this.currencyId = currency.id
			} else {
                this.errors << CURRENCY_NOT_SUPPORTED
			}
		}
	}

    /**
     * set prefix of taskRefNo with exchangeHouse code
     */
	private void setPrefix() {
		RmsExchangeHouse rmsExchangeHouse = (RmsExchangeHouse) uploadBankDepositTaskActionService.rmsExchangeHouseCacheUtility.read(exHouseId)
		this.transactionRefNo = setTaskRefNoPrefix(rmsExchangeHouse, this.transactionRefNo)
	}

	private String getArmsTaskAmountLimit() {
		DecimalFormat armsAmountFormatter = new DecimalFormat("0.00");
		String output = armsAmountFormatter.format(TASK_AMOUNT_LIMIT);
		return output + ' BDT'
	}

	private String setTaskRefNoPrefix(RmsExchangeHouse rmsExchangeHouse, String refNo) {
		refNo = refNo.trim()
		if (refNo.matches(REF_NO_PATTERN_FOR_PREFIX)) {
			return rmsExchangeHouse.code + refNo
		} else {
			return refNo
		}
	}

    /**
     * Check refNo pattern with reg-exp
     * @param refNo
     * @return
     */
	private boolean checkRefNoPattern(String refNo) {
		boolean match
		String refNoPattern = REF_NO_PATTERN
		match = refNo.matches(refNoPattern)
		if (match == true) {
			return true
		} else {
			return false
		}
	}
}
