package com.zt.messagespringboot.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.*;

/**
 * 发送短信工具
 * @Author 黄国刚【1058118328@qq.com】
 */
public class SmsUtils {

    /**
     * 发送验证码接口地址
     */
    private static final String VERIFICATION_CODE_URL = "https://api.netease.im/sms/sendcode.action";

    /**
     * 发送通知消息接口地址<含运营营销接口地址></>
     */
    private static final String NOTICE_MSG_URL = "https://api.netease.im/sms/sendtemplate.action";

    /**
     * 用户下单通知模板ID
     */
    public static final String USER_PAY_ORDER_NOTICE_TEMP_ID = "3912061";

    /**
     * 同意用户的退款申请 模板ID
     */
    public static final String AGREE_USER_RETURN_MONEY = "3912079";

    /**
     * 同意用户的退款退货申请 模板ID
     */
    public static final String AGREE_USER_RETURN_MONEY_AND_GOODS = "3912080";

    /**
     * 拒绝用户退货退款/退款 的申请 模板ID
     */
    public static final String REFUND_USER_APPLY = "3922101";

    /**
     * 卖家发货 模板ID
     */
    public static final String SELLER_SEND_GOODS = "3912072";

    /**
     * 卖家同意用户提现申请 模板ID
     */
    public static final String AGREE_USER_WITHDRAWALS = "3932066";

    /**
     * 卖家拒绝用户提现申请 模板ID
     */
    public static final String REFUND_USER_WITHDRAWALS = "4092082";

    /**
     * 获取成为VIP的资格
     */
    public static final String GEI_SHOPER_VIP = "4102133";

    /**
     * 网易云信分配的账号
     */
    private static final String appKey = "41437dd6014e4bc62577dc9a07b97dab";

    /**
     * 网易云信分配的密钥
     */
    private static final String appSecret = "cb59dc5d86e3";

    /**
     * 发送验证码短信
     * @param code
     * @param mobile
     */
    public static void sendVerificationCodeMsg(String code,String mobile){
        //静态方法加载  应该是工厂模式
        AppConfig config = ConfigLoader.createMessageConfig();
        MESSAGEXsend submail = new MESSAGEXsend(config);
        submail.addTo(mobile);
        submail.setProject("4ccxO1");

        submail.addVar("code", code);

        String response=submail.xsend();
        System.out.println("接口返回数据："+response);

    }
    /**
     * 发送通知类型短信<含运营营销类短信></>
     * @param mobile 接收者手机号码 必填<此方法暂时只支持单个手机号码发送,如需多个,另写方法></>
     * @param tempId 模板ID 必填
     * @param params JSONArray格式 必填 <[{"a":"s","b":"d"}]></>
     */
    public static void sendNoticeMsg(String mobile,String tempId,String params){

        if(StringUtils.isBlank(mobile)){
            throw new RuntimeException("mobile error,may be is null");
        }
        if(StringUtils.isBlank(tempId)){
            throw new RuntimeException("tempId can not null，please check it");
        }
        try {
            // 设置请求的的参数，requestBody参数
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("mobiles", mobile));
            if(!StringUtils.equals("[]",params)&& StringUtils.isNotBlank(params)){
                nameValuePairs.add(new BasicNameValuePair("params", params));
            }
            nameValuePairs.add(new BasicNameValuePair("templateid", tempId));
            sendMsg(nameValuePairs,NOTICE_MSG_URL);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 向网关发起请求
     * @param nameValuePairs 请求参数
     * @param post_url //接口地址
     * @throws Exception
     */
    private static void sendMsg(List<NameValuePair> nameValuePairs,String post_url) throws Exception{

        //随机数
        String nonce = UUID.randomUUID().toString();
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(post_url);
        //时间参数
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        //核验码
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce , curTime);
        //设置请求的header
        httpPost.addHeader("AppKey", appKey);
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
        HttpResponse response = httpClient.execute(httpPost);
        Map resultMap = JSON.parseObject(EntityUtils.toString(response.getEntity(), "utf-8"));
        Integer codeValue = (Integer)resultMap.get("code");
        if(codeValue.intValue()!=200){//如果返回结果不是200 ,则抛出错误信息
            throw new RuntimeException((String)resultMap.get("msg"));
        }
    }

}
