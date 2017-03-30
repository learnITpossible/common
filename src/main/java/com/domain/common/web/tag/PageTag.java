package com.domain.common.web.tag;

import com.domain.common.web.widget.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Map;

public class PageTag extends TagSupport {

    private static final Logger logger = LoggerFactory.getLogger(PageTag.class);

    private static final long serialVersionUID = 4039222672779251275L;

    static final int SIZE = 10;

    static final String PAGE_NUM_NAME = "pageNum";

    PageBean pageBean;

    String url;

    boolean autoParams = true;

    public int doStartTag() throws JspTagException {

        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspTagException {

        String html = null;
        if (pageBean.getPageMode() == PageBean.PAGE_MODE_NORMAL) {
            if (pageBean.getTotalSize() == 0) {
                return EVAL_PAGE;
            }
            html = renderPageNormal();
        } else if (pageBean.getPageMode() == PageBean.PAGE_MODE_ONLY_NEXT_PREV) {
            html = renderPageOnlyNextPrev();
        }

        try {
            pageContext.getOut().write(html);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return EVAL_PAGE;
    }

    protected String renderPageOnlyNextPrev() {

        String url = getParamsUrl();
        StringBuffer html = new StringBuffer();
        html.append("<ul class=\"pagination\">");
        if (pageBean.isFirst()) {
            html.append("<li class=\"disabled\"><a href=\"#\" onclick='return false;'>首页</a></li>");
        } else {
            html.append("<li><a href=\"" + genPageUrl(pageBean.getFirstPage(), url) + "\">首页</a></li>");
        }
        if (!pageBean.isFirst()) {
            html.append("<li><a href=\"" + genPageUrl(pageBean.getPrevPage(), url) + "\">上一页</a></li>");
        }

        html.append("<li><a href=\"" + genPageUrl(pageBean.getNextPage(), url) + "\">下一页</a></li>");

        html.append("<li class=\"\"><span>当前第" + pageBean.getPageNum() + "页, 每页" + pageBean.getPageSize() + "条</span></li>");

        html.append("</ul>");
        return html.toString();
    }

    protected String renderPageNormal() {

        String url = getParamsUrl();
        StringBuffer html = new StringBuffer();
        html.append("<ul class=\"pagination\">");
        if (pageBean.isFirst()) {
            html.append("<li class=\"disabled\"><a href=\"#\" onclick='return false;'>首页</a></li>");
        } else {
            html.append("<li><a href=\"" + genPageUrl(pageBean.getFirstPage(), url) + "\">首页</a></li>");
        }
        if (pageBean.isFirst()) {
            html.append("<li class=\"disabled\"><a href=\"#\" onclick='return false;'><i class=\"entypo-left-open-mini\"></i></a></li>");
        } else {
            html.append("<li><a href=\"" + genPageUrl(pageBean.getPrevPage(), url) + "\"><i class=\"entypo-left-open-mini\"></i></a></li>");
        }
        int times = pageBean.getPageNum() / SIZE;
        if (pageBean.getPageNum() % SIZE == 0 && times > 0) {
            times--;
        }
        int start = times * SIZE + 1;

        int end = start + SIZE - 1;
        if (end > pageBean.getTotalPageCount()) {
            end = pageBean.getTotalPageCount();
        }

        for (int i = start; i <= end; i++) {
            if (i == pageBean.getPageNum()) {
                html.append("<li class=\"active\"><a href=\"#\" onclick='return false;'>" + i + "</a></li>");
            } else {
                html.append("<li><a href=\"" + genPageUrl(i, url) + "\">" + i + "</a></li>");
            }
        }
        if (pageBean.isLast()) {
            html.append("<li class=\"disabled\"><a href=\"#\" onclick='return false;'><i class=\"entypo-right-open-mini\"></i></a></li>");
        } else {
            html.append("<li><a href=\"" + genPageUrl(pageBean.getNextPage(), url) + "\"><i class=\"entypo-right-open-mini\"></i></a></li>");
        }

        if (pageBean.isLast()) {
            html.append("<li class=\"disabled\"><a href=\"#\" onclick='return false;'>末页</a></li>");
        } else {
            html.append("<li><a href=\"" + genPageUrl(pageBean.getLastPage(), url) + "\">末页</a></li>");
        }

        html.append("<li class=\"\"><span>当前" + pageBean.getPageNum() + "/" + pageBean.getTotalPageCount() + "页, 每页" + pageBean.getPageSize() + "条, 共" + pageBean.getTotalSize() + "条记录</span></li>");

        html.append("</ul>");
        return html.toString();
    }

    protected String genPageUrl(int pageNum, String url) {

        StringBuffer pageUrl = new StringBuffer();

        if (StringUtils.isEmpty(url)) {
            pageUrl.append("?");
        } else {
            pageUrl.append(url);
            if (url.indexOf("?") != -1) {
                pageUrl.append("&");
            } else {
                pageUrl.append("?");
            }
        }
        pageUrl.append(PAGE_NUM_NAME + "=").append(pageNum);

        return pageUrl.toString();
    }

    public PageBean getPageBean() {

        return pageBean;
    }

    public void setPageBean(PageBean pageBean) {

        this.pageBean = pageBean;
    }

    private String getParamsUrl() {

        StringBuffer queryStringBuff = null;

        Map<String, String[]> parameterMap = pageContext.getRequest().getParameterMap();
        if (autoParams && !parameterMap.isEmpty()) {
            queryStringBuff = new StringBuffer();
            for (String name : parameterMap.keySet()) {
                if (name.equalsIgnoreCase(PAGE_NUM_NAME)) {
                    continue;
                }
                String[] values = parameterMap.get(name);
                for (String value : values) {
                    queryStringBuff.append(name).append("=").append(value).append("&");
                }
            }
            if (queryStringBuff.toString().endsWith("&")) {
                queryStringBuff.deleteCharAt(queryStringBuff.length() - 1);
            }
        }
        if (queryStringBuff != null && !StringUtils.isEmpty(queryStringBuff.toString())) {
            if (StringUtils.isEmpty(this.url)) {
                return "?" + queryStringBuff;

            } else {
                return this.url + "?" + queryStringBuff;
            }
        } else {
            return this.url;
        }
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {

        this.url = url;
    }

    public boolean isAutoParams() {

        return autoParams;
    }

    public void setAutoParams(boolean autoParams) {

        this.autoParams = autoParams;
    }

}
