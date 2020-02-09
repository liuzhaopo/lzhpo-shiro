package com.lzhpo.common.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.lzhpo.common.realm.AuthRealm;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p> Author：lzhpo </p>
 * <p> Title：</p>
 * <p> Description：</p>
 */
@SpringBootConfiguration
public class ShiroConfig {

    private Logger logger = LoggerFactory.getLogger(ShiroConfig.class);

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("authRealm") AuthRealm authRealm){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager(authRealm));
        bean.setSuccessUrl("/index");
        bean.setLoginUrl("/admin/login");
        Map<String,Filter> map = new HashMap();
        map.put("authc",new FormAuthenticationFilter());
        bean.setFilters(map);
        //配置访问权限
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap();
        filterChainDefinitionMap.put("/","anon");
        filterChainDefinitionMap.put("/static/**","anon");
        filterChainDefinitionMap.put("/admin","anon");
        filterChainDefinitionMap.put("/admin/index","anon");
        filterChainDefinitionMap.put("/admin/login","anon");
        filterChainDefinitionMap.put("/toLogin","anon");
        filterChainDefinitionMap.put("/getCaptcha","anon");
        filterChainDefinitionMap.put("/anonCtrl/","anon");
        filterChainDefinitionMap.put("/sysRole/test","anon");
        filterChainDefinitionMap.put("/systemLogout","authc");
        filterChainDefinitionMap.put("/**","authc");
        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return bean;
    }

    @Bean
    public SecurityManager securityManager(@Qualifier("authRealm") AuthRealm authRealm){
        logger.info("- - - - - - -shiro开始加载- - - - - - ");
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(authRealm);
        defaultWebSecurityManager.setRememberMeManager(rememberMeManager());
        defaultWebSecurityManager.setSessionManager(webSessionManager());
        defaultWebSecurityManager.setCacheManager(cacheManager());
        return defaultWebSecurityManager;
    }


    /**
     * shiro缓存管理器;
     * 需要注入安全管理器：securityManager
     */
    @Bean("shiroCacheManager")
    public EhCacheManager cacheManager(){
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
        return cacheManager;
    }


    @Bean
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        rememberMeManager.setCookie(rememberMeCookie());
        rememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));
        return rememberMeManager;
    }

    @Bean
    public SimpleCookie rememberMeCookie(){
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setHttpOnly(true);
        //记住我有效期长达一周
        cookie.setMaxAge(604800);
        return cookie;
    }

    @Bean
    public SessionManager webSessionManager(){
        DefaultWebSessionManager manager = new DefaultWebSessionManager();
        //设置session过期时间为1小时(单位：毫秒)，默认为30分钟
        manager.setGlobalSessionTimeout(60 * 60 * 1000);
        manager.setSessionValidationSchedulerEnabled(true);
        //manager.setSessionDAO(redisSessionDAO());
        return manager;
    }

    /**
     * AOP式方法级权限检查
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    /**
     *  保证实现了Shiro内部lifecycle函数的bean执行
     * @return
     */
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("authRealm") AuthRealm authRealm) {
        SecurityManager manager = securityManager(authRealm);
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(manager);
        return advisor;
    }

    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }


}
