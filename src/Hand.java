
public class Hand {

	
	private Card handArray[]= new Card[17];
	Card play;
	Hand(){
		for(int i =0;i<17;i++) 
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
		for(int i = 0;i<17;i++ )
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
		handArray[i]=  play;
		handArray[i]= null;
		return play;
		
	}//End playCard
	
	public void showHand() {
		for(int i =0; i<= handArray.length;i++) {
			System.out.println(handArray[i].toString());
		}
	}
	
	
}