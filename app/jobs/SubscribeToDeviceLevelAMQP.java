package jobs;

import other.SetLevelDataQueue;
import other.common.messaging.JsonEnvelope;
import other.common.messaging.JsonMessaging;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.libs.F;

import java.util.UUID;

/**
 * Created by nikolay.viguro on 06.10.2014.
 */

@OnApplicationStart(async = true)
public class SubscribeToDeviceLevelAMQP extends Job
{
	public void doJob()
	{
		F.EventStream<Object> data = SetLevelDataQueue.getInstance().getQueue();
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

		try {

			messaging.subscribe("event.devices.setvalue");
			messaging.start();

			while (true)
			{
				final JsonEnvelope envelope = messaging.receive(5000);
				if (envelope != null)
				{
					data.publish(envelope.getObject());
				}
			}

		} catch (final Throwable t) {
			messaging.close();
		}

		messaging.close();
	}
}
