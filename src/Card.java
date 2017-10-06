/*
 * Matthew Ladin
 * Card Object;
 */


public class Card {
	final private char HEART= '\u2665';
	final private char SPADE= '\u2660';
	final private char DIAMOND= '\u2666';
	final private char CLUB= '\u2663';
	
	private char suit;
	private int number;
	private int rank;
	

	/*
	 * 
	 */
	public Card( char suit, int number) {
		this.suit=suit;
		this.number=number;
		rank = calculateRank();
	}
	
	public Card(String s) {
		this(s.charAt(0), Integer.parseInt(s.substring(1).trim()));
		
	}
	
	private int calculateRank() {
		int rankFactor = 0;
		
		if(suit == SPADE) {
			rankFactor = 39;
		}
		else if(suit == HEART) {
			rankFactor = 26;
		}
		else if(suit == DIAMOND) {
			rankFactor = 13;
		}
		
		System.out.println(rankFactor + number);
		return number + rankFactor;
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
	public int getRank() {
		return this.rank;
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
