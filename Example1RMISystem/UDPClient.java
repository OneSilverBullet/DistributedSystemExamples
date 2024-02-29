import java.net.*;
import java.io.*;
import java.util.HashMap;

public class UDPClient{

	static class A
	{
		private int a;

		A(){
			a = 0;
		}
		public void SetA(int v){a = v;}

	}



    public static void main(String args[]){

		HashMap<String, A> tests = new HashMap<>();

		tests.put("asda", new A());


		tests.get("asda").SetA(2);

	}		      	
}
