package com.example.payment.services.impl;

import com.example.payment.dao.entity.CardHolderEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CardHolderSanitizerServiceImplTest {

    private final CardHolderSanitizerServiceImpl sanitizerService = new CardHolderSanitizerServiceImpl();

    @Test
    void givenCardHolderWithWithClientData_whenSanitizeIsCalled_thenDataIsEncrypted() {
        final var cardHolder = getCardHolder();
        final var sanitizedCardHolder = sanitizerService.sanitizeEntity(cardHolder);

        Assertions.assertNotEquals("test-test", sanitizedCardHolder.getName());
        Assertions.assertEquals("test@test.com", sanitizedCardHolder.getEmail());
    }

    @Test
    void givenEncryptedCardHolderWithWithClientData_whenDesanitizedIsCalled_thenDataIsDecrypted() {
        final var cardHolder = getCardHolder();
        final var sanitizedCardHolder = sanitizerService.sanitizeEntity(cardHolder);
        final var deSanitizeEntity = sanitizerService.deSanitizeEntity(sanitizedCardHolder);

        Assertions.assertEquals("test-test", deSanitizeEntity.getName());
        Assertions.assertEquals("test@test.com", deSanitizeEntity.getEmail());
    }

    private static CardHolderEntity getCardHolder() {
        var cardHolderEntity = new CardHolderEntity();
        cardHolderEntity.setName("test-test");
        cardHolderEntity.setEmail("test@test.com");
        return cardHolderEntity;
    }
}