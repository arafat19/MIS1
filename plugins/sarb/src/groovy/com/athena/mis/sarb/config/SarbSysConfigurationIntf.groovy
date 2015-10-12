package com.athena.mis.sarb.config

public interface SarbSysConfigurationIntf {

	// Url of sent task to sarb (Production Mode)
	public static final String SARB_URL_SEND_TASK_TO_SARB_PROD = "mis.sarb.prod.urlSendTaskToSarb"

	// Url of retrieve reference (Production Mode)
	public static final String SARB_URL_RETRIEVE_REFERENCE_PROD = "mis.sarb.prod.urlRetrieveReference"

	// url of retrieve response (Production Mode)
	public static final String SARB_URL_RETRIEVE_RESPONSE_PROD = "mis.sarb.prod.urlRetrieveResponse"

    // Url of sent task to sarb (Development Mode)
    public static final String SARB_URL_SEND_TASK_TO_SARB_DEV = "mis.sarb.dev.urlSendTaskToSarb"

    // Url of retrieve reference (Development Mode)
    public static final String SARB_URL_RETRIEVE_REFERENCE_DEV = "mis.sarb.dev.urlRetrieveReference"

    // url of retrieve response (Development Mode)
    public static final String SARB_URL_RETRIEVE_RESPONSE_DEV = "mis.sarb.dev.urlRetrieveResponse"

	//Sarb userName
	public static final String SARB_USER_NAME = "mis.sarb.sarbUserName"

	//Sarb Password
	public static final String SARB_PASSWORD = "mis.sarb.sarbPassword"

    // SARB branch code. Default=00000001
    public static final String SARB_BRANCH_CODE = "mis.sarb.branchCode"

    // Determine if is development/production mode; 1 = Production, 0 = development. Default = 0
    public static final String SARB_IS_PRODUCTION_MODE = "mis.sarb.isProductionMode"

}
