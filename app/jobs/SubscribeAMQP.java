package jobs;

import other.AMQPDataQueue;
import other.common.messaging.JsonEnvelope;
import other.common.messaging.JsonMessaging;
import other.common.messaging.JsonNotification;
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
		final F.ArchivedEventStream<JsonEnvelope> data = AMQPDataQueue.getInstance().getQueue();
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID(), "all-data");

			messaging.subscribe("#");
			messaging.setNotification(
					new JsonNotification() {
						@Override
						public void onNotification(JsonEnvelope envelope) {

							Logger.debug("AMQP message from: "+envelope.getSubject());
							data.publish(envelope);

						}
					}
			);
			messaging.start();
	}
}
