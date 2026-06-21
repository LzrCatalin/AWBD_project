package com.awbd.airport_manager.service;

import com.awbd.airport_manager.dto.AccountDto;
import com.awbd.airport_manager.exception.ResourceNotFoundException;
import com.awbd.airport_manager.mapper.AccountMapper;
import com.awbd.airport_manager.model.Account;
import com.awbd.airport_manager.repository.AccountRepository;
import com.awbd.airport_manager.service.impl.AccountServiceImpl;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountServiceImpl accountService;

    private UUID id;
    private Account account;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        account = new Account();
        account.setId(id);
        account.setEmail("test@test.com");
        account.setFirstName("John");
        account.setLastName("Doe");
        account.setPassportNo("AB1234567");

        accountDto = new AccountDto();
        accountDto.setId(id);
        accountDto.setEmail("test@test.com");
        accountDto.setFirstName("John");
        accountDto.setLastName("Doe");
        accountDto.setPassportNo("AB1234567");
    }

    @Test
    void getAll_returnsListOfDtos() {
        when(accountRepository.findAll()).thenReturn(List.of(account));
        when(accountMapper.toList(List.of(account))).thenReturn(List.of(accountDto));

        List<AccountDto> result = accountService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @SuppressWarnings("unchecked")
    void search_returnsPagedResponse() {
        SearchDTO searchDTO = new SearchDTO();
        Page<Account> page = new PageImpl<>(List.of(account));

        when(accountRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(accountMapper.toDto(account)).thenReturn(accountDto);

        PagedResponse<AccountDto> result = accountService.search(searchDTO);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalItems()).isEqualTo(1L);
    }

    @Test
    void getById_whenFound_returnsDto() {
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.getById(id);

        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void getById_whenNotFound_throwsResourceNotFoundException() {
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_encodesPasswordAndSaves() {
        accountDto.setPassword("plain");
        when(accountMapper.toEntity(accountDto)).thenReturn(account);
        when(passwordEncoder.encode("plain")).thenReturn("hashed");
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.create(accountDto);

        verify(passwordEncoder).encode("plain");
        verify(accountRepository).save(account);
        assertThat(result).isNotNull();
    }

    @Test
    void update_whenFound_updatesFields() {
        accountDto.setFirstName("Jane");
        accountDto.setLastName("Smith");
        accountDto.setEmail("new@test.com");
        accountDto.setPassportNo("CD7654321");

        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.update(id, accountDto);

        verify(accountRepository).save(account);
        assertThat(result).isNotNull();
    }

    @Test
    void update_withNewPassword_encodesIt() {
        accountDto.setPassword("newpass");
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(passwordEncoder.encode("newpass")).thenReturn("newhashed");
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(accountDto);

        accountService.update(id, accountDto);

        verify(passwordEncoder).encode("newpass");
    }

    @Test
    void update_whenNotFound_throwsResourceNotFoundException() {
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.update(id, accountDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_whenFound_deletesAccount() {
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        accountService.delete(id);

        verify(accountRepository).delete(account);
    }

    @Test
    void delete_whenNotFound_throwsResourceNotFoundException() {
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByEmail_whenFound_returnsDto() {
        when(accountRepository.findByEmail("test@test.com")).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.getByEmail("test@test.com");

        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void getByEmail_whenNotFound_throwsResourceNotFoundException() {
        when(accountRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getByEmail("missing@test.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
