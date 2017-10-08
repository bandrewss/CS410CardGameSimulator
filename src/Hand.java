/*
 * Matthew Ladin, Waheed Khan,  Ben Andrews
 * Hand Object;
 */
public class Hand {
	/*
	 * Unicode for Suits.
	 */
	final private char HEART= '\u2665';
	final private char SPADE= '\u2660';
	final private char DIAMOND= '\u2666';
	final private char CLUB= '\u2663';
	
	final private int MAX_HAND_SIZE = 17;
	
	private int handSize = 0;
	
	private Card handArray[]= new Card[MAX_HAND_SIZE];
	Card play;
	
	/*
	 * Create the Base Hand object (Hand is an array of Cards objects)
	 */
	public Hand(){
		for(int i =0;i<=handArray.length-1;i++) 
		{
			handArray[i] =null;
		}
		
	}
	
	/*
	 * getCard Method
	 * Returns null
	 * Parameter Card object
	 * task card objects and add it to the closes open spot
	 * in the array of the user
	 */
	public void recieveCard(char suit, int number) {
		if(handSize < MAX_HAND_SIZE)
		if(suit == SPADE || suit == CLUB || suit == DIAMOND || suit == HEART)
		if(number >= 1 && number <= 14) {
			handArray[handSize] = new Card(suit, number);
			++handSize;
		}
	}
	
	/*
	 * Bubble sort hand.
	 *  -could instead sort cards as they come in
	 */
	public void sortHand() {
		Card temp;
		
		for(int i = 0; i < handArray.length; ++i) {
			for(int j = 0; j < handArray.length -1 -i; j++) {
				if(handArray[j].getRank() > handArray[j +1].getRank()) {
					temp = handArray[j];
					handArray[j] = handArray[j+1];
					handArray[j+1] = temp;
				}
			}
		}
	}
	
	/*
	 * 
	 * playCard Method: Send the card if Card is available in the Hand array
	 * Returns Card Object
	 * Parameter integer 
	 */
	public Card playCard(int i) {
		if(handArray[i]==null||i>handArray.length) {
			System.out.println("no card avalible");
			return null;
		}
		else {
		handArray[i]=  play;
		handArray[i]= null;
		return play;
		}
		
	}//End playCard
	
	/*
	 * If the given card is in the hand, it is set to null.
	 * Parameter: A card to find in the hand.
	 * Return: If the card was removed.
	 */
	public boolean removeCard(Card card) {
		boolean containedCard = false;
		
		for(int i = 0; i < handArray.length; ++i) {
			if(handArray[i] != null && card.toString().equals(handArray[i].toString())) {
				containedCard = true;
				handArray[i] = null;
				break;
			}
		}
		
		return containedCard;
	}
	
	
	public void showHand() {
		for(int i =0; i<= handArray.length-1;i++) {
			if(handArray[i]==null) {
				
			}
			else {
			String showCard=handArray[i].toString();
			System.out.println(showCard);
			}
		}
	}
	
	/*
	 * Returns a Sting of the Card Object form the Hand Array
	 * Parameter int location of the card
	 */
	public String showCard(int postion) {
		String singleCard=handArray[postion].toString();
		return singleCard;
	}

	
	/*
	 * Returns true if the players hand is full
	 */
	public boolean isFull() {
		return !(handSize < MAX_HAND_SIZE);
		
	}
	
	/*
	 * Returns true if the given card exists in the hand
	 * Parameter: a card to validate.
	 */
	public boolean containsCard(Card checkCard) {
		boolean found = false;
		
		for(Card testCard:handArray) {
		if(testCard != null && checkCard.toString().equals(testCard.toString())) {
				found = true;
				break;
			}
		}
		
		return found;
	}
	
	/*
	 * Returns true if a card with the given suit exists in the hand.
	 */
	public boolean containsSuit(char suit) {
		boolean found = false;
		
		for(Card card:handArray) {
			if(card != null && card.getSuit() == suit) {
				found = true;
				break;
			}
		}
		
		return found;
	}
	
	/*
	 * Clears the hand of all cards by setting the whole hand array to null.
	 * Returns:
	 */
	public void clearHand() {
		for(int i = 0; i < handArray.length; ++i) {
			handArray[i] = null;
		}
		
		handSize = 0;
	}
	
	/*
	 * Get the hand size
	 * Returns Int
	 */
	public int getHandSize() {
		return handSize;
	}
	
	/*
	 * Gets a Card object from the hand Array at index i
	 * Parameters: Int
	 * Returns: Card Object
	 */
	public Card getCardByIndex(int i) {
		return handArray[i];
	}
	

}