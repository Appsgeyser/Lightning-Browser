package acr.browser.lightning.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import acr.browser.lightning.constant.Constants;
import acr.browser.lightning.domain.GeoData;
import acr.browser.lightning.domain.WeatherData;
import acr.browser.lightning.download.DownloadHandler;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferenceManager {

    private static class Name {
        static final String NOTIFICATION_SEARCH_BAR = "notificationsearchbar";
        static final String NOTIFICATION_WEATHER = "notificationweather";

        static final String ADOBE_FLASH_SUPPORT = "enableflash";
        static final String BLOCK_ADS = "AdBlock";
        static final String BLOCK_IMAGES = "blockimages";
        static final String CLEAR_CACHE_EXIT = "cache";
        static final String COOKIES = "cookies";
        static final String DOWNLOAD_DIRECTORY = "downloadLocation";
        static final String FULL_SCREEN = "fullscreen";
        static final String HIDE_STATUS_BAR = "hidestatus";
        static final String HOMEPAGE = "home";
        static final String INCOGNITO_COOKIES = "incognitocookies";
        static final String JAVASCRIPT = "java";
        static final String LOCATION = "location";
        static final String OVERVIEW_MODE = "overviewmode";
        static final String POPUPS = "newwindows";
        static final String RESTORE_LOST_TABS = "restoreclosed";
        static final String SAVE_PASSWORDS = "passwords";
        static final String SEARCH = "search";
        static final String SEARCH_URL = "searchurl";
        static final String BACKGROUND_URL = "backgroundurl";
        static final String TEXT_REFLOW = "textreflow";
        static final String TEXT_SIZE = "textsize";
        static final String USE_WIDE_VIEWPORT = "wideviewport";
        static final String USER_AGENT = "agentchoose";
        static final String USER_AGENT_STRING = "userAgentString";
        static final String CLEAR_HISTORY_EXIT = "clearHistoryExit";
        static final String CLEAR_COOKIES_EXIT = "clearCookiesExit";
        static final String SAVE_URL = "saveUrl";
        static final String RENDERING_MODE = "renderMode";
        static final String BLOCK_THIRD_PARTY = "thirdParty";
        static final String ENABLE_COLOR_MODE = "colorMode";
        static final String URL_BOX_CONTENTS = "urlContent";
        static final String INVERT_COLORS = "invertColors";
        static final String READING_TEXT_SIZE = "readingTextSize";
        static final String THEME = "Theme";
        static final String TEXT_ENCODING = "textEncoding";
        static final String CLEAR_WEBSTORAGE_EXIT = "clearWebStorageExit";
        static final String SHOW_TABS_IN_DRAWER = "showTabsInDrawer";
        static final String DO_NOT_TRACK = "doNotTrack";
        static final String IDENTIFYING_HEADERS = "removeIdentifyingHeaders";
        static final String SWAP_BOOKMARKS_AND_TABS = "swapBookmarksAndTabs";
        static final String SEARCH_SUGGESTIONS = "searchSuggestions";
        static final String BLACK_STATUS_BAR = "blackStatusBar";
        static final String LAST_BANNER_SHOWN_TIME = "lastBannerShownTime";

        static final String TOOL_BAR_STYLE = "toolBarStyle";

        static final String USE_PROXY = "useProxy";
        static final String PROXY_CHOICE = "proxyChoice";
        static final String USE_PROXY_HOST = "useProxyHost";
        static final String USE_PROXY_PORT = "useProxyPort";
        static final String INITIAL_CHECK_FOR_TOR = "checkForTor";
        static final String INITIAL_CHECK_FOR_I2P = "checkForI2P";

        static final String GEO_DATA_CITY_NAME = "geoDataCityName";
        static final String GEO_DATA_COUNTRY_CODE = "geoDataCountryCode";
        static final String GEO_DATA_LAST_UPDATE_TIME = "geoDataUpdateTime";
        static final String CITY_NAME = "cityName";

        static final String WEATHER_TEMP = "weatherTemp";
        static final String WEATHER_LOCATION = "weatherLocation";
        static final String WEATHER_CODE = "weatherCode";
        static final String WEATHER_TEXT = "weatherText";
        static final String WEATHER_UPDATE_TIME = "weatherUpdateTime";
        static final String WEATHER_DEGREE_SYSTEM = "isCelsius";


        static final String BOOKMARKS_APPLIED = "bookmarksApplies";
        static final String SEARCH_APPLIED = "searchApplies";

        //Ads settings
        static final String ADS_NEW_INCOGNITO_TAB = "newIncognitoTab";
        static final String ADS_NEW_TAB_IN_MINUTES = "newTabInMinutes";
        static final String ADS_ON_HOME_PAGE_PRESSED = "onHomePagePressed";
        static final String ADS_ON_FIRST_PAGE_LOAD_FINISHED = "onFirstPageFinishLoad";


        static final String LEAK_CANARY = "leakCanary";
        static final String ADV_ID = "advertisingId";
    }

    public enum Suggestion {
        SUGGESTION_GOOGLE,
        SUGGESTION_DUCK,
        SUGGESTION_BAIDU,
        SUGGESTION_NONE
    }

    @NonNull
    private final SharedPreferences mPrefs;

    private static final String PREFERENCES = "settings";

    @Inject
    public PreferenceManager(@NonNull final Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES, 0);
    }

    @NonNull
    public Suggestion getSearchSuggestionChoice() {
        try {
            return Suggestion.valueOf(mPrefs.getString(Name.SEARCH_SUGGESTIONS, Suggestion.SUGGESTION_GOOGLE.name()));
        } catch (IllegalArgumentException ignored) {
            return Suggestion.SUGGESTION_NONE;
        }
    }

    public String getCity() {
        return mPrefs.getString(Name.CITY_NAME, "New-York");
    }

    public void setCity(String city) {
        putString(Name.CITY_NAME, city);
    }

    public boolean hasCity() {
        return mPrefs.contains(Name.CITY_NAME);
    }

    public boolean removeCity() {
        return mPrefs.edit().remove(Name.CITY_NAME).commit();
    }

    public void setWeatherDataData(WeatherData weatherData) {
        if (weatherData == null) {
            mPrefs.edit().remove(Name.WEATHER_TEMP)
                    .remove(Name.WEATHER_CODE)
                    .remove(Name.WEATHER_TEXT)
                    .remove(Name.WEATHER_LOCATION)
                    .remove(Name.WEATHER_DEGREE_SYSTEM)
                    .remove(Name.WEATHER_UPDATE_TIME).apply();
        } else {
            putInt(Name.WEATHER_TEMP, weatherData.getTemp());
            putInt(Name.WEATHER_CODE, weatherData.getCode());
            putString(Name.WEATHER_TEXT, weatherData.getText());
            putString(Name.WEATHER_LOCATION, weatherData.getLocation());
            putBoolean(Name.WEATHER_DEGREE_SYSTEM, weatherData.isCecius());
            putInt(Name.WEATHER_UPDATE_TIME, (int) (weatherData.getLastUpdateTime() / 1000));
        }
    }

    public WeatherData getWeatherData() {
        WeatherData weatherData = new WeatherData();
        weatherData.setCode(mPrefs.getInt(Name.WEATHER_CODE, 0));
        weatherData.setTemp(mPrefs.getInt(Name.WEATHER_TEMP, 0));
        weatherData.setText(mPrefs.getString(Name.WEATHER_TEXT, ""));
        weatherData.setLocation(mPrefs.getString(Name.WEATHER_LOCATION, ""));
        weatherData.setCecius(mPrefs.getBoolean(Name.WEATHER_DEGREE_SYSTEM, false));
        weatherData.setLastUpdateTime(mPrefs.getInt(Name.WEATHER_UPDATE_TIME, 0) * 1000L);
        return weatherData;
    }

    public void setGeoData(GeoData geoData) {
        putString(Name.GEO_DATA_CITY_NAME, geoData.getCityName());
        putString(Name.GEO_DATA_COUNTRY_CODE, geoData.getCountryCode());
        putInt(Name.GEO_DATA_LAST_UPDATE_TIME, (int) (geoData.getLastUpdateTime() / 1000));
    }

    public GeoData getGeoData() {
        GeoData geoData = new GeoData();
        geoData.setCityName(mPrefs.getString(Name.GEO_DATA_CITY_NAME, "New-York"));
        geoData.setCountryCode(mPrefs.getString(Name.GEO_DATA_COUNTRY_CODE, "en"));
        geoData.setLastUpdateTime(mPrefs.getInt(Name.GEO_DATA_LAST_UPDATE_TIME, 0) * 1000L);
        return geoData;
    }

    public void setSearchSuggestionChoice(@NonNull Suggestion suggestion) {
        putString(Name.SEARCH_SUGGESTIONS, suggestion.name());
    }

    public boolean getBookmarksAndTabsSwapped() {
        return mPrefs.getBoolean(Name.SWAP_BOOKMARKS_AND_TABS, false);
    }

    public String getAdvertisingId() {
        return mPrefs.getString(Name.ADV_ID, "");
    }

    public void setAdvertisingId(String advid) {
        mPrefs.edit().putString(Name.ADV_ID, advid);
    }

    public void setBookmarkAndTabsSwapped(boolean swap) {
        putBoolean(Name.SWAP_BOOKMARKS_AND_TABS, swap);
    }

    public boolean getAdBlockEnabled() {
        return mPrefs.getBoolean(Name.BLOCK_ADS, false);
    }

    public boolean getNotificationSearchBarEnabled() {
        return mPrefs.getBoolean(Name.NOTIFICATION_SEARCH_BAR, true);
    }

    public boolean getNotificationWeatherEnabled() {
        return mPrefs.getBoolean(Name.NOTIFICATION_WEATHER, true);
    }

    public boolean getBlockImagesEnabled() {
        return mPrefs.getBoolean(Name.BLOCK_IMAGES, false);
    }


    public boolean getBlockThirdPartyCookiesEnabled() {
        return mPrefs.getBoolean(Name.BLOCK_THIRD_PARTY, false);
    }

    public boolean getCheckedForTor() {
        return mPrefs.getBoolean(Name.INITIAL_CHECK_FOR_TOR, false);
    }

    public boolean getCheckedForI2P() {
        return mPrefs.getBoolean(Name.INITIAL_CHECK_FOR_I2P, false);
    }

    public boolean getClearCacheExit() {
        return mPrefs.getBoolean(Name.CLEAR_CACHE_EXIT, false);
    }

    public boolean getClearCookiesExitEnabled() {
        return mPrefs.getBoolean(Name.CLEAR_COOKIES_EXIT, false);
    }

    public boolean getClearWebStorageExitEnabled() {
        return mPrefs.getBoolean(Name.CLEAR_WEBSTORAGE_EXIT, false);
    }

    public boolean getClearHistoryExitEnabled() {
        return mPrefs.getBoolean(Name.CLEAR_HISTORY_EXIT, false);
    }

    public boolean getColorModeEnabled() {
        return false;
    }

    public boolean getCookiesEnabled() {
        return mPrefs.getBoolean(Name.COOKIES, true);
    }

    public boolean getBookmarksApplies() {
        return mPrefs.getBoolean(Name.BOOKMARKS_APPLIED, false);
    }

    public boolean getSearchEngineApplies() {
        return mPrefs.getBoolean(Name.SEARCH_APPLIED, false);
    }

    @NonNull
    public String getDownloadDirectory() {
        return mPrefs.getString(Name.DOWNLOAD_DIRECTORY, DownloadHandler.DEFAULT_DOWNLOAD_PATH);
    }

    public int getFlashSupport() {
        return mPrefs.getInt(Name.ADOBE_FLASH_SUPPORT, 0);
    }

    public boolean getFullScreenEnabled() {
        return mPrefs.getBoolean(Name.FULL_SCREEN, true);
    }

    public boolean getHideStatusBarEnabled() {
        return mPrefs.getBoolean(Name.HIDE_STATUS_BAR, false);
    }

    @NonNull
    public String getHomepage() {
        return mPrefs.getString(Name.HOMEPAGE, Constants.SCHEME_HOMEPAGE);
    }

    public boolean getIncognitoCookiesEnabled() {
        return mPrefs.getBoolean(Name.INCOGNITO_COOKIES, true);
    }

    public boolean getInvertColors() {
        return mPrefs.getBoolean(Name.INVERT_COLORS, false);
    }

    public boolean getJavaScriptEnabled() {
        return mPrefs.getBoolean(Name.JAVASCRIPT, true);
    }

    public boolean getLocationEnabled() {
        return mPrefs.getBoolean(Name.LOCATION, false);
    }

    public boolean getOverviewModeEnabled() {
        return mPrefs.getBoolean(Name.OVERVIEW_MODE, true);
    }

    public boolean getPopupsEnabled() {
        return mPrefs.getBoolean(Name.POPUPS, true);
    }

    @NonNull
    public String getProxyHost() {
        return mPrefs.getString(Name.USE_PROXY_HOST, "localhost");
    }

    public int getProxyPort() {
        return mPrefs.getInt(Name.USE_PROXY_PORT, 8118);
    }

    public int getReadingTextSize() {
        return mPrefs.getInt(Name.READING_TEXT_SIZE, 2);
    }

    public int getRenderingMode() {
        return mPrefs.getInt(Name.RENDERING_MODE, 0);
    }

    public boolean getRestoreLostTabsEnabled() {
        return mPrefs.getBoolean(Name.RESTORE_LOST_TABS, true);
    }

    @Nullable
    public String getSavedUrl() {
        return mPrefs.getString(Name.SAVE_URL, null);
    }

    public boolean getSavePasswordsEnabled() {
        return mPrefs.getBoolean(Name.SAVE_PASSWORDS, true);
    }

    public int getSearchChoice() {
        return mPrefs.getInt(Name.SEARCH, 1);
    }

    @NonNull
    public String getSearchUrl() {
        return mPrefs.getString(Name.SEARCH_URL, Constants.GOOGLE_SEARCH);
    }

    public String getBackgroundUrl() {
        return mPrefs.getString(Name.BACKGROUND_URL, null);
    }

    public boolean getTextReflowEnabled() {
        return mPrefs.getBoolean(Name.TEXT_REFLOW, false);
    }

    public int getTextSize() {
        return mPrefs.getInt(Name.TEXT_SIZE, 3);
    }

    public int getUrlBoxContentChoice() {
        return mPrefs.getInt(Name.URL_BOX_CONTENTS, 0);
    }

    public int getUseTheme() {
        return mPrefs.getInt(Name.THEME, 0);
    }

    public boolean getUseProxy() {
        return mPrefs.getBoolean(Name.USE_PROXY, false);
    }

    @Constants.Proxy
    public int getProxyChoice() {
        @Constants.Proxy int proxy = mPrefs.getInt(Name.PROXY_CHOICE, Constants.NO_PROXY);
        switch (proxy) {
            case Constants.NO_PROXY:
            case Constants.PROXY_ORBOT:
            case Constants.PROXY_I2P:
            case Constants.PROXY_MANUAL:
                return proxy;
            default:
                return Constants.NO_PROXY;
        }
    }

    public int getUserAgentChoice() {
        return mPrefs.getInt(Name.USER_AGENT, 1);
    }

    @Nullable
    public String getUserAgentString(@Nullable String def) {
        return mPrefs.getString(Name.USER_AGENT_STRING, def);
    }

    public boolean getUseWideViewportEnabled() {
        return mPrefs.getBoolean(Name.USE_WIDE_VIEWPORT, true);
    }

    @NonNull
    public String getTextEncoding() {
        return mPrefs.getString(Name.TEXT_ENCODING, Constants.DEFAULT_ENCODING);
    }

    public boolean getShowTabsInDrawer(boolean defaultValue) {
        return mPrefs.getBoolean(Name.SHOW_TABS_IN_DRAWER, defaultValue);
    }

    public boolean getDoNotTrackEnabled() {
        return mPrefs.getBoolean(Name.DO_NOT_TRACK, false);
    }

    public boolean getRemoveIdentifyingHeadersEnabled() {
        return mPrefs.getBoolean(Name.IDENTIFYING_HEADERS, false);
    }

    public boolean getUseBlackStatusBar() {
        return mPrefs.getBoolean(Name.BLACK_STATUS_BAR, false);
    }

    public long getLastBannerShownTime() {
        return mPrefs.getLong(Name.LAST_BANNER_SHOWN_TIME, 0);
    }

    public String getToolBarStyle() {
        return mPrefs.getString(Name.TOOL_BAR_STYLE, "default");
    }



    public void setLastBannerShownTime(long time) {
        mPrefs.edit().putLong(Name.LAST_BANNER_SHOWN_TIME, time).apply();
    }


    private void putBoolean(@NonNull String name, boolean value) {
        mPrefs.edit().putBoolean(name, value).apply();
    }

    private void putInt(@NonNull String name, int value) {
        mPrefs.edit().putInt(name, value).apply();
    }

    private void putString(@NonNull String name, @Nullable String value) {
        mPrefs.edit().putString(name, value).apply();
    }

    public void setUseBlackStatusBar(boolean enabled) {
        putBoolean(Name.BLACK_STATUS_BAR, enabled);
    }

    public void setAdsNewIncognitoTab(boolean enabled) {
        putBoolean(Name.ADS_NEW_INCOGNITO_TAB, enabled);
    }

    public boolean getAdsNewIncognitoTab() {
        return mPrefs.getBoolean(Name.ADS_NEW_INCOGNITO_TAB, false);
    }

    public void setAdsNewTabInMinutes(int period) {
        putInt(Name.ADS_NEW_TAB_IN_MINUTES, period);
    }

    public int getAdsNewTabInMinutes() {
        return mPrefs.getInt(Name.ADS_NEW_TAB_IN_MINUTES, 1);
    }

    public void setAdsOnFirstPageLoadFinished(boolean enabled) {
        putBoolean(Name.ADS_ON_FIRST_PAGE_LOAD_FINISHED, enabled);
    }

    public boolean getAdsOnFirstPageLoadFinished() {
        return mPrefs.getBoolean(Name.ADS_ON_FIRST_PAGE_LOAD_FINISHED, false);
    }

    public void setAdsOnHomePagePressed(boolean enabled) {
        putBoolean(Name.ADS_ON_HOME_PAGE_PRESSED, enabled);
    }

    public boolean getAdsOnHomePagePressed() {
        return mPrefs.getBoolean(Name.ADS_ON_HOME_PAGE_PRESSED, false);
    }

    public void setRemoveIdentifyingHeadersEnabled(boolean enabled) {
        putBoolean(Name.IDENTIFYING_HEADERS, enabled);
    }

    public void setDoNotTrackEnabled(boolean doNotTrack) {
        putBoolean(Name.DO_NOT_TRACK, doNotTrack);
    }

    public void setShowTabsInDrawer(boolean show) {
        putBoolean(Name.SHOW_TABS_IN_DRAWER, show);
    }

    public void setTextEncoding(@NonNull String encoding) {
        putString(Name.TEXT_ENCODING, encoding);
    }

    public void setAdBlockEnabled(boolean enable) {
        putBoolean(Name.BLOCK_ADS, enable);
    }

    public void setNotificationSearchEnabled(boolean enable) {
        putBoolean(Name.NOTIFICATION_SEARCH_BAR, enable);
    }

    public void setNotificationWeatherEnabled(boolean enable) {
        putBoolean(Name.NOTIFICATION_WEATHER, enable);
    }

    public void setBlockImagesEnabled(boolean enable) {
        putBoolean(Name.BLOCK_IMAGES, enable);
    }

    public void setBlockThirdPartyCookiesEnabled(boolean enable) {
        putBoolean(Name.BLOCK_THIRD_PARTY, enable);
    }

    public void setCheckedForTor(boolean check) {
        putBoolean(Name.INITIAL_CHECK_FOR_TOR, check);
    }

    public void setBookmarksApplied(boolean applied) {
        putBoolean(Name.BOOKMARKS_APPLIED, applied);
    }

    public void setSearchEngineApplied(boolean applied) {
        putBoolean(Name.SEARCH_APPLIED, applied);
    }

    public void setCheckedForI2P(boolean check) {
        putBoolean(Name.INITIAL_CHECK_FOR_I2P, check);
    }

    public void setClearCacheExit(boolean enable) {
        putBoolean(Name.CLEAR_CACHE_EXIT, enable);
    }

    public void setClearCookiesExitEnabled(boolean enable) {
        putBoolean(Name.CLEAR_COOKIES_EXIT, enable);
    }

    public void setClearWebStorageExitEnabled(boolean enable) {
        putBoolean(Name.CLEAR_WEBSTORAGE_EXIT, enable);
    }

    public void setClearHistoryExitEnabled(boolean enable) {
        putBoolean(Name.CLEAR_HISTORY_EXIT, enable);
    }

    public void setColorModeEnabled(boolean enable) {
        putBoolean(Name.ENABLE_COLOR_MODE, enable);
    }

    public void setCookiesEnabled(boolean enable) {
        putBoolean(Name.COOKIES, enable);
    }

    public void setDownloadDirectory(@NonNull String directory) {
        putString(Name.DOWNLOAD_DIRECTORY, directory);
    }

    public void setFlashSupport(int n) {
        putInt(Name.ADOBE_FLASH_SUPPORT, n);
    }

    public void setFullScreenEnabled(boolean enable) {
        putBoolean(Name.FULL_SCREEN, enable);
    }

    public void setHideStatusBarEnabled(boolean enable) {
        putBoolean(Name.HIDE_STATUS_BAR, enable);
    }

    public void setHomepage(@NonNull String homepage) {
        putString(Name.HOMEPAGE, homepage);
    }

    public void setIncognitoCookiesEnabled(boolean enable) {
        putBoolean(Name.INCOGNITO_COOKIES, enable);
    }

    public void setInvertColors(boolean enable) {
        putBoolean(Name.INVERT_COLORS, enable);
    }

    public void setJavaScriptEnabled(boolean enable) {
        putBoolean(Name.JAVASCRIPT, enable);
    }

    public void setLocationEnabled(boolean enable) {
        putBoolean(Name.LOCATION, enable);
    }

    public void setOverviewModeEnabled(boolean enable) {
        putBoolean(Name.OVERVIEW_MODE, enable);
    }

    public void setPopupsEnabled(boolean enable) {
        putBoolean(Name.POPUPS, enable);
    }

    public void setReadingTextSize(int size) {
        putInt(Name.READING_TEXT_SIZE, size);
    }

    public void setRenderingMode(int mode) {
        putInt(Name.RENDERING_MODE, mode);
    }

    public void setRestoreLostTabsEnabled(boolean enable) {
        putBoolean(Name.RESTORE_LOST_TABS, enable);
    }

    public void setSavedUrl(@Nullable String url) {
        putString(Name.SAVE_URL, url);
    }

    public void setSavePasswordsEnabled(boolean enable) {
        putBoolean(Name.SAVE_PASSWORDS, enable);
    }

    public void setSearchChoice(int choice) {
        putInt(Name.SEARCH, choice);
    }

    public void setSearchUrl(@NonNull String url) {
        putString(Name.SEARCH_URL, url);
    }

    public void setBackgroundUrl(@NonNull String url) {
        putString(Name.BACKGROUND_URL, url);
    }

    public void setTextReflowEnabled(boolean enable) {
        putBoolean(Name.TEXT_REFLOW, enable);
    }

    public void setTextSize(int size) {
        putInt(Name.TEXT_SIZE, size);
    }

    public void setUrlBoxContentChoice(int choice) {
        putInt(Name.URL_BOX_CONTENTS, choice);
    }

    public void setUseTheme(int theme) {
        putInt(Name.THEME, theme);
    }

    public void setUseLeakCanary(boolean useLeakCanary) {
        putBoolean(Name.LEAK_CANARY, useLeakCanary);
    }

    public boolean getUseLeakCanary() {
        return mPrefs.getBoolean(Name.LEAK_CANARY, false);
    }


    /**
     * Valid choices:
     * <ul>
     * <li>{@link Constants#NO_PROXY}</li>
     * <li>{@link Constants#PROXY_ORBOT}</li>
     * <li>{@link Constants#PROXY_I2P}</li>
     * </ul>
     *
     * @param choice the proxy to use.
     */
    public void setProxyChoice(@Constants.Proxy int choice) {
        putBoolean(Name.USE_PROXY, choice != Constants.NO_PROXY);
        putInt(Name.PROXY_CHOICE, choice);
    }

    public void setProxyHost(@NonNull String proxyHost) {
        putString(Name.USE_PROXY_HOST, proxyHost);
    }

    public void setProxyPort(int proxyPort) {
        putInt(Name.USE_PROXY_PORT, proxyPort);
    }

    public void setUserAgentChoice(int choice) {
        putInt(Name.USER_AGENT, choice);
    }

    public void setUserAgentString(@Nullable String agent) {
        putString(Name.USER_AGENT_STRING, agent);
    }

    public void setUseWideViewportEnabled(boolean enable) {
        putBoolean(Name.USE_WIDE_VIEWPORT, enable);
    }

    public void setToolBarStyle(String style) {
        putString(Name.TOOL_BAR_STYLE, style);
    }



}
