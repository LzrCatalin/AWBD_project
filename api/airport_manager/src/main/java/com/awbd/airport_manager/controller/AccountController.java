package com.awbd.airport_manager.controller;

import com.awbd.airport_manager.dto.AccountDto;
import com.awbd.airport_manager.service.api.AccountService;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.awbd.airport_manager.util.enums.ApiPaths.Accounts;

@RestController
@RequestMapping(Accounts)
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    @Operation(summary = "Get all accounts")
    public ResponseEntity<List<AccountDto>> getAll() {
        return ResponseEntity.ok(accountService.getAll());
    }

    @PostMapping("/search")
    @Operation(summary = "Search accounts with filters and pagination")
    public ResponseEntity<PagedResponse<AccountDto>> search(@RequestBody SearchDTO searchDTO) {
        return ResponseEntity.ok(accountService.search(searchDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by id")
    public ResponseEntity<AccountDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create account")
    public ResponseEntity<AccountDto> create(@Valid @RequestBody AccountDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update account")
    public ResponseEntity<AccountDto> update(@PathVariable UUID id, @Valid @RequestBody AccountDto dto) {
        return ResponseEntity.ok(accountService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
