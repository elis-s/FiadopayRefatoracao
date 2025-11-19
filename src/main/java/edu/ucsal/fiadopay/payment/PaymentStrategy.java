package edu.ucsal.fiadopay.payment;

import edu.ucsal.fiadopay.controller.PaymentRequest;
import java.math.BigDecimal;

public interface PaymentStrategy {
    /**
     * @return payment type e.g. "CARD", "PIX"
     */
    String getType();

    /**
     * Calculate total with interest (if applicable)
     */
    BigDecimal calculateTotal(BigDecimal amount, Integer installments);

    /**
     * Monthly interest rate (percent) e.g. 1.0 -> 1%/mês
     */
    Double monthlyInterest();
    
    /**
     * Whether installments are allowed
     */
    boolean allowInstallments();
}
