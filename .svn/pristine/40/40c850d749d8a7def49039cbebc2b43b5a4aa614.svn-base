package com.athena.mis.fixedassetdetails.controller

import grails.util.Environment

class FixedAssetController {
    static allowedMethods = [renderFixedAssetMenu: "POST"]

    def springSecurityService

    /**
     *
     * @return - fixed asset menu including left-menu, dash board
     */
    def renderFixedAssetMenu() {
        if (!springSecurityService.isLoggedIn()) {
            render('false')
            return
        }
        render(contentType: "text/json") {
            lstTemplates = array {
                element([name: 'menu', content: g.render(plugin: 'fixedAsset', template: '/fixedAsset/leftmenuFixedAsset')])
                element([name: 'dashBoard', content: g.render(plugin: 'fixedAsset', template: '/fixedAsset/dashBoardFixedAsset', model: null)])
            }
        }
    }
}
