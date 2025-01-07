package kr.hhplus.be.server.global.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private PageInfo pageInfo;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class PageInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }

    public <R> PageResponse<R> map(Function<T, R> convertFunction) {
        List<R> convertedContent = content.stream()
                .map(convertFunction)
                .toList();
        return new PageResponse<>(convertedContent, pageInfo);
    }
}
