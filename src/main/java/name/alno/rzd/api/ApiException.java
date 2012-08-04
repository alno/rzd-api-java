package name.alno.rzd.api;

public class ApiException extends Exception {

	private static final long serialVersionUID = 1L;

	public ApiException( String message, Throwable cause ) {
		super( message, cause );
	}

	public ApiException( String message ) {
		super( message );
	}

	public ApiException( Throwable cause ) {
		super( cause );
	}

}
