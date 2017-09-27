
public class Deck {

// variables for symbols
	private String heart= "\uu2300";
	private String spade= "\uu2660";
	private String dimond= "\uu2666";
	private String club= "\uu2663";
	private String suits[]= {heart, spade,dimond,club};
	
	//array of 52 cards
	private Card deckArray[]= new Card[51];
	
	
	Deck(){
		for(int i=0;i<=suits.length-1;i++) {
			for(int count=1; i<=14;count++)
			{
			 Card ob = new Card(suits[i],count);
			 deckArray[i*14+count-1]= ob;
			}
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
//Deal function to deal the hand to each player
    public void deal() {
    	
    	
    }

}