package com.athena.mis.fixedassetdetails.controller

import com.athena.mis.fixedasset.actions.report.consumptionagainstasset.DownloadForConsumptionAgainstAssetActionService
import com.athena.mis.fixedasset.actions.report.consumptionagainstasset.GetForConsumptionAgainstAssetDetailsActionService
import com.athena.mis.fixedasset.actions.report.consumptionagainstasset.ListForConsumptionAgainstAssetActionService
import com.athena.mis.fixedasset.actions.report.consumptionagainstassetdetails.DownloadCSVForConsumptionAgainstAssetDetailsActionService
import com.athena.mis.fixedasset.actions.report.consumptionagainstassetdetails.DownloadForConsumptionAgainstAssetDetailsActionService
import com.athena.mis.fixedasset.actions.report.consumptionagainstassetdetails.ListForConsumptionAgainstAssetDetailsActionService
import com.athena.mis.fixedasset.actions.report.consumptionagainstassetdetails.ShowForConsumptionAgainstAssetDetailsActionService
import com.athena.mis.fixedasset.actions.report.currentfixedasset.*
import com.athena.mis.fixedasset.actions.report.pendingfixedasset.DownloadForPendingFixedAssetActionService
import com.athena.mis.fixedasset.actions.report.pendingfixedasset.ListForPendingFixedAssetActionService
import com.athena.mis.utility.DateUtility
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class FixedAssetReportController {

    static allowedMethods = [
            showPendingFixedAsset: "POST",
            listPendingFixedAsset: "POST",

            showConsumptionAgainstAsset: "POST",
            listConsumptionAgainstAsset: "POST",
            getConsumptionAgainstAssetDetails: "POST",
            showCurrentFixedAsset: "POST",
            listCurrentFixedAsset: "POST"
    ]
    ListForPendingFixedAssetActionService listForPendingFixedAssetActionService
    DownloadForPendingFixedAssetActionService downloadForPendingFixedAssetActionService

    ListForConsumptionAgainstAssetActionService listForConsumptionAgainstAssetActionService
    GetForConsumptionAgainstAssetDetailsActionService getForConsumptionAgainstAssetDetailsActionService
    DownloadForConsumptionAgainstAssetActionService downloadForConsumptionAgainstAssetActionService

    ShowForCurrentFixedAssetActionService showForCurrentFixedAssetActionService
    ListForCurrentFixedAssetActionService listForCurrentFixedAssetActionService
    DownloadForCurrentFixedAssetActionService downloadForCurrentFixedAssetActionService
    SearchForCurrentFixedAssetActionService searchForCurrentFixedAssetActionService
    DownloadForCurrentFixedAssetCsvActionService downloadForCurrentFixedAssetCsvActionService

    ShowForConsumptionAgainstAssetDetailsActionService showForConsumptionAgainstAssetDetailsActionService
    ListForConsumptionAgainstAssetDetailsActionService listForConsumptionAgainstAssetDetailsActionService
    DownloadForConsumptionAgainstAssetDetailsActionService downloadForConsumptionAgainstAssetDetailsActionService
    DownloadCSVForConsumptionAgainstAssetDetailsActionService downloadCSVForConsumptionAgainstAssetDetailsActionService
    /**
     *
     * @return - only render show page for pending fixed asset page
     */
    def showPendingFixedAsset() {
        render(view: '/fixedAsset/report/pendingFixedAsset/show', model: [modelJson: null])
    }
    /**
     *
     * @return - list of pending fixed asset
     */
    def listPendingFixedAsset() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForPendingFixedAssetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForPendingFixedAssetActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForPendingFixedAssetActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForPendingFixedAssetActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForPendingFixedAssetActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }
    /**
     *
     * @return - download pdf report for pending fixed asset
     */
    def downloadPendingFixedAsset() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForPendingFixedAssetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForPendingFixedAssetActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForPendingFixedAssetActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForPendingFixedAssetActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    //************ For Consumption Against Fixed report *******\\
    /**
     *
     * @return - only render show page for consumption against fixed asset page
     */
    def showConsumptionAgainstAsset() {
        render(view: '/fixedAsset/report/consumptionAgainstAsset/show', model: [modelJson: null])
    }
    /**
     *
     * @return - list of consumption against fixed asset
     */
    def listConsumptionAgainstAsset() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForConsumptionAgainstAssetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForConsumptionAgainstAssetActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForConsumptionAgainstAssetActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForConsumptionAgainstAssetActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForConsumptionAgainstAssetActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output

    }
    /**
     *
     * @return - list of consumption against fixed asset details
     */
    def getConsumptionAgainstAssetDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) getForConsumptionAgainstAssetDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getForConsumptionAgainstAssetDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) getForConsumptionAgainstAssetDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getForConsumptionAgainstAssetDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) getForConsumptionAgainstAssetDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }
    /**
     *
     * @return - download pdf report for consumption against fixed asset
     */
    def downloadConsumptionAgainstAsset() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForConsumptionAgainstAssetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForConsumptionAgainstAssetActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForConsumptionAgainstAssetActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForConsumptionAgainstAssetActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()

    }

    //Current Fixed Asset Report
    /**
     *
     * @return - only render show page for current fixed asset page
     */
    def showCurrentFixedAsset() {
        Map executeResult = (Map) showForCurrentFixedAssetActionService.execute(params, null)
        render(view: '/fixedAsset/report/currentFixedAsset/show', model: [modelJson: executeResult as JSON])
    }
    /**
     *
     * @return - list of current fixed asset
     */
    def listCurrentFixedAsset() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        if (params.query) {
            executeResult = (LinkedHashMap) searchForCurrentFixedAssetActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForCurrentFixedAssetActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) searchForCurrentFixedAssetActionService.buildSuccessResultForUI(executeResult);
            output = result as JSON
            render output
        } else {
            preResult = (LinkedHashMap) listForCurrentFixedAssetActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForCurrentFixedAssetActionService.buildFailureResultForUI(preResult);
                output = result as JSON
                render output
                return;
            }

            executeResult = (LinkedHashMap) listForCurrentFixedAssetActionService.execute(params, preResult);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForCurrentFixedAssetActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) listForCurrentFixedAssetActionService.buildSuccessResultForUI(executeResult);
            output = result as JSON
            render output

        }
    }
    /**
     *
     * @return - download pdf report for current fixed asset
     */
    def downloadCurrentFixedAsset() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForCurrentFixedAssetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForCurrentFixedAssetActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForCurrentFixedAssetActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForCurrentFixedAssetActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()

    }
    /**
     *
     * @return - download csv report for current fixed asset
     */
    def downloadCurrentFixedAssetCsv() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForCurrentFixedAssetCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForCurrentFixedAssetCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForCurrentFixedAssetCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForCurrentFixedAssetCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()

    }

    //************ For Consumption Against Fixed Asset Details report *******\\
    /**
     *
     * @return render show page for consumption against fixed asset details page with fixed asset items list
     */
    def showConsumptionAgainstAssetDetails() {
        Map executeResult = (Map) showForConsumptionAgainstAssetDetailsActionService.execute(params, null)
        String date = DateUtility.getDateForUI(new Date() - 30)
        executeResult = [date: date]
        render(view: '/fixedAsset/report/consumptionAgainstAssetDetails/show', model: [modelJson: executeResult as JSON])
    }
    /**
     *
     * @return - list of consumption against fixed asset details(items)
     */
    def listConsumptionAgainstAssetDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForConsumptionAgainstAssetDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForConsumptionAgainstAssetDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) listForConsumptionAgainstAssetDetailsActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForConsumptionAgainstAssetDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForConsumptionAgainstAssetDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }
    /**
     *
     * @return - download pdf report for consumption against fixed asset details
     */
    def downloadConsumptionAgainstAssetDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

            preResult = (LinkedHashMap) downloadForConsumptionAgainstAssetDetailsActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) downloadForConsumptionAgainstAssetDetailsActionService.buildFailureResultForUI(preResult)
                output = result as JSON
                render output
                return
            }

            executeResult = (LinkedHashMap) downloadForConsumptionAgainstAssetDetailsActionService.execute(params, preResult)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) downloadForConsumptionAgainstAssetDetailsActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return
            }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }
    /**
     *
     * @return - download csv report for consumption against fixed asset details
     */
    def downloadConsumptionAgainstAssetDetailsCsv() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

            preResult = (LinkedHashMap) downloadCSVForConsumptionAgainstAssetDetailsActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) downloadCSVForConsumptionAgainstAssetDetailsActionService.buildFailureResultForUI(preResult)
                output = result as JSON
                render output
                return
            }

            executeResult = (LinkedHashMap) downloadCSVForConsumptionAgainstAssetDetailsActionService.execute(params, preResult)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) downloadCSVForConsumptionAgainstAssetDetailsActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return
            }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

}
