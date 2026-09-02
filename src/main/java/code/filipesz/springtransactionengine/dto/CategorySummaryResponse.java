package code.filipesz.springtransactionengine.dto;

public record CategorySummaryResponse(
        Long id,
        String name,
        Long productCount
) {
}