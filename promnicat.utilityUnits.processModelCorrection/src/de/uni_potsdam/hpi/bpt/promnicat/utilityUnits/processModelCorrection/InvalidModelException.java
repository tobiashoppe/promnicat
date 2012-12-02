package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection;

/**
 * Exception that indicates the model has an unexpected state
 * @author Christian Kieschnick
 *
 */
public class InvalidModelException extends RuntimeException {
	private static final long serialVersionUID = -6493730508811651527L;

	public InvalidModelException(String message) {
		super(message);
	}
	
	public InvalidModelException(String message, Exception exception){
		super(message, exception);
	}

}
