package com.example.webservice;

import javax.xml.ws.Endpoint;


public class Publish {
    public static void main(String[] args) {
        Endpoint endpoint = Endpoint.publish("http://localhost:8080/hello", new HelloImpl());
        System.out.println("Hello service is published: " + endpoint.isPublished());
    }
}
