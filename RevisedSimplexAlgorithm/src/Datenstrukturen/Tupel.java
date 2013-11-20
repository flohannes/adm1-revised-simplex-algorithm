package Datenstrukturen;

public class Tupel<A, B> {

	private A num;
//	private int column;
	private B entry;
	
	/**
	 * @param row
	 * @param column
	 * @param entry
	 */
	public Tupel(A num, B entry) {
		this.num = num ;
		this.entry = entry;
	}
	
	public Tupel(){
		
	}

	

	public B getEntry() {
		return entry;
	}

	public void setEntry(B entry) {
		this.entry = entry;
	}



	public A getNum() {
		return num;
	}



	public void setNum(A num) {
		this.num = num;
	}
	
	public Tupel<A,B> clone(){
		Tupel<A,B> tupel = new Tupel<A,B>();
		tupel.setEntry(this.getEntry());
		tupel.setNum(this.getNum());
		return tupel;
	}
	
}
