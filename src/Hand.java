
public class Hand {
	final private char HEART= '\u2665';
	final private char SPADE= '\u2660';
	final private char DIAMOND= '\u2666';
	final private char CLUB= '\u2663';
	final private int MAX_HAND_SIZE = 17;
	
	private int handSize = 0;
	

	
	private Card handArray[]= new Card[MAX_HAND_SIZE];
	Card play;
	
	//public?
	Hand(){
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
	 * playCard Method
	 * Returns Card Object
	 * Parameter integer 
	 *
	 * 
	 */
	public String playCard(Card card) {
		removeCard(card);
		return  showCard(card);		
	}//End playCard
	
	/*
	 * If the given card is in the hand, it is set to null.
	 * Parameter: A card to find in the hand.
	 * Return: If the card was removed.
	 */
	public boolean removeCard(Card card) {
		boolean containedCard = false;
		
		for(Card testCard:handArray) {
			if(card.toString().equals(testCard.toString())) {
				containedCard = true;
				testCard = null;
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
	
	public String showCard(Card card) {
		boolean found = false;
		
		for(Card c:handArray) {
			if( c.toString().equals(card.toString())) {
				found = true;
				break;
			}
			
		}
		
		return found ? card.toString() : null;
	}
	
	/*
	 * Returns true if the players hand is full
	 */
	public boolean isFull() {
		return !(handSize < MAX_HAND_SIZE);
		
	}
	
	public boolean containsSuit(char suit) {
		boolean foundSuit = false;
		
		for(Card card:handArray) {
			if( (foundSuit = card.getSuit() == suit) ) {
				break;
			}
		}
		
		return foundSuit;
	}
	
	/*
	 * Returns true if the given card exists in the hand
	 * Parameter: a card to validate.
	 */
	public boolean containsCard(Card checkCard) {
		boolean found = false;
		
		for(Card testCard:handArray) {
			if(checkCard.toString().equals(testCard.toString())) {
				found = true;
				break;
			}
		}
		
		return found;
	}
}