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

import com.soomla.Soomla;
import com.soomla.SoomlaApp;
import com.soomla.SoomlaConfig;
import com.soomla.SoomlaUtils;
import com.soomla.util.AESObfuscator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return getValue(key, null);
    }

    /**
     * Retrieves the value for the given key from the given storage
     *
     * @param key is the key in the key-val pair
     * @param storageName is the name of the storage to get the value from
     * @return the value for the given key
     */
    public static String getValue(String key, String storageName) {
        SoomlaUtils.LogDebug(TAG, "trying to fetch a value for key: " + key + (storageName != null ? " from storage: " + storageName : ""));

        KeyValueStorage kvs = getKeyValueStorage(storageName);
        key = kvs.getAESObfuscator().obfuscateString(key);

        String val = kvs.getDatabase().getKeyVal(key);

        if (val != null && !TextUtils.isEmpty(val)) {
            try {
                val = kvs.getAESObfuscator().unobfuscateToString(val);
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
        setNonEncryptedKeyValue(key, val, null);
    }

    /**
     * Sets key-val pair in the database according to given key and val in the given storage.
     *
     * @param key key to set in pair
     * @param val value to set in pair
     * @param storageName is the name of the storage to set the value in
     */
    public static void setNonEncryptedKeyValue(String key, String val, String storageName) {
        SoomlaUtils.LogDebug(TAG, "setting " + val + " for key: " + key + (storageName != null? " in storage: " + storageName : ""));

        KeyValueStorage kvs = getKeyValueStorage(storageName);
        val = kvs.getAESObfuscator().obfuscateString(val);

        kvs.getDatabase().setKeyVal(key, val);
    }

    /**
     * Deletes key-val pair that has the given key.
     *
     * @param key the key to indicate which pair to delete
     */
    public static void deleteNonEncryptedKeyValue(String key) {
        deleteNonEncryptedKeyValue(key, null);
    }

    /**
     * Deletes key-val pair that has the given key from the given storage.
     *
     * @param key the key to indicate which pair to delete
     * @param storageName is the name of the storage to delete the value from
     */
    public static void deleteNonEncryptedKeyValue(String key, String storageName) {
        SoomlaUtils.LogDebug(TAG, "deleting " + key + (storageName != null? " from storage: " + storageName : ""));

        getKeyValueStorage(storageName).getDatabase().deleteKeyVal(key);
    }

    /**
     * Retrieves the value of the key-val pair with the given key.
     *
     * @param key key according to which val will be retrieved
     * @return value of key-val pair
     */
    public static String getNonEncryptedKeyValue(String key) {
        return getNonEncryptedKeyValue(key, null);
    }

    /**
     * Retrieves the value of the key-val pair with the given key from the given storage.
     *
     * @param key key according to which val will be retrieved
     * @param storageName is the name of the storage to retrieve the value from
     * @return value of key-val pair
     */
    public static String getNonEncryptedKeyValue(String key, String storageName) {
        SoomlaUtils.LogDebug(TAG, "trying to fetch a value for key: " + key + (storageName != null? " from storage: " + storageName : ""));

        KeyValueStorage kvs = getKeyValueStorage(storageName);
        String val = kvs.getDatabase().getKeyVal(key);

        if (val != null && !TextUtils.isEmpty(val)) {
            try {
                val = kvs.getAESObfuscator().unobfuscateToString(val);
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
        return getNonEncryptedQueryValues(query, 0, null);
    }

    /**
     * Retrieves key-val pairs according to given query from the given storage.
     *
     * @param query query that determines what key-val pairs will be returned
     * @param storageName is the name of the storage to retrieve from
     * @return hashmap of key-val pairs
     */
    public static HashMap<String, String> getNonEncryptedQueryValues(String query, String storageName) {
		return getNonEncryptedQueryValues(query, 0, storageName);
	}

    /**
     * Retrieves key-val pairs according to given query, limiting amount of results returned.
     *
     * @param query query that determines what key-val pairs will be returned
     * @param limit max amount of key-val pairs returned
     * @return hashmap of key-val pairs
     */
    public static HashMap<String, String> getNonEncryptedQueryValues(String query, int limit) {
        return getNonEncryptedQueryValues(query, limit, null);
    }

	/**
	 * Retrieves key-val pairs according to given query from the given storage, limiting amount of results returned.
	 *
	 * @param query query that determines what key-val pairs will be returned
	 * @param limit max amount of key-val pairs returned
     * @param storageName is the name of the storage to retrieve from
	 * @return hashmap of key-val pairs
	 */
	public static HashMap<String, String> getNonEncryptedQueryValues(String query, int limit, String storageName) {
		SoomlaUtils.LogDebug(TAG, "trying to fetch values for query: " + query +
                (limit > 0? " with limit: " + limit : "") +
                (storageName != null? " from storage: " + storageName : ""));

        KeyValueStorage kvs = getKeyValueStorage(storageName);
		HashMap<String, String> vals = kvs.getDatabase().getQueryVals(query, limit);
		HashMap<String, String> results = new HashMap<String, String>();
		for(String key : vals.keySet()) {
			String val = vals.get(key);
			if (val != null && !TextUtils.isEmpty(val)) {
				try {
					val = kvs.getAESObfuscator().unobfuscateToString(val);
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
        return getOneForNonEncryptedQuery(query, null);
    }

    /**
     * Retrieves one key-val according to given query from the given storage.
     *
     * @param query query that determines what key-val will be returned
     * @param storageName is the name of the storage to retrieve from
     * @return string of key-val returned
     */
    public static String getOneForNonEncryptedQuery(String query, String storageName) {
        SoomlaUtils.LogDebug(TAG, "trying to fetch one for query: " + query + (storageName != null? " from storage: " + storageName : ""));

        KeyValueStorage kvs = getKeyValueStorage(storageName);
        String val = kvs.getDatabase().getQueryOne(query);
        if (val != null && !TextUtils.isEmpty(val)) {
            try {
                val = kvs.getAESObfuscator().unobfuscateToString(val);
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
        return getCountForNonEncryptedQuery(query, null);
    }

    /**
     * Retrieves the number key-vals according to given query from the given storage.
     *
     * @param query query that determines what number of key-vals
     * @param storageName is the name of the storage to retrieve from
     * @return number of key-vals according the the given query
     */
    public static int getCountForNonEncryptedQuery(String query, String storageName) {
        SoomlaUtils.LogDebug(TAG, "trying to fetch count for query: " + query + (storageName != null? " from storage: " + storageName : ""));

        return getKeyValueStorage(storageName).getDatabase().getQueryCount(query);
    }

    /**
     * Gets all keys in the storage with no encryption
     *
     * @return a List of unencrypted keys
     */
    public static List<String> getEncryptedKeys() {
        return getEncryptedKeys(null);
    }

    /**
     * Gets all keys in the given storage with no encryption
     *
     * @param storageName is the name of the storage to get from
     * @return a List of unencrypted keys
     */
    public static List<String> getEncryptedKeys(String storageName) {
        SoomlaUtils.LogDebug(TAG, "trying to fetch all keys" + (storageName != null ? " from storage: " + storageName : ""));

        KeyValueStorage kvs = getKeyValueStorage(storageName);
        List<String> encryptedKeys = kvs.getDatabase().getAllKeys();
        List<String> resultKeys = new ArrayList<String>();

        for (String encryptedKey : encryptedKeys) {
            try {
                String unencryptedKey = kvs.getAESObfuscator().unobfuscateToString(encryptedKey);
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
        setValue(key, val, null);
    }

    /**
     * Sets the given value to the given key in the given storage.
     *
     * @param key is the key in the key-val pair.
     * @param val is the val in the key-val pair.
     * @param storageName is the name of the storage to set value to
     */
    public static void setValue(String key, String val, String storageName) {
        SoomlaUtils.LogDebug(TAG, "setting " + val + " for key: " + key + (storageName != null? " in storage: " + storageName : ""));

        KeyValueStorage kvs = getKeyValueStorage(storageName);
        key = kvs.getAESObfuscator().obfuscateString(key);
        val = kvs.getAESObfuscator().obfuscateString(val);

        kvs.getDatabase().setKeyVal(key, val);
    }

    /**
     * Deletes a key-val pair with the given key.
     *
     * @param key is the key in the key-val pair.
     */
    public static void deleteKeyValue(String key) {
        deleteKeyValue(key, null);
    }

    /**
     * Deletes a key-val pair with the given key from the given storage.
     *
     * @param key is the key in the key-val pair.
     * @param storageName is the name of the storage to delete value from
     */
    public static void deleteKeyValue(String key, String storageName) {
        SoomlaUtils.LogDebug(TAG, "deleting " + key + (storageName != null? " from storage: " + storageName : ""));

        KeyValueStorage kvs = getKeyValueStorage(storageName);
        key = kvs.getAESObfuscator().obfuscateString(key);

        kvs.getDatabase().deleteKeyVal(key);
    }

    /**
     * Purges the entire storage
     *
     * NOTE: Use this method with care, it will erase all user data in storage
     * This method is mainly used for testing.
     */
    public static void purge() {
        purge(null);
    }

    /**
     * Purges the entire given storage
     *
     * NOTE: Use this method with care, it will erase all user data in storage
     * This method is mainly used for testing.
     *
     * @param storageName is the name of the storage to purge
     */
    public static void purge(String storageName) {
        SoomlaUtils.LogDebug(TAG, "purging database" + (storageName != null ? " in storage: " + storageName : ""));

        getKeyValueStorage(storageName).getDatabase().purgeDatabaseEntries(SoomlaApp.getAppContext());
    }

    /**
     * Set the secret for the given storage
     *
     * @param storageName is the name of the storage to set secret for
     * @param secret is the secret to set for the storage
     */
    public static void setSecretForStorage(String storageName, String secret) {
        if (mStorageSecrets == null) {
            mStorageSecrets = new HashMap<String, String>();
        }
        mStorageSecrets.put(storageName, secret);
    }

    /**
     * Retrieves the key-value storage according to the given name.
     *
     * @param storageName is the name of the storage to return
     * @return key-value storage
     */
    private static synchronized KeyValueStorage getKeyValueStorage(String storageName){

        if (mKeyValStorages == null) {
            mKeyValStorages = new HashMap<String, KeyValueStorage>();
        }

        if (TextUtils.isEmpty(storageName)) {
            storageName = SOOMLA_DATABASE_NAME;
        }

        KeyValueStorage kvs = mKeyValStorages.get(storageName);
        if (kvs == null) {
            kvs = new KeyValueStorage(storageName);
            mKeyValStorages.put(storageName, kvs);
        }

        return kvs;
    }

    /**
     * Retrieves the key-val database.
     *
     * @return key-val database
     */
    private synchronized KeyValDatabase getDatabase(){

        if (mKvDatabase == null) {
            mKvDatabase = new KeyValDatabase(SoomlaApp.getAppContext(), mDBName);
        }

        return mKvDatabase;
    }

    /**
     * Retrieves AESObfuscator
     *
     * @return AESObfuscator
     */
    private AESObfuscator getAESObfuscator(){
        if (mObfuscator == null) {
            mObfuscator = new AESObfuscator(SoomlaConfig.obfuscationSalt,
                    SoomlaApp.getAppContext().getPackageName(),
                    SoomlaUtils.deviceId(),
                    getSecretForDB(mDBName));
        }

        return mObfuscator;
    }

    private String getSecretForDB(String dbName) {
        if (mDBName.equals(SOOMLA_DATABASE_NAME)) {
            return Soomla.SECRET;
        }
        if (mStorageSecrets != null) {
            String secret = mStorageSecrets.get(dbName);
            if (secret != null) {
                return secret;
            }
        }
        return dbName + new String(new char[] {'.', 's', '0', 'm', 'e', '5', 't', 'r', '1', 'n', 'g', '4', 's', '3', 'c', 'r', 'e', '7'});
    }


    private KeyValueStorage(String dbName) {
        mDBName = dbName;
    }


    /** Private Members **/

    private static final String TAG = "SOOMLA KeyValueStorage"; //used for Log Messages
    public static final String SOOMLA_DATABASE_NAME  = "store.kv.db";

    private AESObfuscator mObfuscator;
    private KeyValDatabase mKvDatabase;
    private String mDBName;

    private static Map<String, KeyValueStorage> mKeyValStorages;
    private static Map<String, String> mStorageSecrets;
}
