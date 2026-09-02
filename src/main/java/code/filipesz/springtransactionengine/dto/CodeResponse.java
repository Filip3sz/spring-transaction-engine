package code.filipesz.springtransactionengine.dto;

public record CodeResponse(
        String code,
        Integer discount,
        Integer usesCount
) {
}

