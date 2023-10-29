package com.heima.app.gateway.filter;

import com.heima.app.gateway.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Description 网关授权过滤器，用户请求的地址如果是login，放行，其他的要验证是否有token，token是否有效，其余情况都返回401
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/10/22
 */
@Component
@Slf4j
public class AuthorizeFilter implements GlobalFilter, Ordered {

    public static final String LOGIN_PATH = "/login";
    public static final String TOKEN_STR = "token";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 1.先获取访问路径，判断是否包含Login
        if (request.getURI().getPath().contains(LOGIN_PATH)) {
            // 2.1 如果是login，放行
            return chain.filter(exchange);
        }
        // 2.2 不是login，判断是否有token
        String token = request.getHeaders().getFirst(TOKEN_STR);
        if (StringUtils.isEmpty(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 3.1 token是否有效
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            int result = AppJwtUtil.verifyToken(claimsBody);     //-1：有效，0：有效，1：过期，2：过期
            if (result == 1 || result == 2) {
                // 过期
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }catch (Exception e) {
            log.info("网关过滤器异常: {}",e);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    /**
    * @description 优先级，数字越小，优先级越高
    *
    * @return int
    * @author Lebr7Wcd
    * @date 2023/10/22 22:19
    */
    @Override
    public int getOrder() {
        return 0;
    }
}
