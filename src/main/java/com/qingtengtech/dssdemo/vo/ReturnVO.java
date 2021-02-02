package com.qingtengtech.dssdemo.vo;

import lombok.ToString;

import java.util.HashMap;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * code: 0 = 成功, 其他为出错或异常
 * @author Moonlight
 * @date 2021/1/25 9:54
 */
@ToString
public class ReturnVO extends HashMap<String, Object> {

    public ReturnVO() {
        put("code", 0);
        put("msg", "");
    }

    public ReturnVO(int code, String msg) {
        put("code", code);
        put("msg", msg);
    }

    public static ReturnVO ok() {
        return new ReturnVO(0, "");
    }

    public static ReturnVO error(int code, String msg) {
        return new ReturnVO(code, msg);
    }

    @Override
    public ReturnVO put(String key, Object val) {
        super.put(key, val);
        return this;
    }
}
