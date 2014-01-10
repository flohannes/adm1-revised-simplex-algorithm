package Datenstrukturen;

import java.util.ArrayList;

public class Vector {

	private Fraction[] vec;
	
	private int size;
	
	public Vector(int size){
		vec = new Fraction[size];
	}
	
	public Vector(Fraction[] d){
		vec = d;
	}
	
	public void negateBi(int i){
		vec[i] = vec[i].negate();
	}
	
//	public void addEntry(int isBasis,int index, double value) throws IllegalArgumentException{
//		
//		if(vec.isEmpty()){
//			vec.add(new Triple(isBasis,index, value));
//		}
//		else if(vec.get(vec.size()-1).getColumn() < index){
//			vec.add(new Triple(isBasis, index, value));
//		}
//		else{
//			int i;
//			for( i=0 ; i< vec.size() ; i++){
//				if( vec.get(i).getColumn() == index)
//					throw new IllegalArgumentException("Element existiert bereits!!!");
//				if( vec.get(i).getColumn()> index)
//					vec.add(index, new Triple(isBasis,index, value));
//			}
//		}
//	}
	
	/**
	 * Laenge der ArrayList
	 * @return
	 */
	public int getLength(){
		return vec.length;
	}
	
	public Fraction get(int index){
		return vec[index];
	}

	@Override
	public String toString() {
		String out = "";
		for( Fraction t : vec){
			out += ""+t+ " ; ";
		}
		return "Vector: \n"+out;
	}

	/**
	 * Tatsaechliche Länge des Vektors, d.h. die 0-Eintraege mitgezaehlt
	 * @return
	 */
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public Fraction[] getVec() {
		return vec;
	}
	
	

	public void setVec(Fraction[] vec) {
		this.vec = vec;
	}
	
	public void deleteEntry(int index){
		Fraction[] ne = new Fraction[vec.length-1];
		int count=0;
		for(int i=0;i< vec.length ;i++){
			if(i != index){
				ne[count]=vec[i];
				count++;
			}
		}
		this.vec = ne;
	}
	
	public Vector clone(){
		Fraction[] newV = new Fraction[this.getVec().length];
		for(int i = 0; i < newV.length; i++){
			newV[i] = this.getVec()[i];
		}
		Vector v = new Vector(newV);
		return v;
	}

}
