/*
 * Copyright (C) 2012-2014 Soomla Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.soomla.data;

import android.text.TextUtils;

import com.soomla.SoomlaApp;
import com.soomla.SoomlaConfig;
import com.soomla.SoomlaUtils;
import com.soomla.util.AESObfuscator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class provides basic storage operations for a simple key-value store.
 */
public class KeyValueStorage {

    /**
     * Retrieves the value for the given key
     *
     * @param key is the key in the key-val pair
     * @return the value for the given key
     */
    public static String getValue(String key) {
        SoomlaUtils.LogDebug(TAG, "trying to fetch a value for key: " + key);

        key = getAESObfuscator().obfuscateString(key);

        String val = getDatabase().getKeyVal(key);

        if (val != null && !TextUtils.isEmpty(val)) {
            try {
                val = getAESObfuscator().unobfuscateToString(val);
            } catch (AESObfuscator.ValidationException e) {
                SoomlaUtils.LogError(TAG, e.getMessage());
                val = "";
            }

            SoomlaUtils.LogDebug(TAG, "the fetched value is " + val);
        }
        return val;
    }

    /**
     * Sets key-val pair in the database according to given key and val.
     *
     * @param key key to set in pair
     * @param val value to set in pair
     */
    public static void setNonEncryptedKeyValue(String key, String val) {
        SoomlaUtils.LogDebug(TAG, "setting " + val + " for key: " + key);

        val = getAESObfuscator().obfuscateString(val);

        getDatabase().setKeyVal(key, val);
    }

    /**
     * Deletes key-val pair that has the given key.
     *
     * @param key the key to indicate which pair to delete
     */
    public static void deleteNonEncryptedKeyValue(String key) {
        SoomlaUtils.LogDebug(TAG, "deleting " + key);

        getDatabase().deleteKeyVal(key);
    }

    /**
     * Retrieves the value of the key-val pair with the given key.
     *
     * @param key key according to which val will be retrieved
     * @return value of key-val pair
     */
    public static String getNonEncryptedKeyValue(String key) {
        SoomlaUtils.LogDebug(TAG, "trying to fetch a value for key: " + key);

        String val = getDatabase().getKeyVal(key);

        if (val != null && !TextUtils.isEmpty(val)) {
            try {
                val = getAESObfuscator().unobfuscateToString(val);
            } catch (AESObfuscator.ValidationException e) {
                SoomlaUtils.LogError(TAG, e.getMessage());
                val = "";
            }

            SoomlaUtils.LogDebug(TAG, "the fetched value is " + val);
        }
        return val;
    }

    /**
     * Retrieves key-val pairs according to given query.
     *
     * @param query query that determines what key-val pairs will be returned
     * @return hashmap of key-val pairs
     */
    public static HashMap<String, String> getNonEncryptedQueryValues(String query) {
		return getNonEncryptedQueryValues(query, 0);
	}

	/**
	 * Retrieves key-val pairs according to given query, limiting amount of results returned.
	 *
	 * @param query query that determines what key-val pairs will be returned
	 * @param limit max amount of key-val pairs returned
	 * @return hashmap of key-val pairs
	 */
	public static HashMap<String, String> getNonEncryptedQueryValues(String query, int limit) {
		SoomlaUtils.LogDebug(TAG, "trying to fetch values for query: " + query + (limit > 0? " with limit: " + limit : ""));

		HashMap<String, String> vals = getDatabase().getQueryVals(query, limit);
		HashMap<String, String> results = new HashMap<String, String>();
		for(String key : vals.keySet()) {
			String val = vals.get(key);
			if (val != null && !TextUtils.isEmpty(val)) {
				try {
					val = getAESObfuscator().unobfuscateToString(val);
					results.put(key, val);
				} catch (AESObfuscator.ValidationException e) {
					SoomlaUtils.LogError(TAG, e.getMessage());
				}
			}
		}

		SoomlaUtils.LogDebug(TAG, "fetched " + results.size() + " results");

		return results;
	}

    /**
     * Retrieves one key-val according to given query.
     *
     * @param query query that determines what key-val will be returned
     * @return string of key-val returned
     */
    public static String getOneForNonEncryptedQuery(String query) {
        SoomlaUtils.LogDebug(TAG, "trying to fetch one for query: " + query);

        String val = getDatabase().getQueryOne(query);
        if (val != null && !TextUtils.isEmpty(val)) {
            try {
                val = getAESObfuscator().unobfuscateToString(val);
                return val;
            } catch (AESObfuscator.ValidationException e) {
                SoomlaUtils.LogError(TAG, e.getMessage());
            }
        }

        return null;
    }

    /**
     * Retrieves the number key-vals according to given query.
     *
     * @param query query that determines what number of key-vals
     * @return number of key-vals according the the given query
     */
    public static int getCountForNonEncryptedQuery(String query) {
        SoomlaUtils.LogDebug(TAG, "trying to fetch count for query: " + query);

        return getDatabase().getQueryCount(query);
    }

    /**
     * Gets all keys in the storage with no encryption
     *
     * @return a List of unencrypted keys
     */
    public static List<String> getEncryptedKeys() {
        SoomlaUtils.LogDebug(TAG, "trying to fetch all keys");

        List<String> encryptedKeys = getDatabase().getAllKeys();
        List<String> resultKeys = new ArrayList<String>();

        for (String encryptedKey : encryptedKeys) {
            try {
                String unencryptedKey = getAESObfuscator().unobfuscateToString(encryptedKey);
                resultKeys.add(unencryptedKey);
            } catch (AESObfuscator.ValidationException e) {
                SoomlaUtils.LogDebug(TAG, e.getMessage());
            } catch (RuntimeException e) {
                SoomlaUtils.LogError(TAG, e.getMessage());
            }
        }

        return resultKeys;
    }

    /**
     * Sets the given value to the given key.
     *
     * @param key is the key in the key-val pair.
     * @param val is the val in the key-val pair.
     */
    public static void setValue(String key, String val) {
        SoomlaUtils.LogDebug(TAG, "setting " + val + " for key: " + key);

        key = getAESObfuscator().obfuscateString(key);
        val = getAESObfuscator().obfuscateString(val);

        getDatabase().setKeyVal(key, val);
    }

    /**
     * Deletes a key-val pair with the given key.
     *
     * @param key is the key in the key-val pair.
     */
    public static void deleteKeyValue(String key) {
        SoomlaUtils.LogDebug(TAG, "deleting " + key);

        key = getAESObfuscator().obfuscateString(key);

        getDatabase().deleteKeyVal(key);
    }

    /**
     * Purges the entire storage
     *
     * NOTE: Use this method with care, it will erase all user data in storage
     * This method is mainly used for testing.
     */
    public static void purge() {
        SoomlaUtils.LogDebug(TAG, "purging database");

        getDatabase().purgeDatabaseEntries(SoomlaApp.getAppContext());
    }

    /**
     * Retrieves the key-val database.
     *
     * @return key-val database
     */
    private static synchronized KeyValDatabase getDatabase(){

        if (mKvDatabase == null) {
            mKvDatabase = new KeyValDatabase(SoomlaApp.getAppContext());
        }

        return mKvDatabase;
    }

    /**
     * Retrieves AESObfuscator
     *
     * @return AESObfuscator
     */
    private static AESObfuscator getAESObfuscator(){
        if (mObfuscator == null) {
            mObfuscator = new AESObfuscator(SoomlaConfig.obfuscationSalt,
                    SoomlaApp.getAppContext().getPackageName(), SoomlaUtils.deviceId());
        }

        return mObfuscator;
    }


    /** Private Members **/

    private static final String TAG = "SOOMLA KeyValueStorage"; //used for Log Messages

    private static AESObfuscator mObfuscator;

    private static KeyValDatabase mKvDatabase;
}
