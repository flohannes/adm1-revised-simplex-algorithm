package Main;

import java.io.IOException;
import java.util.ArrayList;

import Datenstrukturen.LP;
import Parser.Input;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		ArrayList<String> test = new ArrayList<String>();
//		test.add("x1");
//		test.add("x12");
//		test.add(1, "x13");
//		System.out.println(test.indexOf("x1"));
//		System.out.println(test);
		
		try {
			Input in = new Input();
			LP lin = in.readInput("src/InputData/Bsp_28.mps");
			System.out.println(in.getM());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
