package com.example.payment.services.impl;

import com.example.payment.dao.entity.CardEntity;
import com.example.payment.dao.entity.CardHolderEntity;
import com.example.payment.services.impl.CardHolderSanitizerServiceImpl;
import com.example.payment.services.impl.CardSanitizerServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CardSanitizerServiceImplTest {

    private final CardSanitizerServiceImpl cardSanitizerServiceImpl;

    public CardSanitizerServiceImplTest() {
        final var mock = mock(CardHolderSanitizerServiceImpl.class);
        this.cardSanitizerServiceImpl = new CardSanitizerServiceImpl(mock);
    }

    final String testExpiry = "0625";
    final String testName = "test test";
    final String testPan = "5555555555554444";

    @Test
    void givenCardToSanitize_whenSanitizerIsCalled_thenCardAndCardholderAreEncrypted() {
        final var testCard = getTestCard();

        CardEntity sanitizeEntity = cardSanitizerServiceImpl.sanitizeEntity(testCard);

        Assertions.assertNotEquals(testExpiry, sanitizeEntity.getExpiry());
        Assertions.assertNotEquals(testPan, sanitizeEntity.getPan());
    }

    @Test
    void givenCardToSanitizeAndDeSanitize_whenDeSanitizerIsCalled_thenCardAndCardholderAreDecrypted() {

        final var testCard = getTestCard();
        CardEntity sanitizeEntity = cardSanitizerServiceImpl.sanitizeEntity(testCard);
        CardEntity deSanitizeEntity = cardSanitizerServiceImpl.deSanitizeEntity(sanitizeEntity);

        Assertions.assertEquals(testPan, deSanitizeEntity.getPan());
        Assertions.assertEquals(testExpiry, deSanitizeEntity.getExpiry());
        Assertions.assertEquals(testName, deSanitizeEntity.getCardHolderEntity().getName());
    }

    private CardEntity getTestCard() {
        CardHolderEntity cardHolderEntity = new CardHolderEntity();
        cardHolderEntity.setName(testName);

        CardEntity cardEntity = new CardEntity();
        cardEntity.setPan(testPan);
        cardEntity.setExpiry(testExpiry);
        cardEntity.setCardHolderEntity(cardHolderEntity);
        return cardEntity;
    }
}