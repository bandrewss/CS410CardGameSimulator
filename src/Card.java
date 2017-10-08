/*
 * Matthew Ladin, Waheed Khan, Ben Andrews
 * Card Object;
 */


public class Card {
	/*
	 * Unicode for Suits.
	 */
	final private char HEART= '\u2665'; 
	final private char SPADE= '\u2660'; 
	final private char DIAMOND= '\u2666';
	final private char CLUB= '\u2663';
	
	/*
	 * Variables for Card Object
	 */
	private char suit;
	private int number;
	private int rank;
	
	/*
	 * Creates a card Object
	 * Parameters: char, int 
	 */
	public Card( char suit, int number) {
		this.suit=suit;
		this.number=number;
		rank = calculateRank();
	}
	/*
	 * Creates a card Object
	 * Parameters: String
	 * String must be inform of single char then number 
	 * Ex: "B23"
	 */
	public Card(String s) {
		this(s.charAt(0), Integer.parseInt(s.substring(1).trim()));
	}
	
	/*
	 * Sets the rank of the card based on its suit and number.
	 *  Used for sorting and settling ties.
	 *  Returns: the rank of the card
	 */
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
		
		return number + rankFactor;
	}
	
	/*
	 * Gets the Suit of the card
	 * Returns Char
	 */
	public char getSuit () {	
		return suit;
	}
	
	
	/*
	 * Gets the Rank of the card
	 * Returns Int
	 */
	public int getRank() {
		return this.rank;
	}
	
	/*
	 * Gets the number of the card
	 * Returns Int
	 */
	public int getNum () {	
		return number;
	}
	
	/*
	 * Returns: a string representation of the card.
	 */
	public String toString() {
		return String.format("%c%s", suit, number);
	}

}
