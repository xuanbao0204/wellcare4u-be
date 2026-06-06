package vn.wellcare4u.models.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageDTO<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
    
    public static <T> PageDTO<T> from(Page<T> page) {
        return PageDTO.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}