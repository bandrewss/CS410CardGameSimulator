
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
		System.out.println("am here");
		
		for(int i =0; i<= handArray.length-1;i++) {
			if(handArray[i]==null) {
				
			}
			else {
			String showCard=handArray[i].toString();
			System.out.println(showCard);
			}
		}
	}
	
	public String showCard(int postion) {
		String singleCard=handArray[postion].toString();
		//System.out.println(singleCard);
		return singleCard;
	}
	
	public int findCardIndex(Card mycard) {
		int index=(Integer) null;
		for(int i=0;i<handArray.length-1;i++) {
			if(handArray[i].equals(mycard)) {
				index=i;
			}
		}
		
		return index;
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
			if(checkCard.toString().equals(testCard.toString())) {
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
			if(card.getSuit() == suit) {
				found = true;
				break;
			}
		}
		
		return found;
	}
	
	public int getHandSize() {
		return handSize;
	}
	

}