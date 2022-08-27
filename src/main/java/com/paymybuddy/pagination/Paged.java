package com.paymybuddy.pagination;

import org.springframework.data.domain.Page;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class to represents a Custom Page to user with thymeleaf.
 * It contains a generic {@link Page} and a {@link Paging} 
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Paged<T> {

    private Page<T> page;
    private Paging paging;
}
