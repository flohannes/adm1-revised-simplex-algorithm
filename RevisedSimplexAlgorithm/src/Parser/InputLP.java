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
		LPReader lpreader = new LPReader(path);
		try {
			lpreader.readLP();
			rn = new ArrayList<String>();
			cn = new ArrayList<String>();
			ec = new ArrayList<Tupel<String, String>>();
			double[][] matrix = lpreader.constraintsMatrix();
			m = new Matrix();
			boolean[] unboundedVariables = new boolean[lpreader.noOfVariables()];
			for(int i = 0; i < lpreader.noOfConstraints(); i++){
				rn.add(lpreader.constraintName(i));
				m.addRow();
			}
			for(int i = 0; i < lpreader.noOfVariables(); i++){
				cn.add(lpreader.variableName(i));
				m.addColumn();
				unboundedVariables[i] = false;
			}
			for(int i = 0; i < lpreader.noOfVariables(); i ++){
				for(int j = 0; j < lpreader.noOfConstraints(); j++){
					if(matrix[j][i] != 0){
						m.addEntry(j, i, matrix[j][i]);
					}
				}
			}
			
			lpreader.objectiveSense();
			
//			######## BOUNDS ########
			double[] upperboundvector = lpreader.upperBoundVector();
			double[] lowerboundvector = lpreader.lowerBoundVector();
			ArrayList<Double> rhsBounds = new ArrayList<Double>();
			for(int i = 0; i < lpreader.noOfVariables(); i++){
				if(!Double.isInfinite(upperboundvector[i])){
					m.addRow();
					m.addEntry(m.getRowNum()-1, i, 1);
					rn.add("upperBound"+i);
					ec.add(new Tupel("L", "upperBound"+i));
					rhsBounds.add(upperboundvector[i]);
					numberOfSchlupfs++;
				}
				if(lowerboundvector[i] != 0){
//					if(lowerboundvector[i]<0){
//						m.addRow();
//						m.addEntry(m.getRowNum()-1, i, 1);
//						m.addColumn();
//						m.addEntry(m.getRowNum()-1, m.getColNum()-1, -1);
//						cn.add(lpreader.variableName(i)+"-");
//						unboundedVariables[i] = true;
//						rn.add("lowerBound"+i);
//						ec.add(new Tupel("G", "lowerBound"+i));
//						rhsBounds.add(lowerboundvector[i]);
//						numberOfSchlupfs++;
//					} else {
						m.addRow();
						m.addEntry(m.getRowNum()-1, i, 1);
						rn.add("lowerBound"+i);
						ec.add(new Tupel("G", "lowerBound"+i));
						rhsBounds.add(lowerboundvector[i]);
						numberOfSchlupfs++;
//					}
				}
//				System.out.println(i + ": " + lowerboundvector[i]);
			}
//			####### End Bounds ########
			
			
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
			
			double[] obj = lpreader.objectiveVector();
			c = new Fraction[obj.length + numberOfSchlupfs];
			for(int i = 0; i < obj.length; i++){
				if(lpreader.objectiveSense()==lpreader.SENSE_MIN){
					c[i] = new Fraction(-obj[i]);
				} else{
					c[i] = new Fraction( obj[i] );
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
		
//	public LP readInput( String path) throws IOException{
//		setMax(true);
//		m = new Matrix();
//		cn = new ArrayList<String>();
//		rn = new ArrayList<String>();
//		ec = new ArrayList<Tupel<String, String>>();
////		c = new  Vector();
////		b = new  Vector();
//		ArrayList<Double> obj = new ArrayList<Double>();
//		ArrayList<Double> r = new ArrayList<Double>();
//		ArrayList<Tupel<Integer, Double>> cList = new ArrayList<Tupel<Integer, Double>>();
//		
//		BufferedReader in = new BufferedReader(new FileReader(path));
//		String line = null;
//		int numberOfSchlupfs = 0;
//		boolean rows = false;
//		boolean columns = false;
//		boolean rhs = false; 
//		boolean bounds = false;
//		String cost = "";
//		
//		int counter = 0;
////		System.out.println("Test"+in.readLine());
//		while ((line = in.readLine()) != null) {
////			line.trim();
//			
////			System.out.println("Token"+st.nextToken());
////			String s = line.trim().replaceAll(" +"," ");
//////			System.out.println();
////			String[] zeile2 = s.split(" ");
////			System.out.println(counter + " : "+ line.isEmpty() +" . length:" +  zeile2.length);
//			counter++;
////			System.out.println(counter);
////			line = line.trim().replaceAll(" ", "");
////			System.out.println("Test"+line);
//			if(line.isEmpty()){
//				continue;
//			}
////			System.out.println("L: " + line);
//			StringTokenizer lineToken = new StringTokenizer(line);
//			String firstToken = lineToken.nextToken();
////			System.out.println("T: " + firstToken);
//			if( firstToken.equals("ROWS")){
//				rows = true;
//			}else if(firstToken.equals("Minimize")){
//				setMax(false);
//				String minObj = in.readLine();
//				StringTokenizer objToken = new StringTokenizer(minObj);
//				String zeilenName = objToken.nextToken();
//				while(objToken.hasMoreTokens()){
//					String numVar = objToken.nextToken();
//					String var = objToken.nextToken();
//					Double num = Double.parseDouble(numVar);
//					m.addColumn();
//					cn.add(var);
//					obj.add(num);
////					System.out.println(num + " " + var);
//				}
////				System.out.println("MinObj:" + minObj);
////				String tempZeile = minObj.trim().replaceAll(" +", " ");
////				String[] zeile = tempZeile.split(" ");
////				for(int i = 1; i < zeile.length; i = i+2){
////					String var = zeile[i+1];
////					Double num = Double.parseDouble(zeile[i]);
////					System.out.println(num + " " + var);
////				}
//				
//			}else if(line.equals("Maximize")){
//				setMax(true);
//				String maxObj = in.readLine();
//				StringTokenizer objToken = new StringTokenizer(maxObj);
//				String zeilenName = objToken.nextToken();
//				while(objToken.hasMoreTokens()){
//					String numVar = objToken.nextToken();
//					String var = objToken.nextToken();
//					Double num = Double.parseDouble(numVar);
//					m.addColumn();
//					cn.add(var);
//					obj.add(num);
////					System.out.println(num + " " + var);
//				}
//				
//			}
//			else if( firstToken.equals("Subject")){
//				if(line.contains("Subject to")){
//					rows = true;
//				}
//			}else
//			if( line.equals("RHS")){
//				columns = false;
//				b = new Fraction[rn.size()];
//				rhs = true;
//			}else if(firstToken.equals("Bounds")){
//				rows = false;
//				bounds = true;
//			}
//			else if(firstToken.equals("End")){
//				break;
//			}
//			else if (rows){//Zeilen einlesen
//				StringTokenizer objToken = new StringTokenizer(line);
//				String zeilenName = objToken.nextToken();
//				rn.add(zeilenName);
//				m.addRow();
//				while(objToken.hasMoreTokens()){
//					String numVar = objToken.nextToken();
//					String var = objToken.nextToken();
//					if(numVar.equals("=") || numVar.equals("<=") || numVar.equals(">=")){
//						if(numVar.equals("=")){
//							ec.add(new Tupel("E", zeilenName));
//						}else if(numVar.equals("<=")){
//							ec.add(new Tupel("L", zeilenName));
//							numberOfSchlupfs++;
//						}else if(numVar.equals(">=")){
//							ec.add(new Tupel("G", zeilenName));
//							numberOfSchlupfs++;
//						}
//						Double num = Double.parseDouble(var);
//						r.add(num);
//					} else{
//						if(!cn.contains(var)){
//							Double num = Double.parseDouble(numVar);
//							cn.add(var);
//							m.addColumn();
////							System.out.println("RowNo."+rn.indexOf(zeilenName));
////							System.out.println("CoNo."+cn.indexOf(var));
////							System.out.println("Num."+num);
//							m.addEntry(rn.indexOf(zeilenName), cn.indexOf(var), num);
////							System.out.println(num + " " + var);
//						} else {
//							Double num = Double.parseDouble(numVar);
//							m.addEntry(rn.indexOf(zeilenName), cn.indexOf(var), num);
////							System.out.println(num + " " + var);
//						}
//					}
//					
//					
//				}
////				String tempZeile = line.trim().replaceAll(" +", " ");
////				String[] zeile = tempZeile.split(" ");
////				if( !zeile[0].equals("N")){//fuer die kosten keine matrix-zeile erstellen
////					ec.add(new Tupel<String, String>(zeile[0], zeile[1]));
////					rn.add(zeile[1]);
////					m.addRow();
////					if(zeile[0].equals("L") | zeile[0].equals("R")){
////						numberOfSchlupfs++;
////					}
////				} else if(zeile[0].equals("N")){
////					cost = zeile[1];
////				}
//			}else
//			if ( columns){//Spalten einlesen
//				String tempZeile = line.trim().replaceAll(" +", " ");
//				String[] zeile = tempZeile.split(" ");
//				if(!cn.contains(zeile[0])){
//					cn.add(zeile[0]);
//					m.addColumn();
//				}
//				if(zeile[1].equals(cost)){
////					cList.addEntry(0,cn.indexOf(zeile[0]), Double.parseDouble(zeile[2]));
//					cList.add(new Tupel(cn.indexOf(zeile[0]), Double.parseDouble(zeile[2])));
//				} else{
//					m.addEntry(rn.indexOf(zeile[1]), cn.indexOf(zeile[0]), Double.parseDouble(zeile[2]));
//				}
//				if(zeile.length > 3){
//					if(zeile[3].equals(cost)){
//						cList.add(new Tupel(cn.indexOf(zeile[0]), Double.parseDouble(zeile[4])));
//					} else{
//						m.addEntry(rn.indexOf(zeile[3]), cn.indexOf(zeile[0]), Double.parseDouble(zeile[4]));
//					}
//				} 
//				
//			}else
//			if( rhs){//rechte seite einlesen
//				String tempZeile = line.trim().replaceAll(" +", " ");
//				String[] zeile = tempZeile.split(" ");
//				b[rn.indexOf(zeile[1])] = Double.parseDouble(zeile[2]);
//				if(zeile.length > 3){
//					b[rn.indexOf(zeile[3])] =  Double.parseDouble(zeile[4]);
//				}
//			}
////			if(bounds){
////				String tempZeile = line.trim().replaceAll(" +", " ");
////				String[] zeile = tempZeile.split(" ");
////				
////				if(zeile[0].equals("UP")){
////					upperBound.add(new Tupel<Integer, Double>(cn.indexOf(zeile[2]), Double.parseDouble(zeile[3])));
////				} else if (zeile[0].equals("LO")){
////					lowerBound.add(new Tupel<Integer, Double>(cn.indexOf(zeile[2]), Double.parseDouble(zeile[3])));
////				}
////			}
//		}
//		in.close();
//
//		b = new double[r.size()];
//		int counterR = 0;
//		for(Double d : r){
//			b[counterR] = d;
//			counterR++;
//		}
//		c = new double[cn.size() + numberOfSchlupfs];
//		int counterObj = 0;
//		for(Double d : obj){
//			c[counterR] = d;
//			counterObj++;
//		}
////		for(Tupel<Integer, Double> t : cList){
////			c[t.getNum()] = -t.getEntry();
////		}
//		
////		System.out.println("Fertig");
//		return new LP(m, ec, rn, new Vector(c), new Vector(b), bounds, upperBound, lowerBound);
//	}
	
	public static void main (String[] arg){
		InputLP in = new InputLP();
		in.readLP("src/InputData/small2.lp");
		System.out.println("Ausgabe: ");
		System.out.println(in.getM().toString());
//		System.out.println("B: " + );
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

}
