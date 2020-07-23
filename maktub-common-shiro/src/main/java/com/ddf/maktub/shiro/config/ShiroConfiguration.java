package com.ddf.maktub.shiro.config;

import com.ddf.maktub.shiro.OAuth2Filter;
import com.ddf.maktub.shiro.OAuth2Realm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfiguration {

    @Bean
    public OAuth2Realm oAuth2Realm() {
        return new OAuth2Realm();
    }


    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdCookieEnabled(true);
        return sessionManager;
    }


    @Bean("securityManager")
    public SecurityManager securityManager(OAuth2Realm oAuth2Realm, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(oAuth2Realm);
        securityManager.setSessionManager(sessionManager);

        return securityManager;
    }


    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        //oauth过滤
        Map<String, Filter> filters = new HashMap<>();
        filters.put("oauth2", new OAuth2Filter());
        shiroFilter.setFilters(filters);

        Map<String, String> filterMap = new LinkedHashMap<>();

        filterMap.put("/**", "anon");
//        filterMap.put("/login", "anon");
//        filterMap.put("/register", "anon");
//        filterMap.put("/codex.html", "anon");
//        filterMap.put("/valid/*/*", "anon");
//        filterMap.put("/**", "oauth2");
//
//        filterMap.put("/webjars/**", "anon");
//        filterMap.put("/druid/**", "anon");
//        filterMap.put("/swagger/**", "anon");
//        filterMap.put("/v2/api-docs", "anon");
//        filterMap.put("/swagger-ui.html", "anon");
//        filterMap.put("/swagger-resources/**", "anon");
//        filterMap.put("/captcha.jpg", "anon");
//        filterMap.put("/css/**", "anon");

        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }


}
