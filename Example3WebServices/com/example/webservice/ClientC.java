package com.example.webservice;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientC {
    public static void main(String[] args) throws NotBoundException, RemoteException {
        new UserInterface("Distributed Health Care Management System (DHMS) Client C");
    }
}
