package controllers;

import other.DataQueue;
import play.libs.F;
import play.mvc.WebSocketController;
import ru.iris.common.messaging.model.devices.SetDeviceLevelAdvertisement;

/**
 * Created by nikolay.viguro on 06.10.2014.
 */
public class WebSocketApp extends WebSocketController
{
	public static void data()
	{
		F.EventStream<Object> stream = DataQueue.getInstance().getQueue().eventStream();

		// Пока канал открыт
		while(inbound.isOpen())
		{
			// Получаем объект
			Object message = WebSocketController.await(stream.nextEvent());

			// Если объект - оповещение об изменении уровня
			if(message instanceof SetDeviceLevelAdvertisement)
			{
				if(outbound.isOpen())
					outbound.sendJson(message);
			}
		}
	}
}
