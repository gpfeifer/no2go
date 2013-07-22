package de.gpfeifer.no2go.e4.app.preference;

import java.io.IOException;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.google.api.client.auth.oauth2.Credential;

import de.gpfeifer.no2go.google3.CredentialConsumer;
import de.gpfeifer.no2go.google3.CredentialProvider;
import de.gpfeifer.no2go.google3.CredentialProviderState;

@Creatable
class GoogleStatus implements CredentialConsumer {

	private CredentialProvider credentialProvider = CredentialProvider.INSTANCE;
	private Label label;
	private Button button;
	private CredentialProviderState state;
	private Display  display;


	public GoogleStatus(Composite parent) {
		create(parent);
	}


	private void create(Composite parent) {
		display = parent.getShell().getDisplay();
		
		label = new Label(parent, SWT.NONE);
    	label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	
    	button = new Button(parent, SWT.PUSH);
    	button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	
		if (credentialProvider.credentialFileExits()) {
			state = CredentialProviderState.USER_CREDENTIAL_EXISTS;
			showCredentialExits();
		} else {
			state = CredentialProviderState.USER_CREDENTIAL_EXISTS;
			showCredentialNotExists();
		}

		button.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				switch (state) {
				
				case USER_CREDENTIAL_EXISTS:
					credentialProvider.deleteCredential();
					state(CredentialProviderState.USER_CREDENTIAL_NOT_EXISTS);								
					break;
					
				case WAIT_FOR_CODE:
					credentialProvider.stop();
					state(CredentialProviderState.USER_CREDENTIAL_NOT_EXISTS);								
					break;
	
				default:
					getCredential();
					break;
				}
			}
		});
		
	}


	private void getCredential() {
		try {
			credentialProvider.getCredential(this);
		} catch (IOException e) {
		}
	}


	@Override
	public void credential(Credential credential) {
		state(CredentialProviderState.USER_CREDENTIAL_EXISTS);
	}


	private void showCredentialExits() {
		button.setText("Delete Credential");
		label.setText("Google Credential available");
	}

	private void showCredentialNotExists() {
		button.setText("Sign in Google");
		label.setText("No Google Credential available");
	}

	private void showWaitForCode() {
		button.setText("Cancel Waiting for Credential");
		label.setText("Waiting for Credential....");
	}

	@Override
	public void state(final CredentialProviderState newState) {
		this.state = newState;
		if (display != null) {
			display.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					switch (newState) {
					case USER_CREDENTIAL_EXISTS:
						showCredentialExits();
						break;

					case WAIT_FOR_CODE:
						showWaitForCode();
						break;

					default:
						showCredentialNotExists();
						break;
					}
				}

			});
		} else {
			System.out.println();
		}
	}
	
}