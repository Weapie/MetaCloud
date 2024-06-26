package eu.metacloudservice.events.entrys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public  @interface  Subscribe  {
    int priority() default Priority.MEDIUM;
    boolean async() default false;
}
