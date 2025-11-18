package com.marlodev.app_android.dto.banner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BannerRequest {
    private String title;
    private Integer order;
}