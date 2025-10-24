package com.example.core_api.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonMaps {
    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for(Iterator<String> it = object.keys(); it.hasNext();){
            String key = it.next();
            Object value = object.get(key);
            map.put(key, fromJson(value));
        }
        return map;
    }
    public static List<Object> toList (JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>(array.length());
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    private static Object fromJson(Object object) throws JSONException {
        if (object == JSONObject.NULL) return null;
        if (object instanceof JSONObject obj) return toMap(obj);
        if (object instanceof JSONArray arr) return toList(arr);
        return object;
    }
}
