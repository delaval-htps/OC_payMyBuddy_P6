package com.paymybuddy.pagination;

import com.paymybuddy.pagination.PageItem.PageItemType;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class that represents all pageItems for table of transactions to display
 * under table in transfert page.
 */
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

    /**
     * Add a pageitem to the list of items of paging.
     * 
     * @param from       number to start to insert item
     * @param to         number of the last to insert item
     * @param pageNumber the page number of pageItem to insert
     */
    public void addPageItems(int from, int to, int pageNumber) {
        for (int i = from; i < to; i++) {
            items.add(
                    PageItem
                            .builder()
                            .active(pageNumber != i)
                            .index(i)
                            .pageItemType(PageItemType.PAGE)
                            .build());
        }
    }

    /**
     * Metohd to add the first page of list items.
     * we have the first pageItem like page and the second like dots.
     * 
     * @param pageNumber the number of the pageItem to insert first.
     */
    public void first(int pageNumber) {
        items.add(PageItem
                .builder()
                .active(pageNumber != 1)
                .index(1)
                .pageItemType(PageItemType.PAGE)
                .build());
        items.add(PageItem
                .builder()
                .active(false)
                .pageItemType(PageItemType.DOTS)
                .build());
    }

    /**
     * Metohd to add the last page of list items to display.
     * we have the end-1 pageItem like DOTS and the second like PAGE.
     * 
     * @param pageSize the number of the last pageItem.
     */
    public void last(int pageSize) {
        items.add(PageItem
                .builder()
                .active(false)
                .pageItemType(PageItemType.DOTS)
                .build());
        items.add(PageItem
                .builder()
                .index(pageSize)
                .active(true)
                .pageItemType(PageItemType.PAGE)
                .build());
    }

/**
 * Method  static to build a paging
 * @param totalPages number of all pages to display
 * @param pageNumber the number of page that we want to be display (focus on)
 * @param pageSize the number of transaction to have in a page of table.
 * @return void
 */
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
            paging.addPageItems(pageNumber - PAGINATION_STEP, pageNumber + PAGINATION_STEP + 1,
                    pageNumber);
            paging.last(totalPages);
        }

        return paging;
    }
}
