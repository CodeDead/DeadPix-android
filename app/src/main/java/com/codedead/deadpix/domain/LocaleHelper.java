package com.codedead.deadpix.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleHelper {

    /**
     * Method that is called when a Context object is going to be attached
     *
     * @param context The Context object that is going to be attached
     * @return The Context object that should be attached
     */
    public static Context onAttach(Context context) {
        final String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        return setLocale(context, lang);
    }

    /**
     * Method that is called when a Context object is going to be attached
     *
     * @param context         The Context object that is going to be attached
     * @param defaultLanguage The default language
     * @return The Context object that should be attached
     */
    public static Context onAttach(Context context, String defaultLanguage) {
        final String lang = getPersistedData(context, defaultLanguage);
        return setLocale(context, lang);
    }

    /**
     * Set the locale
     *
     * @param context  The Context object that should be updated with the latest language
     * @param language The language that should be applied to the Context object
     * @return The updated Context object
     */
    public static Context setLocale(Context context, String language) {
        persist(context, language);
        return updateResources(context, language);
    }

    /**
     * Get the language from the shared preferences
     *
     * @param context         The Context object that can be used to store shared preferences
     * @param defaultLanguage The default language
     * @return The language from the shared preferences
     */
    private static String getPersistedData(Context context, String defaultLanguage) {
        final SharedPreferences preferences = context.getSharedPreferences("deadpixsettings", Context.MODE_PRIVATE);
        return preferences.getString("language", defaultLanguage);
    }

    /**
     * Store the language in the shared preferences
     *
     * @param context  The Context object that can be used to store the shared preferences
     * @param language The language that should be stored
     */
    private static void persist(Context context, String language) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();

        editor.putString("language", language);
        editor.apply();
    }

    /**
     * Update the context with the latest language
     *
     * @param context  The Context object that should be updated
     * @param language The language that should be applied to the Context object
     * @return The updated Context object
     */
    private static Context updateResources(Context context, String language) {
        final Locale locale = new Locale(language);
        Locale.setDefault(locale);

        final Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }
}
