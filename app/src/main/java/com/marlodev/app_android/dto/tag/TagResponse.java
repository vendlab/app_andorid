package com.marlodev.app_android.dto.tag;
import java.time.LocalDateTime;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponse {
    private Integer id;
    private String name;
    private LocalDateTime createdAt;
}