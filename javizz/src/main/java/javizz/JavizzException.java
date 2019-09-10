package javizz;

/**
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
