import java.io.IOException;

import Algo.Simplex;
import Datenstrukturen.LP;
import Parser.InputLP;
import Parser.Output;


public class simplex {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean showComments = false;
		Output out = null; 
		String dataName = args[0];
		String dataOutputName = args[1];
		if(args.length > 2){
			if(args[2].equals("1"))
				showComments = true;
		}
		InputLP in = new InputLP();
		LP lin = in.readLP(dataName);
		Simplex simplex = new Simplex(lin);
		simplex.calculateOptimum(showComments);
		
		String status = "";
		if(simplex.isPerfect){
			status = "Optimal";
			out = new Output(lin, in.getCn(), simplex.getbQuer(), simplex.getBasis(), simplex.getOptimum(),status, dataOutputName);

		} else if (simplex.istLeer){
			status = "Unbounded";
			out = new Output(status, dataOutputName);

		} else if(simplex.istUnbeschraenkt){
			status = "Infeasable";
			out = new Output(status, dataOutputName);

		}
	}
	
}
