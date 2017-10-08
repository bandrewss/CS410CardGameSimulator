/*
 * Matthew Ladin, Waheed Khan, Ben Andrews
 * Deck Object;
 */
public class Deck {

// variables for symbols
	final private char HEART= '\u2665';
	final private char SPADE= '\u2660';
	final private char DIAMOND= '\u2666';
	final private char CLUB= '\u2663';
	final private int DECK_SIZE = 52;
	
	final private char suits[]= {HEART, SPADE, DIAMOND, CLUB};
	
	//array of 52 cards
	private int cardCount = 0;
	private Card deckArray[];
	
	
	/*
	 * Creates Deck object and builds the deck.
	 */
	public Deck(){
		deckArray = new Card[DECK_SIZE];
		
		buildDeck();
	}//End of Deck
	
	
	/*
	 * Builds a new deck of 52 cards.
	 */
	public void buildDeck() {
		int index=0;
		for(int i=0;i<=suits.length-1;i++) {
			for(int count=2; count<=14;count++) { 
			 deckArray[index]= new Card(suits[i], count);
			 index++;
			 cardCount++;
			}
		}
	}
	
	/*
	 * Clears the whole deck by setting all of the cards to null.
	 */
	public void clearDeck() {
		for(int i = 0; i < deckArray.length; ++i) {
			deckArray[i] = null;
			cardCount = 0;
		}
	}
	
	/*
	 * Shuffles the existing deck.
	 */
    public void shuffle() {
    	for(int reshuffle = 0; reshuffle < 7;reshuffle++) {
    		for ( int i = deckArray.length-1; i > 0; i-- ) {
            	int rand = (int)(Math.random()*(i+1));
            	Card temp = deckArray[i];
            	deckArray[i] = deckArray[rand];
            	deckArray[rand] = temp;
        	}//End For loop
    		
    	}//End Reshuffle loop 
    }//End shuffle
    
    /*
     * Pops the top card of the deck.
     * Returns: the top card of the deck.
     */
    public Card pop() {
    	int cardIndex = cardCount -1;
    	Card returnCard = null;
    	
    	if(cardIndex >= 0) {
    		returnCard = deckArray[cardIndex];
    		deckArray[cardIndex] = null;
    		--cardCount;
    	}
    	
    	return returnCard;
    }
}










