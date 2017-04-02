package lew.bing.http;

/**
 * Created by 刘国兵 on 2017/4/2.
 */
public class MyHttpException extends Exception {

    private int code;
    private String status;
    private String content;

    public MyHttpException(int code,String status,String content){
        super("\ncode:"+code+"\nstatus:"+status+"\n");
        this.code = code;
        this.status = status;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }
}
