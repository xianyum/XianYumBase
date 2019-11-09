package com.base.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.base.service.iservice.AliNetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author zhangwei
 * @date 2019/11/9 17:07
 * @desc
 */
@Service
@Slf4j
public class AliNetServiceImpl implements AliNetService {

    private static final String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCA+SCRa//VSObR7V+Nv5Qn69iGYGQ0s3rNfnDphBX6j7GxvSU1man04PgR6EBNu+C5uOmg+fYcSnsLolbldNbCzGMUr2l3MiaLxo/TL/OEdtehxPEvuq8y6LjYed8aP18n7WLU4LvyMSPEyq0yeTZFjHkbve3MnobBL64ChN471sD7SdpYaZ6FzB66der9wkdH1PLYhA6iKsFrGMAQ3yj5kOGWpnBHPPGQZctQE03+IxxXZeJbF+DR+2MO6bSlBYX1L6hgXRRkNN81Y1Zr/2TaeAMP/aMwZqYqA7iYuCv//VgVwGvKrXFkCR2dxX9X62TUWi9mIr7okUT0HGVfmVUJAgMBAAECggEAD7VIAC41NJbydfMBRlRlCQ+yiXtH+Rs7A7g4rFhIDQJfVBSDaanTh123GQc+fbk6P6SD3/VRwlRX9CzLhJrcZVFbZ/ONVtUm2LNQZRpcAHAxByTjczq7h+nMSaFeSOYbAtPoy6Crg230u00sFw+oSoQ3Y+eYBOnx46mY+fmQz5zDgeQlytKGbsx0TJodeIQRteWzRJqzUp4SV/EaaKB80A+uFhVlIaQf17C4cnQvtpaVnycZLDEBp+eHeLT/wCbkdA2N62bOJ9F76IXwkn8op8PIjuxHBOSfj1cJisM4jRVu7VQ6CczEeH2L55ePe0wZXkHRuq6OkD7lvuSiXEphYQKBgQD/KfkI9WzsnGm7aib+zElIaS7YCl0yi9bzyenHomN//qWfCvJcWO1XQGC4AQ2gteH+u0jCbCU+EoDT8AqTIY4i6sXLGpWfsQ0IJroLjzIBnm47KAXMgYv7kjyk3ppv6BJfeoNXH9IaHPx0M2reRyaG+HmX/2f8nOkhWKFYWiEwJQKBgQCBZU7Gg+II6b2hTrD/OVAg3ga7mvcz7DQ2oinT4MJt4riEXYlBLv4HG3eyacACwwRdP9nkHAkXVUgpYU9eaeqSF6CEJTwVnjEmVuSG0s3tkHAlFc30R7i4MVHr1e4pYrXsZP/oR1fWHVrnuiqtzX68TMwhOzsB9xFWCBCYYJg6FQKBgQDAir6VyQR5gwmx10c3q5V844nqkd+DncwEyA71io8EbXYyjgMWNjBaT4TsNDdBVx4CpvYfZ5Wuwi2eP5rrLT3hEUFRomzXRLWtufNXd6VnXVKWpvvBNtwXaGEEk8j7uqXqkDr9ilBzTf4d/RKeShhDbsnqLVsdXpX4iLWvQsuaVQKBgHszuo3JhK5339Z21pAsQd8Jh9+Y7UbC3/Qk8y6KEpYOCSH+7XDjDXw26wE1L11x0fX3lb9b8kwFJ3A+9xMuRepUFMG9sjfjJ2kZ0v2MOga4QA8Pc8njgD3gt9+VEe7VzeIHSKIhS8DSsntxBrH1t3YSqUn623RDin4fr61mS4sBAoGAbFn6PnNPzZPd/892iX51w/o3stENqgyJjjmNhJVALuonnOjKtILnyppoFgeXjQilXigaXfvKsFKyX8BGkgzZnEhIWQrCHE0lIGBj7v1LqH05pmODvU/6goXAy3REjxqiHnW/VyWPF367s1C0p5IcJ8Mvi5D29Ej+n6+BTiCBN9E=";
    private static final String ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApumTnqBpkQw+d/2ZpW8jfzKZwaKUTcnKBT11FCGXCJ9gFGusyix+fMJHUMgrl6UKO4UWJ8/DiBCV9/vb/Afb1/jGFMbHgnS31ioWYWlr7gnOu8z8M4+xFQm/2SKt/GaRnQVV2CihUzCY+DuAUO0yH7axOpvvnyYctIiUOf1EiwWXjnMCDZLmvG8dANK7vKsHduQFaDpLHVzZ9pcKuiFZuD8PMDg3IC+VPTuI7AaVhI+7sRvP0EIrJ0mnpgSF10JCEjD9eO3lBUnGmaQ/NQ8W0YF8nOtw1OuplzZgwinbswy/HAqkezaMFZiWecYacNNZDjJ1IiW77xtQKIcSRbeLaQIDAQAB";
    private static final String APP_ID="2019110868997443";
    private static final String SERVER_URL="https://openapi.alipay.com/gateway.do";
    private static final String FORMAT="json";
    private static final String CHAR_SET="utf-8";
    private static final String SIGN_TYPE="RSA2";


    @Override
    public String getAccessToken(String authCode) {
        AlipayClient alipayClient = new DefaultAlipayClient(SERVER_URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHAR_SET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(authCode);
        request.setGrantType("authorization_code");
        try {
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
            return oauthTokenResponse.getAccessToken();
        } catch (AlipayApiException e) {
            //处理异常
            log.error("获取支付宝用户accestoken失败，{}",e.getErrMsg());
        }
        return null;
    }

    @Override
    public AlipayUserInfoShareResponse getALiUserInfo(String accessToken) {
        AlipayClient alipayClient = new DefaultAlipayClient(SERVER_URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHAR_SET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
        AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
        try {
            AlipayUserInfoShareResponse response = alipayClient.execute(request, accessToken);
            return response;
        } catch (AlipayApiException e) {
            //处理异常
            log.error("获取支付宝用户详细信息失败，{}",e.getErrMsg());
        }
        return null;
    }
}
