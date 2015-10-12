package com.athena.mis.exchangehouse.config

public interface ExhSysConfigurationIntf {

    // Determine if Regular fee will be evaluated based on local/foreign currency.
    // 1= local(AUD,GBP etc.), 0=foreign(BDT). default is 1.
    public static final String EXH_REGULAR_FEE_EVALUATE_ON_LOCAL_CURRENCY = "mis.exchangehouse.EvaluateRegFeeOnLocalCurrency"

    // Amount limit to send task without any photo-ID, if amount exceeds then task creation requires customer photo ID
    // if config not found then default amount is 0.
    public static final String EXH_AMOUNT_LIMIT_FOR_MANDATORY_PHOTOID = "mis.exchangehouse.amountLimitForMandatoryPhotoID"

    // Check if sanction verification is must for customer
    // 1 = must verify; 0 = don't verify
    // if config not found then default value is 1.
    public static final String EXH_VERIFY_CUSTOMER_SANCTION = "mis.exchangehouse.verifyCustomerSanction"

    // Check if sanction verification is must for beneficiary
    // 1 = must verify; 0 = don't verify
    // if config not found then default value is 1.
    public static final String EXH_VERIFY_BENEFICIARY_SANCTION = "mis.exchangehouse.verifyBeneficiarySanction"


    // Check if company has integration with payPoint
    // 1 = has integration; 0 = no integration
    // if config not found then default value is 0.
    public static final String EXH_HAS_PAY_POINT_INTEGRATION = "mis.exchangehouse.hasPayPointIntegration"


	// Check if customer surname is required
	// 1 = is required; 0 = is optional
	// if config not found then default value is 0.
	public static final String EXH_CUSTOMER_SURNAME_REQUIRED = "mis.exchangehouse.customerSurnameRequired"


	// Check if company has to validate postal code
	// 1 = will be validated; 0 = no validation
	// if config not found then default value is 0.
	public static final String EXH_POSTAL_CODE_VALIDATION = "mis.exchangehouse.validatePostalCode"

	// Check if customer photoIdNo is required
	// 1 = is required; 0 = is optional
	// if config not found then default value is 0.
	public static final String EXH_PHOTO_ID_NO_REQUIRED = "mis.exchangehouse.photoIdNoRequired"

	// Check if beneficiary lastName is required
	// 1 = is required; 0 = is optional
	// if config not found then default value is 0.
	public static final String EXH_BENEFICIARY_LAST_NAME_REQUIRED = "mis.exchangehouse.beneficiaryLastNameRequired"

    // Check if customer declaration amount, declaration Start/End is required
    // 1 = is required; 0 = is optional
    // if config not found then default value is 0.
    public static final String EXH_CUSTOMER_DECLARATION_AMOUNT = "mis.exchangehouse.customerDeclarationAmountRequired"

    // Check if customer address verification required
    // 1 = is required; 0 = is optional
    // if config not found then default value is 0.
    public static final String EXH_CUSTOMER_ADDRESS_VERIFICATION_REQUIRED = "mis.exchangehouse.customerAddressVerificationRequired"

    // Local currency Amount limit per transaction.
    // If config not found then throw error/exception
    public static final String EXH_TASK_PER_TRANSACTION_AMOUNT = "mis.exchangehouse.maxAmountPerTransaction"

    // Check if new Customer registration is enabled
    // 1 = Enabled; 0 = Disabled
    // if config not found then default value is 0.
    public static final String ENABLE_NEW_USER_REGISTRATION = "mis.exchangehouse.enableNewUserRegistration"

    //Check monthly transaction limit per customer. Any negative value will disable this config. Default value 25000.
    public static final String MONTHLY_TRAN_LIMIT_PER_CUSTOMER = "mis.exchangehouse.monthlyTranLimitPerCustomer"
}