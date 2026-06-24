package com.awbd.airport_manager.service.api;

import com.awbd.airport_manager.dto.AccountDto;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    List<AccountDto> getAll();

    PagedResponse<AccountDto> search(SearchDTO searchDTO);

    AccountDto getById(UUID id);

    AccountDto create(AccountDto dto);

    AccountDto update(UUID id, AccountDto dto);

    void delete(UUID id);

    AccountDto getByEmail(String email);

    AccountDto getCurrentAccount();
}
