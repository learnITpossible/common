package com.domain.common.utils;

public class EscapeHtml {

    public static String escape(String html) {

        if (html == null || html.length() == 0) {
            return html;
        }
        html = html.replace("<", "&lt;");
        html = html.replace(">", "&gt;");
        //TODO 暂时关闭单双引号转义
        // html = html.replace("\"", "&quot;");
        // html = html.replace("'", "&apos;");
        return html;
    }

}
