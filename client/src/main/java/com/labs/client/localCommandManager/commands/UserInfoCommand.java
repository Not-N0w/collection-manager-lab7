package com.labs.client.localCommandManager.commands;

import com.labs.client.DataManager;
import com.labs.client.UserManager;
import com.labs.common.Command;

public class UserInfoCommand implements Command {

    private UserManager userManager;

    public UserInfoCommand(UserManager userManager) {
        this.userManager = userManager;
    }

    public Object execute() {
        return userManager.getUser().toString();
    }
}