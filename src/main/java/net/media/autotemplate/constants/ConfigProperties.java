package net.media.autotemplate.constants;

import net.media.autotemplate.bean.config.ConfigLine;
import net.media.autotemplate.util.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.function.Function;

import static net.media.autotemplate.dal.configs.AutoTemplateConfigMaster.parseAdAttribution;

/*
    Created by shubham-ar
    on 12/3/18 2:32 PM   
*/
public enum ConfigProperties {

    HEADER_TEXT(null, e -> {
        try {
            return URLDecoder.decode(e, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return e;
    }, null),
    AD_ATTRIBUTION(null, e -> parseAdAttribution(e).getLink(), e -> String.format("%s%s", ConfigConstants.AD_ATTRIBUTION_SEPARATOR, e)),
    MARK_TEMPLATE_INACTIVE(null, null, null);

    private final String defaultVal;
    private final Function<String, String> parser;
    private final Function<String, String> formatter;


    ConfigProperties(String defaultVal, Function<String, String> parser, Function<String, String> formatter) {

        if (!Util.isSet(parser))
            this.parser = s -> s;
        else this.parser = parser;

        if (!Util.isSet(formatter))
            this.formatter = s -> s;
        else this.formatter = formatter;

        this.defaultVal = defaultVal;

    }

    public Function<String, String> getParser() {
        return parser;
    }

    public Function<String, String> getFormatter() {
        return formatter;
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public String extractVal(List<ConfigLine> configs) {

        for (ConfigLine config : configs) {
            if (config.getProperty().equals(this.name())) {
                return config.getValue();
            }
        }
        return defaultVal;
    }
}
