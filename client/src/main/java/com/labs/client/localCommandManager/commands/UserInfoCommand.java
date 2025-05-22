package com.labs.client.localCommandManager.commands;

import com.labs.client.DataManager;
import com.labs.client.UserManager;
import com.labs.common.Command;
import com.labs.common.DataContainer;

public class UserInfoCommand implements Command {

    private UserManager userManager;

    public UserInfoCommand(UserManager userManager) {
        this.userManager = userManager;
    }

    public DataContainer execute() {
        DataContainer dataContainer = new DataContainer();
        dataContainer.add("status", "ok");
        dataContainer.add("message", "User info command executed successfully");
        dataContainer.add("return-data", userManager.getUser().toString());
        return dataContainer;
    }
}