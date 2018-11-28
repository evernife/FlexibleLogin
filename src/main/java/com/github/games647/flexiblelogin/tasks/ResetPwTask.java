/*
 * This file is part of FlexibleLogin
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.games647.flexiblelogin.tasks;

import com.github.games647.flexiblelogin.FlexibleLogin;
import com.github.games647.flexiblelogin.storage.Account;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

public class ResetPwTask implements Runnable {

    private final FlexibleLogin plugin;

    private final CommandSource src;
    private final String password;

    private final Object accountIdentifier;

    public ResetPwTask(FlexibleLogin plugin, CommandSource src, String password, Object accountIdentifier) {
        this.plugin = plugin;
        this.src = src;
        this.password = password;

        this.accountIdentifier = accountIdentifier;
    }

    public ResetPwTask(FlexibleLogin plugin, CommandSource src, String password, String playerName) {
        this(plugin, src, password, (Object) playerName);
    }

    public ResetPwTask(FlexibleLogin plugin, CommandSource src, String password, UUID uuid) {
        this(plugin, src, password, (Object) uuid);
    }

    @Override
    public void run() {
        Optional<Player> player;
        Optional<Account> account;
        if (accountIdentifier instanceof String) {
            player = Sponge.getServer().getPlayer((String) accountIdentifier);
            account = player.map(player1 -> plugin.getDatabase().getAccount(player1))
                    .orElseGet(() -> plugin.getDatabase().loadAccount((String) accountIdentifier));
        }else {
            player = Sponge.getServer().getPlayer((UUID) accountIdentifier);
            account = player.map(player1 -> plugin.getDatabase().getAccount(player1))
                    .orElseGet(() -> plugin.getDatabase().loadAccount((UUID) accountIdentifier));
        }

        if (account.isPresent()) {
            resetPassword(account.get());
        } else {
            src.sendMessage(plugin.getConfigManager().getText().getAccountNotFound());
        }
    }

    private void resetPassword(Account account) {
        try {
            account.setPasswordHash(plugin.getHasher().hash(password));
            plugin.getDatabase().save(account);

            src.sendMessage(plugin.getConfigManager().getText().getChangePassword());
        } catch (Exception ex) {
            plugin.getLogger().error("Error creating hash", ex);
            src.sendMessage(plugin.getConfigManager().getText().getErrorExecutingCommand());
        }
    }
}
