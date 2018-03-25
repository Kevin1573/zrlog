package com.zrlog.web.handler;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.jfinal.handler.Handler;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.util.ZrLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.util.*;

/**
 * 用于对静态文件的请求的检查，和静态化文章页，加快文章页的响应。
 */
public class StaticFileCheckHandler extends Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticFileCheckHandler.class);

    //不希望部分技术人走后门，拦截一些不合法的请求
    private static final Set<String> FORBIDDEN_URI_EXT_SET = new HashSet<>();

    static {
        //由于程序的.jsp文件没有存放在WEB-INF目录，为了防止访问.jsp页面获得的没有数据的页面，或则是错误的页面。
        FORBIDDEN_URI_EXT_SET.add(".jsp");
        //这主要用在主题目录下面的配置文件。
        FORBIDDEN_URI_EXT_SET.add(".properties");
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        String ext = null;
        if (target.contains("/")) {
            String name = target.substring(target.lastIndexOf('/'));
            if (name.contains(".")) {
                ext = name.substring(name.lastIndexOf('.'));
            }
        }
        if (ext != null) {
            if (!FORBIDDEN_URI_EXT_SET.contains(ext)) {
                // 处理静态化文件,仅仅缓存文章页(变化较小)
                if (target.endsWith(".html") && target.startsWith("/post/")) {
                    target = target.substring(0, target.lastIndexOf("."));
                    if (Constants.isStaticHtmlStatus()) {
                        try {
                            String path = new String(request.getServletPath().getBytes("ISO-8859-1"), "UTF-8");
                            File htmlFile = new File(PathKit.getWebRootPath() + path);
                            response.setContentType("text/html;charset=UTF-8");
                            if (htmlFile.exists() && !ZrLogUtil.isStaticBlogPlugin(request)) {
                                isHandled[0] = true;
                                response.getOutputStream().write(IOUtil.getByteByInputStream(new FileInputStream(htmlFile)));
                                this.next.handle(target, request, response, isHandled);
                            } else {
                                final CopyPrintWriter writer = new CopyPrintWriter(response.getWriter());
                                response = new HttpServletResponseWrapper(response) {
                                    @Override
                                    public PrintWriter getWriter() throws IOException {
                                        return writer;
                                    }
                                };
                                this.next.handle(target, request, response, isHandled);
                                saveResponseBodyToHtml(PathKit.getWebRootPath(), htmlFile, writer);
                            }
                        } catch (Exception e) {
                            LOGGER.error("error", e);
                        }
                    } else {
                        this.next.handle(target, request, response, isHandled);
                    }
                } else {
                    this.next.handle(target, request, response, isHandled);
                }
            } else {
                try {
                    //非法请求, 返回403
                    request.getSession();
                    response.sendError(403);
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        } else {
            this.next.handle(target, request, response, isHandled);
        }
    }

    /**
     * 将一个网页转化对应文件，用于静态化文章页
     */
    private void saveResponseBodyToHtml(String webRoot, final File file, CopyPrintWriter printStream) {
        try {
            byte[] bytes = printStream.getCopy().getBytes("UTF-8");
            tryResizeDiskSpace(webRoot, bytes.length);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            IOUtil.writeBytesToFile(bytes, file);
        } catch (IOException e) {
            LOGGER.error("saveResponseBodyToHtml error", e);
        }
    }

    /**
     * 避免过多磁盘资源占用,超过阀值时,情况比较旧的文件
     *
     * @param webRoot
     * @param currentLength
     */
    private void tryResizeDiskSpace(String webRoot, int currentLength) {
        List<File> fileList = new ArrayList<>();
        FileUtils.getAllFiles(webRoot + "/post", fileList);
        int totalSize = currentLength;
        for (File tFile : fileList) {
            totalSize += tFile.length();
        }
        if (totalSize >= Constants.getMaxCacheHtmlSize()) {
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Long.compare(o1.lastModified(), o2.lastModified());
                }
            });
            int needRemoveSize = totalSize - Constants.getMaxCacheHtmlSize();
            for (File tFile : fileList) {
                needRemoveSize -= tFile.length();
                tFile.delete();
                if (needRemoveSize <= 0) {
                    break;
                }
            }
        }
    }

    class CopyPrintWriter extends PrintWriter {

        private StringBuilder copy = new StringBuilder();

        CopyPrintWriter(Writer writer) {
            super(writer);
        }

        @Override
        public void write(int c) {
            copy.append((char) c); // It is actually a char, not an int.
            super.write(c);
        }

        @Override
        public void write(char[] chars, int offset, int length) {
            copy.append(chars, offset, length);
            super.write(chars, offset, length);
        }

        @Override
        public void write(String string, int offset, int length) {
            copy.append(string, offset, length);
            super.write(string, offset, length);
        }

        public String getCopy() {
            return copy.toString();
        }

    }
}