/*
 * Matthew Ladin
 * Card Object;
 */


public class Card {
	final private char HEART= '\u2665';
	final private char SPADE= '\u2660';
	final private char DIAMOND= '\u2666';
	final private char CLUB= '\u2663';
	
	public char suit;
	public int number ;

	/*
	 * 
	 */
	public Card( char suit, int number) {
		this.suit=suit;
		this.number=number;
	}
	
	/*
	 * 
	 * 
	 */
	public char getSuit () {	
		return suit;
	}
	/*
	 * 
	 */
	public void setSuit(char diffSuit){
		this.suit=diffSuit;	
	}
	
	/*
	 * 
	 */
	public int getNum () {	
		return number;
	}
	
	/*
	 * 
	 */
	public void setNum(int diffNum){
		this.number=diffNum;	
	}
	
 public String toString() {
	return String.format("%c%s", suit, number);
 }

}
