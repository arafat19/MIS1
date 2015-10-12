package com.athena.mis.application.taglib

import com.athena.mis.PluginConnector
import com.athena.mis.utility.Tools
import grails.plugins.springsecurity.SpringSecurityService

/**
 * PluginTagLib methods check if plugin installed for given name(s)
 */

class PluginTagLib {

    static namespace = "app"

    SpringSecurityService springSecurityService

    private static final String STR_NAME = 'name'
    private static final String STR_NAMES = 'names'
    private static final String COMMA = ','
    private static final String EMPTY_SPACE = ''
    private static final String SPACE_CHARACTER = "\\s"

    /**
     * Renders the body if the specified plugin is installed
     * attr takes the attribute 'name' as plugin name
     * example: <app:ifPlugin name="Accounting"></app:ifPlugin>
     *
     * @attr name REQUIRED -the plugin name
     */

    def ifPlugin = { attrs, body ->
        String strPluginName = attrs.remove(STR_NAME)
        strPluginName = strPluginName.replaceAll(SPACE_CHARACTER, EMPTY_SPACE)
        if (hasPlugin(strPluginName)) {
            out << body()
        } else {
            out << Tools.EMPTY_SPACE
        }
    }

    /**
     * Renders the body if all specified plugins are installed
     * attr takes the attribute 'names' as plugin name
     * example: <app:ifAllPlugins names="Accounting,Inventory"></app:ifAllPlugins>
     *
     * @attr names REQUIRED -comma separated plugin names
     */
    def ifAllPlugins = { attrs, body ->
        String strPluginNames = attrs.remove(STR_NAMES)
        strPluginNames = strPluginNames.replaceAll(SPACE_CHARACTER, EMPTY_SPACE)

        if (strPluginNames.size() <= 0) {
            out << EMPTY_SPACE
            return
        }
        boolean allInstalled = true
        List<String> lstPlugins = strPluginNames.split(COMMA);
        for (int i = 0; i < lstPlugins.size(); i++) {
            String pluginName = lstPlugins[i]
            if (pluginName.length() == 0) {
                out << EMPTY_SPACE
                return
            }
            if (!hasPlugin(pluginName)) {
                allInstalled = false
                break
            }
        }
        if (allInstalled) {
            out << body()
        } else {
            out << Tools.EMPTY_SPACE
        }
    }

   /**
    * Check if given plugin is installed using PluginConnector class
    * @param strPluginName -name of the plugin
    * @return - true(if plugin installed), false(otherwise)
    */
    private boolean hasPlugin(String strPluginName) {
        switch (strPluginName) {
            case PluginConnector.ACCOUNTING:
                if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
                    return true
                }
                break
            case PluginConnector.APPLICATION:
                if (PluginConnector.isPluginInstalled(PluginConnector.APPLICATION)) {
                    return true
                }
                break
            case PluginConnector.BUDGET:
                if (PluginConnector.isPluginInstalled(PluginConnector.BUDGET)) {
                    return true
                }
                break
            case PluginConnector.INVENTORY:
                if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
                    return true
                }
                break
            case PluginConnector.PROCUREMENT:
                if (PluginConnector.isPluginInstalled(PluginConnector.PROCUREMENT)) {
                    return true
                }
                break
            case PluginConnector.QS:
                if (PluginConnector.isPluginInstalled(PluginConnector.QS)) {
                    return true
                }
                break
            case PluginConnector.FIXED_ASSET:
                if (PluginConnector.isPluginInstalled(PluginConnector.FIXED_ASSET)) {
                    return true
                }
                break
            case PluginConnector.DEVELOPMENT:
                if (PluginConnector.isPluginInstalled(PluginConnector.DEVELOPMENT)) {
                    return true
                }
                break
            case PluginConnector.EXCHANGE_HOUSE:
                if (PluginConnector.isPluginInstalled(PluginConnector.EXCHANGE_HOUSE)) {
                    return true
                }
                break
			case PluginConnector.PROJECT_TRACK:
				if (PluginConnector.isPluginInstalled(PluginConnector.PROJECT_TRACK)) {
					return true
				}
				break
            case PluginConnector.ARMS:
				if (PluginConnector.isPluginInstalled(PluginConnector.ARMS)) {
					return true
				}
				break
			case PluginConnector.SARB:
				if (PluginConnector.isPluginInstalled(PluginConnector.SARB)) {
					return true
				}
				break
            case PluginConnector.DOCUMENT:
                if (PluginConnector.isPluginInstalled(PluginConnector.DOCUMENT)) {
                    return true
                }
                break
            default:
                return false
                break
        }
        return false
    }

}
