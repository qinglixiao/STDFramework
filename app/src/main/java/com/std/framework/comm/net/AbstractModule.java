package com.std.framework.comm.net;

/**
 * Description:
 * Created by 李晓 ON 2017/12/19.
 * Job number:137289
 * Phone:18611867932
 * Email:lixiao3@syswin.com
 * Person in charge:李晓
 * Leader: 李晓
 */
public abstract class AbstractModule extends NetBase {
    public AbstractModule() {
        super();
        init();
    }

    /**
     * 初始化网络参数
     */
    public abstract void init();

}
