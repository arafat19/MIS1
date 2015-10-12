package com.athena.mis.sarb.controller

class SarbController {

    static allowedMethods = [renderSarbMenu: "POST"]

    def springSecurityService

    def renderSarbMenu() {
        if (!springSecurityService.isLoggedIn()) {
            render('false')
            return
        }
        render(contentType: "text/json") {
            lstTemplates = array {
                element([name: 'menu', content: g.render(plugin: 'sarb', template: '/sarb/leftmenuSarb')])
                element([name: 'dashBoard', content: g.render(plugin: 'sarb', template: '/sarb/dashBoardSarb', model: null)])
            }
        }
    }
}
