package com.example.payment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    private String invoice;
    private String amount;
    private String currency;
    private CardHolder cardholder;
    private Card card;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Card {
        private String pan;
        private String expiry;
        private String cvv;
    }

    @Data
    public static class CardHolder {
        private String name;
        private String email;
    }
}
