package com.awbd.airport_manager.service.impl;

import com.awbd.airport_manager.config.security.UserInfo;
import com.awbd.airport_manager.model.Account;
import com.awbd.airport_manager.model.AccountRole;
import com.awbd.airport_manager.repository.AccountRepository;
import com.awbd.airport_manager.repository.AccountRoleRepository;
import com.awbd.airport_manager.repository.RoleRepository;
import com.awbd.airport_manager.util.enums.RoleName;
import com.awbd.airport_manager.util.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Just-in-time provisioning of local accounts for Auth0 identities.
 *
 * The app authenticates against Auth0 but keeps a local {@code accounts} table; this service
 * links (or creates) the local row for the currently authenticated user, keyed by the stable
 * Auth0 {@code sub} claim. This is what makes bookings attach to a real account and what makes
 * real users visible to admins.
 */
@Service
@RequiredArgsConstructor
public class AccountProvisioningService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;

    @Transactional
    public Account resolveCurrentAccount() {
        return resolve(SecurityUtils.getCurrentUserInfo());
    }

    @Transactional
    public Account resolve(UserInfo info) {
        String sub = info.getSub();
        if (sub == null || sub.isBlank()) {
            throw new IllegalStateException("Authenticated token has no 'sub' claim; cannot resolve account.");
        }

        // 1) Already linked by Auth0 sub — the common case.
        Optional<Account> bySub = accountRepository.findByAuth0Sub(sub);
        if (bySub.isPresent()) {
            return bySub.get();
        }

        String email = info.getEmail();

        // 2) A pre-existing/seeded account with this email — link it to the Auth0 identity.
        if (email != null && !email.isBlank()) {
            Optional<Account> byEmail = accountRepository.findByEmail(email);
            if (byEmail.isPresent()) {
                Account account = byEmail.get();
                account.setAuth0Sub(sub);
                return accountRepository.save(account);
            }
        }

        // 3) First time we see this user — create their local account.
        if (email == null || email.isBlank()) {
            throw new IllegalStateException(
                    "Cannot provision account: access token is missing the email claim. " +
                            "Ensure the Auth0 post-login Action adds the configured email claim to the access token.");
        }

        Account account = new Account();
        account.setAuth0Sub(sub);
        account.setEmail(email);
        applyName(account, info.getName(), email);
        account.setActive(true);

        Account saved = accountRepository.save(account);
        saved.setRoles(assignRoles(saved, info.getRoles()));
        return saved;
    }

    private void applyName(Account account, String name, String email) {
        String first = null;
        String last = null;
        if (name != null && !name.isBlank()) {
            String[] parts = name.trim().split("\\s+", 2);
            first = parts[0];
            last = parts.length > 1 ? parts[1] : parts[0];
        }
        String localPart = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        account.setFirstName(isBlank(first) ? localPart : first);
        account.setLastName(isBlank(last) ? (isBlank(first) ? localPart : first) : last);
    }

    private List<AccountRole> assignRoles(Account account, List<String> roleNames) {
        List<String> names = (roleNames == null || roleNames.isEmpty())
                ? List.of(RoleName.PASSENGER.name())
                : roleNames;

        List<AccountRole> accountRoles = new ArrayList<>();
        for (String raw : names) {
            RoleName roleName;
            try {
                roleName = RoleName.valueOf(raw);
            } catch (IllegalArgumentException ex) {
                continue; // ignore roles that aren't part of the domain model
            }
            roleRepository.findByName(roleName).ifPresent(role -> {
                AccountRole accountRole = new AccountRole();
                accountRole.setAccount(account);
                accountRole.setRole(role);
                accountRole.setActive(true);
                accountRoles.add(accountRole);
            });
        }
        return accountRoleRepository.saveAll(accountRoles);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
