<script type="text/javascript">
    $(document).ready(function () {
        loadNumberedMenu(MENU_ID_DOCUMENT, "#docCategory/showCategories");
        var containerHeight = $('#contentHolder').height();
        var headingHeight = $('.panel-heading').height();
        $('#listMyCategory').css('height', containerHeight - headingHeight - 125);
    });
</script>