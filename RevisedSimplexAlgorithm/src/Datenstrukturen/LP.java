package Datenstrukturen;

import java.util.ArrayList;

public class LP {

	private Matrix m;	
//	private ArrayList<Tupel<String, String>> ec;	//Is row equation or inequality? (eq?, name)
	private Vector c;	//objective function /cost
	private Vector b;	//RHS
	private boolean isMax;
	private ArrayList<Tupel<Integer, Double>> upperBound;
	private ArrayList<Tupel<Integer, Double>> lowerBound;
	private int[] basis;
	private int[] nichtbasis;
	
	private int indexOfKuenstlicheVar;
	
	public LP(Matrix m,	ArrayList<Tupel<String, String>> ec, ArrayList<String> rn, Vector c, Vector b,
			boolean isMax, ArrayList<Tupel<Integer, Double>> upperBound, ArrayList<Tupel<Integer, Double>> lowerBound) {
		this.m = m;
//		this.ec = ec;
		this.c = c;
		this.b = b;
		this.isMax = isMax;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
		this.basis = new int[m.getRowNum()];
		
		indexOfKuenstlicheVar = m.getColNum();
		
		for(Tupel<String, String> eq : ec){
			if(eq.getNum().equals("L")){//positive Schlupfvariablen (<= )
				m.addColumn();
				indexOfKuenstlicheVar++;
				m.addEntry(rn.indexOf(eq.getEntry()), m.getColNum()-1, 1);
				for(int i = 0; i < b.getVec().length; i++){//Vektor b durchsuchen nach passenden eintrag
					if(i == rn.indexOf(eq.getEntry())){
						if(b.getVec()[i] > 0){//schlupfvariable als basis
							basis[rn.indexOf(eq.getEntry())] = m.getColNum()-1;
							break;
						}else if(b.getVec()[i] < 0){//gesamte Gleichung negieren
							m.negateRow(rn.indexOf(eq.getEntry()));
							b.negateBi(rn.indexOf(eq.getEntry()));
							break;
						}
					}
					else if(i > rn.indexOf(eq.getEntry())){//rechte seite ist Null --> Schlupf als basis
						basis[rn.indexOf(eq.getEntry())] = m.getColNum()-1;
						break;
					}
				}
			} else if(eq.getNum().equals("G")){//negative Schlupfvariablen (>= )
				m.addColumn();
				indexOfKuenstlicheVar++;
				m.addEntry(rn.indexOf(eq.getEntry()), m.getColNum()-1, -1);
				for(int i = 0; i < b.getVec().length; i++){
					if(i == rn.indexOf(eq.getEntry())){
						if(b.getVec()[i] < 0){
							basis[rn.indexOf(eq.getEntry())] = m.getColNum()-1;
							m.negateRow(rn.indexOf(eq.getEntry()));
							b.negateBi(rn.indexOf(eq.getEntry()));
							break;
						}else if(b.getVec()[i] > 0){
							break;
						}
					}
					else if(i > rn.indexOf(eq.getEntry())){
						basis[rn.indexOf(eq.getEntry())] = m.getColNum()-1;
						break;
					}
				}
			} else if(eq.getNum().equals("E")){//Gleichungen
				for(int i = 0; i < b.getVec().length; i++){
					if(i == rn.indexOf(eq.getEntry()) && b.getVec()[i] < 0){
						m.negateRow(rn.indexOf(eq.getEntry()));
						b.negateBi(rn.indexOf(eq.getEntry()));
						break;
					}else if(i > rn.indexOf(eq.getEntry())){
						break;
					}
				}
			}
		}
		
		//falls wir noch keine Startbasis haben, werden hier die KünstlichenVar. erstellt
		for(int i = 0; i < basis.length; i++){
			if(basis[i] == 0){
				m.addColumn();
				m.addEntry(i, m.getColNum()-1, 1);
				basis[i]= m.getColNum()-1;
			}
		}
		int index=0;
		this.nichtbasis = new int[m.getColNum() - basis.length];
		for( int i=0 ; i< m.getColNum() ; i++){
			boolean counter = true;
			for( int j : basis){
				if(i==j){
					counter = false;
					break;
				}
			}
			if( counter){
				
				nichtbasis[index]= i;
				index++;
			}
				
		}
		
//		System.out.println(basisToString());
//		System.out.println("K-Index: "+ indexOfKuenstlicheVar);
	}
	
	 
	
	
	
	public Matrix getM() {
		return m;
	}





	public void setM(Matrix m) {
		this.m = m;
	}





	public Vector getC() {
		return c;
	}





	public void setC(Vector c) {
		this.c = c;
	}





	public Vector getB() {
		return b;
	}





	public void setB(Vector b) {
		this.b = b;
	}





	public boolean isMax() {
		return isMax;
	}





	public void setMax(boolean isMax) {
		this.isMax = isMax;
	}





	public ArrayList<Tupel<Integer, Double>> getUpperBound() {
		return upperBound;
	}





	public void setUpperBound(ArrayList<Tupel<Integer, Double>> upperBound) {
		this.upperBound = upperBound;
	}





	public ArrayList<Tupel<Integer, Double>> getLowerBound() {
		return lowerBound;
	}





	public void setLowerBound(ArrayList<Tupel<Integer, Double>> lowerBound) {
		this.lowerBound = lowerBound;
	}





	public int[] getBasis() {
		return basis;
	}





	public void setBasis(int[] basis) {
		this.basis = basis;
	}





	public int getIndexOfKuenstlicheVar() {
		return indexOfKuenstlicheVar;
	}





	public void setIndexOfKuenstlicheVar(int indexOfKuenstlicheVar) {
		this.indexOfKuenstlicheVar = indexOfKuenstlicheVar;
	}





	public int[] getNichtBasis() {
		return nichtbasis;
	}





	public void setNichtBasis(int[] nichtbasis) {
		this.nichtbasis = nichtbasis;
	}





	/**
	 * Gibt die Basis als String aus
	 * @return
	 */
	private String basisToString(){
		String res ="";
		for( int i : basis){
			res += " ; "+i;
		}
		return res;
	}

}
