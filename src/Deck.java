
public class Deck {

// variables for symbols
	final private char HEART= '\u2665';
	final private char SPADE= '\u2660';
	final private char DIAMOND= '\u2666';
	final private char CLUB= '\u2663';
	
	final private char suits[]= {HEART, SPADE, DIAMOND, CLUB};
	
	//array of 52 cards
	private Card deckArray[]= new Card[52];
	
	//public?
	Deck(){
		int index=0;
		for(int i=0;i<=suits.length-1;i++) {
			//System.out.println("1Suit");
			for(int count=2; count<=14;count++)
			{ 
			//	System.out.println(index);
			 Card ob = new Card(suits[i],count);
			 deckArray[index]= ob;
			 index++;
			// System.out.println(count);
			}
			//System.out.println("SuitEnd");
		}
	}//End of Deck
	
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
}