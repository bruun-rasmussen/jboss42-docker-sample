package dk.br;

import java.io.IOException;
import java.util.Properties;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connects to JMS testTopic and listens for messages or sends a message
 */
public class JBossClientTest {
    static Logger LOG = LoggerFactory.getLogger(JBossClientTest.class);

    private final InitialContext ctx;

    private JBossClientTest(InitialContext ctx) {
        this.ctx = ctx;
    }

    public static void main(String args[]) throws IOException, NamingException, JMSException  {
        Properties env = new Properties();
        env.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        env.setProperty("jnp.disableDiscovery", "true");
        env.setProperty("java.naming.provider.url", System.getProperty("java.naming.provider.url", "localhost:1099"));

        InitialContext ctx = new InitialContext(env);

        if (args.length == 1 && "listen".equals(args[0])) {
            new JBossClientTest(ctx).listen();
        }
        else {
            new JBossClientTest(ctx).speak();
        }
    }

    public TopicConnection connect() throws NamingException, JMSException {
        Object o = ctx.lookup("ConnectionFactory");
        LOG.info("connectionFactory: {} ({})", o, o.getClass());
        TopicConnectionFactory tcf = (TopicConnectionFactory) o;

        TopicConnection conn = tcf.createTopicConnection();
        LOG.info("connected {} ({})", conn, conn.getClass());

        return conn;
    }

    public void disconnect(TopicConnection conn) throws JMSException {
        if (conn != null) {
            conn.setExceptionListener(null);
            conn.close();
            LOG.info("disconnected");
        }
    }

    public Topic getTestTopic() throws NamingException {
        Object o = ctx.lookup("topic/allPointsBulletin");
        LOG.info("topic: {} ({})", o, o.getClass());
        Topic testTopic = (Topic)o;

        return testTopic;
    }

    public void speak() throws NamingException, JMSException {
        TopicConnection conn = connect();
        try {
            TopicSession session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
            LOG.info("session: {} ({})", session, session.getClass());

            Topic testTopic = getTestTopic();
            TopicPublisher publisher = session.createPublisher(testTopic);
            LOG.info("publisher: {} ({})", publisher, publisher.getClass());

            MapMessage message = session.createMapMessage();
            message.setString("foo", "Hello, world!");
            message.setInt("bar", 42);

            publisher.publish(message);
            LOG.info("published message!");
        }
        finally {
            disconnect(conn);
        }
    }

    public void listen() throws NamingException, JMSException {
        TopicConnection conn = connect();
        TopicSession session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
        LOG.info("session: {} ({})", session, session.getClass());

        Topic testTopic = getTestTopic();
        TopicSubscriber subscriber = session.createSubscriber(testTopic);

        subscriber.setMessageListener((Message msg) -> { LOG.info("onMessage({})", msg); });

        conn.start();
    }
}
