package com.athena.mis

import javax.annotation.PostConstruct

public abstract class PluginConnector {
    public static final String INVENTORY = "Inventory";
    public static final int INVENTORY_ID = 4
    public static final String ACCOUNTING = "Accounting";
    public static final int ACCOUNTING_ID = 2
    public static final String PROCUREMENT = "Procurement";
    public static final int PROCUREMENT_ID = 5
    public static final String BUDGET = "Budget";
    public static final int BUDGET_ID = 3
    public static final String APPLICATION = "Application";
    public static final int APPLICATION_ID = 1
    public static final String QS = "QS";
    public static final int QS_ID = 6
    public static final String FIXED_ASSET = "FixedAsset";
    public static final int FIXED_ASSET_ID = 7
    public static final String DEVELOPMENT = "Development";
    public static final int DEVELOPMENT_ID = 8
    public static final String EXCHANGE_HOUSE = "ExchangeHouse";
    public static final int EXCHANGE_HOUSE_ID = 9
	public static final String PROJECT_TRACK = "ProjectTrack";
    public static final int PROJECT_TRACK_ID = 10
    public static final String ARMS = "ARMS";
    public static final int ARMS_ID = 11
    public static final String SARB = "SARB";
    public static final int SARB_ID = 12
    public static final String DOCUMENT = "Document"
    public static final int DOCUMENT_ID = 13

    private static Map<String, Object> plugins = new HashMap<String, Object>();

    @PostConstruct
    public abstract boolean initialize();

    public abstract String getName();
    public abstract int getId();

    public final static boolean isPluginInstalled(String key) {
        return plugins.containsKey(key);
    }

    public final static void setPlugin(String key, Object pluginConnector) {
        plugins.put(key, pluginConnector);
    }

    public final static Object getPlugin(String key) {
        return (Object) plugins.get(key);
    }

    public final static Collection<Object> getAllPlugins() {
        return plugins.values();
    }

}