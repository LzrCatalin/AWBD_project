package com.awbd.airport_manager.service.api;

import com.awbd.airport_manager.dto.AccountDto;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    List<AccountDto> getAll();

    AccountDto getById(UUID id);

    AccountDto create(AccountDto dto);

    AccountDto update(UUID id, AccountDto dto);

    void delete(UUID id);
}
