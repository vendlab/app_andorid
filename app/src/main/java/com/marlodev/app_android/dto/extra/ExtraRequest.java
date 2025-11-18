package com.marlodev.app_android.dto.extra;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtraRequest {
    private String name;
    private BigDecimal price;
}