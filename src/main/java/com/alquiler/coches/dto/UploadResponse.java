package com.alquiler.coches.dto;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record UploadResponse(
        String publicId,
        String url,
        String secureUrl,
        String format,
        long bytes,
        @Nullable String resourceType
) {


}
