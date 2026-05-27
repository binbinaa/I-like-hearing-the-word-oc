package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        /*
        * 这段代码是 Spring MVC 拦截器中的一个判断逻辑，用于判断当前请求是否需要进行 JWT 校验。
        ✅ handler 是什么？
        在 Spring MVC 的拦截器中，handler 表示被请求的目标处理器（即控制器方法）。
        如果请求的是一个 Controller 中的方法（如 /login、/user/list），则 handler 是 HandlerMethod 类型。
        如果请求的是静态资源（如 .html, .css, .js 文件），则 handler 不是 HandlerMethod。
        ✅ instanceof HandlerMethod 判断的意义
        只有当请求的目标是一个 Controller 方法时，才需要执行后续的 JWT 校验逻辑。
        否则（例如访问静态资源或非 Controller 接口），直接放行，不进行任何校验。
        * */
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);// 解析令牌
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());//解析成功后获取当前员工id
            log.info("当前员工id：", empId);
            BaseContext.setCurrentId(empId);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }
}
