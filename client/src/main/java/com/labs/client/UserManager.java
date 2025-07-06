package com.labs.client;

import com.labs.common.DataContainer;
import com.labs.common.dataConverter.Serializer;
import com.labs.common.user.User;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class UserManager {
    private DataManager dataManager;
    private Input input;
    private Transmitter transmitter;
    private Output output;
    private User user;

    public UserManager(Input input, DataManager dataManager, Transmitter transmitter, Output output) {
        this.dataManager = dataManager;
        this.input = input;
        this.transmitter = transmitter;
        this.output = output;
    }


    public User getUser() {
        return user;
    }
    public User verifyUser(User user) {
        DataContainer autorizeRequest = new DataContainer();
        autorizeRequest.add("type", "verify-user");
        dataManager.nextSilent();
        dataManager.send(autorizeRequest, user);
        DataContainer response = dataManager.getResponse();
        var userOut = (User)response.get("User");
        if(response.get("status").equals("error")) {
            output.responseOut(response);
            return null;
        }
        else if(userOut.isVerified()) {
            output.outOk("Login successful");
        }
        return response.get("User");
    }

    public User addUser(User user) {
        DataContainer autorizeRequest = new DataContainer();
        autorizeRequest.add("type", "user-add");
        if(!dataManager.send(autorizeRequest, user)) return null;
        DataContainer response = dataManager.getResponse();

        var userOut = (User)response.get("User");
        if(response.get("status").equals("error")) {
            output.responseOut(response);
            return null;
        }
        else if(userOut.isVerified()) {
            output.outOk("Login successful");
        }
        return response.get("User");
    }

    public void authenticateUser() {
        User user = input.autorizeUser();
        boolean isServerConnected = transmitter.connectionCheck().get("status").equals("ok");
        while(!isServerConnected) {
            output.outError("Server disconnected. You are not authenticated.");
            isServerConnected = transmitter.connectionCheck().get("status").equals("ok");
            user = input.autorizeUser();
        }
        var newUser = verifyUser(user);
        while(newUser == null) {
            user = input.autorizeUser();
            newUser = verifyUser(user);
        }

        if(!newUser.isVerified()) {
            if(input.wantNewUser()) {
                newUser = addUser(newUser);
                if(newUser == null || !newUser.isVerified()) {
                    authenticateUser();
                    return;
                }
            }
            else {
                authenticateUser();
            }
        }
        this.user = newUser;
    }
}
