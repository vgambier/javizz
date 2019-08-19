package javizz;

/**
 * 
 * @author Victor Gambier
 *
 */

public class ModelException extends Exception {

	private static final long serialVersionUID = 1L;

	public ModelException() {
		super();
	}

	public ModelException(String motif) {
		super(motif);
	}
}
