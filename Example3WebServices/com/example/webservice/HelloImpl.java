package com.example.webservice;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService(endpointInterface = "com.example.webservice.IHello")
@SOAPBinding(style = Style.RPC)
public class HelloImpl implements IHello {
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }
}