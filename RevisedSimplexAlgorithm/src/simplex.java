import java.io.IOException;

import Algo.Simplex;
import Datenstrukturen.LP;
import Parser.Input;
import Parser.Output;


public class simplex {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean showComments = false;
		Output out = null; 
		try {
			String dataName = args[0];
			String dataOutputName = args[1];
			if(args.length > 2){
				if(args[2].equals("1"))
					showComments = true;
			}
			Input in = new Input();
			LP lin = in.readInput("InputData/"+dataName);
			Simplex simplex = new Simplex(lin);
			simplex.calculateOptimum(showComments);
			
			if(simplex.isPerfect)
				out = new Output(in.getCn(), simplex.getbQuer(), simplex.getBasis(), simplex.getOptimum(), "OutputData/"+dataOutputName);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
