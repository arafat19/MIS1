class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }
        "/"(view: "/index")
        "500"(view: '/404')  // Internal Server Error
        "505"(view: '/404')  // HTTP version not supported
        "400"(view: '/404')  // Bad request or syntax
        "404"(view: '/404')  // Page not found
        "405"(view: '/404')  // Method not allowed
        "408"(view: '/404')  // Request timeout
        "414"(view: '/404')  // Request-URI too long
        "403"(view: '/404')  // Access Forbidden (e.g. Attempts to access the WEB-INF or META-INF directories)
    }
}
