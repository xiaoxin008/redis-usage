package com.xiaoxin008.redisusage.web;

import com.xiaoxin008.redisusage.constant.SysConstant;
import com.xiaoxin008.redisusage.domain.RateLimiterBucket;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 限流过滤器
 *
 * @author xiaoxin008(313595055 @ qq.com)
 * @since 1.0.0
 */
@WebFilter(urlPatterns = "/index",filterName = "rateLimiterFilter")
public class RateLimiterFilter implements Filter {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    private RedisScript<Boolean> redisScript;

    private RateLimiterBucket limiterBucket;

    @Override
    public void init(FilterConfig config){
        //初始化令桶
        RateLimiterBucket bucket = new RateLimiterBucket();
        bucket.setCapacity(20L);
        bucket.setRate(100L);
        limiterBucket = bucket;
        //初始化脚本
        DefaultRedisScript script = new DefaultRedisScript<>();
        ClassPathResource resource=new ClassPathResource("rate_limiter.lua");
        script.setScriptSource(new ResourceScriptSource(resource));
        script.setResultType(Boolean.class);
        redisScript = script;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        String requestURI = req.getServletPath();
        List<String> keys = generateKeys(requestURI);
        Boolean isAllow = redisTemplate.execute(redisScript, keys, limiterBucket);
        if(isAllow){
            chain.doFilter(request,response);
        }else{
            res.getWriter().print("server is too busy!!!");
        }
    }

    @Override
    public void destroy() {
        //清除所有限流
        redisTemplate.delete(SysConstant.REDIS_GROUP.concat(":").concat(SysConstant.RATE_LIMITER_GROUP));
    }

    private List<String> generateKeys(String url){
        List<String> keys = Arrays.asList(SysConstant.REDIS_GROUP, SysConstant.RATE_LIMITER_GROUP, url);
        return Arrays.asList(StringUtils.join(keys, ":"));
    }
}
