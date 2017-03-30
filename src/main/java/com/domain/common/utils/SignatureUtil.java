package com.domain.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SignatureUtil {

    private static Log log = LogFactory.getLog(SignatureUtil.class);

    public static final String ACCESS_SECRET = "secret";

    public static final String SIG = "sig";

    public static String renderSignature(Map<String, String[]> params, String secret) {

        List<String> names = new ArrayList<String>();
        names.addAll(params.keySet());
        names.remove(SIG);
        names.remove(ACCESS_SECRET);
        Collections.sort(names);
        StringBuilder sb = new StringBuilder();

        sb.append(secret);
        for (String name : names) {
            sb.append("&").append(name).append("=");
            String[] paramValues = params.get(name);
            sb.append(StringUtils.join(paramValues, ","));
        }
        String sig;
        try {
            sig = Base64.encodeBytes(DigestUtils.md5Hex(sb.toString()).getBytes("UTF8"));
        } catch (UnsupportedEncodingException e) {
            sig = Base64.encodeBytes(DigestUtils.md5Hex(sb.toString()).getBytes());
            log.error(e.getMessage(), e);
        }
        return sig;
    }

}
