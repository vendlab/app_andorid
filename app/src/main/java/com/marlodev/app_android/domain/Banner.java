package com.marlodev.app_android.domain;


import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Modelo de banner profesional para ViewPager2.
 * - url: URL remota de la imagen en Cloudinary.
 * - id: identificador único del banner.
 * - order: orden de aparición.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {
    private Long id;
    private String title;
    private Integer order;
    private String url;
    private String publicId;


    // --------------------------
    // 🔹 Utilidades profesionales
    // --------------------------

    public boolean hasImage() {
        return url != null && !url.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Banner banner)) return false;
        return Objects.equals(id, banner.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}