package cn.xianyum.framwork.security.filter;

import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.system.entity.po.LoginUserEntity;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * token过滤器 验证token有效性
 * @author zhangwei
 * @date 2021/7/15 20:41
 */
@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserTokenService userTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        UserEntity userEntity = userTokenService.getUser();
        if(null != userEntity){
            LoginUserEntity loginUserEntity = BeanUtils.copy(userEntity,LoginUserEntity.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUserEntity, null, loginUserEntity.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
