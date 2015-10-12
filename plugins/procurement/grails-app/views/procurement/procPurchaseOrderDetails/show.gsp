
<g:render template='/procurement/procPurchaseOrderDetails/script'/>

<app:ifAllUrl urls="/procPurchaseOrderDetails/create,/procPurchaseOrderDetails/update, /procPurchaseOrderDetails/select">
    <g:render template='/procurement/procPurchaseOrderDetails/create'/>
</app:ifAllUrl>


<table id="flex1" style="display:none"></table>

