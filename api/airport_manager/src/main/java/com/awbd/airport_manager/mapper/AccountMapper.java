package com.awbd.airport_manager.mapper;

import com.awbd.airport_manager.dto.AccountDto;
import com.awbd.airport_manager.model.Account;
import com.awbd.airport_manager.model.AccountRole;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AccountMapper implements EntityMapper<Account, AccountDto> {

    @Override
    public AccountDto toDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setEmail(account.getEmail());
        dto.setFirstName(account.getFirstName());
        dto.setLastName(account.getLastName());
        dto.setPassportNo(account.getPassportNo());

        List<String> roles = account.getRoles() == null ? Collections.emptyList() :
                account.getRoles().stream()
                        .map(AccountRole::getRole)
                        .filter(role -> role != null)
                        .map(role -> role.getName().name())
                        .toList();
        dto.setRoles(roles);

        return dto;
    }

    @Override
    public Account toEntity(AccountDto dto) {
        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setPasswordHash(dto.getPassword());
        account.setFirstName(dto.getFirstName());
        account.setLastName(dto.getLastName());
        account.setPassportNo(dto.getPassportNo());
        return account;
    }
}
