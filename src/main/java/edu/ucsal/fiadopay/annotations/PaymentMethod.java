package edu.ucsal.fiadopay.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PaymentMethod {
    String type();
    double monthlyInterest() default 0.0; // percent, ex: 1.0 -> 1% ao mês
    boolean allowInstallments() default false;
}
