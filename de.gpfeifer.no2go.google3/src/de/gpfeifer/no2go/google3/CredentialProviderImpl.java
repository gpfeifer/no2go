package de.gpfeifer.no2go.google3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;

public class CredentialProviderImpl implements CredentialProvider {
	
	public CredentialProviderImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final String USER_CREDENTIAL_STORE_FILE_NAME = System.getProperty("user.home") + "/no2go-user-credential.json";
	private static final String NO2GO_SECRETS_FILE_NAME = "/no2go_secrets.json";
	private Thread thread;
	private LocalVerificationCodeReceiver codeReceiver;
	
	// TODO Synchronize setting ang getting?
	volatile private Credential credential;


	@Override
	public void getCredential(final CredentialConsumer consumer) throws IOException {
		getCredential(consumer, true);
	}

	
	public void getCredential(final CredentialConsumer consumer, boolean asynchron) throws IOException {
		// TODO Auto-generated method stub
		final HttpTransport httpTransport = new NetHttpTransport.Builder().build();
		final JsonFactory jsonFactory = new JacksonFactory();

		InputStream resourceAsStream = this.getClass().getResourceAsStream(NO2GO_SECRETS_FILE_NAME);
		if (resourceAsStream == null && consumer != null) {
			consumer.state(CredentialProviderState.APP_CREDENTIAL_NOT_EXISTS);
			return;
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,new InputStreamReader(resourceAsStream));
		if (consumer != null) {
			consumer.state(CredentialProviderState.APP_CREDENTIAL_EXISTS);
		}
		final File credentialFile = new java.io.File(USER_CREDENTIAL_STORE_FILE_NAME);
		if (credentialFile.exists()) {
			if (consumer != null) {
				consumer.state(CredentialProviderState.USER_CREDENTIAL_EXISTS);
			}
		} else {
			if (consumer != null) {
				consumer.state(CredentialProviderState.USER_CREDENTIAL_NOT_EXISTS);
			}
		}
		
		final CredentialStore credentialStore = new FileCredentialStore(credentialFile, jsonFactory);

	    
		// Need "offline" and "force" in order to ensure we get a URL that will
		// return a refresh_token too.

		AuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, 
				jsonFactory,
				clientSecrets,
				Arrays.asList(CalendarScopes.CALENDAR))
				.setAccessType("offline").
//				setApprovalPrompt("force").
				setCredentialStore(credentialStore).build();

		codeReceiver = new LocalVerificationCodeReceiver(this, consumer);

		// Retrieves a valid credential. It refreshes the access_token if
		// requiredand prompts the user (opening a browser page pointing at the
		// appropriate auth page) for auth token if required.
		// and then persists the returned credentials to our CredentialStore
		final AuthorizationCodeInstalledApp app = new AuthorizationCodeInstalledApp(flow, codeReceiver);
		
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				Credential credential;
				try {
					credential = app.authorize("user");
					setCredential(credential);
					if (consumer != null) {
						consumer.credential(credential);
					}
				} catch (IOException e) {
				}
				endOfThread();

			}
		};

		if (asynchron) {
			thread = new Thread(runnable);
			thread.start();
		} else {
			runnable.run();
		}
		
	}

	private void endOfThread() {
		thread = null;
	}
	public void redirectUri(CredentialConsumer consumer, String uri) {
		if (consumer != null) {
			consumer.state(CredentialProviderState.REDIRECT_URI);
		}
	}

	public void waitForCode(CredentialConsumer consumer) {
		if (consumer != null) {
			consumer.state(CredentialProviderState.WAIT_FOR_CODE);
		}
	}

	@Override
	public void deleteCredential() {
		final File credentialFile = new java.io.File(USER_CREDENTIAL_STORE_FILE_NAME);
		credentialFile.delete();
	}
	
	public boolean credentialFileExits() {
		return new java.io.File(USER_CREDENTIAL_STORE_FILE_NAME).exists();
	}
	
	public Credential getCredential() throws IOException {
		System.out.println("C " + this);
		if (credential != null && credential.getExpiresInSeconds() > 60) {
			return credential;
		}
		credential = null;
		if (!credentialFileExits()) {
			// In case there is no credential file we can't get the credential immediately
			return null;
		}
		getCredential(null, false);
		return credential;
	}

	private void setCredential(Credential c) {
			credential = c;			
	}

	public void stop() {
		if (codeReceiver != null) {
			try {
				codeReceiver.stop();
		
			} catch (IOException e) {
				System.out.println(e);
			} finally {
				codeReceiver = null;
			}
		}
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}


	

}
