package com.workintech.s19d2.controller;

import com.workintech.s19d2.entity.Account;
import com.workintech.s19d2.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/account", "/accounts"})
public class AccountController {

    private final AccountService accountService;

    @GetMapping({"", "/"})
    public List<Account> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public Account find(@PathVariable Long id) {
        validateId(id);

        Account account = accountService.find(id);

        if (account == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Account not found"
            );
        }

        return account;
    }

    @PostMapping({"", "/"})
    public Account save(@RequestBody Account account) {
        validateAccount(account);
        return accountService.save(account);
    }

    @PutMapping("/{id}")
    public Account update(
            @PathVariable Long id,
            @RequestBody Account input) {

        validateId(id);
        validateAccount(input);

        Account existing = find(id);
        existing.setName(input.getName());

        return accountService.save(existing);
    }

    @DeleteMapping("/{id}")
    public Account delete(@PathVariable Long id) {
        validateId(id);

        find(id);

        Account deleted = accountService.delete(id);

        if (deleted == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Account not found"
            );
        }

        return deleted;
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "ID must be positive"
            );
        }
    }

    private void validateAccount(Account account) {
        if (account == null ||
            account.getName() == null ||
            account.getName().isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Account name is required"
            );
        }
    }
}
