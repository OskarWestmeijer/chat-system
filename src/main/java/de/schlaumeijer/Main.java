package de.schlaumeijer;

import de.schlaumeijer.bl.ServerCtrl;

public class Main {

    public static void main(String[] args){
        System.out.println("App started");
        ServerCtrl serverController = new ServerCtrl(5123);
        serverController.listenForConnection();
    }

}
