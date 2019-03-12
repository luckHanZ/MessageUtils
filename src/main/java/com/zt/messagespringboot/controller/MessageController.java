package com.zt.messagespringboot.controller;

import com.zt.messagespringboot.utils.ResultJson;
import com.zt.messagespringboot.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * 创链科技
 *
 * @Description: 验证码获取
 * @Project:    工具类demo
 * @CreateDate: Created in 2019/3/7 15:05
 * @Author: zhaotong
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    private static Logger logger = LoggerFactory.getLogger(MessageController.class);
/**
 * @Author zhaotong
 * @Description  通过网易云短信平台发送短信
 * @Date 15:44 2019/3/7
 * @Param [mobile]
 *          手机号
 * @return com.zt.messagespringboot.utils.ResultJson
 **/
    @RequestMapping("senMessage")
    public ResultJson senMessage(String mobile) {
        System.out.println("sasssss");
        logger.info("发送短信");
        System.out.println("ran = " );
        if (StringUtils.isBlank(mobile)) {
            return ResultJson.errorCodeMsg(0, "手机号不可以为空");
        }
        System.out.println("ssdsdasdasdsa");
        //生成4位随机数
        Random random = new Random();
        int ran = random.nextInt(9001) + 1000;
        logger.info(mobile + "手机号号的人，你的注册账号是" + ran);
        //发送短信验证码
        SmsUtils.sendVerificationCodeMsg(Integer.toString(ran), mobile);
        return ResultJson.build(1, "短信验证码发送成功", String.valueOf(ran));
    }
}
