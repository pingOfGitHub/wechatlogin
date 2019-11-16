package com.chat.controller;

import cn.bdqn.common.UrlUtils;
import com.alibaba.fastjson.JSONObject;
import com.chat.config.WeChatConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Controller
@RequestMapping("/wechat")
public class WeChatController {
    @Autowired
    WeChatConfig weChatConfig;
    @RequestMapping("/login")
    public String getCode(){

        //https://open.weixin.qq.com/connect/qrconnect?
        // appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
        StringBuilder getCodeUrl=new StringBuilder("https://open.weixin.qq.com/connect/qrconnect?");
        try {
            getCodeUrl.append("appid=").append(weChatConfig.getAppid())
                    .append("&redirect_uri=").append(URLEncoder.encode(weChatConfig.getRedirect_uri(),"UTF-8"))
                    .append("&response_type=code").append("&scope=snsapi_login");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "redirect:" + getCodeUrl.toString();
    }
    @RequestMapping("/chatCallBack")
    @ResponseBody
    public Object getToken(String code) {
      //https://api.weixin.qq.com/sns/oauth2/access_token?
        // appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        StringBuilder getToken=new StringBuilder("https://api.weixin.qq.com/sns/oauth2/access_token?");
        getToken.append("appid=").append(weChatConfig.getAppid())
                .append("&secret=").append(weChatConfig.getAppSecret())
                .append("&code=").append(code)
                .append("&grant_type=authorization_code");
        String tokenData = UrlUtils.loadURL(getToken.toString());
        Map<String,String> map = JSONObject.parseObject(tokenData, Map.class);
        /**返回的正确数据
         * {
         * "access_token":"ACCESS_TOKEN",
         * "expires_in":7200,
         * "refresh_token":"REFRESH_TOKEN",
         * "openid":"OPENID",
         * "scope":"SCOPE",
         * "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
         * }
         */
        //发送获取用户数据请求
        /**
         * http请求方式: GET
         * https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid=OPENID
         */
        StringBuilder userDataUrl=new StringBuilder("https://api.weixin.qq.com/sns/auth?");
        userDataUrl.append("access_token=").append(map.get("access_token"))
                .append("&openid=").append(map.get("openid"));
        String userData = UrlUtils.loadURL(userDataUrl.toString());


        Map userInfo = JSONObject.parseObject(userData, Map.class);
        return userInfo;
    }
}
