package net.media.autotemplate.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.AclStatus;
import net.media.autotemplate.bean.EntityParentsData;
import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.bean.TemplateMetaInfo;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.db.CMTemplateDbUtil;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.database.DatabaseException;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

import static net.media.database.util.Util.implode;

/*
    Created by shubham-ar
    on 9/10/17 3:41 PM   
*/
public class Util {
    private static final Logger LOG = LogManager.getLogger(Util.class);
    private static final String EMAIL_PROVIDER = "@media.net";
    private static final String EMAIL_PROVIDER2 = "@directi.com";

    public static Gson getGson() {
        return new Gson();
    }

    public static String getConcatString(Object... strings) {
        StringBuilder sb = new StringBuilder();
        for (Object str : strings) {
            sb.append(String.valueOf(str));
        }
        return sb.toString();
    }

    public static boolean isSet(Object object) {
        return object != null;
    }

    public static boolean isStringSet(String string) {
        return string != null && string.trim().length() > 0;
    }

    public static boolean isStringSetAndDefined(String string) {
        return isStringSet(string) && !string.equals("undefined") && !string.equals("null");
    }

    public static boolean isStringEmpty(String string) {
        return !isStringSet(string);
    }

    public static boolean empty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean empty(Map map) {
        return map == null || map.size() == 0;
    }

    public static <T> boolean empty(T[] array) {
        return array == null || array.length == 0;
    }

    public static JsonObject getJsonObject(Object bean, Type classType) {
        return getGson().fromJson(getGson().toJson(bean), classType);
    }

    public static String concatenate(String separator, Object... segments) {
        return implode(separator, Arrays.asList(segments));
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static <T> boolean doSetsIntersect(Set<T> list1, Set<T> list2) {
        Set<T> commonSet = new HashSet<>(list1);
        commonSet.retainAll(list2);
        return commonSet.size() > 0;
    }

    private static void findFields(Class metaClass2, List<Field[]> fields) {
        Class next = metaClass2;
        Stack<Field[]> orderStack = new Stack<>();
        while (true) {
            Field[] f = next.getDeclaredFields();
            orderStack.push(f);
            next = next.getSuperclass();
            if (next.equals(Object.class))
                break;
        }
        while (!orderStack.empty()) {
            fields.add(orderStack.pop());
        }
    }

    public static <T> List<T> safeSublist(List<T> urls, int i, int j) {
        if (Objects.isNull(urls) || urls.size() == 0)
            return Collections.emptyList();
        j = Integer.min(j, urls.size());
        i = Integer.max(0, i);
        i = Integer.min(i, j);
        return urls.subList(i, j);
    }

    public static String getEntityId(String key) {
        return key.split(ConfigConstants.UNDERSCORE_SEPARATOR)[0];
    }

    public static String getHierarchy(String key) {
        return key.split(ConfigConstants.UNDERSCORE_SEPARATOR)[1];
    }

    public static String getKey(String entityId, String hierarchy) {
        return entityId.trim() + ConfigConstants.UNDERSCORE_SEPARATOR + hierarchy.trim();
    }

    public static String getCampaignKey(String campaignId) {
        return getKey(campaignId, CreativeConstants.Level.CAMPAIGN.name());
    }

    public static String getAdvertiserKey(String advertiserId) {
        return getKey(advertiserId, CreativeConstants.Level.ADVERTISER.name());
    }

    public static Map<String, EntityParentsData> getParentData(String entity, CreativeConstants.Level level) throws DatabaseException {
        Set<String> singleEntityHierarchy = new HashSet<>();
        singleEntityHierarchy.add(getKey(entity, level.name()));
        return getParentData(singleEntityHierarchy);
    }

    public static Map<String, EntityParentsData> getParentData(Set<String> entityHierarchySet) throws DatabaseException {
        Map<String, EntityParentsData> parentsDataMap = new HashMap<>();
        Set<String> remainingEntries = new HashSet<>();
        for (String entry : entityHierarchySet) {
            if (getHierarchy(entry).equalsIgnoreCase(CreativeConstants.Level.ADVERTISER.name())) {
                parentsDataMap.put(entry, new EntityParentsData(getEntityId(entry), getHierarchy(entry), null, Arrays.asList(entry)));
            } else {
                remainingEntries.add(entry);
            }
        }
        parentsDataMap.putAll(AutoTemplateDAL.getEntityParentData(remainingEntries));
        return parentsDataMap;
    }

    public static List<String> getDistinctEntityKeys(Map<String, EntityParentsData> entityParentsData) {
        Set<String> entityKeys = new HashSet<>();
        for (Map.Entry<String, EntityParentsData> entry : entityParentsData.entrySet()) {
            if (Util.isSet(entry.getValue().getCampaignKey())) {
                entityKeys.add(entry.getValue().getCampaignKey());
            }
            if (Util.isSet(entry.getValue().getAdvertiserKeys())) {
                entityKeys.addAll(entry.getValue().getAdvertiserKeys());
            }
        }
        List<String> keys = new ArrayList<>();
        keys.addAll(entityKeys);
        return keys;
    }

    public static List<Pair<String, String>> getRemainingEntriesToInsert(Set<String> entityKeySet, Map<String, Long> entityDataMap) {
        List<Pair<String, String>> remainingPairs = new ArrayList<>();
        for (String entry : entityKeySet) {
            if (!entityDataMap.containsKey(entry)) {
                String[] split = entry.split(ConfigConstants.UNDERSCORE_SEPARATOR);
                remainingPairs.add(new Pair<>(split[0], split[1]));
            }
        }
        return remainingPairs;
    }

    public static AclStatus aclStatusForPartner(String entity) {
        return new AclStatus(HttpStatus.SC_FORBIDDEN, "You don't have write access to PARTNER " + entity);
    }

    public static String getSessionAttributeAsString(HttpSession session, String attribute) {
        try {
            Object value = session.getAttribute(attribute);
            if (Util.isSet(value)) {
                return String.valueOf(value);
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static Long getSessionAttributeAsLong(HttpSession session, String attribute) {
        try {
            Object value = session.getAttribute(attribute);
            if (Util.isSet(value)) {
                return Long.valueOf(String.valueOf(value));
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String key) {
        Optional<Cookie> cookie = Optional.empty();
        try {
            Cookie[] cookies = request.getCookies();
            cookie = Arrays.stream(cookies)
                    .filter(c -> key.equals(c.getName()))
                    .findAny();
        } catch (NullPointerException ignored) {
            ignored.printStackTrace();
        }
        return cookie;
    }

    @NotNull
    public static List<String> getEmailList(String canonicalName, String email) {
        List<String> emailList = new ArrayList<>(Arrays.asList(email, canonicalName + EMAIL_PROVIDER,
                canonicalName + EMAIL_PROVIDER2));
        return emailList;
    }

    @NotNull
    public static String getUri(HttpServletRequest req) {
        String uri = "";
        if (req.isSecure()) {
            uri += "https://";
        } else {
            uri += "http://";
        }
        uri += req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + req.getServletPath();
        if (req.getQueryString() != null) {
            uri += "?" + req.getQueryString();
        }
        return uri;
    }

    public static String getTrimmedStringIfSet(String s) {
        return isStringSet(s) ? s.trim() : s;
    }

    public static int getAdCountFromString(String adCountString) {
        int adCount = 1;
        try {
            adCount = Util.isStringSet(adCountString) ? Integer.parseInt(adCountString) : 1;
        } catch (Exception ignored) {

        } finally {
            return adCount;
        }
    }

    public static boolean isNumeric(String str) {
        if (!Util.isStringSet(str)) {
            return false;
        }
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static TemplateMetaInfo getTemplateMetaInfo(String templateId) {
        TemplateMetaInfo templateMetaInfo = new TemplateMetaInfo(-1, "ERROR_IN_GETTING_TEMPLATE_NAME", -1, "", "");
        String templateName = "ERROR_IN_GETTING_TEMPLATE_NAME";
        try {
            templateMetaInfo = CMTemplateDbUtil.getTemplateMetaInfo(templateId);
        } catch (Exception e) {
            LoggingService.log(LOG, Level.error, "ERROR_IN_GETTING_TEMPLATE_NAME", e);
        }
        return templateMetaInfo;
    }

    public static String getFrameworkName(String frameworkId) {
        String frameworkName = "ERROR_IN_GETTING_FRAMEWORK_NAME";
        try {
            frameworkName = CMTemplateDbUtil.getFrameworkName(frameworkId);
        } catch (Exception e) {
            LoggingService.log(LOG, Level.error, "ERROR_IN_GETTING_FRAMEWORK_NAME", e);
        }
        return frameworkName;
    }
}
