package de.gpfeifer.no2go.e4.app.preference;

import java.io.IOException;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.Calendar;

import de.gpfeifer.no2go.google3.CredentialConsumer;
import de.gpfeifer.no2go.google3.CredentialProvider;
import de.gpfeifer.no2go.google3.CredentialProviderState;
import de.gpfeifer.no2go.google3.GoogleCalendarV3;

@Creatable
class GoogleStatus implements CredentialConsumer {
	private static Logger logger = LoggerFactory.getLogger(GoogleStatus.class);

	private CredentialProvider credentialProvider = CredentialProvider.INSTANCE;
	private Label label;
	private Button button;
	private CredentialProviderState state;
	private Display  display;

	private Composite parent;



	public GoogleStatus(Composite parent) {
		create(parent);
	}


	private void create(Composite parent) {
		this.parent = parent;
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
					button.setEnabled(false);
					getCredential();
					break;
				}
			}
		});
	
		Button validateButton = new Button(parent, SWT.PUSH);
		validateButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		validateButton.setText("Validate");
		validateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					Credential c = credentialProvider.getCredential();
					if (c == null) {
						message("No Credential");
						return;
					}
					Calendar googleCalendar = new GoogleCalendarV3().getGoogleCalendar();
					if (googleCalendar != null) {
						message("Valid");
					} else {
						message("Invalid");
					}
				} catch (IOException e) {
					showError(e);
				}
			}


		});
	
	}


	private void getCredential() {
		try {
			credentialProvider.getCredential(this);
		} catch (IOException e) {
			showError(e);
		}
	}


	private void showError(IOException e) {
		MessageDialog.openError(parent.getShell(), "Error", e.toString());
	}

	private void message(String msg) {
		MessageDialog.openInformation(parent.getShell(), "Info", msg);
		
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
		logger.debug("Call asynch: " + newState);
		if (display != null) {
			display.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					System.out.println(newState);
					logger.debug("Called asynch " + newState);
					switch (newState) {
					case USER_CREDENTIAL_EXISTS:
						showCredentialExits();

						break;

					case WAIT_FOR_CODE:
						button.setEnabled(true);
						showWaitForCode();
						break;
						
					case APP_CREDENTIAL_EXISTS:	
						break;
					
					case REDIRECT_URI:
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