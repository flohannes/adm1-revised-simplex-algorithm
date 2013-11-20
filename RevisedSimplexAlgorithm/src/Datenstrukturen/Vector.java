package Datenstrukturen;

import java.util.ArrayList;

public class Vector {

	private double[] vec;
	
	private int size;
	
	public Vector(int size){
		vec = new double[size];
	}
	
	public Vector(double[] d){
		vec = d;
	}
	
	public void negateBi(int i){
		vec[i] = -vec[i];
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
	
	public double get(int index){
		return vec[index];
	}

	@Override
	public String toString() {
		String out = "";
		for( Double t : vec){
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
	
	public double[] getVec() {
		return vec;
	}
	
	

	public void setVec(double[] vec) {
		this.vec = vec;
	}
	
	public void deleteEntry(int index){
		double[] ne = new double[vec.length-1];
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
		double[] newV = new double[this.getVec().length];
		for(int i = 0; i < newV.length; i++){
			newV[i] = this.getVec()[i];
		}
		Vector v = new Vector(newV);
		return v;
	}

}
