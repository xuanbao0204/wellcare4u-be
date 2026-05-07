package vn.wellcare4u.models.dto;

import java.util.List;

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
}