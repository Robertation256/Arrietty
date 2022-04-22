package com.arrietty.annotations;

import com.arrietty.consts.AuthModeEnum;
import com.arrietty.consts.RedirectPolicyEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedirectPolicy {
    RedirectPolicyEnum redirectPolicy() default RedirectPolicyEnum.NO_REDIRECT;
}
