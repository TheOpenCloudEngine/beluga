import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * REST API Test
 *
 * @author Sang Wook, Song
 *
 */
public class RESTAPITest {

	@Test
	public void responseTypeTest() {

		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080").path("/human/list");

		Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON_TYPE);
		//Response response  = builder.get();
		String result = builder.get(String.class);
		System.out.println(target.getUri().toString());
		System.out.println("Result=" + result);
	}
}
