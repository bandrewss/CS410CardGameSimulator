
public class Hand {
	final private char HEART= '\u2665';
	final private char SPADE= '\u2660';
	final private char DIAMOND= '\u2666';
	final private char CLUB= '\u2663';
	
	int handSize = 0;

	
	private Card handArray[]= new Card[17];
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
	public void getCard(char suit, int number) {
		for(int i = 0;i<=handArray.length-1;i++ )
			{
			 if(handArray[i]==null) {
				 //handArray[i]= receivedCard;
				 break;
			 }
				
			}
				
	}// End getCard
	
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
	
	public String showCard(int postion) {
		String singleCard=handArray[postion].toString();
		//System.out.println(singleCard);
		return singleCard;
	}
	
}