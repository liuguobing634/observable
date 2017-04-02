package lew.bing.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 刘国兵 on 2017/4/2.
 */
public class MyHttpResponse {

    private String content;
    private int statusCode;

    public MyHttpResponse(HttpResponse response) throws Exception{
        HttpEntity entity = response.getEntity();
        statusCode = response.getStatusLine().getStatusCode();
        try (InputStream is = entity.getContent()) {
            byte[] next = new byte[1024];
            StringBuilder buffer = new StringBuilder();
            while ((is.read(next)) != -1) {
                buffer.append(new String(next));
            }
            content = buffer.toString();
            if (statusCode >= 300) {
                throw new MyHttpException(statusCode,response.getStatusLine().getReasonPhrase(),content);
            }
        }
    }

    public String getContent() {
        return content;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
