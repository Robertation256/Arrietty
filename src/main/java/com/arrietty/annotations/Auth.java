package com.arrietty.annotations;

import com.arrietty.consts.AuthModeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/5 16:13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auth {

    AuthModeEnum authMode() default AuthModeEnum.REGULAR;

}
