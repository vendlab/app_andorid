package com.marlodev.app_android.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
/**
 * Modelo de dominio para Tag.
 * Se usa en la app Android, independiente del DTO.
 * Puede incluir referencias a productos o relaciones si es necesario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    private Integer id;
    private String name;
    private LocalDateTime createdAt;
}
