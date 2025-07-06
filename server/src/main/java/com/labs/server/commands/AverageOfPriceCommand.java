package com.labs.server.commands;

import com.labs.common.Command;
import com.labs.common.DataContainer;
import com.labs.server.CollectionManager;

public class AverageOfPriceCommand implements Command {
    private CollectionManager collectionManager;

    public AverageOfPriceCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public DataContainer execute() {
        return collectionManager.averageOfPrice();
    }

}
