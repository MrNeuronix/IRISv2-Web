package jobs;

import other.AMQPDataQueue;
import other.common.messaging.JsonEnvelope;
import other.common.messaging.JsonMessaging;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.libs.F;

import java.util.UUID;

/**
 * Created by nikolay.viguro on 06.10.2014.
 */

@OnApplicationStart(async = true)
public class SubscribeAMQP extends Job
{
	public void doJob() throws InterruptedException
	{
		F.ArchivedEventStream<JsonEnvelope> data = AMQPDataQueue.getInstance().getQueue();
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID(), "all-data");

			messaging.subscribe("#");
			messaging.start();

			while (true)
			{
				final JsonEnvelope envelope = messaging.receive(5000);
				if (envelope != null)
				{
					Logger.debug("AMQP message from: "+envelope.getSubject());
					data.publish(envelope);
				}
			}
	}
}
