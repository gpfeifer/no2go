package de.gpfeifer.no2go.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.api.client.auth.oauth2.Credential;

import de.gpfeifer.no2go.google3.CredentialConsumer;
import de.gpfeifer.no2go.google3.CredentialProviderImpl;
import de.gpfeifer.no2go.google3.CredentialProviderState;

public class ManualCredentialProviderTest implements CredentialConsumer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new ManualCredentialProviderTest().run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("This is the end");

	}

	private void run() throws IOException {
		CredentialProviderImpl credentialProvider = new CredentialProviderImpl();
		credentialProvider.deleteCredential();
		credentialProvider.getCredential(this);
		BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		while (!line.equalsIgnoreCase("exit")) {
			System.out.print(">");
			try {
				line = buffer.readLine();
			} catch (IOException e) {
				System.out.println(e);
				line="exit";
			}
			if (line.equals("stop")) {
				System.out.println("Stopping");
				credentialProvider.stop();
				
			}
		}

	}

	@Override
	public void credential(Credential credential) {
		System.out.println("CREDENTIAL " + credential);
		
	}

	@Override
	public void state(CredentialProviderState state) {
		System.out.println(state);
		
	}

}
