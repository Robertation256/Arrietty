package com.arrietty.annotations;

import com.arrietty.consts.RateLimitPolicy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    int rateLimit() default RateLimitPolicy.STANDARD;
}
