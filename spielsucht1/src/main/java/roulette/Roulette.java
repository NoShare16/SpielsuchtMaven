package roulette;




public class Roulette {
	
	 public static void main(String[] args) {
		 Logic l = new Logic();
		 View v = new View();
		 
		 
		 l.setView(v);
		 v.setLogic(l);
		 
	 }
}