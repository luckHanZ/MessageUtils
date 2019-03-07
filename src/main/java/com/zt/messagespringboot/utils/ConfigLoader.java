package com.zt.messagespringboot.utils;


/**
 * 这个类通过加载app_config.properties文件创建配置对象并获取值，包括创建
 * MailConfig，MessageConfig，VoiceConfig,InternationalsmsConfig,MobiledataConfig
 *
 * @author submail
 * @see AppConfig
 * @see MailConfig
 * @see MessageConfig
 * @see VoiceConfig
 * @see InternationalsmsConfig
 * @see MobiledataConfig
 */
public class ConfigLoader {


    /**
     * 加载文件时，类载入，静态块内部的操作将被运行一次
     * */

    /**
     * enum define two kinds of configuration.
     */

    public static AppConfig createMessageConfig() {
        //多态  父类类型指向子类对象
        AppConfig config = new MessageConfig();
//这是你自己的
        config.setAppId("30402");
        config.setAppKey("9f4111f58a39393eb2df9fe8503fbeba");
        //提示消息
        config.setSignType("KHGC");
        return config;
    }


}
