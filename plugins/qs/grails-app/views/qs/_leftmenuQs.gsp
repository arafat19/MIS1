<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" %>
<app:ifAnyUrl urls="/qsMeasurement/show,/qsMeasurement/showGovt,/budgSprint/showForCurrentSprint">
    <a href="#"><span><i class="tap-pre budget-tab"></i></span><span class="tabText">QS</span></a>

    <div id='0'>
        <ul class="menuDiv">
            <app:ifPlugin name="Budget">
                <app:ifAnyUrl urls="/qsMeasurement/show,/qsMeasurement/showGovt">
                    <li><span><i class="pre-icon qs_mesurment"></i></span>
                        <span class="menuText" style="padding-left:36px">QS Measurement</span>
                        <ul class="menuDivSub">
                            <sec:access url="/qsMeasurement/show">
                                <li><a class='autoload' href="#qsMeasurement/show"><span><i
                                        class="pre-icon-sub qsMeasurement"></i></span> <span
                                        class="menuTextSub">Internal</span></a></li>
                            </sec:access>
                            <sec:access url="/qsMeasurement/showGovt">
                                <li><a class='autoload' href="#qsMeasurement/showGovt"><span><i
                                        class="pre-icon-sub pre-icon  pay-cheque"></i></span> <span
                                        class="menuTextSub">Government</span></a></li>
                            </sec:access>
                            <li>&nbsp;</li>
                        </ul>
                    </li>
                    <sec:access url="/budgSprint/showForCurrentSprint">
                        <li><a class='autoload' href="#budgSprint/showForCurrentSprint"><span><i
                                class="pre-icon budget-wise-qs"></i></span> <span
                                class="menuText">Current Sprint</span></a></li>
                    </sec:access>
                </app:ifAnyUrl>
            </app:ifPlugin>
        </ul>
    </div>
</app:ifAnyUrl>


<app:ifAnyUrl
        urls="/qsReport/showQsMeasurementRpt,/qsReport/showBudgetContractDetails,/qsReport/showBudgetFinancialSummary,
        /qsReport/showBudgetWiseQs,/qsReport/showCombinedQSM">
    <a href="#"><span><i class="tap-pre report-tab"></i></span><span class="tabText">Report</span></a>

    <div id='1'>
        <ul class="menuDiv">
            <sec:access url="/qsReport/showQsMeasurementRpt">
                <li><a class='autoload' href="#qsReport/showQsMeasurementRpt"><span><i
                        class="pre-icon qsMeasurement_report"></i></span> <span
                        class="menuText">QS Measurement</span></a></li>
            </sec:access>
            <sec:access url="/qsReport/showBudgetContractDetails">
                <li><a class='autoload' href="#qsReport/showBudgetContractDetails"><span><i
                        class="pre-icon budget_con_deatails"></i></span> <span
                        class="menuText">Budget Contract Details</span></a></li>
            </sec:access>
            <sec:access url="/qsReport/showBudgetFinancialSummary">
                <li><a class='autoload' href="#qsReport/showBudgetFinancialSummary"><span><i
                        class="pre-icon budged_financial_sum"></i></span> <span
                        class="menuText">Budget Financial Summary</span></a></li>
            </sec:access>

            <app:ifPlugin name="Budget">
                <sec:access url="/qsReport/showBudgetWiseQs">
                    <li><a class='autoload' href="#qsReport/showBudgetWiseQs"><span><i
                            class="pre-icon budget-wise-qs"></i></span> <span
                            class="menuText">Budget Wise QS</span></a></li>
                </sec:access>
            </app:ifPlugin>

            <app:ifPlugin name="Budget">
                <sec:access url="/qsReport/showCombinedQSM">
                    <li><a class='autoload' href="#qsReport/showCombinedQSM"><span><i
                            class="pre-icon combined_qs_mgt"></i></span> <span
                            class="menuText">Combined QS Measurement</span></a></li>
                </sec:access>
            </app:ifPlugin>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl
        urls="appMail/show,/systemConfiguration/show,/sms/showSms">
    <a href="#"><span><i class="tap-pre development-tab"></i></span><span class="tabText">Development</span></a>

    <div id='2'>
        <ul class="menuDiv">
            <sec:access url="/systemConfiguration/show">
                <li><a class='autoload' href="#systemConfiguration/show?plugin=6"><span><i
                        class="pre-icon sys-configuration"></i></span> <span
                        class="menuText">System Configuration</span></a></li>
            </sec:access>

            <sec:access url="/appMail/show">
                <li><a class='autoload' href="#appMail/show?plugin=6"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">Mail</span></a></li>
            </sec:access>

            <sec:access url="/sms/showSms">
                <li><a class='autoload' href="#sms/showSms?plugin=6"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">SMS</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>


<div id="menuBottomDiv" style="height:60px; display: none;">
</div>
