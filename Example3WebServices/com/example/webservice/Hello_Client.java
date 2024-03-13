package com.example.webservice;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class Hello_Client {
	public static void main(String[] args) throws MalformedURLException{

		URL url = new URL("http://localhost:8080/hello?wsdl");
		QName qName = new QName("http://webservice.example.com/", "HelloImplService");
		Service service = Service.create(url, qName);
		IHello helloService = service.getPort(IHello.class);
		System.out.println(helloService.sayHello("Vaibhav"));

	}
}
