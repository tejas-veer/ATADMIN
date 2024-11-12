package net.media.autotemplate.dal.configs;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.media.autotemplate.bean.FrameworkSize;
import net.media.autotemplate.bean.QueryTuple;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.util.Separator;
import net.media.autotemplate.util.Util;
import net.media.database.DatabaseException;

import java.util.*;
import java.util.stream.Collectors;

public class GlobalConfig {
    private static Gson gson = new Gson();

    public static JsonArray getProperty(String property) throws DatabaseException {
        QueryTuple tuple = AutoTemplateDAL.getAutoTemplateConfigMaster(ConfigConstants.GLOBAL_CONFIG, property);
        HashSet<String> values = getHashSet(tuple.getValue());
        if(property.equals("VALID_SIZES")){
            values = getSortedValue(values);
        }
        if (values.size() == 0)
            return new JsonArray();
        return gson.fromJson(gson.toJson(values), JsonArray.class);
    }

    public static HashSet<String> getSortedValue(HashSet<String> sizes){
        List<FrameworkSize> unsortedSizeList  = new ArrayList<>();
        for(String size: sizes){
            String [] split = size.toLowerCase().split("x");
            unsortedSizeList.add(new FrameworkSize(-1, Integer.parseInt(split[0]), Integer.parseInt(split[1]), size));
        }

        Comparator<FrameworkSize> sizeComparator = Comparator.comparingInt(FrameworkSize::getWidth).thenComparingInt(FrameworkSize::getHeight);
        List<FrameworkSize> sortedSizeList = unsortedSizeList.stream().sorted(sizeComparator).collect(Collectors.toList());
        return sortedSizeList.stream().map(FrameworkSize::getWidthByHeight).collect(Collectors.toCollection(LinkedHashSet::new));
    }


    public static String getSimpleProperty(String property) throws DatabaseException {
        QueryTuple tuple = AutoTemplateDAL.getAutoTemplateConfigMaster(ConfigConstants.GLOBAL_CONFIG, property);
        if (Util.isSet(tuple))
            return tuple.getValue();
        return null;
    }

    public static HashSet<String> getHashSet(String value) {
        HashSet<String> values = new HashSet<>();
        String[] valueStrs = value.split(Separator.REGEX_DATA);
        values.addAll(Arrays.asList(valueStrs));
        return values;
    }

    public static String getString(HashSet<String> values) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iterator = values.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) builder.append(Separator.DATA);
        }
        return builder.toString();
    }

    public static List<String> getAllValidTemplateSizes() throws DatabaseException {
        JsonArray j = getProperty("VALID_SIZES");
        List<String> sizes = new ArrayList<>();
        for (JsonElement jle : j) {
            sizes.add(jle.getAsString().trim());
        }
        Collections.sort(sizes);
        return sizes;
    }
}
