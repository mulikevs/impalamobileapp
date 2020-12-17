package com.impalapay.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PrefManager<T> {

    private Context context;

    public PrefManager(Context context) {
        this.context = context;
    }


    /**
     * And it can be consumed for storing data as follows
     * new PrefManager<String>(applicationContext).set("background_color","green");
     * and for retrieving it as follows
     * new PrefManager<String>(applicationContext).get("background_color","white");
     *
     * As it can be seen with only two methods all the possible data types are being handled
     * moreover the usage is clean,practical and self explanatory.
     */



    /**
     * Set any type of shared preferences value
     * @param key of the shared preferences which the caller is desire to set
     * @param value types:
     *                     Boolean ,
     *                     StringSet,
     *                     String,
     *                     Float,
     *                     Long,
     *                     Int
     */


    public void set(String key, T value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
        else if (value instanceof Set) {
            editor.putStringSet(key, (Set<String>) value);
        }
        else if (value instanceof String) {
            editor.putString(key, (String) value);
        }
        else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        }
        else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        }

        editor.apply();
    }

    /**
     * Get any type of value from the shared preference
     * @param key the key of the shared preference
     * @param defaultValue types:
     *                     Boolean ,
     *                     StringSet,
     *                     String,
     *                     Float,
     *                     Long,
     *                     Int
     * @return same type of the default value which has been passed in
     */
    public T get(String key, T defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        if (defaultValue instanceof Boolean) {
            Boolean ret = sp.getBoolean(key, (Boolean) defaultValue);
            return (T) ret;
        }
        else if (defaultValue instanceof Collection) {
            Set<String> result = sp.getStringSet(key, new HashSet<String>());
            return (T) result;
        }
        else if (defaultValue instanceof String) {
            String ret = sp.getString(key, (String) defaultValue);
            return (T) ret;
        }
        else if (defaultValue instanceof Float) {
            Float result = sp.getFloat(key, (Float) defaultValue);
            return (T) result;
        }
        else if (defaultValue instanceof Long) {
            Long result = sp.getLong(key, (Long) defaultValue);
            return (T) result;
        }
        else if (defaultValue instanceof Integer) {
            Integer result = sp.getInt(key, (Integer) defaultValue);
            return (T) result;
        }

        return null;
    }

    //Additional Methods
    public void Preferencesclear(){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();

    }


    public void deletePref(String key){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit().remove(key);
        editor.clear();
        editor.commit();


    }




}


