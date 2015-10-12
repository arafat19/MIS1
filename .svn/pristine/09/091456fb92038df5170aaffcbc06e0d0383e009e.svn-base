package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Service class for basic SysConfiguration CRUD (Except Create)
 *  For details go through Use-Case doc named 'SysConfigurationService'
 */
class SysConfigurationService extends BaseService {

    static transactional = false

    CompanyService companyService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService

    private static final String SORT_BY_KEY = "key"

    /**
     * Pull object in readOnly mode
     * @param id
     * @return retrieved object
     */
    public read(long id) {
        SysConfiguration sysConfiguration = SysConfiguration.read(id)
        return sysConfiguration
    }

    /**
     * Method to count system configuration
     * @param companyId - company id
     * @return - an integer value of system configuration count
     */
    public int countByCompanyIdAndPluginId(long companyId, int pluginId) {
        int count = SysConfiguration.countByCompanyIdAndPluginId(companyId, pluginId)
        return count
    }

    /**
     * Method to count system configuration
     * @param key - system configuration key
     * @param companyId - company id
     * @return - an integer value of system configuration count
     */
    public int countByKeyAndCompanyIdAndPluginId(String key, long companyId, int pluginId) {
        int count = SysConfiguration.countByKeyIlikeAndCompanyIdAndPluginId(key, companyId, pluginId)
        return count
    }

    /**
     * Method to find the list of system configuration
     * @param companyId - company id
     * @return - a list of system configuration
     */
    public List findAllByCompanyIdAndPluginId(long companyId, int pluginId,BaseService baseService) {
        List sysConList = SysConfiguration.findAllByCompanyIdAndPluginId(companyId, pluginId, [max: baseService.resultPerPage, offset: baseService.start, sort: SORT_BY_KEY, order: ASCENDING_SORT_ORDER, readOnly: true])
        return sysConList
    }

    /**
     * Method to find the list of system configuration
     * @param key - system configuration key
     * @param companyId - company id
     * @return - a list of system configuration
     */
    public List findAllByKeyAndCompanyIdAndPluginId(String key, long companyId, int pluginId, BaseService baseService) {
        List sysConList = SysConfiguration.findAllByKeyIlikeAndCompanyIdAndPluginId(key, companyId, pluginId, [readOnly: true, max: baseService.resultPerPage, offset: baseService.start, sort: SORT_BY_KEY, order: ASCENDING_SORT_ORDER])
        return sysConList
    }

    /**
     * Method to find system configuration object
     * @param sysConId - system configuration id
     * @return - an object of system configuration
     */
    public SysConfiguration findById(int sysConId) {
        SysConfiguration sysCon = SysConfiguration.findById(sysConId, [readOnly: true])
        return sysCon
    }

    /**
     * get list of all SysConfiguration object(s) of a specific plugin
     * @param pluginId
     * @return -list of SysConfiguration objects
     */
    @Transactional(readOnly = true)
    public List listByPlugin(int pluginId) {
        return SysConfiguration.findAllByPluginId(pluginId, [readOnly: true])
    }

    /**
     * get list of all SysConfiguration object(s)
     * @return -list of SysConfiguration objects
     */
    public List list() {
        return SysConfiguration.list(sort: "key", order: "asc", readOnly: true)
    }

    /**
     * SQL to update sysConfiguration object in database
     * @param sysCon -SysConfiguration object
     * @return -int value(updateCount)
     */
    public int update(SysConfiguration sysCon) {
        String queryStr = """
                    UPDATE sys_configuration SET
                          value=:value
                      WHERE
                          id=:id
                          """
        Map queryParams = [
                id: sysCon.id,
                value: sysCon.value
        ]

        int updateCount = executeUpdateSql(queryStr, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating information')
        }
        return updateCount;
    }

    /**
     * insert default SysConfiguration into database of Inventory plugin when application starts with bootstrap
     */
    public void createDefaultAppSysConfig(long companyId) {
        SysConfiguration passwordExpirationTime = new SysConfiguration(key: 'mis.application.defaultPasswordExpireDuration', value: '180')
        passwordExpirationTime.description = """
            Duration for password expire from creation date of user, value= No. of Days, if config not found expire date=current date
        """
        passwordExpirationTime.pluginId = PluginConnector.APPLICATION_ID
        passwordExpirationTime.companyId = companyId
        passwordExpirationTime.save()
    }

    /**
     * insert default SysConfiguration into database of Accounting plugin when application starts with bootstrap
     */
    public void createDefaultAccSysConfig(long companyId) {
        SysConfiguration showPostedVouchers = new SysConfiguration(key: 'mis.accounting.showPostedVouchers', value: '-1')
        showPostedVouchers.description = """
            ShowPostedVouchers determines whether all posted-only vouchers will be considered to generate reports.
             0 = consider only posted voucher;
            -1 = consider both vouchers;
            default value is -1
        """
        showPostedVouchers.pluginId = accountingImplService.getId()
        showPostedVouchers.companyId = companyId
        showPostedVouchers.save()
    }

    /**
     * insert default SysConfiguration into database of ExchangeHouse plugin when application starts with bootstrap
     */
    public void createDefaultDataForExh(long companyId) {
		SysConfiguration sysConfiguration2 = new SysConfiguration(key: 'mis.exchangehouse.EvaluateRegFeeOnLocalCurrency', value: '1')
		sysConfiguration2.description = """
            Determine if Regular fee will be evaluated based on local/foreign currency.
            1= local(AUD,GBP etc.), 0=foreign(BDT). default is 1.
        """
		sysConfiguration2.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
		sysConfiguration2.companyId = companyId
		sysConfiguration2.save(flush: true)

		SysConfiguration sysConfiguration3 = new SysConfiguration(key: 'mis.exchangehouse.amountLimitForMandatoryPhotoID', value: '750')
		sysConfiguration3.description = """
            Amount limit to send task without any photo-ID,
            if amount exceeds then task creation requires customer photo ID. Default amount is 0.
        """
		sysConfiguration3.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
		sysConfiguration3.companyId = companyId
		sysConfiguration3.save(flush: true)

		SysConfiguration sysConfiguration4 = new SysConfiguration(key: 'mis.exchangehouse.verifyCustomerSanction', value: '0')
		sysConfiguration4.description = """
            Check if sanction verification is must
            for customer 1 = must verify; 0 = do not verify if config not found then default value is 0
        """
		sysConfiguration4.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
		sysConfiguration4.companyId = companyId
		sysConfiguration4.save(flush: true)

		SysConfiguration sysConfiguration5 = new SysConfiguration(key: 'mis.exchangehouse.verifyBeneficiarySanction', value: '0')
		sysConfiguration5.description = """
            Check if sanction verification is must for
            beneficiary 1 = must verify; 0 = do not verify if config not found then default value is 0.
        """
		sysConfiguration5.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
		sysConfiguration5.companyId = companyId
		sysConfiguration5.save(flush: true)

		SysConfiguration sysConfiguration6 = new SysConfiguration(key: 'mis.exchangehouse.hasPayPointIntegration', value: '0')
		sysConfiguration6.description = """
            Check if company has integration with payPoint
        """
		sysConfiguration6.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
		sysConfiguration6.companyId = companyId
		sysConfiguration6.save(flush: true)

		SysConfiguration sysConfiguration7 = new SysConfiguration(key: 'mis.exchangehouse.customerSurnameRequired', value: '0')
		sysConfiguration7.description = """
            Check if customer surname is required. 1 = is required; 0 = is optional. If config not found then default value is 0.
        """
		sysConfiguration7.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
		sysConfiguration7.companyId = companyId
		sysConfiguration7.save(flush: true)

		SysConfiguration sysConfiguration8 = new SysConfiguration(key: 'mis.exchangehouse.validatePostalCode', value: '0')
		sysConfiguration8.description = """
            Check if company has to validate postal code. 1 = will be validated; 0 = no validation. If config not found then default value is 0.
        """
		sysConfiguration8.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
		sysConfiguration8.companyId = companyId
		sysConfiguration8.save(flush: true)

		SysConfiguration sysConfiguration9 = new SysConfiguration(key: 'mis.exchangehouse.photoIdNoRequired', value: '0')
		sysConfiguration9.description = """
            Check if customer photoId is required. 1 = is required; 0 = is optional. If config not found then default value is 0.
        """
		sysConfiguration9.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
		sysConfiguration9.companyId = companyId
		sysConfiguration9.save(flush: true)

		SysConfiguration sysConfiguration10 = new SysConfiguration(key: 'mis.exchangehouse.beneficiaryLastNameRequired', value: '0')
		sysConfiguration10.description = """
            Check if beneficiary lastName is required. 1 = is required; 0 = is optional. If config not found then default value is 0.
        """
		sysConfiguration10.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
		sysConfiguration10.companyId = companyId
		sysConfiguration10.save(flush: true)


        SysConfiguration sysConfiguration11 = new SysConfiguration(key: 'mis.exchangehouse.customerDeclarationAmountRequired', value: '0')
        sysConfiguration11.description = """
            Check if declaration amount is required. 1 = is required; 0 = is optional. If config not found then default value is 0.
        """
        sysConfiguration11.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
        sysConfiguration11.companyId = companyId
        sysConfiguration11.save(flush: true)

        SysConfiguration sysConfiguration12 = new SysConfiguration(key: 'mis.exchangehouse.customerAddressVerificationRequired', value: '0')
        sysConfiguration12.description = """
            Check if customer address verification is required. 1 = is required; 0 = is optional. If config not found then default value is 0.
        """
        sysConfiguration12.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
        sysConfiguration12.companyId = companyId
        sysConfiguration12.save(flush: true)

        SysConfiguration sysConfiguration13 = new SysConfiguration(key: 'mis.exchangehouse.maxAmountPerTransaction', value: '10000')
        sysConfiguration13.description = """
            Local currency Amount limit per transaction. If config not found then throw error/exception.
        """
        sysConfiguration13.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
        sysConfiguration13.companyId = companyId
        sysConfiguration13.save(flush: true)

        SysConfiguration sysConfiguration14 = new SysConfiguration(key: 'mis.exchangehouse.enableNewUserRegistration', value: '1')
        sysConfiguration14.description = """
            Check if customer registration link will be visible. 1 = Enabled; 0 = Disabled. If config not found then default value is 0.
        """
        sysConfiguration14.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
        sysConfiguration14.companyId = companyId
        sysConfiguration14.save(flush: true)

        SysConfiguration sysConfiguration15 = new SysConfiguration(key: 'mis.exchangehouse.monthlyTranLimitPerCustomer', value: '25000')
        sysConfiguration15.description = """
            Check monthly transaction limit per customer. Any negative value will disable this config. Default value 25000.
        """
        sysConfiguration15.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
        sysConfiguration15.companyId = companyId
        sysConfiguration15.save(flush: true)
    }

	public void createDefaultSarbSysConfig(long companyId) {

		SysConfiguration sysConfiguration1 = new SysConfiguration(key: 'mis.sarb.prod.urlSendTaskToSarb', value: 'https://sarbdexqp.resbank.co.za:444/SARBDEX/sarbdex.aspx?Method=file')
		sysConfiguration1.description = """
            Url of sent task to sarb (Production Mode).
        """
		sysConfiguration1.pluginId = PluginConnector.SARB_ID
		sysConfiguration1.companyId = companyId
		sysConfiguration1.save(flush: true)

		SysConfiguration sysConfiguration2 = new SysConfiguration(key: 'mis.sarb.prod.urlRetrieveReference', value: 'https://sarbdexqp.resbank.co.za:444/SARBDEX/getmsgbysarbref.asp')
		sysConfiguration2.description = """
            Url of retrieve reference (Production Mode).
        """
		sysConfiguration2.pluginId = PluginConnector.SARB_ID
		sysConfiguration2.companyId = companyId
		sysConfiguration2.save(flush: true)

		SysConfiguration sysConfiguration3 = new SysConfiguration(key: 'mis.sarb.prod.urlRetrieveResponse', value: 'https://sarbdexqp.resbank.co.za:444/SARBDEX/getmsgbysarbref.asp')
		sysConfiguration3.description = """
            url of retrieve response (Production Mode)
        """
		sysConfiguration3.pluginId = PluginConnector.SARB_ID
		sysConfiguration3.companyId = companyId
		sysConfiguration3.save(flush: true)

        SysConfiguration sysConfiguration4 = new SysConfiguration(key: 'mis.sarb.dev.urlSendTaskToSarb', value: 'https://sarbdexqp.resbank.co.za:444/SARBDEX/sarbdex.aspx?Method=file')
        sysConfiguration4.description = """
            Url of sent task to sarb (Development Mode).
        """
        sysConfiguration4.pluginId = PluginConnector.SARB_ID
        sysConfiguration4.companyId = companyId
        sysConfiguration4.save(flush: true)

        SysConfiguration sysConfiguration5 = new SysConfiguration(key: 'mis.sarb.dev.urlRetrieveReference', value: 'https://sarbdexqp.resbank.co.za:444/SARBDEX/getmsgbysarbref.asp')
        sysConfiguration5.description = """
            Url of retrieve reference (Development Mode).
        """
        sysConfiguration5.pluginId = PluginConnector.SARB_ID
        sysConfiguration5.companyId = companyId
        sysConfiguration5.save(flush: true)

        SysConfiguration sysConfiguration6 = new SysConfiguration(key: 'mis.sarb.dev.urlRetrieveResponse', value: 'https://sarbdexqp.resbank.co.za:444/SARBDEX/getmsgbysarbref.asp')
        sysConfiguration6.description = """
            url of retrieve response (Development Mode)
        """
        sysConfiguration6.pluginId = PluginConnector.SARB_ID
        sysConfiguration6.companyId = companyId
        sysConfiguration6.save(flush: true)

		SysConfiguration sysConfiguration7 = new SysConfiguration(key: 'mis.sarb.sarbUserName', value: 'SOUTHEASTBANK_FINSURV')
		sysConfiguration7.description = """
            Sarb UserName.
        """
		sysConfiguration7.pluginId = PluginConnector.SARB_ID
		sysConfiguration7.companyId = companyId
		sysConfiguration7.save(flush: true)

		SysConfiguration sysConfiguration8 = new SysConfiguration(key: 'mis.sarb.sarbPassword', value: 'athena@321')
		sysConfiguration8.description = """
            Sarb Password.
        """
		sysConfiguration8.pluginId = PluginConnector.SARB_ID
		sysConfiguration8.companyId = companyId
		sysConfiguration8.save(flush: true)

        SysConfiguration sysConfiguration9 = new SysConfiguration(key: 'mis.sarb.branchCode', value: 'SECL0205')
        sysConfiguration9.description = """
           SARB branch code. Default=SECL0205.
        """
        sysConfiguration9.pluginId = PluginConnector.SARB_ID
        sysConfiguration9.companyId = companyId
        sysConfiguration9.save(flush: true)

        SysConfiguration sysConfiguration10 = new SysConfiguration(key: 'mis.sarb.isProductionMode', value: '0')
        sysConfiguration10.description = """
            Determine if is development/production mode; 1 = Production, 0 = development. Default = 0.
        """
        sysConfiguration10.pluginId = PluginConnector.SARB_ID
        sysConfiguration10.companyId = companyId
        sysConfiguration10.save(flush: true)
    }

    public void createDefaultDataDocSysConfig(long companyId) {

        SysConfiguration categorySysConfiguration = new SysConfiguration(key: 'mis.document.categoryLabel', value: 'Category')
        categorySysConfiguration.description = 'Determine Category Label, Default Value is Category'
        categorySysConfiguration.pluginId = PluginConnector.DOCUMENT_ID
        categorySysConfiguration.companyId = companyId
        categorySysConfiguration.save(flush: true)

        SysConfiguration subCategorySysConfiguration = new SysConfiguration(key: 'mis.document.subCategoryLabel', value: 'Sub Category')
        subCategorySysConfiguration.description = 'Determine Sub Category Label, Default Value is Sub Category'
        subCategorySysConfiguration.pluginId = PluginConnector.DOCUMENT_ID
        subCategorySysConfiguration.companyId = companyId
        subCategorySysConfiguration.save(flush: true)

        SysConfiguration supportedExtensionsSysConfiguration = new SysConfiguration(key: 'mis.document.supportedExtensions', value: 'pdf, doc')
        supportedExtensionsSysConfiguration.description = 'Determine Supported Extensions, Default Value is pdf,doc'
        supportedExtensionsSysConfiguration.pluginId = PluginConnector.DOCUMENT_ID
        supportedExtensionsSysConfiguration.companyId = companyId
        supportedExtensionsSysConfiguration.save(flush: true)

        SysConfiguration suCategoryDocumentPath = new SysConfiguration(key: 'mis.document.subCategoryDocumentPath', value: 'D:\\Document')
        suCategoryDocumentPath.description = 'Determine Sub Category Document Path, Default Value is D:\\Document'
        suCategoryDocumentPath.pluginId = PluginConnector.DOCUMENT_ID
        suCategoryDocumentPath.companyId = companyId
        suCategoryDocumentPath.save(flush: true)

        SysConfiguration docExpireSendInvitation = new SysConfiguration(key: 'mis.document.expirationDurationInvitedMembers', value: '7')
        docExpireSendInvitation.description = 'Determine Document send invitation expiration date.Default Value is 7 day'
        docExpireSendInvitation.pluginId = PluginConnector.DOCUMENT_ID
        docExpireSendInvitation.companyId = companyId
        docExpireSendInvitation.save(flush: true)

    }
}
