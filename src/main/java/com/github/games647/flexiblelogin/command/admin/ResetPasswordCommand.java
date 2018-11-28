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
package com.github.games647.flexiblelogin.command.admin;

import com.github.games647.flexiblelogin.FlexibleLogin;
import com.github.games647.flexiblelogin.command.AbstractCommand;
import com.github.games647.flexiblelogin.config.Settings;
import com.github.games647.flexiblelogin.storage.AuthMeDatabase;
import com.github.games647.flexiblelogin.tasks.ResetPwTask;
import com.google.inject.Inject;

import org.slf4j.Logger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;

import static org.spongepowered.api.command.args.GenericArguments.onlyOne;
import static org.spongepowered.api.command.args.GenericArguments.string;
import static org.spongepowered.api.command.args.GenericArguments.user;
import static org.spongepowered.api.text.Text.of;

public class ResetPasswordCommand extends AbstractCommand {

    @Inject
    ResetPasswordCommand(FlexibleLogin plugin, Logger logger, Settings settings) {
        super(plugin, logger, settings);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        User user = args.<User>getOne("user").get();
        String password = args.<String>getOne("password").get();

        ResetPwTask resetPwTask;
        if (plugin.getDatabase() instanceof AuthMeDatabase){
            resetPwTask = new ResetPwTask(plugin, src, password, user.getName());
        }else {
            resetPwTask = new ResetPwTask(plugin, src, password, user.getUniqueId());
        }
        //check if the account is a valid player name
        Task.builder()
                //Async as it could run a SQL query
                .async()
                .execute(resetPwTask)
                .submit(plugin);
        return CommandResult.success();
    }

    @Override
    public CommandSpec buildSpec(Settings settings) {
        return CommandSpec.builder()
                .executor(this)
                .arguments(
                        onlyOne(
                                user(of("user"))
                        ),
                        onlyOne(
                                string(of("password"))
                        )
                )
                .build();
    }
}
