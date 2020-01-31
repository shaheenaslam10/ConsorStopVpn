package com.shaheen.developer.consorstopvpn.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class LocaleManager {

    public static void setLocale(Context c) {
        setNewLocale(c, getLanguage(c));
    }

    public static void setNewLocale(Context c, String language) {
        persistLanguage(c, language);
        updateResources(c, language);
    }

    public static String getLanguage(Context c) {
        SharedPreferences prefs = c.getSharedPreferences("Language", MODE_PRIVATE);
        if (prefs!=null){
            if (prefs.getString("locale", null)!=null){
                return prefs.getString("locale", null);
            }else {
                return "pl";
            }
        }else {
            return "pl";
        }
    }

    private static void persistLanguage(Context c, String language) {
        SharedPreferences.Editor editor = c.getSharedPreferences("Language", MODE_PRIVATE).edit();
        editor.putString("locale", language);
        editor.apply();
    }

    public static Locale getLocale(Resources res) {
        Configuration config = res.getConfiguration();
        return Build.VERSION.SDK_INT >= 24 ? config.getLocales().get(0) : config.locale;
    }

    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}
