package md.mirrerror;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQPublisher {

    private final static String QUEUE_NAME = "app_queue";

    private final Connection connection;
    private final Channel channel;

    public RabbitMQPublisher(String host, String username, String password) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }

    public void publishMessage(String message) throws Exception {
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
    }

    public void close() throws Exception {
        channel.close();
        connection.close();
    }

}