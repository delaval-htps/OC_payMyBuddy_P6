package com.paymybuddy.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class represents a item of pagination under the tab of transaction.
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageItem {
    
    public enum PageItemType {
        DOTS, PAGE
    }
    
    private PageItemType pageItemType;
    private int index;
    private boolean active;
}
