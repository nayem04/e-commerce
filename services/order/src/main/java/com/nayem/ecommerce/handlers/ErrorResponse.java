package com.nayem.ecommerce.handlers;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) {
}