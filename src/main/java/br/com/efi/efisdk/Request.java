package br.com.efi.efisdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.json.JSONTokener;

import br.com.efi.efisdk.exceptions.AuthorizationException;
import br.com.efi.efisdk.exceptions.EfiPayException;

/**
 * This class is responsible to create an HttpURLConnection Object, generate the
 * request body and send it to a given endpoint. The send method return a
 * response for that request.
 * 
 * @author Consultoria Técnica
 *
 */
public class Request {

	private HttpURLConnection client;

	public Request(String method, HttpURLConnection conn) throws IOException {
		this.client = conn;		
		this.client.setRequestProperty("Content-Type", "application/json");
		this.client.setRequestProperty("charset", "UTF-8");
		this.client.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36");
		this.client.setRequestProperty("api-sdk", "efi-java-" + Config.getVersion());

		if (method.toUpperCase().equals("PATCH")) {
			this.client.setRequestProperty("X-HTTP-Method-Override", "PATCH");
		} else {
			this.client.setRequestMethod(method.toUpperCase());
		}
	}

	public void addHeader(String key, String value) {
		client.setRequestProperty(key, value);
	}

	public JSONObject send(JSONObject requestOptions) throws AuthorizationException, EfiPayException, IOException {
		byte[] postDataBytes;
		postDataBytes = requestOptions.toString().getBytes("UTF-8");
		this.client.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		if (!client.getRequestMethod().toLowerCase().equals("get")) {
			client.setDoOutput(true);
			OutputStream os = client.getOutputStream();
			os.write(postDataBytes);
			os.flush();
			os.close();
		}

		int responseCode = client.getResponseCode();
		if (client.getResponseMessage().equals("No Content") || client.getResponseMessage().equals("Accepted")){
			throw new RuntimeException("{\"code: " + responseCode + "\"}");
		} else{
			if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED
			|| responseCode == HttpURLConnection.HTTP_ACCEPTED) {
			InputStream responseStream = client.getInputStream();
			JSONTokener responseTokener = new JSONTokener(responseStream);
			return new JSONObject(responseTokener);
		} else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED
				|| responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
			throw new AuthorizationException();
		} else {
			InputStream responseStream = client.getErrorStream();
			JSONTokener responseTokener = new JSONTokener(responseStream);
			JSONObject response = new JSONObject(responseTokener);
			throw new EfiPayException(response);
		}
		}
	} 

	private String readInputStreamToString(InputStream inputStream) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}
			return stringBuilder.toString();
		}
	}

	public String sendString(JSONObject requestOptions)
			throws AuthorizationException, EfiPayException, IOException {
		byte[] postDataBytes;
		postDataBytes = requestOptions.toString().getBytes("UTF-8");
		this.client.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		if (!client.getRequestMethod().toLowerCase().equals("get")) {
			client.setDoOutput(true);
			OutputStream os = client.getOutputStream();
			os.write(postDataBytes);
			os.flush();
			os.close();
		}

		int responseCode = client.getResponseCode();
		if (client.getResponseMessage().equals("No Content")){
			throw new RuntimeException("{\"code: " + responseCode + "\"}");
		} else{
			if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED
					|| responseCode == HttpURLConnection.HTTP_ACCEPTED) {
				InputStream responseStream = client.getInputStream();
				String response = readInputStreamToString(responseStream);
				return new String(response);
			} else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED
					|| responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
				throw new AuthorizationException();
			} else {
				InputStream responseStream = client.getErrorStream();
				JSONTokener responseTokener = new JSONTokener(responseStream);
				JSONObject response = new JSONObject(responseTokener);
				throw new EfiPayException(response);
			}
		}
	}
}
