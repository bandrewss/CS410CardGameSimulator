
public class Deck {

// variables for symbols
	private String heart= "\u2665";
	private String spade= "\u2660";
	private String dimond= "\u2666";
	private String club= "\u2663";
	private String suits[]= {heart, spade,dimond,club};
	
	//array of 52 cards
	private Card deckArray[]= new Card[52];
	
	
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
//Deal function to deal the hand to each player
    public void dealCard( Hand player) {
		for(int i =0; i<=deckArray.length-1;i++) {
			if(deckArray[i]!=null){
				
			 player.getCard(deckArray[i]);
			 deckArray[i] = null;
			
			}
		}
    	
    	
    }

}