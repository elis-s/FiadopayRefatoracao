package edu.ucsal.fiadopay.payment;
import org.springframework.stereotype.Component;
import edu.ucsal.fiadopay.annotations.PaymentMethod;
import java.math.BigDecimal;

@Component
@PaymentMethod(type = "BOLETO", monthlyInterest = 0.0, allowInstallments = false)
public class BoletoPaymentStrategy implements PaymentStrategy {
    @Override public String getType(){ return "BOLETO"; }
    @Override public java.math.BigDecimal calculateTotal(java.math.BigDecimal amount, Integer installments){ return amount.setScale(2); }
    @Override public Double monthlyInterest(){ return 0.0; }
    @Override public boolean allowInstallments(){ return false; }
}
