package de.gpfeifer.no2go.google3;

public enum CredentialProviderState {
	APP_CREDENTIAL_NOT_EXISTS,
	APP_CREDENTIAL_EXISTS,
	USER_CREDENTIAL_EXISTS,
	USER_CREDENTIAL_NOT_EXISTS,
	REDIRECT_URI, 
	WAIT_FOR_CODE, 

}
