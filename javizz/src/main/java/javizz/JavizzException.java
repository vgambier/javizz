package javizz;

/**
 * Custom exception that is thrown when a problem specific to the Javizz project occurs
 * 
 * @author Victor Gambier
 *
 */

public class JavizzException extends Exception {

	private static final long serialVersionUID = 1L;

	public JavizzException() {
		super();
	}

	public JavizzException(String motif) {
		super(motif);
	}
}
