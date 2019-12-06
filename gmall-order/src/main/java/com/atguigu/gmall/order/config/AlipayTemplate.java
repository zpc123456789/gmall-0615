package com.atguigu.gmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.stereotype.Component;

//@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "\t\n" +
            "2016101500694721";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCJGpdTyznnRZhfkXWKXQ2J+mRvrVL5LEofvZeMmjWMUFvcRcGkNprcchaELDYGvaT5WElJlq/dmPI6FETIgDfeH6Mw/YHjZpaXl0GZIPnAWgcnWCnwpQITJk6RydEjhojrogeN9fdL5RjRN0Ya49GytefgPg8zx+AkL0nF6Xq2zk8P8X/MzAQxsSafyLQSk1RbX7R+IAmeS23xtm8tD2RtEgAa18tRVeJc018Awt8sVprZJSaAO87+cR4wczEcNdweJCy7pn9RvslY4kv54hSZwMKMtsWe/kPhJTJdUvAoggBFA+whUXumF/LkpO6swwQEgZydgro0igHV1JeFg2gzAgMBAAECggEAGK3tsYqyiJvxerp/UwG/DyhAbg3gErRTP3VsU0Xq/6/zaSoxWjLuG2J6BR7ENuBqUuv+fT2fuJ7wc130p7bU16oQQSBRAjMURoNWro7OBj7ubLmlCwlY30OU/w86E1ADU48eFwLAmivKFNcDSpHZzHaMH8RsDiEbisfsWrDVgxRKW3JQ5JA6eK27CdguhmuVvMN5oI3ug282aEg5u3lxAOmP3jJSMk+jBU5w/LwaakkT50wXphg4N8cS09no3GWusjBJv23jZvwvkJBy38nbyLYTuSetpCmtcuM3hYCVZLgb/i9/CMEmpz8BI+2H3gWcEzI7X2uTy4IyaGbluM7F8QKBgQDMDVyQs8aRE25/5Q/U+WJboTRj+PA0DmVWgNEAy8UhRlWZyejRRsUOF7gSgKC0yJSIOHok7ZKSERgsxjroAQVL71jqsvZq4qhs6V5ano2tC/BTs2TgXR1GdasPe6uj74234GAoWvlFKZWUK+ZE5v4ptRVjCknYij1X9SBdn2WaywKBgQCsAgZPte86SWsKu/RNCpsxGO4tChcB6Ot/l6OzQ4NpLzrwjZqTQRpANYmS9T+sx3X8qJxF0RMK4tD/lkF4mg9yQrytN/ADUKaMa7IiLXX2cF6l8Pb0FQnycufo7PqWFYzHKSBitAwOosC6iaPpNm1gUItlZVOtR01hqtvOGPizOQKBgCQrhin8RAl5WVAAaZMF9cJqS+Agw2tm1d3EjcfHVxUz436URNm74rhByhY1iMci+vk+kaA0IJQVxaRCKzTu4WJ5ddh8iqOds99CasBDdLek8x/TH+GJf+P6fa0L7fvsPcHHWyQsJB13ZpVSRsoV7B22hKj+rOJedss9n1QZKiiLAoGAZwlHcpRPW6EDKAjzDPtff4eplP1ITvKRr3rMquO0YlvSeKq6f58t9GydnpSesgdBnDn8cq4NNCYugAwn5/CRdCaaE0FA2xuCyPbyXBrTYO5rJAg2Rnwb20oNta/PY/xAukTei4HC/zlrLdKph1f9KQbWmcSa3V+q8OiChvDaebECgYEAgdx1wqQJUXw5pmllr2U3PuOvYI75wquMzO2ZHzIm+NITJJ2bvyR9pJDl06Snj9G2oHwviBlfsipuABpoFd+qpBX/kmNGYcl47MG9Na5C71lxRrm++sLJmtIfFR/dbdwPcEixEPKADOoRDaj9zckPR9MWmxBSXJ1kxVnZp6I4nXo=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkrBjMs40byuH8PeI1te5PEPjaiFkgQu99y1umriU9IrxzUdY+NYf6cKU9pMmBpZt3DOb8T2bPhTYjFvi1X+x83v5U8snmEhefV/7q9g+wnXelAm1bACgf8d+IBLni13gWufTeImSb4jABnRU930iagvtxIyiGp9s9iQYRuadIAudeNqYfwlPG8t250zLiHdRCZefuYp8QN9HNoVoEIFB+3/48DnOxRFLezFXZSUHDAEkB+ivc9CoihgMwtqydei593e5ZAIfvBrHztjAevHmTRWvJoSgDs213v5m6eYj5KxJ8tydL4cqCrjNKiDaDh6yA2QkZF3s5r+QMTqTDAhXdQIDAQAB";

    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url="http://vwas38o1wc.52http.net/api/order/pay/success";//哲西云配置的内网穿透地址;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = null;

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
