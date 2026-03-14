package com.ailearningassistant.common.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private Long total;
    private Long pages;
    private Long pageNum;
    private Long pageSize;
    private List<T> records;

    public static <S, T> PageResult<T> from(IPage<S> page, Function<S, T> converter) {
        return PageResult.<T>builder()
                .total(page.getTotal())
                .pages(page.getPages())
                .pageNum(page.getCurrent())
                .pageSize(page.getSize())
                .records(page.getRecords().stream().map(converter).toList())
                .build();
    }
}
