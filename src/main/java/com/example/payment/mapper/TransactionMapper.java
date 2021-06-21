package com.example.payment.mapper;

import com.example.payment.dao.entity.CardEntity;
import com.example.payment.dao.entity.CardHolderEntity;
import com.example.payment.dao.entity.TransactionEntity;
import com.example.payment.model.PaymentDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionMapper {

    private static final int CARD_DATE_LENGTH = 4;
    private static final int AMOUNT_NOT_MASKED_LAST_CARD_NUMBERS = 4;
    private static final int AMOUNT_MASKED_CARD_NUMBERS = 12;

    public static PaymentDto mapTransactionEntityToPaymentDto(final TransactionEntity transactionEntity) {
        if (isNull(transactionEntity)) {
            return null;
        }
        final var paymentDto = new PaymentDto();
        paymentDto.setAmount(transactionEntity.getAmount());
        paymentDto.setInvoice(transactionEntity.getInvoice());
        paymentDto.setCurrency(transactionEntity.getCurrency());
        final var cardHolder = cardHolderToDto(transactionEntity.getCardEntity().getCardHolderEntity());
        final var card = cardToDto(transactionEntity.getCardEntity());
        paymentDto.setCardholder(cardHolder);
        paymentDto.setCard(card);
        return paymentDto;
    }


    public static TransactionEntity mapTransactionToEntity(final PaymentDto paymentDto) {
        final var transactionEntity = new TransactionEntity();
        transactionEntity.setAmount(paymentDto.getAmount());
        transactionEntity.setInvoice(paymentDto.getInvoice());
        transactionEntity.setCurrency(paymentDto.getCurrency());
        final var cardHolderEntity = cardHolderToEntity(paymentDto.getCardholder());
        transactionEntity.setCardEntity(cardToEntity(cardHolderEntity, paymentDto.getCard()));
        return transactionEntity;
    }

    private static CardHolderEntity cardHolderToEntity(final PaymentDto.CardHolder cardHolder) {
        final var entity = new CardHolderEntity();
        entity.setEmail(cardHolder.getEmail());
        entity.setName(cardHolder.getName());
        return entity;
    }

    private static CardEntity cardToEntity(final CardHolderEntity cardHolderEntity, final PaymentDto.Card card) {
        final var cardEntity = new CardEntity();
        cardEntity.setExpiry(card.getExpiry());
        cardEntity.setPan(card.getPan());
        cardEntity.setLastFourDigits(card.getPan().substring(card.getPan().length() - AMOUNT_NOT_MASKED_LAST_CARD_NUMBERS));
        cardEntity.setCardHolderEntity(cardHolderEntity);
        cardEntity.setCvv(card.getCvv());
        return cardEntity;
    }

    private static PaymentDto.CardHolder cardHolderToDto(final CardHolderEntity cardHolderEntity) {
        var cardHolder = new PaymentDto.CardHolder();
        cardHolder.setName(getMaskedString(cardHolderEntity.getName().length()));
        cardHolder.setEmail(cardHolderEntity.getEmail());
        return cardHolder;
    }

    private static PaymentDto.Card cardToDto(final CardEntity cardEntity) {
        var card = new PaymentDto.Card();
        card.setPan(getMaskedString(AMOUNT_MASKED_CARD_NUMBERS) + cardEntity.getLastFourDigits());
        card.setExpiry(getMaskedString(CARD_DATE_LENGTH));
        return card;
    }

    private static String getMaskedString(int length) {
        return IntStream.range(0, length).mapToObj(i -> "*").collect(Collectors.joining());
    }
}
