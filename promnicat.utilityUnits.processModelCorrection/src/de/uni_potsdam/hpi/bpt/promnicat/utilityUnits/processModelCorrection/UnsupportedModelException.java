package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection;

/**
 * Exception to indicate that the correction for given model is not possible
 * @author Christian Kieschnick
 */
public class UnsupportedModelException extends Exception {
	private static final long serialVersionUID = -6248291365942897292L;

	public UnsupportedModelException(String message) {
		super(message);
	}
	
	public UnsupportedModelException(String message, Exception e) {
		super(message, e);
	}

}
