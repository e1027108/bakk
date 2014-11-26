package exceptions;

public class InvalidInputException extends Exception {

	private static final long serialVersionUID = -4346322581772139027L;
	
	public InvalidInputException(String string) {
		super(string);
	}

	//TODO maybe take another constructor argument and specify error message further
}
