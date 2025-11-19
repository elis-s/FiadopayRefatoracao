package edu.ucsal.fiadopay.payment;

import org.springframework.stereotype.Component;
import edu.ucsal.fiadopay.annotations.PaymentMethod;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@PaymentMethod(type = "CARD", monthlyInterest = 1.0, allowInstallments = true)
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public String getType() {
        return "CARD";
    }

    @Override
    public BigDecimal calculateTotal(BigDecimal amount, Integer installments) {
        if (installments == null || installments <= 1) return amount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal base = new BigDecimal("1.01"); // 1% per month
        BigDecimal factor = base.pow(installments);
        return amount.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Double monthlyInterest() { return 1.0; }

    @Override
    public boolean allowInstallments() { return true; }
}
