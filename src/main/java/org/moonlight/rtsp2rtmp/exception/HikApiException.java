package org.moonlight.rtsp2rtmp.exception;

/**
 * 〈功能简述〉<br>
 * 〈海康Api异常〉
 *
 * @author Moonlight
 * @date 2021/6/25 11:24
 */
public class HikApiException extends RuntimeException {

    /** 异常信息描述 **/
    private String desc;
    /** 海康Last Error **/
    private int lastError;

    public HikApiException(String exceptionDesc, int lastError) {
        super(exceptionDesc);
        this.desc = exceptionDesc;
        this.lastError = lastError;
    }

    public HikApiException(String exceptionDesc, int lastError, Throwable t) {
        super(exceptionDesc, t);
        this.desc = exceptionDesc;
        this.lastError = lastError;
    }

}
