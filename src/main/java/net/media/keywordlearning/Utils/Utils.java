package net.media.keywordlearning.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

/**
 * Created by autoopt/rohit.aga.
 */
public class Utils {

    private static Gson gson = new Gson();

    public static String getTrimmedString(String str){
        if(isEmpty(str)) {
            return str;
        }
        else {
            return str.trim();
        }
    }

    public static Boolean isEmpty(Object ob){
        return ob == null;
    }

    public static Boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }


    public static Gson getGson(){
        if(gson != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            gson = gsonBuilder.create();
        }
        return gson;
    }

    public static <T> T safeReturn(List<T> list, int index){
        if(list.size() > index){
            return list.get(index);
        }else{
            return null;
        }
    }

    public static String getProperty(String propertyName){
        return PropertyManager.getProperties().getProperty(propertyName);
    }

    public static <K, V> Map<K, V> entrySetToMap(Collection<Map.Entry<K, V>> entryList) {
        return entrySetToMap(entryList, true);
    }

    public static <K, V> Map<K, V> entrySetToMap(Collection<Map.Entry<K, V>> entryList, boolean isOverwrite) {
        HashMap<K, V> map = new LinkedHashMap<>(entryList.size());
        if(isOverwrite){
            for (Map.Entry<K, V> kvEntry : entryList) {
                map.put(kvEntry.getKey(), kvEntry.getValue());
            }
        }
        else{
            for (Map.Entry<K, V> kvEntry : entryList) {
                if(!map.containsKey(kvEntry.getKey())){
                    map.put(kvEntry.getKey(), kvEntry.getValue());
                }
            }
        }
        return map;
    }

    public static List<String> getListFromString(String list){
        String[] split = list.split(",");
        return new ArrayList<String>(Arrays.asList(split));
    }


    public static String getMySqlDBProcString(String procName, Map<String, String> map) {
        String str = "CALL " + procName + "(";
        str += "@domain_name:= " + map.get("domain_name") + ",";
        str += "@keyword_type_id:= " + map.get("keyword_type_id") + ",";
        str += "@basis:= " + map.get("basis") + ",";
        str += "@num_of_urls:= " + map.get("num_of_urls") + ",";
        str += "@download:= " + map.get("download") + ",";
        str += "@learner_id:= " + map.get("learner_id") + ",";
        str += "@bucket_id:= " + map.get("bucket_id") + ",";
        str += "@actual_data:= " + map.get("actual_data") + ",";
        str += "@ad_tag_id:= " + map.get("ad_tag_id");
        str += ")";
        return str;
    }

    public static String formatDouble(Double value){
        return String.format("%.2f", value);
    }

    public static Double getRpc(double revenue, double conversion){
        if(conversion == 0){
            return 0.00;
        }
        return revenue / conversion;
    }
}