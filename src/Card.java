/*
 * Matthew Ladin
 * Card Object;
 */


public class Card {
	public String suit;
	public int number ;

	/*
	 * 
	 */
	public  Card( String suit, int number) {
		this.suit=suit;
		this.number=number;
	}
	
	/*
	 * 
	 * 
	 */
	public String getSuit () {	
		return suit;
	}
	/*
	 * 
	 */
	public void setSuit(String diffSuit){
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
	return (suit + number);
 }

}
