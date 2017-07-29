package com.yisingle.driver.app.utils;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class JsonUtils {

    //protected final static Log logger = LogFactory.getLog(JsonUtils.class);

    public static boolean isBadJson(String json) {
        return !isGoodJson(json);
    }

    public static boolean isGoodJson(String json) {

        if (StringUtils.isEmpty(json)) {
            return false;
        }
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            // logger.error("bad json: " + json);
            return false;
        }
    }
}