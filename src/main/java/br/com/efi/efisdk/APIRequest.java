package br.com.efi.efisdk;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.efi.efisdk.exceptions.AuthorizationException;
import br.com.efi.efisdk.exceptions.EfiPayException;

/**
 * This class instance a Auth Object, to authenticate client credentials in efi
 * API. After client's credentials are validated a client Object send a given
 * request body to a given endpoint throw a given route.
 *
 * @author Consultoria Técnica
 *
 */
public class APIRequest {

    private Request requester;
    private Auth authenticator;
    private String route;
    private JSONObject body;
    private JSONArray bodyArray;

    public APIRequest(String method, String route, JSONObject body, JSONObject auth, Config config) throws Exception {
        this.route = route;
        String authenticateRoute = auth.getString("route");
        String authenticateMethod = auth.getString("method");
        this.authenticator = new Auth(config.getOptions(), authenticateMethod, authenticateRoute);

        String url = config.getOptions().getString("baseUri") + route;
        URL link = new URL(url);
        HttpURLConnection client = (HttpURLConnection) link.openConnection();

        this.requester = new Request(method, client);

        if (config.getOptions().has("partnerToken")) {
            this.requester.addHeader("partner-token", config.getOptions().getString("partnerToken"));
        }

        if (config.getOptions().has("headers")) {
            this.requester.addHeader("x-skip-mtls-checking", config.getOptions().getString("headers"));
            this.requester.addHeader("x-idempotency-key", config.getOptions().getString("headers"));
        }

        this.body = body;
    }

    public APIRequest(String method, String route, JSONArray bodyArray, JSONObject auth, Config config) throws Exception {
        this.route = route;
        String authenticateRoute = auth.getString("route");
        String authenticateMethod = auth.getString("method");
        this.authenticator = new Auth(config.getOptions(), authenticateMethod, authenticateRoute);

        String url = config.getOptions().getString("baseUri") + route;
        URL link = new URL(url);
        HttpURLConnection client = (HttpURLConnection) link.openConnection();

        this.requester = new Request(method, client);
        this.bodyArray = bodyArray;
    }

    public APIRequest(Auth auth, Request request, JSONObject body) {
        this.authenticator = auth;
        this.requester = request;
        this.body = body;
    }

    public JSONObject send() throws AuthorizationException, EfiPayException, IOException {
        Date expiredDate = this.authenticator.getExpires();
        if (this.authenticator.getExpires() == null || expiredDate.compareTo(new Date()) <= 0) {
            this.authenticator.authorize();
        }
        this.requester.addHeader("Authorization", "Bearer " + this.authenticator.getAccessToken());
        try {
            return this.requester.send(this.body);
        } catch (AuthorizationException e) {
            this.authenticator.authorize();
            return this.requester.send(body);
        }
    }

    public String sendString() throws AuthorizationException, EfiPayException, IOException {
        Date expiredDate = this.authenticator.getExpires();
        if (this.authenticator.getExpires() == null || expiredDate.compareTo(new Date()) <= 0) {
            this.authenticator.authorize();
        }
        this.requester.addHeader("Authorization", "Bearer " + this.authenticator.getAccessToken());
        try {
            return this.requester.sendString(this.body);
        } catch (AuthorizationException e) {
            this.authenticator.authorize();
            return this.requester.sendString(body);
        }
    }

    public JSONArray sendArray() throws AuthorizationException, EfiPayException, IOException {
        Date expiredDate = this.authenticator.getExpires();
        if (this.authenticator.getExpires() == null || expiredDate.compareTo(new Date()) <= 0) {
            this.authenticator.authorize();
        }
        this.requester.addHeader("Authorization", "Bearer " + this.authenticator.getAccessToken());

        return this.requester.sendArray(this.bodyArray);
    }

    public byte[] sendAsBytes() throws AuthorizationException, EfiPayException, IOException {
        Date expiredDate = this.authenticator.getExpires();
        if (expiredDate == null || expiredDate.compareTo(new Date()) <= 0) {
            this.authenticator.authorize();
        }
        this.requester.addHeader("Authorization", "Bearer " + this.authenticator.getAccessToken());
        try {
            return this.requester.sendAsBytes(this.body);
        } catch (AuthorizationException e) {
            this.authenticator.authorize();
            return this.requester.sendAsBytes(this.body);
        }
    }

    public Request getRequester() {
        return requester;
    }

    public String getRoute() {
        return route;
    }

    public JSONObject getBody() {
        return body;
    }
}
