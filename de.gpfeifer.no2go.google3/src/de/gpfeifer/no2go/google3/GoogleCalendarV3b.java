package de.gpfeifer.no2go.google3;

import java.io.IOException;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

/**
 * @author gpfeifer@google.com (Your Name Here)
 * 
 */
public class GoogleCalendarV3b {

	private static final String CREDENTIAL_STORE_FILE_NAME = "no2go-credential.json";
	private static final String CLIENT_ID = "299902296118-6qnqejp58qkkku2pfafsjeopcl2t7sf0.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "ZfecPRZdCdIWRJCgE_FPoKn1";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		run();

	}

	public static boolean  run() throws IOException {
		// TODO Auto-generated method stub

		// If you need a local proxy (eg for getting out from behind a corporate
		// firewall)
		// final Proxy proxy = new Proxy(Proxy.Type.HTTP, new
		// InetSocketAddress(PROXY_HOST,
		// PROXY_PORT));
		final HttpTransport httpTransport = new NetHttpTransport.Builder().build();
		final JsonFactory jsonFactory = new JacksonFactory();
		final java.io.File credentialFile = new java.io.File(System.getProperty("user.home"), "/" + CREDENTIAL_STORE_FILE_NAME);
		final CredentialStore credentialStore = new FileCredentialStore(credentialFile, jsonFactory);

		AuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET,
				Arrays.asList(CalendarScopes.CALENDAR))
		// Need "offline" and "force" in order to ensure we get a URL that will
		// return a
		// refresh_token too.
				.setAccessType("offline").setApprovalPrompt("force").setCredentialStore(credentialStore).build();

		VerificationCodeReceiver recserver = new LocalServerReceiver();


		// Retrieves a valid credential. It refreshes the access_token if
		// required
		// and prompts the user (opening a browser page pointing at the
		// appropriate auth page) for auth
		// token if required.
		// and then persists the returned credentials to our CredentialStore
		final AuthorizationCodeInstalledApp app = new AuthorizationCodeInstalledApp(flow, recserver);

		final Credential credential = app.authorize("gregor.pfeifer.gp@gmail.com");
		System.out.println(flow);
		System.out.println(credential);

		// set up global Calendar instance
		Calendar client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName("no2go").build();
		Events events = client.events().list("primary").execute();

		while (true) {
			for (Event event : events.getItems()) {
				System.out.println(event.getSummary());
			}
			String pageToken = events.getNextPageToken();
			if (pageToken != null && !pageToken.isEmpty()) {
				events = client.events().list("primary").setPageToken(pageToken).execute();
			} else {
				break;
			}
		}
		return !events.isEmpty();
	}

}
