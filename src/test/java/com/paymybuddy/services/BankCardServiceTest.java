package com.paymybuddy.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.model.BankCard;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankCardRepository;
import com.paymybuddy.service.BankCardService;

@ExtendWith(MockitoExtension.class)
public class BankCardServiceTest {

    @Mock
    private BankCardRepository bankCardRepository;

    @InjectMocks
    private BankCardService cut;

    private static BankCard bankCard;
    private static User existedUser;

    @BeforeAll
    public static void setUp() {

        existedUser = new User();
        bankCard = new BankCard();
        existedUser.setLastName("lastname");
        existedUser.setFirstName("firstName");
        existedUser.setEmail("test@gmail.com");
        bankCard.setCardCode(1234);
        bankCard.setCardNumber("1234567890");
        bankCard.setExpirationDate(new Date());
    }

    @Test
    void findByUser_whenUserExisted_thenReturnBankCard() {
        when(bankCardRepository.findByUser(Mockito.any(User.class))).thenReturn(Optional.of(bankCard));
        Optional<BankCard> userBankCard = cut.findByUser(existedUser);

        assertEquals(userBankCard.get(),bankCard);
    }

    @Test
    void findByUser_whenUserNotExisted_thenEmptyOptional() {
        when(bankCardRepository.findByUser(Mockito.any(User.class))).thenReturn(Optional.empty());
        Optional<BankCard> userBankCard = cut.findByUser(existedUser);

        assertTrue(userBankCard.isEmpty());
    }
    
    @Test
    void findById_whenbankCardExisted_thenReturnBankCard(){
        when(bankCardRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bankCard));
        Optional<BankCard> userBankCard = cut.findById(1L);

        assertEquals(userBankCard.get(),bankCard);
    }

    @Test
    void findById_whenbankCardNotExisted_thenReturnEmptyOptional() {
        when(bankCardRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Optional<BankCard> userBankCard = cut.findById(1L);

        assertTrue(userBankCard.isEmpty());
    }

    @Test
    void save_whenBankCardNotNull_thenReturnBankcard() {
        when(bankCardRepository.save(Mockito.any(BankCard.class))).thenReturn(bankCard);

        BankCard userBankCard = cut.save(bankCard);
        assertEquals(userBankCard, bankCard);
    }
    
    @Test
    void save_whenBankCardNull_thenThrowIlleagalArgumentException() {
        when(bankCardRepository.save(null)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            cut.save(null);
        });
    }

  
}
