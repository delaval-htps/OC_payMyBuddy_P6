package com.paymybuddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paymybuddy.model.Invoice;
import com.paymybuddy.model.User;

/**
 * Repository extends {@link JpaRepository} to acces to {@link Invoice}
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    

    @Query(value= "select i from Invoice i left join fetch  i.transaction t where t.sender=?1")
    public List<Invoice> findAllBySender(User user);
}
