<%@ page import="com.athena.mis.exchangehouse.config.ExhSysConfigurationIntf; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>

<form name="additionalInfoForm" id="additionalInfoForm" class="form-horizontal form-widgets" role="form" method="post">
    <div class="panel-body">
        <div class="col-md-6">
            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="phone">Phone:</label>

                <div class="col-md-5">
                    <div class="input-group">
                        <span class="input-group-addon" style="padding: 3px" id="isdCode"></span>
                        <input type="text" class="k-textbox" id="phone" name="phone" tabindex="1"
                               required validationMessage="Required"/>
                    </div>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="phone"></span>
                </div>
            </div>
            <div class="form-group">
                <label class="col-md-4 control-label label-optional" for="email">Email:</label>

                <div class="col-md-5">
                    <input type="text" class="k-textbox" id="email" name="email" tabindex="2"/>
                </div>

            </div>
            <div class="form-group">
                <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_CUSTOMER_DECLARATION_AMOUNT}" value="0">
                    <label class="col-md-4 control-label label-optional"
                           for="declarationAmount">Declaration Amount:</label>

                    <div class="col-md-5">
                        <input type="text" class="k-textbox" id="declarationAmount" name="declarationAmount"
                               tabindex="3"/>
                    </div>
                </exh:checkSysConfig>
                <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_CUSTOMER_DECLARATION_AMOUNT}" value="1">
                    <label class="col-md-4 control-label label-required"
                           for="declarationAmount">Declaration Amount:</label>

                    <div class="col-md-5">
                        <input type="text" class="k-textbox" id="declarationAmount" name="declarationAmount"
                               tabindex="3"
                               required validationMessage="Required"/>
                    </div>
                </exh:checkSysConfig>
                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="declarationAmount"></span>
                </div>
            </div>

            <div class="form-group">
                <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_CUSTOMER_DECLARATION_AMOUNT}" value="0">
                    <label class="col-md-4 control-label label-optional"
                           for="declarationStart">Declaration Start:</label>

                    <div class="col-md-5">
                        <app:dateControl name="declarationStart" tabindex="4" value="" placeholder="dd/MM/yyyy">
                        </app:dateControl>

                    </div>
                </exh:checkSysConfig>
                <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_CUSTOMER_DECLARATION_AMOUNT}" value="1">
                    <label class="col-md-4 control-label label-required"
                           for="declarationStart">Declaration Start:</label>

                    <div class="col-md-5">
                        <app:dateControl name="declarationStart" tabindex="4" placeholder="dd/MM/yyyy"
                                         value="" validationMessage="Required">
                        </app:dateControl>

                    </div>
                </exh:checkSysConfig>
                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="declarationStart"></span>
                </div>
            </div>

            <div class="form-group">
                <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_CUSTOMER_DECLARATION_AMOUNT}" value="0">
                    <label class="col-md-4 control-label label-optional" for="declarationEnd">Declaration End:</label>

                    <div class="col-md-5">
                        <app:dateControl name="declarationEnd"
                                         value="" tabindex="5" placeholder="dd/MM/yyyy">
                        </app:dateControl>

                    </div>
                </exh:checkSysConfig>
                <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_CUSTOMER_DECLARATION_AMOUNT}" value="1">
                    <label class="col-md-4 control-label label-required" for="declarationEnd">Declaration End:</label>

                    <div class="col-md-5">
                        <app:dateControl name="declarationEnd" tabindex="5"
                                         value="" placeholder="dd/MM/yyyy" validationMessage="Required">
                        </app:dateControl>

                    </div>
                </exh:checkSysConfig>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="declarationEnd"></span>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="profession">Profession:</label>

                <div class="col-md-5">
                    <input type="text" class="k-textbox" id="profession" name="profession" tabindex="6"
                           required validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="profession"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="sourceOfFund">Source of Fund:</label>

                <div class="col-md-5">
                    <input type="text" class="k-textbox" id="sourceOfFund" name="sourceOfFund" tabindex="7"
                           required validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="sourceOfFund"></span>
                </div>
            </div>
            <div class="form-group">
                <label class="col-md-4 control-label label-optional"
                       for="mailSubscription">Mail Subscription:</label>

                <div class="col-md-5">
                    <input type="checkbox" id="mailSubscription" name="mailSubscription" tabindex="8"/>
                </div>
            </div>
            <div class="form-group">
                <label class="col-md-4 control-label label-optional"
                       for="smsSubscription">SMS Subscription:</label>

                <div class="col-md-5">
                    <input type="checkbox" id="smsSubscription" name="smsSubscription" tabindex="9"/>
                </div>
            </div>
        </div>
    </div>
</form>