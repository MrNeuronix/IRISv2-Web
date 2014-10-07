package controllers;

import other.AMQPDataQueue;
import other.common.messaging.JsonEnvelope;
import play.libs.F;
import play.mvc.Http;
import play.mvc.WebSocketController;
import ru.iris.common.messaging.model.devices.SetDeviceLevelAdvertisement;

import static play.libs.F.Matcher.ClassOf;
import static play.mvc.Http.WebSocketEvent.SocketClosed;

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
			if(obj instanceof SetDeviceLevelAdvertisement)
			{
				if(outbound.isOpen())
					outbound.sendJson(message);
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


				if(outbound.isOpen())
					outbound.sendJson(message);
		}
	}
}
