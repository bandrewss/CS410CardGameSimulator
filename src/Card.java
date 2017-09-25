/*
 * Matthew Ladin
 * Card Object;
 */


public class Card {
	public char suit;
	public int number ;

	/*
	 * 
	 */
	public  Card( char suit, int number) {
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
	


}



