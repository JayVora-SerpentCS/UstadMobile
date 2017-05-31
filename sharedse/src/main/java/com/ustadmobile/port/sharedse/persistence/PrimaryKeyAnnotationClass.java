package com.ustadmobile.port.sharedse.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by varuna on 5/14/2017.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //can use in method only.
public @interface PrimaryKeyAnnotationClass {
    String str();
}
