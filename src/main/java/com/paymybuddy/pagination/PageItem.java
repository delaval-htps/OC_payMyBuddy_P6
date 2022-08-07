package com.paymybuddy.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
