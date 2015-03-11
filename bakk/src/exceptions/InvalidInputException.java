package exceptions;

public class InvalidInputException extends Exception {

	/**
	 * id of the exception
	 */
	private static final long serialVersionUID = -4346322581772139027L;
	
	/**
	 * creates a new Exception
	 * @param string error message
	 */
	public InvalidInputException(String string) {
		super(string);
	}
}
