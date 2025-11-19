package edu.ucsal.fiadopay.payment;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import edu.ucsal.fiadopay.annotations.PaymentMethod;

@Component
public class PaymentStrategyRegistry {

    private final Map<String, PaymentStrategy> strategies;

    @Autowired
    public PaymentStrategyRegistry(List<PaymentStrategy> list) {
        // build map by reading bean class annotation
        this.strategies = list.stream().collect(Collectors.toMap(
            s -> {
                PaymentMethod ann = s.getClass().getAnnotation(PaymentMethod.class);
                if (ann != null) return ann.type().toUpperCase();
                // fallback to getType()
                return s.getType().toUpperCase();
            },
            s -> s
        ));
    }

    public PaymentStrategy get(String type) {
        if (type == null) return null;
        return strategies.get(type.toUpperCase());
    }
}
