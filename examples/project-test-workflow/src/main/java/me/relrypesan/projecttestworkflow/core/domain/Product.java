package me.relrypesan.projecttestworkflow.core.domain;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Product(
        String id,
        String name,
        BigDecimal price,
        BigDecimal weight,
        Integer quantity
) {
}
