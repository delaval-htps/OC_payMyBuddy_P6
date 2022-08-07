package com.paymybuddy.pagination;

import org.springframework.data.domain.Page;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Paged<T> {
    
    private Page<T> page;
    private Paging paging;
    
}
