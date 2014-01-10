import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import Parser.LPReader;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dataName = "small3.lp";
		LPReader in = new LPReader("src/InputData/"+dataName);
		try {
			in.readLP();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int[] obj = in.senseVector();
		for(int i = 0; i < obj.length; i++){
			System.out.println(i + ": " + obj[i]);
		}
		
	}

}
