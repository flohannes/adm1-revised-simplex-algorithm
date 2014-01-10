package Datenstrukturen;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Fraction implements Comparable<Fraction>{

	private BigInteger numerator;
	private BigInteger denominator;

	public final static Fraction ZERO = new Fraction(BigInteger.ZERO, BigInteger.ONE);
	public final static Fraction ONE = new Fraction(BigInteger.ONE, BigInteger.ONE);
	
	public Fraction(BigInteger numerator, BigInteger denominator)
	  {
	    if(numerator == null)
	      throw new IllegalArgumentException("Numerator is null");
	    if(denominator == null)
	      throw new IllegalArgumentException("Denominator is null");
	    if(denominator.equals(BigInteger.ZERO))
	      throw new ArithmeticException("Divide by zero.");

	    //only numerator should be negative.
	    if(denominator.signum() < 0)
	    {
	      numerator = numerator.negate();
	      denominator = denominator.negate();
	    }

	    //create a reduced fraction
	    BigInteger gcd = numerator.gcd(denominator);
	    this.numerator = numerator.divide(gcd);
	    this.denominator = denominator.divide(gcd);
	}
	
	public Fraction(BigInteger numerator)
	  {
	    this(numerator, BigInteger.ONE);
	  }
	
	public Fraction(double d)
	  {
	    if(Double.isInfinite(d))
	      throw new IllegalArgumentException("double val is infinite");
	    if(Double.isNaN(d))
	      throw new IllegalArgumentException("double val is NaN");

	    //special case - math below won't work right for 0.0 or -0.0
	    if(d == 0)
	    {
	      numerator = BigInteger.ZERO;
	      denominator = BigInteger.ONE;
	      return;
	    }

	    final long bits = Double.doubleToLongBits(d);
	    final int sign = (int)(bits >> 63) & 0x1;
	    final int exponent = ((int)(bits >> 52) & 0x7ff) - 0x3ff;
	    final long mantissa = bits & 0xfffffffffffffL;

	    //number is (-1)^sign * 2^(exponent) * 1.mantissa
	    BigInteger tmpNumerator = BigInteger.valueOf(sign==0 ? 1 : -1);
	    BigInteger tmpDenominator = BigInteger.ONE;

	    //use shortcut: 2^x == 1 << x.  if x is negative, shift the denominator
	    if(exponent >= 0)
	      tmpNumerator = tmpNumerator.multiply(BigInteger.ONE.shiftLeft(exponent));
	    else
	      tmpDenominator = tmpDenominator.multiply(BigInteger.ONE.shiftLeft(-exponent));

	    //1.mantissa == 1 + mantissa/2^52 == (2^52 + mantissa)/2^52
	    tmpDenominator = tmpDenominator.multiply(BigInteger.valueOf(0x10000000000000L));
	    tmpNumerator = tmpNumerator.multiply(BigInteger.valueOf(0x10000000000000L + mantissa));

	    BigInteger gcd = tmpNumerator.gcd(tmpDenominator);
	    numerator = tmpNumerator.divide(gcd);
	    denominator = tmpDenominator.divide(gcd);
	  }
	
	
	public Fraction add(Fraction f)
	  {
	    if(f == null)
	      throw new IllegalArgumentException("Null argument");

	    //n1/d1 + n2/d2 = (n1*d2 + d1*n2)/(d1*d2) 
	    return new Fraction(numerator.multiply(f.denominator).add(denominator.multiply(f.numerator)),
	                           denominator.multiply(f.denominator));
	  }
	
	public Fraction subtract(Fraction f)
	  {
	    if(f == null)
	      throw new IllegalArgumentException("Null argument");

	    return new Fraction(numerator.multiply(f.denominator).subtract(denominator.multiply(f.numerator)),
	                           denominator.multiply(f.denominator));
	  }
	
	public Fraction multiply(Fraction f)
	  {
	    if(f == null)
	      throw new IllegalArgumentException("Null argument");

	    return new Fraction(numerator.multiply(f.numerator), denominator.multiply(f.denominator));
	  }
	
	public Fraction divide(Fraction f)
	  {
	    if(f == null)
	      throw new IllegalArgumentException("Null argument");

	    if(f.numerator.equals(BigInteger.ZERO))
	      throw new ArithmeticException("Divide by zero");

	    return new Fraction(numerator.multiply(f.denominator), denominator.multiply(f.numerator));
	  }
	
	 public Fraction negate()
	  {
	    return new Fraction(numerator.negate(), denominator);
	  }
	 
	 public String toString()
	  {
	    return numerator.toString() + "/" + denominator.toString();
	  }
	 
	 public boolean equals(Object o)
	  {
	    if(!(o instanceof Fraction))
	      return false;

	    Fraction f = (Fraction)o;
	    return numerator.equals(f.numerator) && denominator.equals(f.denominator);
	  }
	
	 public int signum()
	  {
	    return numerator.signum();
	  }
	 
	@Override
	public int compareTo(Fraction f)
	  {
	    if(f == null)
	      throw new IllegalArgumentException("Null argument");

	    //easy case: this and f have different signs
	    if(signum() != f.signum())
	      return signum() - f.signum();

	    //next easy case: this and f have the same denominator
	    if(denominator.equals(f.denominator))
	      return numerator.compareTo(f.numerator);

	    //not an easy case, so first make the denominators equal then compare the numerators 
	    return numerator.multiply(f.denominator).compareTo(denominator.multiply(f.numerator));
	  }

	  /**
	   * Returns the smaller of this and f.
	   */
	  public Fraction min(Fraction f)
	  {
	    if(f == null)
	      throw new IllegalArgumentException("Null argument");

	    return (this.compareTo(f) <= 0 ? this : f);
	  }

	  /**
	   * Returns the maximum of this and f.
	   */
	  public Fraction max(Fraction f)
	  {
	    if(f == null)
	      throw new IllegalArgumentException("Null argument");

	    return (this.compareTo(f) >= 0 ? this : f);
	  }

	  public double doubleValue() { 
		  return toBigDecimal(18).doubleValue(); 
      }
	  public BigDecimal toBigDecimal(int precision)
	  {
	    return new BigDecimal(numerator).divide(new BigDecimal(denominator), new MathContext(precision, RoundingMode.HALF_EVEN));
	  }
	
}
