package nl.ead.webservice;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

@Endpoint
public class CalculatorEndpoint {
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    public CalculatorEndpoint(Marshaller marshaller, Unmarshaller unmarshaller) {
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    @PayloadRoot(localPart = "CalculateRequest", namespace = "http://www.han.nl/schemas/messages")
    public CalculateResponse calculateSumForName(CalculateRequest req) {
        // a sequence of a minimum of 1 and unbounded max is generated as a
        // List<>
        List<Integer> paramList = req.getInput().getParamlist().getParam();
        CalculateOperation op = req.getInput().getOperation();
        int retValue = paramList.get(0);

        for (int i = 1; i < paramList.size(); i++) {
            // CalculateOperation is generated as an enum
            if (op.equals(CalculateOperation.ADD)) {
                retValue += paramList.get(i).intValue();
            } else if (op.equals(CalculateOperation.SUBTRACT)) {
                retValue -= paramList.get(i).intValue();
            } else if (op.equals(CalculateOperation.MULTIPLY)) {
                retValue *= paramList.get(i).intValue();
            } else if (op.equals(CalculateOperation.DIVIDE)) {
                retValue /= paramList.get(i).intValue();
            }
        }

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {

            config.setServerURL(new URL("http://www.moviemeter.nl/ws"));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            Object[] params = new Object[]{new String("6e3fjzydznzxv2q2qgntyu0g10crthmm")};
            HashMap results = (HashMap) client.execute("api.startSession", params);
            String sessionKey = (String) results.get("session_key");
            params = new Object[]{sessionKey, "Bond"};
            Object[] searchResponse = (Object[]) client.execute("film.search", params);
            for (int i = 0; i < searchResponse.length; i++) {
                HashMap film = (HashMap) searchResponse[i];
                System.out.println(film.get("title"));
                System.out.println(film.get("actors_text"));
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        CalculateResult result = new CalculateResult();
        result.setMessage("Here are the results of the jury");
        result.setValue(retValue);
        CalculateResponse resp = new CalculateResponse();
        resp.setResult(result);
        return resp;
    }
}
