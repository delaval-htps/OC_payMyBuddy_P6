package com.paymybuddy.pagination;

import java.util.ArrayList;
import java.util.List;
import com.paymybuddy.pagination.PageItem.PageItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Paging {

    private static final int PAGINATION_STEP = 3;

    private int pageNumber;
    private int pageSize;
    private boolean nextEnable;
    private boolean previousEnable;
    private List<PageItem> items = new ArrayList<>();

    public void addPageItems(int from, int to, int pageNumber) {
        for (int i = from; i < to; i++) {
            items.add(PageItem.builder().active(pageNumber != i).index(i).pageItemType(PageItemType.PAGE).build());
        }
    }

    public void first(int pageNumber) {
        items.add(PageItem.builder().active(pageNumber != 1).index(1).pageItemType(PageItemType.PAGE).build());
        items.add(PageItem.builder().active(false).pageItemType(PageItemType.DOTS).build());
    }

    public void last(int pageSize) {
        items.add(PageItem.builder().active(false).pageItemType(PageItemType.DOTS).build());
        items.add(PageItem.builder().active(true).pageItemType(PageItemType.PAGE).build());
    }

    public static Paging of(int totalPages, int pageNumber, int pageSize) {
        Paging paging = new Paging();
        paging.setPageNumber(pageNumber);
        paging.setPageSize(pageSize);
        paging.setNextEnable(pageNumber != totalPages);
        paging.setPreviousEnable(pageNumber != 1);

        if (totalPages < PAGINATION_STEP * 2 + 6) {
            paging.addPageItems(1, totalPages + 1, pageNumber);

        } else if (pageNumber < PAGINATION_STEP * 2 + 1) {
            paging.addPageItems(1, PAGINATION_STEP * 2 + 4, pageNumber);
            paging.last(totalPages);

        } else if (pageNumber > totalPages - PAGINATION_STEP * 2) {
            paging.first(pageNumber);
            paging.addPageItems(totalPages - PAGINATION_STEP * 2 - 2, totalPages + 1, pageNumber);

        } else {
            paging.first(pageNumber);
            paging.addPageItems(pageNumber - PAGINATION_STEP, pageNumber + PAGINATION_STEP + 1, pageNumber);
            paging.last(totalPages);
        }

        return paging;
    }

}
