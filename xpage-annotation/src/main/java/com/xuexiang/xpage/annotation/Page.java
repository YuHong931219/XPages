package com.xuexiang.xpage.annotation;


import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.enums.PageCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Page {

    String name() default "";

    String[] params() default {""};

    CoreAnim anim() default CoreAnim.slide;

    int extra() default -1;

    PageCategory category() default PageCategory.empty;

    int sort() default 0;
}
