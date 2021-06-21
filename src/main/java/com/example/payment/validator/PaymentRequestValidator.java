package com.example.payment.validator;

import com.example.payment.exception.PaymentExceptionResult;
import com.example.payment.model.PaymentDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.payment.exception.PaymentExceptionResult.CARD_EXPIRED;
import static com.example.payment.exception.PaymentExceptionResult.INVALID_AMOUNT;
import static com.example.payment.exception.PaymentExceptionResult.INVALID_CARD_DATE_FORMAT;
import static com.example.payment.exception.PaymentExceptionResult.INVALID_EMAIL;
import static com.example.payment.exception.PaymentExceptionResult.INVALID_PAN;
import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentRequestValidator {

    private static final Pattern PAN_REGEX = Pattern.compile("^[0-9]{16}$");
    private static final DateTimeFormatter EXPIRE_FORMATTER = DateTimeFormatter.ofPattern("MMyy");

    public static Map<String, String> validatePaymentRequest(final PaymentDto paymentDto) {
        final Map<String, String> errorMap = validateRequestForNullValues(paymentDto);
        validateAmount(paymentDto.getAmount(), errorMap);
        validateEmail(Optional.ofNullable(paymentDto.getCardholder()), errorMap);
        validatePan(Optional.ofNullable(paymentDto.getCard()), errorMap);
        validateExpireDate(Optional.ofNullable(paymentDto.getCard()), errorMap);
        return errorMap;
    }

    static Map<String, String> validateRequestForNullValues(final PaymentDto paymentDto) {
        return Stream.of(FieldValidator.checkNull(paymentDto, PaymentDto.class),
                FieldValidator.checkNull(paymentDto.getCard(), PaymentDto.Card.class),
                FieldValidator.checkNull(paymentDto.getCardholder(), PaymentDto.CardHolder.class))
                .flatMap(Collection::stream)
                .map(fieldName -> {
                    var errorMessage = String.format(PaymentExceptionResult.NULLABLE_VALUE.getMessage(), fieldName);
                    return new ImmutablePair<>(fieldName, errorMessage);
                })
                .collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }


    static void validateAmount(final String amount, final Map<String, String> errorMap) {
        var validatedFieldName = "amount";
        if (nonNull(errorMap.get(validatedFieldName))) {
            return;
        }

        if (!StringUtils.isNumeric(amount) || Integer.parseInt(amount) <= 0) {
            enrichErrorMapAndTheError(validatedFieldName, INVALID_AMOUNT, errorMap);
        }
    }

    static void validateEmail(final Optional<PaymentDto.CardHolder> cardHolder, final Map<String, String> errorMap) {
        var validatedFieldName = "email";
        if (nonNull(errorMap.get(validatedFieldName))) {
            return;
        }
        cardHolder.ifPresent(ch -> {
            boolean valid = EmailValidator.getInstance().isValid(ch.getEmail());
            if (valid) {
                return;
            }
            enrichErrorMapAndTheError(validatedFieldName, INVALID_EMAIL, errorMap);
        });
    }

    static void validateExpireDate(final Optional<PaymentDto.Card> card, final Map<String, String> errorMap) {
        var validatedFieldName = "expiry";
        if (nonNull(errorMap.get(validatedFieldName))) {
            return;
        }
        card.ifPresent(c -> {
            try {
                TemporalAccessor cardDate = EXPIRE_FORMATTER.withResolverStyle(ResolverStyle.LENIENT).parse(c.getExpiry());
                var cardMonthAndYear = YearMonth.from(cardDate);
                validateExpireDateBygone(errorMap, cardMonthAndYear, validatedFieldName);
            } catch (DateTimeParseException e) {
                // could not parse
                enrichErrorMapAndTheError(validatedFieldName, INVALID_CARD_DATE_FORMAT, errorMap);
            }
        });
    }

    // I'm not sure if this should be inclusive or exclusive. 
    // As most of my cards work even when expired I made in inclusive.
    private static void validateExpireDateBygone(final Map<String, String> errorMap, final YearMonth cardDate, final String fieldName) {
        if (cardDate.compareTo(YearMonth.now()) <= 0) {
            enrichErrorMapAndTheError(fieldName, CARD_EXPIRED, errorMap);
        }
    }

    static void validatePan(final Optional<PaymentDto.Card> card, final Map<String, String> errorMap) {
        var validatedFieldName = "pan";
        if (nonNull(errorMap.get(validatedFieldName))) {
            return;
        }
        card.ifPresent(c -> {
            if (PAN_REGEX.matcher(c.getPan()).matches() && LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(c.getPan())) {
                return;
            }
            enrichErrorMapAndTheError(validatedFieldName, INVALID_PAN, errorMap);
        });
    }

    private static void enrichErrorMapAndTheError(String validatedFieldName, PaymentExceptionResult exceptionResult, Map<String, String> errorMap) {
        var errorMessage = String.format(exceptionResult.getMessage(), validatedFieldName);
        errorMap.put(validatedFieldName, errorMessage);
    }
}
