package javizz;

public class MethodLink {

	private MethodModel methodModel;
	private String path; // uniquely defines the method within the file system

	public MethodLink(MethodModel methodModel) {
		this.methodModel = methodModel;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
