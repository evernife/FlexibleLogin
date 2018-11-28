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

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

public class UnregisterTask implements Runnable {

    private final FlexibleLogin plugin;
    private final CommandSource src;

    private final Object accountIdentifier;

    UnregisterTask(FlexibleLogin plugin, CommandSource src, Object accountIdentifier) {
        this.plugin = plugin;
        this.src = src;
        this.accountIdentifier = accountIdentifier;
    }

    public UnregisterTask(FlexibleLogin plugin, CommandSource src, UUID uuid) {
        this(plugin, src, (Object) uuid);
    }

    public UnregisterTask(FlexibleLogin plugin, CommandSource src, String playerName) {
        this(plugin, src, (Object) playerName);
    }

    @Override
    public void run() {
        boolean accountFound;
        boolean usingName = false;
        if (accountIdentifier instanceof String) {
            usingName = true;
            accountFound = plugin.getDatabase().deleteAccount((String) accountIdentifier);
        } else {
            accountFound = plugin.getDatabase().deleteAccount((UUID) accountIdentifier);
        }

        if (accountFound) {
            src.sendMessage(plugin.getConfigManager().getText().getAccountDeleted(accountIdentifier.toString()));

            Optional<Player> optPlayer;
            if (usingName){
                optPlayer = Sponge.getServer().getPlayer((String) accountIdentifier);
            }else {
                optPlayer = Sponge.getServer().getPlayer((UUID) accountIdentifier);
            }

            if (optPlayer.isPresent()){
                optPlayer.get().kick(plugin.getConfigManager().getText().getAccountDeletedKickMessage());
            }
        } else {
            src.sendMessage(plugin.getConfigManager().getText().getAccountNotFound());
        }
    }
}
