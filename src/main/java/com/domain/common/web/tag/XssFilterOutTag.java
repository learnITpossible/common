package com.domain.common.web.tag;

import com.domain.common.web.el.XssFilterELResolver;

import javax.servlet.jsp.tagext.TagSupport;

public class XssFilterOutTag extends TagSupport {

    private static final long serialVersionUID = 1L;

    private static final boolean ESCAPE_HTML_DEFAULT = true;

    private boolean escapeHtml;

    public XssFilterOutTag() {

        release();
    }

    @Override
    public int doStartTag() {

        pageContext.setAttribute(XssFilterELResolver.ESCAPE_HTML_ATTRIBUTE, escapeHtml);
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() {

        pageContext.setAttribute(XssFilterELResolver.ESCAPE_HTML_ATTRIBUTE, ESCAPE_HTML_DEFAULT);
        return EVAL_PAGE;
    }

    @Override
    public void release() {

        escapeHtml = ESCAPE_HTML_DEFAULT;
    }

    public void setEscapeHtml(boolean escapeHtml) {

        this.escapeHtml = escapeHtml;
    }
}
