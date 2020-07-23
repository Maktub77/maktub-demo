package com.ddf.maktub.core.controller;

import com.ddf.maktub.shiro.token.UserContextInfo;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller公共组件
 */
public abstract class AbstractController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected UserContextInfo getUser() {
        return (UserContextInfo) SecurityUtils.getSubject().getPrincipal();
    }

}
