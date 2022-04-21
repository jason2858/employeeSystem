package employeeSystem.com.website.system.exception;

public class SystemOutException extends Exception {
	private String message;

	public SystemOutException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}