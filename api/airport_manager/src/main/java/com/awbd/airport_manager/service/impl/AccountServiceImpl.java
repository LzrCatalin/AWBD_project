package com.awbd.airport_manager.service.impl;

import com.awbd.airport_manager.dto.AccountDto;
import com.awbd.airport_manager.exception.ResourceNotFoundException;
import com.awbd.airport_manager.mapper.AccountMapper;
import com.awbd.airport_manager.model.Account;
import com.awbd.airport_manager.repository.AccountRepository;
import com.awbd.airport_manager.service.api.AccountService;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;
import com.awbd.airport_manager.util.spec.QuerySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<AccountDto> getAll() {
        return accountMapper.toList(accountRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AccountDto> search(SearchDTO searchDTO) {
        return new PagedResponse<>(
                accountRepository.findAll(new QuerySpecification<>(searchDTO), searchDTO.buildPageable())
                        .map(accountMapper::toDto)
        );
    }

    @Override
    public AccountDto getById(UUID id) {
        return accountMapper.toDto(findById(id));
    }

    @Override
    @Transactional
    public AccountDto create(AccountDto dto) {
        Account account = accountMapper.toEntity(dto);
        account.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        return accountMapper.toDto(accountRepository.save(account));
    }

    @Override
    @Transactional
    public AccountDto update(UUID id, AccountDto dto) {
        Account account = findById(id);
        account.setEmail(dto.getEmail());
        account.setFirstName(dto.getFirstName());
        account.setLastName(dto.getLastName());
        account.setPassportNo(dto.getPassportNo());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            account.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        return accountMapper.toDto(accountRepository.save(account));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        accountRepository.delete(findById(id));
    }

    private Account findById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id));
    }
}
