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

import com.github.games647.flexiblelogin.storage.Account;
import com.github.games647.flexiblelogin.FlexibleLogin;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

public class ForceRegTask implements Runnable {

    private final FlexibleLogin plugin;

    private final CommandSource src;
    private final Object accountIdentifier;
    private final String password;

    public ForceRegTask(FlexibleLogin plugin, CommandSource src, String password, Object accountIdentifier) {
        this.plugin = plugin;
        this.src = src;
        this.password = password;
        this.accountIdentifier = accountIdentifier;
    }

    public ForceRegTask(FlexibleLogin plugin, CommandSource src, String password, String playerName) {
        this(plugin, src, password, (Object) playerName);
    }

    public ForceRegTask(FlexibleLogin plugin, CommandSource src, String password, UUID uuid) {
        this(plugin, src, password, (Object) uuid);
    }



    @Override
    public void run() {
        Optional<Player> player;
        Optional<Account> optAccount;

        boolean ustinPlayerName = false;
        if (accountIdentifier instanceof String) {
            ustinPlayerName = true;
            player = Sponge.getServer().getPlayer((String) accountIdentifier);
            optAccount = player.map(player1 -> plugin.getDatabase().getAccount(player1))
                    .orElseGet(() -> plugin.getDatabase().loadAccount((String) accountIdentifier));
        }else {
            player = Sponge.getServer().getPlayer((UUID) accountIdentifier);
            optAccount = player.map(player1 -> plugin.getDatabase().getAccount(player1))
                    .orElseGet(() -> plugin.getDatabase().loadAccount((UUID) accountIdentifier));
        }

        if (optAccount.isPresent()) {
            src.sendMessage(plugin.getConfigManager().getText().getAccountAlreadyExists());
        } else {
            try {
                String hash = plugin.getHasher().hash(password);
                Account account;

                if (ustinPlayerName){
                    account = new Account(null,(String)accountIdentifier , hash, null);
                }else {
                    account = new Account((UUID)accountIdentifier, "", hash, null);
                }
                plugin.getDatabase().createAccount(account);

                Optional<Player> optPlayer;
                if (ustinPlayerName){
                    optPlayer = Sponge.getServer().getPlayer((String) accountIdentifier);
                }else {
                    optPlayer = Sponge.getServer().getPlayer((UUID) accountIdentifier);
                }

                if (optPlayer.isPresent()){
                    plugin.getDatabase().addCache(optPlayer.get().getUniqueId(), account);
                }

                src.sendMessage(plugin.getConfigManager().getText().getForceRegisterSuccess());
            } catch (Exception ex) {
                plugin.getLogger().error("Error creating hash", ex);
                src.sendMessage(plugin.getConfigManager().getText().getErrorExecutingCommand());
            }
        }
    }
}
