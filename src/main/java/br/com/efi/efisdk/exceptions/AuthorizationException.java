package br.com.efi.efisdk.exceptions;

/** This class extends to Exception and is developed to deal with authenticate errors
 * @author Consultoria Técnica
 */

public class AuthorizationException extends Exception 
{
	private static final long serialVersionUID = 1L;

	public AuthorizationException() {
		super();
	}
	
	@Override
	public String getMessage() {
		return "Authorization Error: Client_id or Client_secret are wrong";
	}
}
