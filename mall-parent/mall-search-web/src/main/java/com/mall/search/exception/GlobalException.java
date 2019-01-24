package com.mall.search.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

/**
 * 全局的异常处理类
 */
@Component
public class GlobalException implements HandlerExceptionResolver {

    private static Logger logger = LoggerFactory.getLogger(GlobalException.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse,
                                         Object o, Exception e) {

        logger.error("系统出现错误");

        // 展示错误页面
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message","您的网络不太好，请重试!");
        modelAndView.setViewName("/error/exception");
        return modelAndView;
    }
}
