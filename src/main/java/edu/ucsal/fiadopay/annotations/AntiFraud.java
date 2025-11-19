package edu.ucsal.fiadopay.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AntiFraud {
    String name();
    double threshold() default 1000.0; // valor limiar como exemplo
}
