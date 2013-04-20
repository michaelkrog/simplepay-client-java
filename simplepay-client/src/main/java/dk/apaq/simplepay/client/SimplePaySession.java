package dk.apaq.simplepay.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import dk.apaq.simplepay.client.model.Token;
import dk.apaq.simplepay.client.model.Transaction;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.joda.money.Money;

/**
 * Manages a session for communicating with SimplePay.
 * <br><br>
 * An application can have as many sessions it likes with each having their own key. In most systems a single session will suffice, but larger
 * multiuser systems might need access to multiple SimplePay accounts through the same Java application.<br><br>
 *
 * Example:<br>
 * <code>
 * SimplePaySession spay = new SimplePaySession("<key>");
 * Token token = spay.createToken("<card>", 2015, 11, "<cvd>");
 *
 */
public class SimplePaySession {

    private static final String DEFAULT_SERVICEURL = "http://simplepay.dk";
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();

    static {
        GSON_BUILDER.registerTypeAdapter(Date.class, new DateTimeDeserializer());
    }
    
    private static class DateTimeDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return new Date(json.getAsJsonPrimitive().getAsLong());
        }
    }

    private final String key;
    private final String serviceUrl;
    private final DefaultHttpClient client;
    private final Gson gson;
    
    public SimplePaySession(String key) {
        this(key, DEFAULT_SERVICEURL);
    }

    public SimplePaySession(String key, String serviceUrl) {
        this.key = key;
        this.serviceUrl = serviceUrl;
        this.client = new DefaultHttpClient();
        this.client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(key, ""));
        gson = GSON_BUILDER.create();
    }

    public Token createToken(String cardNumber, int expireYear, int expireMonth, String cvd) throws IOException {
        HttpPost method = new HttpPost(serviceUrl + "/tokens");
        method.addHeader("Accept", "application/json");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("cardNumber", cardNumber));
        nvps.add(new BasicNameValuePair("expireYear", Integer.toString(expireYear)));
        nvps.add(new BasicNameValuePair("expireMonth", Integer.toString(expireMonth)));
        nvps.add(new BasicNameValuePair("cvd", cvd));
        method.setEntity(new UrlEncodedFormEntity(nvps));
        HttpResponse response = client.execute(method);

        JsonReader reader = new JsonReader(new InputStreamReader(response.getEntity().getContent()));
        return gson.fromJson(reader, Token.class);
    }

    public Transaction createTransaction(String tokenId, String refId, Money amount) throws IOException {
        HttpPost method = new HttpPost(serviceUrl + "/transactions");
        method.addHeader("Accept", "application/json");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("token", tokenId));
        nvps.add(new BasicNameValuePair("refId", refId));
        nvps.add(new BasicNameValuePair("amount", Long.toString(amount.getAmountMinorLong())));
        nvps.add(new BasicNameValuePair("currency", amount.getCurrencyUnit().getCurrencyCode()));
        method.setEntity(new UrlEncodedFormEntity(nvps));
        HttpResponse response = client.execute(method);

        JsonReader reader = new JsonReader(new InputStreamReader(response.getEntity().getContent()));
        return gson.fromJson(reader, Transaction.class);
    }

    public Transaction getTransaction(String id) {
        return null;
    }

    public Transaction chargeTransaction(String id) {
        return chargeTransaction(id, null);
    }

    public Transaction chargeTransaction(String id, Money amount) {
        return null;
    }

    public Transaction refundTransaction(String id) {
        return refundTransaction(id, null);
    }

    public Transaction refundTransaction(String id, Money amount) {
        return null;
    }

    public Transaction cancelTransaction(String id) {
        return null;
    }
}
