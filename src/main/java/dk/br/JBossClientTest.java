package dk.br;

import java.io.IOException;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connects to stateless session bean on remote server, and call a method
 */
public class JBossClientTest {
    static Logger LOG = LoggerFactory.getLogger(JBossClientTest.class);

    public static void main(String args[]) throws IOException, NamingException  {
        Properties env = new Properties();
        env.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        env.setProperty("java.naming.provider.url", "localhost:1099");

        InitialContext ctx = new InitialContext(env);
        Object testTopic = ctx.lookup("topic/testTopic");
        LOG.info("topic: {} ({})", testTopic, testTopic.getClass());
    }
}
