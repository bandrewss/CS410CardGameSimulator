
public class Hand {

	
	private Card handArray[]= new Card[17];
	Card play;
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
	public void getCard(Card receivedCard) {
		for(int i = 0;i<=handArray.length-1;i++ )
			{
			 if(handArray[i]==null) {
				 handArray[i]= receivedCard;
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