package de.gpfeifer.no2go.google3;

import java.io.IOException;

import com.google.api.client.auth.oauth2.Credential;

public interface CredentialProvider {
	static final CredentialProvider  INSTANCE = new CredentialProviderImpl();
	
	Credential getCredential() throws IOException;
	
	void getCredential(CredentialConsumer consumer) throws IOException;
	
	void deleteCredential();

	void stop();

	boolean credentialFileExits();
}
