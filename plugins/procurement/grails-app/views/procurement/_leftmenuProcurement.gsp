<app:ifAnyUrl urls="/procPurchaseRequest/show,/procPurchaseOrder/show,/procIndent/show">
    <a href="#"><span><i class="tap-pre procurement-tab"></i></span><span class="tabText">Procurement</span></a>

    <div class="2">
        <ul class="menuDiv">

            <app:ifPlugin name="Budget">
                <sec:access url="/procPurchaseRequest/show">
                    <li><a class='autoload' href="#procPurchaseRequest/show"><span><i
                            class="pre-icon purchase-request"></i>
                    </span> <span
                            class="menuText">Purchase Request</span></a></li>
                </sec:access>
            </app:ifPlugin>

            <app:ifAnyUrl
                    urls="/procPurchaseOrder/show,/procCancelledPO/show">
                <li>&nbsp;</li>
                <li><span><i class="pre-icon purchase-order"></i></span>
                    <span class="menuText" style="padding-left:36px">Purchase Order</span>
                    <ul class="menuDivSub">
                        <sec:access url="/procPurchaseOrder/show">
                            <li><a class='autoload'
                                   href="#procPurchaseOrder/show"><span><i
                                        class="pre-icon-sub supplier_in"></i></span> <span
                                        class="menuTextSub">Issued</span></a></li>
                        </sec:access>
                        <sec:access url="/procCancelledPO/show">
                            <li><a class='autoload'
                                   href="#procCancelledPO/show"><span><i
                                        class="pre-icon-sub pre-icon site_in"></i></span> <span
                                        class="menuTextSub">Cancelled</span></a></li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

            <sec:access url="/procIndent/show">
                <li><a class='autoload' href="#procIndent/show"><span><i class="pre-icon material-demand-form"></i>
                </span> <span
                        class="menuText">Indent</span></a></li>
            </sec:access>
        </ul>
    </div>

</app:ifAnyUrl>



<app:ifAnyUrl urls="/procReport/showPurchaseRequestRpt,/procReport/showPurchaseOrderRpt,
/procReport/showIndentRpt,/procReport/showSupplierWisePO">
    <a href="#"><span><i class="tap-pre report-tab"></i></span><span class="tabText">Report</span></a>


    <div class="2">
        <ul class="menuDiv">
            <sec:access url="/procReport/showPurchaseRequestRpt">
                <li><a class='autoload' href="#procReport/showPurchaseRequestRpt"><span>
                    <i class="pre-icon purchase-request-report"></i></span> <span
                        class="menuText">Purchase Request</span></a></li>
            </sec:access>
            <sec:access url="/procReport/showPurchaseOrderRpt">
                <li><a class='autoload' href="#procReport/showPurchaseOrderRpt"><span><i
                        class="pre-icon purchase-order"></i></span> <span
                        class="menuText">Purchase Order</span></a></li>
            </sec:access>
            <sec:access url="/procReport/showIndentRpt">
                <li><a class='autoload' href="#procReport/showIndentRpt"><span><i
                        class="pre-icon material-demand-report"></i></span> <span
                        class="menuText">Indent</span></a></li>
            </sec:access>

            <app:ifAllPlugins names="Inventory,FixedAsset">
                <sec:access url="/procReport/showSupplierWisePO">
                    <li><a class='autoload' href="#procReport/showSupplierWisePO"><span><i
                            class="pre-icon supplier-wise-po"></i></span> <span
                            class="menuText">Supplier Wise PO</span></a></li>
                </sec:access>
            </app:ifAllPlugins>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl
        urls="/systemConfiguration/show,/sms/showSms,/appMail/show">
    <a href="#"><span><i class="tap-pre development-tab"></i></span><span class="tabText">Development</span></a>

    <div id='3'>
        <ul class="menuDiv">
            <sec:access url="/systemConfiguration/show">
                <li><a class='autoload' href="#systemConfiguration/show?plugin=5"><span><i
                        class="pre-icon sys-configuration"></i></span> <span
                        class="menuText">System Configuration</span></a></li>
            </sec:access>

            <sec:access url="/appMail/show">
                <li><a class='autoload' href="#appMail/show?plugin=5"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">Mail</span></a></li>
            </sec:access>

            <sec:access url="/sms/showSms">
                <li><a class='autoload' href="#sms/showSms?plugin=5"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">SMS</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<div id="menuBottomDiv" style=" height:60px; display: none;">
</div>



