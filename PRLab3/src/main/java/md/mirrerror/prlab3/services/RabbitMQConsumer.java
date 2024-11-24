package md.mirrerror.prlab3.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

    @RabbitListener(queues = "app_queue")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }

}