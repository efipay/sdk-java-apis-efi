package br.com.efi.efisdk;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
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
    private String method;

    public Request(String method, HttpURLConnection conn) throws IOException {
        this.method = method.toUpperCase();
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
        if (!requestOptions.isEmpty()) {
            byte[] postDataBytes = requestOptions.toString().getBytes(StandardCharsets.UTF_8);
            client.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            client.setDoOutput(true);
            try (OutputStream os = client.getOutputStream()) {
                os.write(postDataBytes);
                os.flush();
            }
        }

        int responseCode = client.getResponseCode();
        if (client.getResponseMessage().equals("No Content") || client.getResponseMessage().equals("Accepted")) {
            throw new RuntimeException("{\"code: " + responseCode + "\"}");
        } else {
            switch (responseCode) {
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_CREATED:
                case HttpURLConnection.HTTP_ACCEPTED: {
                    InputStream responseStream = client.getInputStream();
                    String responseBody = readInputStreamToString(responseStream);

                    if (responseBody == null || responseBody.isEmpty()) {
                        throw new RuntimeException("{\"code\": " + responseCode + "}");
                    }

                    if (responseBody.startsWith("[") && responseBody.endsWith("]")) {
                        JSONArray jsonArray = new JSONArray(responseBody);

                        if (jsonArray.length() == 0) {
                            throw new RuntimeException("[]");
                        }
                        return new JSONObject().put("data", jsonArray);
                    } else {
                        return new JSONObject(responseBody);
                    }

                }
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                case 422: {
                    InputStream responseStream = client.getErrorStream();
                    JSONTokener responseTokener = new JSONTokener(responseStream);
                    JSONObject response = new JSONObject(responseTokener);
                    throw new EfiPayException(response);
                }
                default: {
                    InputStream responseStream = client.getErrorStream();
                    JSONTokener responseTokener = new JSONTokener(responseStream);
                    JSONObject response = new JSONObject(responseTokener);
                    throw new EfiPayException(response);
                }
            }
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
        if (client.getResponseMessage().equals("No Content")) {
            throw new RuntimeException("{\"code: " + responseCode + "\"}");
        } else {
            switch (responseCode) {
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_CREATED:
                case HttpURLConnection.HTTP_ACCEPTED: {
                    InputStream responseStream = client.getInputStream();
                    String response = readInputStreamToString(responseStream);
                    return response;
                }
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                case HttpURLConnection.HTTP_FORBIDDEN:
                    throw new AuthorizationException();
                default: {
                    InputStream responseStream = client.getErrorStream();
                    JSONTokener responseTokener = new JSONTokener(responseStream);
                    JSONObject response = new JSONObject(responseTokener);
                    throw new EfiPayException(response);
                }
            }
        }
    }

    public JSONArray sendArray(JSONArray bodyArray)
            throws AuthorizationException, EfiPayException, IOException {
        String body = bodyArray.toString();

        this.client.setRequestProperty("Content-Length", String.valueOf(body.getBytes("UTF-8").length));
        client.setDoOutput(true);

        try (OutputStream os = client.getOutputStream()) {
            os.write(body.getBytes("UTF-8"));
            os.flush();
        }

        int responseCode = client.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_ACCEPTED) {
            InputStream responseStream = client.getInputStream();
            String responseString = readInputStreamToString(responseStream);
            try {
                return new JSONArray(responseString);
            } catch (Exception e) {
                throw new IOException("Erro ao converter a resposta para JSONArray: " + e.getMessage());
            }
        } else {
            InputStream responseStream = client.getErrorStream();
            JSONTokener responseTokener = new JSONTokener(responseStream);
            JSONObject response = new JSONObject(responseTokener);
            throw new EfiPayException(response);
        }
    }

    public byte[] sendAsBytes(JSONObject requestOptions)
            throws AuthorizationException, EfiPayException, IOException {

        if (!"GET".equalsIgnoreCase(this.method)) {
            String bodyString = requestOptions.toString();
            this.client.setRequestProperty("Content-Length", String.valueOf(bodyString.getBytes("UTF-8").length));
            client.setDoOutput(true);

            try (OutputStream os = client.getOutputStream()) {
                os.write(bodyString.getBytes("UTF-8"));
                os.flush();
            }
        } else {
            client.setDoOutput(false);
        }

        int responseCode = client.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK
                || responseCode == HttpURLConnection.HTTP_CREATED
                || responseCode == HttpURLConnection.HTTP_ACCEPTED) {

            try (InputStream responseStream = client.getInputStream(); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] data = new byte[4096];
                int nRead;
                while ((nRead = responseStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();

                byte[] responseBytes = buffer.toByteArray();

                return responseBytes;
            }

        } else {

            try (InputStream errorStream = client.getErrorStream()) {
                JSONTokener responseTokener = new JSONTokener(errorStream);
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

}
