package controllers;

import other.AMQPDataQueue;
import other.common.messaging.JsonEnvelope;
import play.Logger;
import play.libs.F;
import play.mvc.WebSocketController;
import ru.iris.common.messaging.model.devices.GenericAdvertisement;

/**
 * Created by nikolay.viguro on 06.10.2014.
 */
public class WebSocketApp extends WebSocketController
{
	public static void setDeviceLevel()
	{
		F.EventStream<JsonEnvelope> stream = AMQPDataQueue.getInstance().getQueue().eventStream();

		boolean init = true;

		// Пока канал открыт
		while(inbound.isOpen())
		{
			// Получаем объект
			JsonEnvelope message = WebSocketController.await(stream.nextEvent());

			// skip first entry
			if(init)
			{
				init = false;
				continue;
			}

			Object obj = message.getObject();

			// Если объект - оповещение об изменении уровня
			if(obj instanceof GenericAdvertisement)
			{
                String label = (String)((GenericAdvertisement) obj).getValue("label");

                if(!label.isEmpty()) {
                    if (outbound.isOpen())
                        outbound.sendJson(message);
                }
			}
		}
	}

	public static void dataChannel()
	{
		F.EventStream<JsonEnvelope> stream = AMQPDataQueue.getInstance().getQueue().eventStream();

		boolean init = true;

		// Пока канал открыт
		while(inbound.isOpen())
		{
			// Получаем объект
			JsonEnvelope message = WebSocketController.await(stream.nextEvent());

			// skip first entry
			if(init)
			{
				init = false;
				continue;
			}

			if(outbound.isOpen())
				outbound.sendJson(message);

			Logger.info("Get WS message: " + message.getSubject());
		}
	}
}
