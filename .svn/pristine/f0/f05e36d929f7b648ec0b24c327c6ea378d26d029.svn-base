<app:ifAnyUrl urls="/fxdFixedAssetDetails/show,/fxdFixedAssetTrace/show,/fxdMaintenance/show">
    <a href="#"><span><i class="tap-pre fixed-asset-tab"></i></span><span class="tabText">Fixed Asset</span></a>

    <div id='0'>
        <ul class="menuDiv">
            <app:ifAnyUrl urls="/fxdFixedAssetDetails/show,/fxdFixedAssetTrace/show,/fxdMaintenance/show">
                <li><span><i class="pre-icon store_in_from_site"></i></span> <span class="menuText"
                                                                                   style="padding-left:36px">Fixed Asset Details</span>


                    <ul class="menuDivSub">
                        <app:ifAllPlugins names="Procurement,Inventory">
                            <sec:access url="/fxdFixedAssetDetails/show">
                                <li><a class='autoload' href="#fxdFixedAssetDetails/show"><span><i
                                        class="pre-icon-sub budget"></i></span> <span
                                        class="menuTextSub">Create</span></a></li>
                            </sec:access>
                        </app:ifAllPlugins>

                        <app:ifPlugin name="Inventory">
                            <sec:access url="/fxdFixedAssetTrace/show">
                                <li><a class='autoload' href="#fxdFixedAssetTrace/show"><span><i
                                        class="pre-icon-sub move"></i>
                                </span> <span
                                        class="menuTextSub">Move</span></a></li>
                            </sec:access>
                        </app:ifPlugin>

                        <sec:access url="/fxdMaintenance/show">
                            <li><a class='autoload' href="#fxdMaintenance/show"><span><i
                                    class="pre-icon-sub maintainance"></i>
                            </span> <span
                                    class="menuTextSub">Maintenance</span></a></li>
                        </sec:access>
                    </ul>

                </li>
            </app:ifAnyUrl>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl
        urls="/fixedAssetReport/showConsumptionAgainstAsset,/fixedAssetReport/showConsumptionAgainstAssetDetails,
        /fixedAssetReport/showPendingFixedAsset,/fixedAssetReport/showCurrentFixedAsset">
    <a href="#">
        <span>
            <i class="tap-pre report-tab"></i>
        </span>
        <span class="tabText">Report</span>
    </a>

    <div id='2'>
        <ul class="menuDiv">
            <app:ifPlugin name="Inventory">
                <app:ifAnyUrl
                        urls="/fixedAssetReport/showConsumptionAgainstAsset,/fixedAssetReport/showConsumptionAgainstAssetDetails">
                    <li>
                        <span>
                            <i class="pre-icon inventory-sotore-report"></i>
                        </span>
                        <span class="menuText" style="padding-left:36px">Consumption Against Asset</span>
                        <ul class="menuDivSub">
                            <sec:access url="/fixedAssetReport/showConsumptionAgainstAsset">
                                <li>
                                    <a class='autoload' href="#fixedAssetReport/showConsumptionAgainstAsset">
                                        <span>
                                            <i class="pre-icon-sub consumption_against_assets"></i>
                                        </span>
                                        <span class="menuTextSub">Summary</span>
                                    </a>
                                </li>
                            </sec:access>

                            <sec:access url="/fixedAssetReport/showConsumptionAgainstAssetDetails">
                                <li>
                                    <a class='autoload' href="#fixedAssetReport/showConsumptionAgainstAssetDetails">
                                        <span>
                                            <i class="pre-icon-sub supplier_in"></i>
                                        </span>
                                        <span class="menuTextSub">Details</span>
                                    </a>
                                </li>
                            </sec:access>
                        </ul>
                    </li>
                    <li>&nbsp</li>
                </app:ifAnyUrl>

                <sec:access url="/fixedAssetReport/showPendingFixedAsset">
                    <li><a class='autoload' href="#fixedAssetReport/showPendingFixedAsset"><span><i
                            class="pre-icon Pending-Fixed-Asset"></i></span> <span
                            class="menuText">Pending Fixed Asset</span></a></li>
                </sec:access>
                <sec:access url="/fixedAssetReport/showCurrentFixedAsset">
                    <li><a class='autoload' href="#fixedAssetReport/showCurrentFixedAsset"><span><i
                            class="pre-icon Current-Fixed-Asset"></i></span> <span
                            class="menuText">Current Fixed Asset</span></a></li>
                </sec:access>
            </app:ifPlugin>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl
        urls="/systemConfiguration/show,/sms/showSms,/appMail/show">
    <a href="#"><span><i class="tap-pre development-tab"></i></span><span class="tabText">Development</span></a>

    <div id='3'>
        <ul class="menuDiv">
            <sec:access url="/systemConfiguration/show">
                <li><a class='autoload' href="#systemConfiguration/show?plugin=7"><span><i
                        class="pre-icon sys-configuration"></i></span> <span
                        class="menuText">System Configuration</span></a></li>
            </sec:access>
            <sec:access url="/appMail/show">
                <li><a class='autoload' href="#appMail/show?plugin=7"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">Mail</span></a></li>
            </sec:access>

            <sec:access url="/sms/showSms">
                <li><a class='autoload' href="#sms/showSms?plugin=7"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">SMS</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/fxdMaintenanceType/show, /item/showFixedAssetItem">
    <a href="#">
        <span><i class="tap-pre settings-tab"></i></span>
        <span class="tabText">Setting</span>
    </a>

    <div id='4'>
        <ul class="menuDiv">
            <sec:access url="/fxdMaintenanceType/show">
                <li><a class='autoload' href="#fxdMaintenanceType/show">
                    <span><i class="pre-icon maintainance"></i></span>
                    <span class="menuText">Maintenance Type</span></a>
                </li>
            </sec:access>

            <sec:access url="/item/showFixedAssetItem">
                <li><a class="autoload" href="#item/showFixedAssetItem"><span><i class="pre-icon item"></i>
                </span><span class="menuText">Fixed Asset Item</span></a></li>
            </sec:access>
        </ul>
    </div>

</app:ifAnyUrl>

<div id="menuBottomDiv" style="height:60px; display: none;">
</div>
