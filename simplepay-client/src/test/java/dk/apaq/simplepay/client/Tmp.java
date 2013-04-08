
package dk.apaq.simplepay.client;

import java.io.IOException;

import dk.apaq.simplepay.client.model.Token;
import dk.apaq.simplepay.client.model.Transaction;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

/**
 * Javadoc
 */
public class Tmp {

    public static void main(String[] args) throws IOException {
        //Basic info
        String orderId = "123";
        Money m = Money.of(CurrencyUnit.USD, 99.99);
        
        //Create transaction
        SimplePaySession session = new SimplePaySession("qwerty", "http://localhost:8084/simplepay-web/data");
        Token token = session.createToken("4111111111111111", 2013, 11, "123");
        Transaction t = session.createTransaction(token.getId(), orderId, m);
        
        //Charge the money from the card later on
        session.chargeTransaction(t.getId());
        
        
    }
}
