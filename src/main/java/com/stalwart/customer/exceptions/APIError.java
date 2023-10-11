package com.stalwart.customer.exceptions;

import java.time.LocalDateTime;

public record APIError(
        String path,
        String message,
        int statuscode,
        LocalDateTime dateTime

) {
}
