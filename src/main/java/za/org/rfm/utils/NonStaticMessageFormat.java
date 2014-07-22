package za.org.rfm.utils;

import java.text.MessageFormat;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/18
 * Time: 3:43 PM
 */
public class NonStaticMessageFormat {
    public String msg;
    public NonStaticMessageFormat(String format,Object...args) {
        this.msg = MessageFormat.format(format,args);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
