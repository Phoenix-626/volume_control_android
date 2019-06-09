package com.example.punksta.volumecontrol.util;

import android.content.SharedPreferences;

import com.example.punksta.volumecontrol.data.SoundProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SoundProfileStorage {
    private final SharedPreferences preferences;
    private List<Integer> ids;


    public SoundProfileStorage(SharedPreferences preferences) {
        this.preferences = preferences;
    }


    private void loadIds() throws JSONException {
        if (ids == null) {
            ids = deserializeIds(preferences.getString("ids", "[]"));
        }
    }

    public SoundProfile[] loadAll() throws JSONException {
        loadIds();
        SoundProfile[] profiles = new SoundProfile[ids.size()];

        for (int i = 0; i < ids.size(); i++) {
            profiles[i] = loadById(ids.get(i));
        }

        return profiles;
    }

    public SoundProfile loadById(int id) throws JSONException {
        return deserialize(preferences.getString("" + id, ""));
    }

    public void removeProfile(int id) {
        ids.remove(ids.indexOf(Integer.valueOf(id)));
        preferences.edit().remove("" + id).apply();
        preferences.edit().putString("ids", serializeIds(ids)).apply();
    }

    public void saveProfile(SoundProfile profile) throws JSONException {
        boolean found = false;
        for (int id : ids) {
            if (id == profile.id) {
                found = true;
                break;
            }
        }
        SharedPreferences.Editor editor = preferences.edit();
        if (!found) {
            ids.add(profile.id);
            editor.putString("ids", serializeIds(ids));
        }
        editor.putString(profile.id.toString(), serialize(profile)).apply();

        editor.apply();
    }


    private String serializeIds(List<Integer> ids) {
        JSONArray r = new JSONArray();
        for (int id : ids) {
            r.put(id);
        }
        return r.toString();
    }


    private List<Integer> deserializeIds(String str) throws JSONException {
        JSONArray r = new JSONArray(str);
        List<Integer> result = new ArrayList<>(r.length());
        for (int i = 0; i < r.length(); i++) {
            result.add(r.getInt(i));
        }
        return result;
    }

    private static String serialize(SoundProfile profile) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("name", profile.name);
        object.put("id", profile.id);
        JSONObject settings = new JSONObject();
        for (Map.Entry<Integer, Integer> integerIntegerEntry : profile.settings.entrySet()) {
            settings.put(integerIntegerEntry.getKey().toString(), integerIntegerEntry.getValue());
        }
        object.put("settings", settings);
        return object.toString();
    }

    private static SoundProfile deserialize(String string) throws JSONException {
        SoundProfile result = new SoundProfile();

        JSONObject object = new JSONObject(string);

        result.name = object.getString("name");
        result.id = object.getInt("id");

        JSONObject settings = object.getJSONObject("settings");

        for(int i = 0; i< settings.names().length(); i++){
            String key = settings.names().getString(i);
            int value = settings.getInt(key);

            Integer volumeName = Integer.parseInt(key);

            result.settings.put(volumeName, value);
        }
        return result;
    }

}
