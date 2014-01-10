package Algo;

import Datenstrukturen.Fraction;
import Datenstrukturen.LP;
import Datenstrukturen.Matrix;
import Datenstrukturen.Tupel;
import Datenstrukturen.Vector;
import Parser.InputLP;
import Parser.Output;

public class Simplex {

	private LP lp;
	private Vector originalCostFunction;
	private Matrix basisInverse;
	private int[] basis;
	private int[] nichtbasis;
	private Vector schattenpreise;
	private Matrix m;
	public boolean isPerfect;
	private Vector bQuer;
	public boolean istUnbeschraenkt;
	public boolean istLeer;
	private Vector b;
	private boolean showComments;
	private boolean isBlandOn;

	public Simplex(LP lp){
		this.istUnbeschraenkt = false;
		this.istLeer = false;
		this.lp = lp;
		this.originalCostFunction = lp.getC().clone();
		this.basisInverse = new Matrix();
		this.basis = lp.getBasis();
		this.nichtbasis = lp.getNichtBasis();
		this.m = lp.getM();
		this.isPerfect = false;
		this.bQuer = lp.getB().clone();
		this.b = lp.getB().clone();
		this.isBlandOn = false;
	}
	
	
	public void calculateOptimum(boolean showComments){
		this.showComments = showComments;
		
		this.phase1();
		isPerfect = false;
		if(istUnbeschraenkt){
			System.out.println("Problem ist unbeschraenkt");
		}else if(istLeer){
			System.out.println("Problem ist leer");
		}else{
			System.out.println("Phase 2:  ");
			this.phase2();
		}
		
		if(isPerfect){
			Fraction erg;
			if(!lp.isMax())
				erg = this.getOptimum().negate();
			else
				erg = this.getOptimum();
			System.out.println("Optimales Ergebnis: " + erg);
		}
		else if(istUnbeschraenkt){
			System.out.println("Unbeschraenkt!!");
		}
			
	}
	
	private void phase1(){
		int iterationsWithoutChanges = 0;
		Fraction actualOptimum = new Fraction(0);
		Fraction[] costP = new Fraction[lp.getM().getColNum()];
		int indexOfKuenstlicheVar = lp.getIndexOfKuenstlicheVar();
		for(int b : basis){
			if(b>=indexOfKuenstlicheVar){
				costP[b] = new Fraction(-1) ;
			}
		}
		Vector costP1 = new Vector(costP);

		this.lp.setC(costP1);
		
		basisInverse.createI(basis.length);
//		System.out.println(this.bQuer);
//		for(int i = 0; i < this.basis.length; i++){
//			System.out.println(this.basis[i]);
//		}
		int counter = 1;
		while(true){
			System.out.println("Runde: "+counter);
			this.BTRAN(costP1);
			int maxIndex = this.PRICE(costP1);
			if(maxIndex == -1)
				break;
			Vector d = this.FTRAN(maxIndex);
			
			int indexChuzr = this.CHUZR(d);
			if(indexChuzr ==-1){
				this.istUnbeschraenkt = true;
				System.out.println("ist unbeschraenkt");
				break;
			}
			this.WRETA(maxIndex, indexChuzr, d);
//			System.out.println(this.bQuer);
//			for(int i = 0; i < this.basis.length; i++){
//				System.out.println(this.basis[i]);
//			}
//			System.out.println();
			
			Fraction optimum = new Fraction(0);
			for(int i = 0; i < basis.length; i++){
				if(costP1.get(basis[i])==null||bQuer.get(i)==null){
					
				} else{
					optimum = optimum.add( costP1.get(basis[i]).multiply(bQuer.get(i)) );
				}
			}
			if(actualOptimum == optimum){
				iterationsWithoutChanges++;
			} else {
				actualOptimum = optimum;
				iterationsWithoutChanges = 0;
			}
			if(iterationsWithoutChanges >= 20){
				System.out.println("TRUE");
				this.isBlandOn = true;
			} else {
				this.isBlandOn = false;
//				System.out.println(actualOptimum);
			}
			
			counter++;
		}
		int basisLengthCounter=0;
		m.deleteColumns(indexOfKuenstlicheVar);
//		System.out.println("BasisInv: "+basisInverse);
		for(int i = 0; i < this.basis.length; i++){
			if(basis[i] >= indexOfKuenstlicheVar){
				if(this.bQuer.getVec()[i].signum() > 0){
					System.out.println(this.bQuer.getVec()[i].doubleValue());
					this.istLeer = true;
					break;
				}
				else{
					boolean count = true;
					for(int j = 0; j < this.nichtbasis.length; j++){
						if(nichtbasis[j] < indexOfKuenstlicheVar){
							if(basisInverse.multiplyRowColumn(m, i, nichtbasis[j]).doubleValue() != 0){
//								System.out.println("BasisInv: "+basisInverse);
//								System.out.println("Matrix: "+m );
//								System.out.println("row: "+i);
//								System.out.println("col: "+nichtbasis[j]);
								this.WRETA(j, i, this.FTRAN(j));
								count = false;
								break;
							}
						}
					}
					if(count){
//						System.out.println("hallo");
//						m.deleteRow(basis[i]);
						basis[i]=-1;
						basisLengthCounter++;
						basisInverse.deleteColumn(i);
						basisInverse.deleteRow(i);
						bQuer.deleteEntry(i);
						b.deleteEntry(i);
						for(int k = 0; k < nichtbasis.length;k++){
							if(nichtbasis[k] <indexOfKuenstlicheVar){
								m.deleteRow(nichtbasis[k]);
								break;
								
							}
						}
					}
				}
			}
		}
		if(!istLeer){
			int[] tmpN = new int[m.getColNum() - basis.length +basisLengthCounter];
			int[] tmpB = new int[basis.length -basisLengthCounter];
			int count=0;
			for(int i=0 ; i<nichtbasis.length ;i++){
				if(nichtbasis[i]< indexOfKuenstlicheVar){
					tmpN[count] = nichtbasis[i];
					count++;
				}
			}
			nichtbasis = tmpN;
			count=0;
			for(int i=0 ;i<basis.length ;i++){
				if(basis[i] != -1){
					tmpB[count]=basis[i];
					count++;
				}
			}
			basis = tmpB;
		}
		
	}
	
	private void phase2(){
		int iterationsWithoutChanges = 0;
		Fraction actualOptimum = new Fraction(0);
//		System.out.println(BasisToString());
//		basisInverse.createI(basis.length);
//		System.out.println(this.bQuer);
//		for(int i = 0; i < this.basis.length; i++){
//			System.out.println(this.basis[i]);
//		}
		int counter = 1;
		while(true){
			this.BTRAN(originalCostFunction);
			int maxIndex = this.PRICE(originalCostFunction);
			if(maxIndex == -1)
				break;
			Vector d = this.FTRAN(maxIndex);
			
			int indexChuzr = this.CHUZR(d);
			if(indexChuzr ==-1){
				this.istUnbeschraenkt = true;
				System.out.println("ist unbeschraenkt");
				break;
			}
			this.WRETA(maxIndex, indexChuzr, d);
			System.out.println("Runde: "+counter);
//			System.out.println(this.bQuer);
//			for(int i = 0; i < this.basis.length; i++){
//				System.out.println(this.basis[i]);
//			}
			counter++;
//			System.out.println("BasisInv: "+basisInverse);
			
			if(actualOptimum == this.getOptimum()){
				iterationsWithoutChanges++;
			} else {
				actualOptimum = this.getOptimum();
				iterationsWithoutChanges = 0;
			}
			if(iterationsWithoutChanges >= 20){
				this.isBlandOn = true;
				System.out.println("TRUE");
			} else {
				this.isBlandOn = false;
			}
		}
	}
	
	
	private void BTRAN(Vector cost){
//		Vector cB = new Vector();
		Fraction[] cBi = new Fraction[basis.length];
//		System.out.println(cost.getLength());
//		System.out.println(m.getColNum());
		for(int i = 0; i < basis.length; i++){
			cBi[i] = cost.get(basis[i]);
		}
		Vector cB = new Vector(cBi);
		schattenpreise = basisInverse.multiplyVectorMatrix(cB);
//		System.out.println(basisInverse.getColNum()+" , "+basisInverse.getRowNum());
		if(showComments){
			System.out.println(schattenpreise.toString());
		}
	}
	
	private int PRICE( Vector cost){
		int MaxIndex =-1;
		Fraction max= new Fraction(0);
		int MaxMinIndex= Integer.MAX_VALUE; //
		
		for( int i=0 ; i<nichtbasis.length ; i++){
			Fraction redCost;
			if(cost.get(nichtbasis[i]) == null && m.multiplyVectorMatrixColumn(schattenpreise, nichtbasis[i]) != null){
				redCost = m.multiplyVectorMatrixColumn(schattenpreise, nichtbasis[i]).negate();
			} else if (cost.get(nichtbasis[i]) != null && m.multiplyVectorMatrixColumn(schattenpreise, nichtbasis[i]) == null){
				redCost = cost.get(nichtbasis[i]);
			} else if (cost.get(nichtbasis[i]) == null && m.multiplyVectorMatrixColumn(schattenpreise, nichtbasis[i]) == null){
				redCost = new Fraction(0);
			} else{
				redCost = cost.get(nichtbasis[i]).subtract( m.multiplyVectorMatrixColumn(schattenpreise, nichtbasis[i]) );
			}
			
			if(this.isBlandOn){
				if(redCost.doubleValue() > 0 && nichtbasis[i] < MaxMinIndex){//Kleinster-Variablen-Index-Regel
					MaxIndex = i;
					MaxMinIndex = nichtbasis[i];
					max=redCost;
				}
			} else {
				if( redCost.doubleValue()>max.doubleValue()){//Steilster-Anstieg-Regel
					MaxIndex = i;	
					max = redCost;
				}
			}
		}
		if( max.doubleValue()== 0)
			isPerfect = true;
//		System.out.println("Reduzierte Kosten: "+max);
		if(showComments){
			System.out.println("Reduzierte Kosten: "+max + ", Index: " + MaxIndex);
		}
		return MaxIndex;
	}
	
	public Vector FTRAN(int maxIndex){
		
		Vector d = basisInverse.multiplyMatrixMatrixColumn(m, nichtbasis[maxIndex]);
//		int counter = 0;
//		for(double eintrag : d.getVec()){
//			if(eintrag <= 0){
//				counter++;
//			}
//		}
		if(showComments){
			System.out.println(d.toString());
		}
//		if(counter == d.getVec().length)
//			return null;
		return d;
	}
	
	public int CHUZR(Vector d){
		Tupel<Integer,Fraction> lambda0 = this.lambda0(d);
		if(showComments){
			System.out.println(lambda0.getNum());
		}
		return lambda0.getNum();
	}
	
	private Tupel<Integer, Fraction> lambda0(Vector d){
		Fraction minLambda= new Fraction(999999999/0.00000000000001);
		int index = -1;
		int MinIndex=0;
		for(int i = 0; i < this.bQuer.getVec().length; i++){
//			if(isBlandOn){
				if(minLambda.doubleValue() > this.bQuer.get(i).doubleValue() / d.get(i).doubleValue()  && d.get(i).doubleValue() > 0){
					minLambda = this.bQuer.get(i).divide( d.get(i) );
					index = i;
					MinIndex = basis[i];
				}
				else if(minLambda.doubleValue()==this.bQuer.get(i).doubleValue()/d.get(i).doubleValue() && d.get(i).doubleValue() > 0){//Kleinster-Variablen-Index-Regel
					if(MinIndex > basis[i]){
						MinIndex = basis[i];
						index = i;
					}
				}
//			} else {
//				if(minLambda.doubleValue() > this.bQuer.get(i).doubleValue() / d.get(i).doubleValue()  && d.get(i).doubleValue() > 0){
//					minLambda = this.bQuer.get(i).divide( d.get(i) );
//					index = i;
//					MinIndex = basis[i];
//				}
//			}
				
			
		}
		return new Tupel<Integer, Fraction>(index, minLambda);
	}
	
	public void WRETA(int indexPrice, int indexChuzr, Vector d){
		if( d== null)System.out.println("null");
		Fraction[] eta = new Fraction[d.getVec().length];
		Fraction eintragStelleChuzr = d.get(indexChuzr);
		for(int i = 0; i < d.getVec().length; i++){
			if(i == indexChuzr)
				eta[i] = new Fraction(1).divide(eintragStelleChuzr);
			else
				eta[i] = d.get(i).negate().divide(eintragStelleChuzr);
		}
//		Vector et = new Vector(eta);
		basisInverse.multiplyEta(new Vector(eta), indexChuzr);
		bQuer = basisInverse.multiplyMatrixVektor(b);
		int basisTmp = basis[indexChuzr];
		basis[indexChuzr] = nichtbasis[indexPrice];
		nichtbasis[indexPrice] = basisTmp;
		if(showComments){
			System.out.println("WRETA");
			System.out.println(basisInverse.toString());
			System.out.println(bQuer.toString());
			System.out.println("Basis:");
			for(int i = 0; i < basis.length; i++){
				System.out.println(basis[i]);
			}
		}
	}
	
	public Vector getSchattenpreise() {
		return schattenpreise;
	}
	
	
	public String BasisToString(){
		String bas="Basis: ";
		String nichtbas="Nichtbasis: ";
		for(int i= 0 ; i<basis.length ;i++){
			bas += "; "+basis[i];
		}
		for( int j=0 ; j<nichtbasis.length ; j++)
			nichtbas += "; "+nichtbasis[j];
		return bas +"\n"+nichtbas;
	}


	public Fraction getOptimum(){
		Fraction optimum = new Fraction(0);
		for(int i = 0; i < basis.length; i++){
			if(originalCostFunction.get(basis[i])==null||bQuer.get(i)==null){
				
			} else {
				optimum = optimum.add( originalCostFunction.get(basis[i]).multiply(bQuer.get(i)) );
			}
		}
		return optimum;
	}
	

	public int[] getBasis() {
		return basis;
	}


	public Vector getbQuer() {
		return bQuer;
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Output out = null;
		String dataName = "small2.lp";
		InputLP in = new InputLP();
		LP lin = in.readLP("src/InputData/"+dataName);
//			System.out.println(in.getM().toString());
//			System.out.println(in.getRn());
		Simplex simplex = new Simplex(lin);
		simplex.calculateOptimum(false);
		
//		if(simplex.isPerfect)
		String status = "";
		if(simplex.isPerfect){
			status = "Optimal";
			out = new Output(in.getCn(), simplex.bQuer, simplex.basis, simplex.getOptimum(),status, "src/OutputData/Lsg"+dataName);

		} else if (simplex.istLeer){
			status = "Unbounded";
			out = new Output(status, "src/OutputData/Lsg"+dataName);

		} else if(simplex.istUnbeschraenkt){
			status = "Infeasable";
			out = new Output(status, "src/OutputData/Lsg"+dataName);

		}
//			System.out.println(simplex.bQuer);
//			for(int i = 0; i < simplex.basis.length; i++){
//				System.out.println(simplex.basis[i]);
//			}
//			for()
	}

}
