package com.base.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.Date;

/**
 * JWT 工具类
 * @author zhangwei
 * @date 2019/3/27 17:11
 */
@ConfigurationProperties(prefix = "redis.token")
@Component
@Data
public class JwtUtils {

    private String secret;

    private long expire;

    /**
     * 生成 token
     * userId不能为空
     * @param userId 用户ID
     * @return 加密的token
     */
    public  String createToken(Long userId) {
        try {
            Date nowDate = new Date();
            //过期时间
            Date expireDate = new Date(nowDate.getTime() + expire * 1000);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带username信息
            String token = JWT.create()
                    .withClaim("userId", null == userId ? null : userId.toString())
                    //到期时间
                    .withExpiresAt(expireDate)
                    //创建一个新的JWT，并使用给定的算法进行标记
                    .sign(algorithm);
            byte[] encode = Base64.getEncoder().encode(token.getBytes());
            return new String(encode);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 校验 token 是否正确
     *
     * @param token    密钥
     * @return 是否正确
     */
    public String verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            //在token中附带了username信息
            JWTVerifier verifier = JWT.require(algorithm).build();
            //验证 token
            verifier.verify(decode(token));
            return null;
        }catch (TokenExpiredException e) {
            return "token已失效，请重新登录";
        } catch (Exception e) {
            return "token认证失败";
        }
    }

    /**
     * 获得token中的信息，无需secret解密也能获得
     * @return token中包含的用户名
     */
    public  Long getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(decode(token));
            return Long.valueOf(jwt.getClaim("userId").asString());
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 解密token
     * @return
     */
    public String decode(String token){
        byte[] decodeByte = Base64.getDecoder().decode(token.getBytes());
        return new String(decodeByte);
    }
}
