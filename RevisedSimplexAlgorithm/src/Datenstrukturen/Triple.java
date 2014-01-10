package Datenstrukturen;

public class Triple{

	private int row;	//sonst: 0 für nichtbasis, 1 für basis
	private int column;	//sonst: row
	private Fraction entry;
	
	/**
	 * @param row
	 * @param column
	 * @param entry
	 */
	public Triple(int row, int column, Fraction entry) {
		this.row = row ;
		this.column = column;
		this.entry = entry;
	}
	
	public Triple(int row, int column, double entry) {
		this.row = row ;
		this.column = column;
		this.entry = new Fraction(entry);
//		this.entry = this.entry.doubleToFraction(entry);
	}
	
	public Triple(){
		
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public Fraction getEntry() {
		return entry;
	}

	public void setEntry(Fraction entry) {
		this.entry = entry;
	}
	
	public void setEntry(double entry) {
		this.entry = new Fraction(entry);
//		this.entry.doubleToFraction(entry);
	}
	
	public int getNum(){
		return this.getColumn();
	}
	
	public boolean isBasis(){
		if(this.row == 0)
			return false;
		else 
			return true;
	}
	
	public void setBasis(boolean b){
		if(b)
			this.row = 1;
		else
			this.row = 0;
	}

	
	public Triple clone(){
		Triple t = new Triple(this.getRow(), this.getColumn(), this.getEntry());
		return t;
	}
	
}
