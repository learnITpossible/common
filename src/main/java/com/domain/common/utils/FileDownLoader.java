package com.domain.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileDownLoader {

    private static final String REALNAME = "realName";

    private static final String STORENAME = "storeName";

    private static final String SIZE = "size";

    private static final String SUFFIX = "suffix";

    private static final String CONTENTTYPE = "contentType";

    private static final String CREATETIME = "createTime";

    private static final String UPLOADDIR = "uploadDir/";

    /**
     * 下载
     * @param request
     * @param response
     * @param content
     * @param contentType
     * @param realName
     * @throws Exception
     * @author geloin
     * @date 2012-5-5 下午12:25:39
     */
    public static void download(HttpServletRequest request,
                                HttpServletResponse response, String content, String contentType,
                                String realName) throws Exception {

        response.setContentType("text/html;charset=utf-8");
        response.setContentType("application/msword;charset=utf-8");
        request.setCharacterEncoding("UTF-8");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        response.setContentType(contentType);
        response.setHeader("Content-disposition", "attachment; filename="
                + new String(realName.getBytes("utf-8"), "ISO8859-1"));
        response.setHeader("Accept-Ranges", "bytes");
        byte b[] = content.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(b);

        bis = new BufferedInputStream(bais);
        bos = new BufferedOutputStream(response.getOutputStream());
        byte[] buff = new byte[2048];
        int bytesRead;
        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
            bos.write(buff, 0, bytesRead);
        }
        bis.close();
        bos.close();
    }
}
