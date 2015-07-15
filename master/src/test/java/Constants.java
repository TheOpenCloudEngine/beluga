import java.util.concurrent.TimeUnit;

/**
 * Created by swsong on 2015. 7. 10..
 */
public class Constants {
    public static final String PROVIDER = System.getProperty("provider.cs", "rackspace-cloudservers-us");
    public static final String REGION = System.getProperty("region", "IAD");

    public static final String NAME = "jclouds-example";
    public static final String POLL_PERIOD_TWENTY_SECONDS = String.valueOf(TimeUnit.SECONDS.toMillis(20));
}
