/*
 * Copyright 2012-2014 Nikolay A. Viguro
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package other.common.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import play.Play;

import java.io.IOException;

/**
 * Prototype for JSON message broadcasting.
 *
 * @author Nikolay A. Viguro, Tommi S.E. Laukkanen
 */

public class JsonConnection
{

	private static final Logger LOGGER = LogManager.getLogger(JsonConnection.class);

	/**
	 * The AMQ connection.
	 */
	private Connection connection;
	private Channel channel;

	/**
	 * Singleton (On Demand Holder)
	 */
	public static class SingletonHolder
	{
		public static final JsonConnection HOLDER_INSTANCE = new JsonConnection();
	}

	public static JsonConnection getInstance()
	{
		return SingletonHolder.HOLDER_INSTANCE;
	}

	public JsonConnection()
	{
		// Create a ConnectionFactory
		ConnectionFactory connectionFactory = new ConnectionFactory();

		// Create a Connection
		try
		{
			// Create a ConnectionFactory
			connectionFactory.setHost(Play.configuration.getProperty("AMQPhost"));
			connectionFactory.setPort(Integer.valueOf(Play.configuration.getProperty("AMQPport")));

			connection = connectionFactory.newConnection();
			channel = connection.createChannel();

			// Create exchange
			channel.exchangeDeclare("iris", "topic", true);
		}
		catch (IOException e)
		{
			LOGGER.error("Error while connection to AMQP broker! " + e);
		}
	}
	/**
	 * Closes connection to message broker.
	 */
	public void close()
	{
		try
		{
			channel.close();
			connection.close();
		}
		catch (final Exception e)
		{
			LOGGER.error("Error shutting down JsonMessaging.", e);
		}
	}

	public Channel getChannel()
	{
		return channel;
	}
}
