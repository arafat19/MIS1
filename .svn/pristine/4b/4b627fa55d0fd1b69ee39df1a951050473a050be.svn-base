package com.athena.mis.sarb.actions.taskmodel

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.sarb.config.SarbSysConfigurationCacheUtility
import com.athena.mis.sarb.entity.SarbTaskDetails
import com.athena.mis.sarb.model.SarbTaskModel
import com.athena.mis.sarb.service.SarbTaskDetailsService
import com.athena.mis.sarb.service.SarbTaskModelService
import com.athena.mis.sarb.utility.SarbSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class RetrieveSarbTaskResponseActionService extends BaseService implements ActionIntf {

    SarbTaskModelService sarbTaskModelService

    private static final String FAILED_TO_SEND = "Failed to retrieve task status."
    private static final String TASK_NOT_FOUND = "Task not found."
    private static final String TASK_STATUS_ALREADY_RETRIEVED = "Task status already retrieved."
    private static final String TASK_RECEIVED_SUCCESSFULLY = "Status successfully retrieved from SARB."
    private static final String CHARSET_UTF8 = "UTF-8"

    private static final String GET = "GET"
    private static final String ACCEPTED = "ACCEPTED"
    private static final String STATUS = "Status"
    private static final String TRANSACTION = "Transaction"
    private static final String TRN_REFERENCE = "TrnReference"
    private static final String REF_NUMBER = "RefNumber"
    private static final String FILE_REFERENCE = "FileReference"
    private static final String DESCRIPTION = "description"
    private static final String CODE = "code"
    private static final String ERROR = "Error"
    private static final String ERRORS = "Errors"
    private static final String REFERENCE = "reference"
    private static final String ERROR_IN_CONNECTION = "Connection to SARB failed. Exception: "

    private final Logger log = Logger.getLogger(getClass())

    SarbTaskDetailsService sarbTaskDetailsService
    @Autowired
    SarbSysConfigurationCacheUtility sarbSysConfigurationCacheUtility
    @Autowired
    SarbSessionUtil sarbSessionUtil

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {

        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long taskId = Long.parseLong(params.id)
            SarbTaskModel sarbTaskModel = sarbTaskModelService.read(taskId)
            if (!sarbTaskModel) {
                result.put(Tools.MESSAGE, TASK_NOT_FOUND)
                return result
            }
            if (sarbTaskModel.sarbRefNo) {
                result.put(Tools.MESSAGE, TASK_STATUS_ALREADY_RETRIEVED)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, sarbTaskModel)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_SEND)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            SarbTaskModel taskModel = (SarbTaskModel) executeResult.get(Tools.ENTITY)
            SarbTaskDetails taskDetails = sarbTaskDetailsService.findByTaskIdAndEnabled(taskModel.id)
            String msg = getFirstResponse(taskModel)
            if (msg) {
                result.put(Tools.MESSAGE, msg)
                return result
            }
            parseFirstResponse(taskModel)
            if (taskModel.sarbRefNo) {
                msg = getSecondResponse(taskModel)
                if (msg) {
                    result.put(Tools.MESSAGE, msg)
                    return result
                }
                parseSecondResponse(taskModel)
            }
            buildSarbTaskDetails(taskModel, taskDetails)
            sarbTaskDetailsService.updateForRetrieveResponse(taskDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_SEND)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {

        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, TASK_RECEIVED_SUCCESSFULLY)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_SEND)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {

        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILED_TO_SEND)
            }
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_SEND)
            return result
        }
    }

    //"https://sarbdexqp.resbank.co.za:444/SARBDEX/getmsgbysarbref.asp?sarbref=${taskModel.originalFileName}?SOUTHEASTBANK_FINSURV?athena@321"
    private String getFirstResponse(SarbTaskModel taskModel) {
        String retrieveReferenceUrl =
                getRetrieveReferenceUrl() + "?sarbref=" + taskModel.originalFileName + "?" + getSarbUserName() + "?" + getSarbPassword()
        URL url = new URL(retrieveReferenceUrl)
        HttpURLConnection myConnection = null
        try {
            myConnection = (HttpURLConnection) url.openConnection()
            myConnection.setDoOutput(true)
            myConnection.setDoInput(true)
            myConnection.setUseCaches(false)
            myConnection.setDefaultUseCaches(false)
            myConnection.setRequestMethod(GET)
            myConnection.connect()


            InputStream inputStream
            int responseCode = myConnection.getResponseCode()
            if ((responseCode >= 200) && (responseCode <= 202)) {
                inputStream = myConnection.getInputStream()
                String output = convertToString(inputStream)
                taskModel.responseOfRetrieveReference = output
            }
            myConnection.disconnect()
            return null
        } catch (ProtocolException e) {
            return ERROR_IN_CONNECTION + e.getMessage()
        }
    }

    //"https://sarbdexqp.resbank.co.za:444/SARBDEX/getmsgbysarbref.asp?sarbref=${taskModel.sarbRefNo + Tools.XML_EXTENSION}?SOUTHEASTBANK_FINSURV?athena@321"
    private String getSecondResponse(SarbTaskModel taskModel) {
        String retrieveResponseUrl =
                getRetrieveResponseUrl() + "?sarbref=" + taskModel.sarbRefNo + Tools.XML_EXTENSION + "?" + getSarbUserName() + "?" + getSarbPassword()
        URL url = new URL(retrieveResponseUrl)
        HttpURLConnection myConnection = null
        try {
            myConnection = (HttpURLConnection) url.openConnection()
            myConnection.setDoOutput(true)
            myConnection.setDoInput(true)
            myConnection.setUseCaches(false)
            myConnection.setDefaultUseCaches(false)
            myConnection.setRequestMethod(GET)
            myConnection.connect()


            InputStream inputStream
            int responseCode = myConnection.getResponseCode()
            if ((responseCode >= 200) && (responseCode <= 202)) {
                inputStream = myConnection.getInputStream()
                String output = convertToString(inputStream)
                taskModel.responseOfReference = output
            }
            myConnection.disconnect()
            return null
        } catch (ProtocolException e) {
            return ERROR_IN_CONNECTION + e.getMessage()
        }
    }


    private String convertToString(InputStream inputStream) {
        String inputStreamString = new Scanner(inputStream, CHARSET_UTF8).useDelimiter("\\A").next();
        return inputStreamString
    }

/*  <Errors reference='10820140000010'>
    <Error code='0' description='SARBREFNEE2074AFEA884519BC45E9FEFEF3D780' level='' severity=''/>
    </Errors> */

    private void parseFirstResponse(SarbTaskModel taskModel) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
        DocumentBuilder docBuilder = factory.newDocumentBuilder()
        InputSource is = new InputSource(new StringReader(taskModel.responseOfRetrieveReference))
        Document doc = docBuilder.parse(is)
        doc.getDocumentElement().normalize()
        Element rootElement = doc.getDocumentElement()
        if (!rootElement.nodeName.equalsIgnoreCase(ERRORS)) {
            return
        }
        String fileReference = rootElement.getAttribute(REFERENCE)
        if (!fileReference.equals(taskModel.submittedFileCount.toString())) {
            return
        }

        NodeList lstNodes = doc.getElementsByTagName(ERROR)
        if (lstNodes.length == 0) return

        Node errorNode = lstNodes.item(0)
        Element errorElement = (Element) errorNode
        String errCode = errorElement.getAttribute(CODE)
        if (Integer.parseInt(errCode) != 0) {
            return
        }
        String sarbRefNo = errorElement.getAttribute(DESCRIPTION)
        taskModel.sarbRefNo = sarbRefNo
    }

    /*
       <FINSURV>
          <FileReference RefNumber="10820140000013" Status="ACCEPTED">
            <Transaction LineNumber="1" ReportingQualifier="BOPCUS" Status="ACCEPTED" TrnReference="SFSL0113">
            </Transaction>
          </FileReference>
       </FINSURV>
     */

    private void parseSecondResponse(SarbTaskModel taskModel) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
        DocumentBuilder docBuilder = factory.newDocumentBuilder()
        InputSource is = new InputSource(new StringReader(taskModel.responseOfReference))
        Document doc = docBuilder.parse(is)
        doc.getDocumentElement().normalize()

        // check FileReference node
        NodeList lstFileRefNodes = doc.getElementsByTagName(FILE_REFERENCE)
        if (lstFileRefNodes.length == 0) return

        Node fileRefNode = lstFileRefNodes.item(0)
        Element fileRefElement = (Element) fileRefNode

        String fileCount = fileRefElement.getAttribute(REF_NUMBER)
        if (!fileCount.equals(taskModel.submittedFileCount.toString())) {
            return
        }
        String fileRefStatus = fileRefElement.getAttribute(STATUS)
        if (!fileRefStatus.equalsIgnoreCase(ACCEPTED)) {
            return
        }

        // check Transaction node

        NodeList lstTransactionNodes = doc.getElementsByTagName(TRANSACTION)
        if (lstTransactionNodes.length == 0) return

        Node transactionNode = lstTransactionNodes.item(0)
        Element transactionElement = (Element) transactionNode

        String transactionRefNo = transactionElement.getAttribute(TRN_REFERENCE)
        if (!transactionRefNo.equals(taskModel.refNo)) {
            return
        }

        String transactionStatus = transactionElement.getAttribute(STATUS)
        if (!transactionStatus.equalsIgnoreCase(ACCEPTED)) {
            return
        }
        taskModel.isAcceptedBySarb = true
    }

    private void buildSarbTaskDetails(SarbTaskModel taskModel, SarbTaskDetails taskDetails) {
        taskDetails.responseOfRetrieveReference = taskModel.responseOfRetrieveReference
        taskDetails.sarbRefNo = taskModel.sarbRefNo
        taskDetails.responseOfReference = taskModel.responseOfReference
        taskDetails.isAcceptedBySarb = taskModel.isAcceptedBySarb
    }

    private String getRetrieveReferenceUrl() {
        long companyId = sarbSessionUtil.appSessionUtil.getCompanyId()
        String url = Tools.EMPTY_SPACE
        boolean isProd = sarbSysConfigurationCacheUtility.isProductionMode(companyId)
        if (isProd) {
            SysConfiguration prodSendToSarb = sarbSysConfigurationCacheUtility.readByKeyAndCompanyId(SarbSysConfigurationCacheUtility.SARB_URL_RETRIEVE_REFERENCE_PROD, companyId)
            url = prodSendToSarb.value
        } else {
            SysConfiguration devSendToSarb = sarbSysConfigurationCacheUtility.readByKeyAndCompanyId(SarbSysConfigurationCacheUtility.SARB_URL_RETRIEVE_REFERENCE_DEV, companyId)
            url = devSendToSarb.value
        }
        return url
    }

    private String getRetrieveResponseUrl() {
        long companyId = sarbSessionUtil.appSessionUtil.getCompanyId()
        String url = Tools.EMPTY_SPACE
        boolean isProd = sarbSysConfigurationCacheUtility.isProductionMode(companyId)
        if (isProd) {
            SysConfiguration prodSendToSarb = sarbSysConfigurationCacheUtility.readByKeyAndCompanyId(SarbSysConfigurationCacheUtility.SARB_URL_RETRIEVE_RESPONSE_PROD, companyId)
            url = prodSendToSarb.value
        } else {
            SysConfiguration devSendToSarb = sarbSysConfigurationCacheUtility.readByKeyAndCompanyId(SarbSysConfigurationCacheUtility.SARB_URL_RETRIEVE_RESPONSE_DEV, companyId)
            url = devSendToSarb.value
        }
        return url
    }

    private String getSarbUserName() {
        long companyId = sarbSessionUtil.appSessionUtil.getCompanyId()
        String userName = Tools.EMPTY_SPACE
        SysConfiguration configUserName = sarbSysConfigurationCacheUtility.readByKeyAndCompanyId(SarbSysConfigurationCacheUtility.SARB_USER_NAME, companyId)
        if (configUserName) {
            userName = configUserName.value
        }
        return userName
    }

    private String getSarbPassword() {
        long companyId = sarbSessionUtil.appSessionUtil.getCompanyId()
        String pwd = Tools.EMPTY_SPACE
        SysConfiguration configPwd = sarbSysConfigurationCacheUtility.readByKeyAndCompanyId(SarbSysConfigurationCacheUtility.SARB_PASSWORD, companyId)
        if (configPwd) {
            pwd = configPwd.value
        }
        return pwd
    }

}
