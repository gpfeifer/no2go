package de.gpfeifer.no2go.google3;

import com.google.api.client.auth.oauth2.Credential;

public interface CredentialConsumer {
	
	void credential(Credential credential);
//	void state

	void state(CredentialProviderState noAppCredential);
	
	

}
