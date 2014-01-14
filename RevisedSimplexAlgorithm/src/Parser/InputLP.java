package Parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import Datenstrukturen.Fraction;
import Datenstrukturen.LP;
import Datenstrukturen.Matrix;
import Datenstrukturen.Tupel;
import Datenstrukturen.Vector;

public class InputLP {
	private Matrix m;	
	private ArrayList<String> cn;	//column names
	private ArrayList<String> rn;	//row names
	private ArrayList<Tupel<String, String>> ec;	//Is row equation or inequality? (eq?, name)
	private Fraction[] c;	//objective function /cost
	private Fraction[] b;	//RHS
	private boolean isMax;
	private ArrayList<Tupel<Integer, Double>> upperBound;
	private ArrayList<Tupel<Integer, Double>> lowerBound;
	
	
	public LP readLP( String path){
		int numberOfSchlupfs = 0;
		int numberOfUnboundedVariables = 0;
		int numberOfNegativeVariables = 0;
		LPReader lpreader = new LPReader(path);
		try {
			lpreader.readLP();
			rn = new ArrayList<String>(); //Contraint namen
			cn = new ArrayList<String>(); // Variablen namen
			ec = new ArrayList<Tupel<String, String>>(); //Ob Contraint =:E, <=:L, >=:G
			double[][] matrix = lpreader.constraintsMatrix();
			m = new Matrix();
			boolean[] unboundedVariables = new boolean[lpreader.noOfVariables()];
			boolean[] negativeVariables = new boolean[lpreader.noOfVariables()];
			for(int i = 0; i < lpreader.noOfConstraints(); i++){
				rn.add(lpreader.constraintName(i));
				m.addRow();
			}
			for(int i = 0; i < lpreader.noOfVariables(); i++){
				cn.add(lpreader.variableName(i));
				m.addColumn();
			}
			double[] upperboundvector = lpreader.upperBoundVector();
			double[] lowerboundvector = lpreader.lowerBoundVector();
			for(int i = 0; i < lpreader.noOfVariables(); i++){
//				if((lowerboundvector[i]<0 || upperboundvector[i]<=0) && !(lowerboundvector[i]<0 && upperboundvector[i]<=0)){
//				if(!((lowerboundvector[i]>=0 && upperboundvector[i]>=0) || (lowerboundvector[i]<=0 && upperboundvector[i]<=0))){
				if(lowerboundvector[i]<0 && upperboundvector[i]<=0){ //negative
					negativeVariables[i] = true;
					numberOfNegativeVariables++;
//					System.out.println("BLA");
				} else if(lowerboundvector[i]<0 && upperboundvector[i]>0){ //unbounded
					unboundedVariables[i] = true;
					cn.add(lpreader.variableName(i)+"-");
					m.addColumn();
					numberOfUnboundedVariables++;
//					System.out.println("BLA");
				} else { //positiv variables
					unboundedVariables[i] = false;
					negativeVariables[i] = false;
				}
			}
			for(int i = 0; i < lpreader.noOfVariables(); i ++){
				for(int j = 0; j < lpreader.noOfConstraints(); j++){
					if(matrix[j][i] != 0){
						if(unboundedVariables[i]){
							m.addEntry(j, cn.indexOf(cn.get(i)+"-"), -matrix[j][i]);
							m.addEntry(j, i, matrix[j][i]);
						}else if(negativeVariables[i]){
							m.addEntry(j, i, -matrix[j][i]);
//							System.out.println("BLA");
						} else {
							m.addEntry(j, i, matrix[j][i]);
						}
					}
				}
			}
			
			lpreader.objectiveSense();
			
			for(int i = 0 ; i < lpreader.noOfConstraints(); i++){
				if(lpreader.senseVector()[i] == lpreader.SENSE_EQ){
					ec.add(new Tupel("E", lpreader.constraintName(i)));
				}else if(lpreader.senseVector()[i] == lpreader.SENSE_LEQ){
					ec.add(new Tupel("L", lpreader.constraintName(i)));
					numberOfSchlupfs++;
				}else if(lpreader.senseVector()[i] == lpreader.SENSE_GEQ){
					ec.add(new Tupel("G", lpreader.constraintName(i)));
					numberOfSchlupfs++;
				}
			}
			
//			######## BOUNDS ########
			
			ArrayList<Double> rhsBounds = new ArrayList<Double>();
			for(int i = 0; i < lpreader.noOfVariables(); i++){
				if(!Double.isInfinite(upperboundvector[i])){
					if(unboundedVariables[i]){
						m.addRow();
						m.addEntry(m.getRowNum()-1, i, 1);
						m.addEntry(m.getRowNum()-1, cn.indexOf(cn.get(i)+"-"), -1);
						rn.add("upperBound"+i);
						ec.add(new Tupel("L", "upperBound"+i));
						rhsBounds.add(upperboundvector[i]);
						numberOfSchlupfs++;
					} else if (negativeVariables[i]){
						if(upperboundvector[i]!=0){
//							System.out.println("BLA");
							m.addRow();
							m.addEntry(m.getRowNum()-1, i, 1);
							rn.add("upperBound"+i);
							ec.add(new Tupel("G", "upperBound"+i));
							rhsBounds.add((-1)*upperboundvector[i]);
							numberOfSchlupfs++;
						}
					} else {
						m.addRow();
						m.addEntry(m.getRowNum()-1, i, 1);
						rn.add("upperBound"+i);
						ec.add(new Tupel("L", "upperBound"+i));
						rhsBounds.add(upperboundvector[i]);
						numberOfSchlupfs++;
					}
				}
				if(lowerboundvector[i] != 0){
					if(unboundedVariables[i]){
						m.addRow();
						m.addEntry(m.getRowNum()-1, i, 1);
						m.addEntry(m.getRowNum()-1, cn.indexOf(cn.get(i)+"-"), -1);
						rn.add("lowerBound"+i);
						ec.add(new Tupel("G", "lowerBound"+i));
						rhsBounds.add(lowerboundvector[i]);
						numberOfSchlupfs++;
					} else if (negativeVariables[i]){
//						System.out.println("BLA");
						m.addRow();
						m.addEntry(m.getRowNum()-1, i, 1);
						rn.add("lowerBound"+i);
						ec.add(new Tupel("L", "lowerBound"+i));
						rhsBounds.add((-1)*lowerboundvector[i]);
						numberOfSchlupfs++;
					} else {
						m.addRow();
						m.addEntry(m.getRowNum()-1, i, 1);
						rn.add("lowerBound"+i);
						ec.add(new Tupel("G", "lowerBound"+i));
						rhsBounds.add(lowerboundvector[i]);
						numberOfSchlupfs++;
					}
				}
//				System.out.println(i + ": " + lowerboundvector[i]);
			}
//			####### End Bounds ########
			
						
			double[] obj = lpreader.objectiveVector();
			c = new Fraction[obj.length + numberOfSchlupfs + numberOfUnboundedVariables];
			for(int i = 0; i < obj.length; i++){
				if(lpreader.objectiveSense()==lpreader.SENSE_MIN){
					if(unboundedVariables[i]){
						c[i] = new Fraction(-obj[i]);
						c[cn.indexOf(cn.get(i)+"-")] = new Fraction(obj[i]);
					} else if (negativeVariables[i]){
//						System.out.println("BLA");
						c[i] = new Fraction(obj[i]);
					} else {
						c[i] = new Fraction(-obj[i]);
					}
				} else{
					if(unboundedVariables[i]){
						c[i] = new Fraction(obj[i]);
						c[cn.indexOf(cn.get(i)+"-")] = new Fraction(-obj[i]);
					} else if (negativeVariables[i]){
						c[i] = new Fraction(-obj[i]);
						System.out.println(c[i]);
					} else {
						c[i] = new Fraction( obj[i] );
					}
				}
			}
			if(lpreader.objectiveSense()==lpreader.SENSE_MIN){
				isMax = false;
			} else{
				isMax = true;
			}
			
			double[] tmpb = lpreader.rhsVector();
			b = new Fraction[tmpb.length + rhsBounds.size()];
			for(int i = 0; i < tmpb.length; i++){
				b[i] = new Fraction(tmpb[i]);
			}
			for(int i = 0; i < rhsBounds.size(); i++){
				b[tmpb.length + i] = new Fraction(rhsBounds.get(i));
			}
			
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new LP(m, ec, rn, new Vector(c), new Vector(b), isMax, upperBound, lowerBound);
	}
		
	
	public static void main (String[] arg){
		InputLP in = new InputLP();
		in.readLP("src/InputData/small2.lp");
		System.out.println("Ausgabe: ");
		System.out.println(in.getM().toString());
		for(int i = 0; i< in.getC().length;i++){
			System.out.println("C: " + in.getC()[i]);
		}
		System.out.println(in.getRn());

	}

	public Matrix getM() {
		return m;
	}
	
	public ArrayList<String> getCn() {
		return cn;
	}

	public ArrayList<String> getRn() {
		return rn;
	}

	public boolean isMax() {
		return isMax;
	}

	public void setMax(boolean isMax) {
		this.isMax = isMax;
	}


	public Fraction[] getC() {
		return c;
	}


	public Fraction[] getB() {
		return b;
	}

}
