package controllers;

import other.AMQPDataQueue;
import other.common.messaging.JsonEnvelope;
import play.Logger;
import play.libs.F;
import play.mvc.WebSocketController;
import ru.iris.common.messaging.model.devices.noolite.NooliteDeviceLevelBrightAdvertisement;
import ru.iris.common.messaging.model.devices.noolite.NooliteDeviceLevelDimAdvertisement;
import ru.iris.common.messaging.model.devices.noolite.NooliteDeviceLevelSetAdvertisement;
import ru.iris.common.messaging.model.devices.noolite.NooliteDeviceLevelStopDimBrightAdvertisement;
import ru.iris.common.messaging.model.devices.zwave.ZWaveSetDeviceLevelAdvertisement;

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
			if(obj instanceof NooliteDeviceLevelBrightAdvertisement ||
					obj instanceof NooliteDeviceLevelDimAdvertisement ||
					obj instanceof NooliteDeviceLevelSetAdvertisement ||
					obj instanceof NooliteDeviceLevelStopDimBrightAdvertisement ||
					obj instanceof ZWaveSetDeviceLevelAdvertisement)
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
