package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Sms
import com.athena.mis.application.utility.SmsCacheUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 * SmsService is used to handle only CRUD related object manipulation
 * (e.g. list, update etc.)
 */
class SmsService extends BaseService {

    static transactional = false

    @Autowired
    SmsCacheUtility smsCacheUtility

    public final String SORT_ON_ID = "id"

    /**
     * Method to get sms list
     * @return - list of SMS
     */
    public List list() {
        return Sms.list(sort: smsCacheUtility.SORT_ON_ID, order: smsCacheUtility.SORT_ORDER_ASCENDING, readOnly: true)
    }

    /**
     * Method to read SMS by id
     * @param id - SMS.id
     * @return - SMS object
     */
    public Sms read(long id) {
        Sms sms = Sms.read(id)
        return sms
    }

    private static final String UPDATE_QUERY = """
                    UPDATE sms SET
                          url=:url,
                          body=:body,
                          recipients=:recipients,
                          version=:newVersion,
                          transaction_code=:transactionCode,
                          is_active=:isActive
                      WHERE id=:id AND
                            version=:version
                      """

    /**
     * Method to update Sms object
     * @param sms - object of Sms
     * @return - updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(Sms sms) {
        Map queryParams = [
                id: sms.id,
                body: sms.body,
                url: sms.url,
                recipients: sms.recipients,
                version: sms.version,
                transactionCode: sms.transactionCode,
                isActive: sms.isActive,
                newVersion: sms.version + 1
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating sms')
        }
        return updateCount
    }

    /**
     * Method to count SMS
     * @param companyId - company id
     * @return - an integer value of SMS count
     */
    public int countByCompanyIdAndPluginId(long companyId, int pluginId) {
        int count = Sms.countByCompanyIdAndPluginId(companyId, pluginId)
        return count
    }

    /**
     * Method to find the list of sms
     * @param companyId - company id
     * @return - a list of sms
     */
    public List findAllByCompanyIdAndPluginId(long companyId, int pluginId) {
        List smsList = Sms.findAllByCompanyIdAndPluginId(companyId, pluginId, [max: resultPerPage, offset: start, sort: SORT_ON_ID, order: ASCENDING_SORT_ORDER, readOnly: true])
        return smsList
    }

    /**
     * Create default data for sms
     * @param companyId -id of company
     */
    public void createDefaultData(long companyId) {
        new Sms(url: '"http://api2.planetgroupbd.com/api/sendsms/plain?user=tajul&password=asd123&sender=Corolla&SMSText=" + content + "&GSM=" + recipient',
        body: '"""This is a TEST SMS"""', description: 'Test SMS', transactionCode: 'SendSmsActionService', companyId: companyId,
        isActive: true, recipients: '+8801675207859', isManualSend: true, pluginId: 1, controllerName: 'sms', actionName: 'sendSms').save()
    }
}
