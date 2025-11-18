package com.marlodev.app_android.domain;


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
public class BannerModel {
    private int id;
    private String url;      // URL remota de Cloudinary
    private int order;       // Orden de aparición
}