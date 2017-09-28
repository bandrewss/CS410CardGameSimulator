
public class Hand {

	
	private Card handArray[]= new Card[17];
	Card play;
	Hand(){
		for(int i =0;i<17;i++) 
		{
			handArray[i] =null;
		}
		
	}
	public void getCard(Card receivedCard) {
		for(int i = 0;i<17;i++ )
			{
			 if(handArray[i]==null) {
				 handArray[i]= receivedCard;
				 break;
			 }
				
			}
				
	}
	
	public Card playCard(int i) {
		handArray[i]=  play;
		handArray[i]= null;
		return play;
		
	}
	
	
	
}