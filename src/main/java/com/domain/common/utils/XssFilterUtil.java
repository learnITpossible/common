package com.domain.common.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XssFilterUtil {

    static String scriptWords = "javascript|jscript|vbscript|expression|data:";

    static String xssHtmlTags = "applet,meta,xml,blink,link,style,script,embed,object,iframe,frame,frameset,ilayer,layer,bgsound,title,base";

    static String[] xssHtmlEvents = new String[]{"onabort", "onactivate", "onafterprint", "onafterupdate", "onbeforeactivate", "onbeforecopy", "onbeforecut", "onbeforedeactivate", "onbeforeeditfocus", "onbeforepaste", "onbeforeprint", "onbeforeunload", "onbeforeupdate", "onblur", "onbounce", "oncellchange", "onchange", "onclick", "oncontextmenu", "oncontrolselect", "oncopy", "oncut", "ondataavailable", "ondatasetchanged", "ondatasetcomplete", "ondblclick", "ondeactivate", "ondrag", "ondragend", "ondragenter", "ondragleave", "ondragover", "ondragstart", "ondrop", "onerror", "onerrorupdate", "onfilterchange", "onfinish", "onfocus", "onfocusin", "onfocusout", "onhelp", "onkeydown", "onkeypress", "onkeyup", "onlayoutcomplete", "onload", "onlosecapture", "onmousedown", "onmouseenter", "onmouseleave", "onmousemove", "onmouseout", "onmouseover", "onmouseup", "onmousewheel", "onmove", "onmoveend", "onmovestart", "onpaste", "onpropertychange", "onreadystatechange", "onreset", "onresize", "onresizeend", "onresizestart", "onrowenter", "onrowexit", "onrowsdelete", "onrowsinserted", "onscroll", "onselect", "onselectionchange", "onselectstart", "onstart", "onstop", "onsubmit", "onunload"};

    static String[] xssHtmlAttrs = new String[]{"href", "src", "style", "background", "dynsrc", "lowsrc"};

    static String xssHtmlAttrsSelector = null;

    static {
        int iMax = xssHtmlAttrs.length - 1;
        if (iMax > -1) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; ; i++) {
                b.append("[").append(xssHtmlAttrs[i]).append("]");
                if (i == iMax) {
                    xssHtmlAttrsSelector = b.toString();
                    break;
                }
                b.append(",");
            }
        }
    }

    public static String filter2Text(String str) {

        if (str == null) {
            return null;
        }
        Pattern p = Pattern.compile("<[\\w]+.*>", Pattern.DOTALL);
        Matcher matcher = p.matcher(str);
        if (matcher.find()) {
            Document doc = Jsoup.parse(str);
            str = doc.body().text();
        }
        return str;
    }

    public static String filter2Html(String str) {

        if (str == null) return null;
        Pattern p = Pattern.compile("<[\\w]+.*>", Pattern.DOTALL);
        Matcher matcher = p.matcher(str);
        if (!matcher.find()) {
            return str;
        }
        str = str.replaceAll("\r|\n|\t", "");
        Document doc = Jsoup.parse(str);

        doc.select(xssHtmlTags).remove();
        String html = doc.html();
        for (String htmlEvent : xssHtmlEvents) {
            if (html.contains(htmlEvent)) {
                doc.select("*").removeAttr(htmlEvent);
            }
        }
        if (xssHtmlAttrsSelector != null) {
            Elements els = doc.select(xssHtmlAttrsSelector);
            for (Element el : els) {
                for (String attr : xssHtmlAttrs) {
                    String attrVal = el.attr(attr);
                    if (el.hasAttr(attr) && attrVal != null) {
                        Pattern p2 = Pattern.compile(scriptWords);
                        Matcher matcher2 = p2.matcher(attrVal);
                        if (matcher2.find()) {
                            el.removeAttr(attr);
                        }

                    }
                }
            }
        }
        html = doc.body().html().replaceAll("\r|\n|\t", "");

        return html;
    }

}
