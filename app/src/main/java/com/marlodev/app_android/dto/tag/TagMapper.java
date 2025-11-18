package com.marlodev.app_android.dto.tag;

import com.marlodev.app_android.domain.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagMapper {

    // ───────────────────────────────
    // TagResponse -> Tag (Domain)
    // ───────────────────────────────
    public static Tag fromResponse(TagResponse dto) {
        if (dto == null) return null;
        return Tag.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt()) // incluir fecha
                .build();
    }

    public static List<Tag> fromResponseList(List<TagResponse> list) {
        List<Tag> result = new ArrayList<>();
        if (list != null) {
            for (TagResponse dto : list) {
                result.add(fromResponse(dto));
            }
        }
        return result;
    }

    // ───────────────────────────────
    // Tag -> TagRequest (para enviar al backend)
    // ───────────────────────────────
    public static TagRequest toRequest(Tag tag) {
        if (tag == null) return null;
        return TagRequest.builder()
                .name(tag.getName())
                .build();
    }

    public static List<TagRequest> toRequestList(List<Tag> list) {
        List<TagRequest> result = new ArrayList<>();
        if (list != null) {
            for (Tag t : list) {
                result.add(toRequest(t));
            }
        }
        return result;
    }
}
