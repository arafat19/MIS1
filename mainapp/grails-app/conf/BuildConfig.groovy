grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [
        // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
        //compile: [maxMemory: 2048, minMemory: 2048, debug: false, maxPerm: 350, daemon: true],
        // configure settings for the test-app JVM, uses the daemon by default
        //test: [maxMemory: 2G, minMemory: 2G, debug: false, maxPerm: 350, daemon: true],
        // configure settings for the run-app JVM
        //run: [maxMemory: 3072, minMemory: 3072, debug: false, maxPerm: 350, forkReserve: false],
        // configure settings for the run-war JVM
        //war: [maxMemory: 3072, minMemory: 3072, debug: false, maxPerm: 350, forkReserve: false],
        // configure settings for the Console UI JVM
        //console: [maxMemory: 3072, minMemory: 3072, debug: false, maxPerm: 350]
]

grails.project.dependency.resolver = "maven" // maven or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false
    // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    plugins {
        // following plugins will be installed on respective target
        // plugins for the compile step
        compile ":spring-security-core:1.2.7.3"
        compile ":jasper:1.7.0"  // previous(1.6.1) latest(1.7.0)
        compile ":mail:1.0.6"
        compile ":executor:0.3"
        compile ":jcaptcha:1.2.1"
        //for ARMS(Agent)
        compile ":csv:0.3.1"
        // plugins for the build system only
        build ':tomcat:7.0.54'        // required for 2.3.7

        // plugins needed at runtime but not for compilation
        runtime ':hibernate:3.6.10.16' // ':hibernate4:4.3.4.1' for Hibernate 4  (required for grails 2.3.7)
        runtime ":jquery:1.10.2.2"      // upgraded from 1.8.3
        //runtime ":database-migration:1.3.2"
        runtime(':export:1.1') { excludes 'itext', 'itext-rtf' }
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        // runtime 'mysql:mysql-connector-java:5.1.13' compile 'org.imgscalr:imgscalr-lib:4.2'
        compile 'postgresql:postgresql:9.0-801.jdbc4'
        compile 'org.imgscalr:imgscalr-lib:4.2'
        compile 'net.sf.uadetector:uadetector-resources:2013.09'
        build "com.lowagie:itext:2.1.7" // workaround for version conflict
    }
}
//../plugins/applicationPlugin

grails.plugin.location.'applicationPlugin' = "../plugins/applicationPlugin"
//grails.plugin.location.'exchangeHouse' = "../plugins/exchangeHouse"
//grails.plugin.location.'sarb' = "../plugins/sarb"
//grails.plugin.location.'arms' = "../plugins/arms"
//grails.plugin.location.'document' = "../plugins/document"
//grails.plugin.location.'projectTrack' = "../plugins/projectTrack"
//grails.plugin.location.'procurement' = "../plugins/procurement"
grails.plugin.location.'budget' = "../plugins/budget"
//grails.plugin.location.'accounting' = "../plugins/accounting"
//grails.plugin.location.'inventory' = "../plugins/inventory"
//grails.plugin.location.'qs' = "../plugins/qs"
//grails.plugin.location.'fixedAsset' = "../plugins/fixedAsset"
