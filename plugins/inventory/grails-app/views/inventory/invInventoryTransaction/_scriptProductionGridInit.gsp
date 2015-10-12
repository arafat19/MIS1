<script type="text/javascript">

    function initFlexRawMaterials() {
        $("#flexRawMaterial").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Item Id", name: "materialIdRaw", width: 20, align: "right", hide: true},
                        {display: "Material", name: "materialNameRaw", width: 150, align: "left"},
                        {display: "Quantity", name: "quantityRaw", width: 80, align: "left"},
                        {display: "Quantity Value", name: "quantityValueRaw", width: 50, align: "right", hide: true},
                        {display: "Tr. Details ID", name: "trDetailsId", width: 20, align: "right", hide: true}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editRawMaterials},
                        {separator: true}
                    ],
                    searchitems: [
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    singleSelect: true,
                    title: false,
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: 80,
                    resizable: false,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }
    function initFlexFinishedProducts() {
        $("#flexFinishedMaterial").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "ID", name: "materialIdFinish", width: 20, align: "right", hide: true},
                        {display: "Material", name: "materialNameFinish", width: 150, align: "left"},
                        {display: "Quantity", name: "quantityFinish", width: 80, align: "left"},
                        {display: "Quantity Value", name: "quantityValueFinish", width: 50, align: "right", hide: true},
                        {display: "Tr. Details ID", name: "trDetailsId", width: 20, align: "right", hide: true},
                        {display: "Overhead Cost", name: "overheadCost", width: 50, align: "right", hide: true}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editFinishedProducts},
                        {separator: true}
                    ],
                    searchitems: [
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    singleSelect: true,
                    title: false,
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: 80,
                    resizable: false,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        )
    }
    function initFlexProduction() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    %{--url:"${createLink(controller: 'accVoucher', action: 'list')}",--}%
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "left", hide: true},
                        {display: "Production ID", name: "id", width: 80, sortable: false, align: "left"},
                        {display: "Inventory", name: "inventory", width: 225, sortable: false, align: "left"},
                        {display: "Production Line Item", name: "lineItem", width: 120, sortable: false, align: "left"},
                        {display: "Raw Materials", name: "rawCount", width: 80, sortable: false, align: "right"},
                        {display: "Finished Materials", name: "finishCount", width: 100, sortable: false, align: "right"},
                        {display: "Production Date", name: "createdOn", width: 85, sortable: false, align: "left"},
                        {display: "Created By", name: "createdBy", width: 120, sortable: false, align: "left"},
                        {display: "Updated By", name: "updatedBy", width: 120, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/invInventoryTransaction/selectInvProductionWithConsumption,/invInventoryTransaction/updateInvProductionWithConsumption">
                        {name: 'Edit', bclass: 'edit', onpress: editProduction},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invInventoryTransaction/deleteInvProductionWithConsumption">
                        {name: 'Delete', bclass: 'delete', onpress: deleteInventoryProduction},
                        </app:ifAllUrl>

                        <sec:access url="/invInventoryTransaction/approveInvProdWithConsumption">
                        {name: 'Approve', bclass: 'approve', onpress: approveProduction},
                        </sec:access>

                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadProductionGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Inventory", name: "inv.name", width: 180, sortable: true, align: "left"},
                        {display: "Line Item", name: "line_item.name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "transaction_date",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Unapproved Productions',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 10,
                    customPopulate: customPopulateProductionGrid,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

</script>