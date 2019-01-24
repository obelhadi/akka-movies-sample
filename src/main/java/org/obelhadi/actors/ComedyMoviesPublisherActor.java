package org.obelhadi.actors;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.obelhadi.events.PublishComedyMovie;

public class ComedyMoviesPublisherActor extends AbstractActor {


	private final static Config config = ConfigFactory.load();

	private final static ObjectMapper objectMapper = new ObjectMapper();

	private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);


	private Channel channel;

	private String exchangeName;

	private String routingKey;


	public static Props props() {
		return Props.create(ComedyMoviesPublisherActor.class, () -> new ComedyMoviesPublisherActor());
	}


	@Override
	public Receive createReceive() {
		return
				receiveBuilder()
						.match(PublishComedyMovie.class, publishComedyMovie -> {
							log.info("publishing comedy movie to RMQ : {}", publishComedyMovie);
							channel.basicPublish(exchangeName, routingKey,
									new AMQP.BasicProperties.Builder()
											.contentType("text/json")
											.deliveryMode(2)
											.priority(1)
											.build(), objectMapper.writeValueAsBytes(publishComedyMovie)
							);
						}).build();
			}

	@Override
	public void preStart() {
		final Config rmq = config.getConfig("rmq");
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(rmq.getString("username"));
		factory.setPassword(rmq.getString("password"));
		factory.setVirtualHost(rmq.getString("vhost"));
		factory.setHost(rmq.getString("host"));
		factory.setPort(rmq.getInt("port"));
		try {
			Connection conn = factory.newConnection();
			channel = conn.createChannel();
			exchangeName = rmq.getString("exchange.name");
			routingKey = rmq.getString("movies.comedy.routing.key");
			final String queueName = rmq.getString("movies.comedy.queue");
			channel.exchangeDeclare(exchangeName, "direct", true);
			channel.queueDeclare(queueName, true, false, false, null);
			channel.queueBind(queueName, exchangeName, routingKey);
		}
		catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}

	}

}
