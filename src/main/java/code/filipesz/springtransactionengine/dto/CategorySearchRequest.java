package code.filipesz.springtransactionengine.dto;

public record CategorySearchRequest(
        Long categoryId,
        String categoryName
) {
}
