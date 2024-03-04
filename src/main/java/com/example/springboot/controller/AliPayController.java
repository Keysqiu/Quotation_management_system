package com.example.springboot.controller;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.springboot.config.AliPayConfig;
import com.example.springboot.controller.dto.AliPayDTO;
import com.example.springboot.entity.Orders;
import com.example.springboot.service.IOrdersService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qiuqingsheng
 * @since 2023-11-20
 */


@RestController
@RequestMapping("/alipay")
public class AliPayController {
    // 支付宝沙箱网关地址
    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    // 签名方式
    private static final String SIGN_TYPE = "RSA2";

    @Resource
    private AliPayConfig aliPayConfig;

    @Resource
    private IOrdersService ordersService;

    /**
     * 支付接口
     * @param orderNo 订单ID
     * @param httpResponse
     * @throws IOException
     */
    @GetMapping("/pay")
    public void buy(@RequestParam String orderNo, HttpServletResponse httpResponse) throws IOException {
        //查询订单信息
        Orders orders = ordersService.selectByOrderNo(orderNo);
        if (orders == null) { //如果订单不存在
            return;
        }
        // 1.创建Client,通过SDK提供的Client，负责调用支付宝的API
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);
        // 2.创建 Request 并设置Request参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest(); //发送请求的 Request类
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", orders.getOrderNo()); //我们自己生成的订单编号
        bizContent.set("total_amount", orders.getTotal()); //订单的总金额
        bizContent.set("subject", orders.getGoodsName()); //支付的名称
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY"); //固定配置
        request.setBizContent(bizContent.toString());
        request.setReturnUrl("http://localhost:8080/alipay"); //支付成功之后跳转的界面
        //执行请求，拿到响应的结果，返回给浏览器
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form); //直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    /**
     * 订单支付成功后自动执行的异步回调函数
     * @param request 支付宝官方发来的响应体
     * @throws AlipayApiException
     */
    @PostMapping("/notify")
    public void payNotify(HttpServletRequest request) throws AlipayApiException {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("==========支付宝异步回调==========");
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }
            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, aliPayConfig.getAlipayPublicKey(), "UTF-8");
            // 支付宝验签
            if (checkSignature) {
                //验签通过
                System.out.println("交易名称：" + params.get("subject"));
                System.out.println("交易状态：" + params.get("trade_status"));
                System.out.println("支付宝交易凭证号：" + params.get("trade_no"));
                System.out.println("商户订单号：" + params.get("out_trade_no"));
                System.out.println("交易金额：" + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id：" + params.get("buyer_id"));
                System.out.println("买家付款时间：" + params.get("gmt_payment"));
                System.out.println("买家付款金额：" + params.get("buyer_pay_amount"));

                //订单编号
                String tradeNo = params.get("out_trade_no");
                //支付时间
                String gmtPayment = params.get("gmt_payment");
                //支付宝交易编号
                String alipayTradeNo = params.get("trade_no");
                //更新订单状态为已支付，设置支付信息
                AliPayDTO aliPayDTO=new AliPayDTO();
                aliPayDTO.setOrderNo(tradeNo);
                aliPayDTO.setPayNo(alipayTradeNo);
                aliPayDTO.setPayTime(gmtPayment);
                aliPayDTO.setStatus("已支付");
                ordersService.updateByOrderNo(aliPayDTO);
            }
        }
        System.out.println("request.getParameter(\"trade_status\")"+request.getParameter("trade_status"));
    }
}
