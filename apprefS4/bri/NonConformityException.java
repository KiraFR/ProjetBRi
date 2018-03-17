package bri;

public class NonConformityException extends Exception {
	
	String msg;
	
	public NonConformityException(String msg){
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		return this.msg;
	}
}
