/*
 * Copyright (c) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.gpfeifer.no2go.google3;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;

/**
 * @author gpfeifer@google.com (Your Name Here)
 * 
 */
public class LocalVerificationCodeReceiver implements VerificationCodeReceiver {
	private static final String CALLBACK_PATH = "/Callback";
	/** Verification code or {@code null} for none. */
	String code;

	/** Error code or {@code null} for none. */
	String error;
	
	boolean isWaiting = false;

	class CodeReader implements Runnable {

		/**
		 * @param serverSocket
		 */
		public CodeReader() {
		}

		void runServer() throws NumberFormatException, IOException {

			try {
				lock.lock();
				Socket socket = serverSocket.accept();
				InputStream is = socket.getInputStream();
				OutputStream os = socket.getOutputStream();

				BufferedReader in = new BufferedReader(new InputStreamReader(is)); // initiating

				String line = in.readLine();

				readHttpGet(line);
				while ((line = in.readLine()) != null && !line.isEmpty()) {
					// System.out.println(line);
				}
				String html = getLandingHtml();
				PrintWriter out = new PrintWriter(os);
				out.println("HTTP/1.1 200");
				out.println("Content-Type: text/html; charset=iso-8859-1");
				out.println("Content-Length: " + (html.length()));
				out.println();
				out.print(new String(html));
				out.flush();
				out.close();
				is.close();
				socket.close();

				gotAuthorizationResponse.signal();
			} finally {
				lock.unlock();
			}
		}

		private String getLandingHtml() {

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintWriter doc = new PrintWriter(stream);
			doc.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
			doc.println("<html>");
			doc.println("<head><title>OAuth 2.0 Authentication Token Recieved</title></head>");
			doc.println("<body>");
			doc.println("<h1>NO2GO</h1>");
			doc.println("OAuth 2.0 Authentication Token from Google recieved.<br/>");
			doc.println("You can close this window.");
			// doc.println("<script type='text/javascript'>");
			// // We open "" in the same window to trigger JS ownership of it,
			// which lets
			// // us then close it via JS, at least in Chrome.
			// doc.println("window.setTimeout(function() {");
			// doc.println("    window.open('', '_self', ''); window.close(); }, 1000);");
			// doc.println("if (window.opener) { window.opener.checkToken(); }");
			// doc.println("</script>");
			doc.println("</body>");
			doc.println("</html>");
			doc.flush();
			return stream.toString();
		}

		/**
		 * @param line
		 */
		private void readHttpGet(String line) {
			System.out.println(line);

			int index = line.indexOf(CALLBACK_PATH);
			if (index < 0) {
				error = line;
			}
			line = line.substring(index + CALLBACK_PATH.length());
			error = getParameter(line, "error");
			code = getParameter(line, "code");

		}

		/**
		 * @param line
		 * @param string
		 * @return
		 */
		private String getParameter(String line, String key) {
			int index = line.indexOf(key);
			if (index < 0) {
				return null;
			}
			return line.substring(index + key.length() + 1);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				runServer();
			} catch (IOException exception) {
				System.out.println("Stopped " + exception);
			}

		}
	}

	private ServerSocket serverSocket;
	private int port;
	
	/** Lock on the code and error. */
	final Lock lock = new ReentrantLock();

	/** Condition for receiving an authorization response. */
	final Condition gotAuthorizationResponse = lock.newCondition();
	
	private CredentialProviderImpl provider;
	private CredentialConsumer consumer;
	private Thread thread;

	public LocalVerificationCodeReceiver(CredentialProviderImpl provider, CredentialConsumer consumer) {
		this.provider = provider;
		this.consumer = consumer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver
	 * #getRedirectUri()
	 */
	@Override
	public String getRedirectUri() throws IOException {
		serverSocket = new ServerSocket(0);
		port = serverSocket.getLocalPort();
		CodeReader codeReader = new CodeReader();
		thread = new Thread(codeReader);
		thread.start();
		String uri = "http://localhost:" + port + CALLBACK_PATH;
		provider.redirectUri(consumer, uri);
		return uri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver
	 * #waitForCode()
	 */
	@Override
	public String waitForCode() throws IOException {
		isWaiting = true;
		provider.waitForCode(consumer);
		lock.lock();
		try {
			while (code == null && error == null) {
				gotAuthorizationResponse.await();
			}
			if (error != null) {
				throw new IOException("User authorization failed (" + error + ")");
			}
			return code;
		} catch (InterruptedException e) {
			throw new IOException("Interrupted");
		} finally {
			lock.unlock();
		}

		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver
	 * #stop()
	 */
	@Override
	public void stop() throws IOException {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} finally {
				serverSocket = null;
			}
		}
	}

}
