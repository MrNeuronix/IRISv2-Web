package jobs;

import other.DataQueue;
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
public class SubscribeToAMQP extends Job
{
	public void doJob()
	{
		F.ArchivedEventStream<Object> data = DataQueue.getInstance().getQueue();
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

		try {

			messaging.subscribe("#");
			messaging.start();

			while (true)
			{
				final JsonEnvelope envelope = messaging.receive(5000);
				if (envelope != null)
				{
					Logger.info("Got adv subj: "+envelope.getSubject());
					Logger.info("Receiver: "+envelope.getClass().getSimpleName());
					data.publish(envelope.getObject());
				}
			}

		} catch (final Throwable t) {
			messaging.close();
		}

		messaging.close();
	}
}
