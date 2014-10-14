package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Device;
import models.Speaks;
import play.Logger;
import play.mvc.Controller;
import other.common.messaging.JsonEnvelope;
import other.common.messaging.JsonMessaging;
import ru.iris.common.messaging.model.command.CommandAdvertisement;
import ru.iris.common.messaging.model.devices.*;
import ru.iris.common.messaging.model.devices.noolite.ResponseNooliteDeviceInventoryAdvertisement;
import ru.iris.common.messaging.model.devices.zwave.ResponseZWaveDeviceInventoryAdvertisement;
import ru.iris.common.messaging.model.devices.zwave.ZWaveAddNodeRequest;
import ru.iris.common.messaging.model.devices.zwave.ZWaveCancelCommand;
import ru.iris.common.messaging.model.devices.zwave.ZWaveRemoveNodeRequest;
import ru.iris.common.messaging.model.speak.SpeakAdvertisement;
import ru.iris.common.messaging.model.speak.SpeakRecognizedAdvertisement;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * IRISv2 Project
 * Author: Nikolay A. Viguro
 * WWW: iris.ph-systems.ru
 * E-Mail: nv@ph-systems.ru
 * Date: 04.01.14
 * Time: 19:31
 * License: GPL v3
 */

public class REST extends Controller {

    private static SetDeviceLevelAdvertisement setDeviceLevelAdvertisement = new SetDeviceLevelAdvertisement();
    private static SetDeviceNameAdvertisement setDeviceNameAdvertisement = new SetDeviceNameAdvertisement();
    private static SetDeviceZoneAdvertisement setDeviceZoneAdvertisement = new SetDeviceZoneAdvertisement();
	private static ZWaveAddNodeRequest setNodeAdd = new ZWaveAddNodeRequest();
	private static ZWaveRemoveNodeRequest setNodeRemove = new ZWaveRemoveNodeRequest ();
	private static ZWaveCancelCommand setCancel = new ZWaveCancelCommand();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////
    /// Common
    //////////////////////////////////////////

    public static void queue(String device)  {
        Speaks queue = Speaks.find("device=? and isActive=true", device).first();
        render(queue);
    }

    // Speak text on terminal
    public static void say(String device, String text) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.speak", new SpeakAdvertisement().set(text, 100, device));

        renderText("{ status: \"sent\" }");
    }

    // Recognized text from terminal
    public static void recognized(String device, String text)  {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.speak.recognized", new SpeakRecognizedAdvertisement().set(text, 100, device));

        renderText("{ status: \"sent\" }");
    }

    // Command
    public static void cmd(String text) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.command", new CommandAdvertisement().set(text));

        renderText("{ status: \"sent\" }");
    }

    //////////////////////////////////////////
    /// Devices
    //////////////////////////////////////////

    public static void device(String uuid) {

		if(!uuid.equals("all"))
		{
			Device device = Device.find("uuid = ?", uuid).first();
			device.getValues();

			renderText(gson.toJson(device));
		}
		else
		{
			List<Device> devices = Device.findAll();

			for(Device device : devices)
			{
				device.getValues();
			}
			renderText(gson.toJson(devices));
		}

    }

    //@Path("/{uuid}/{label}/{level}")
    public static void devSetLevel(String uuid, String label, String level) {
        renderText("{ status: " + sendLevelMessage(uuid, label, level) + " }");
    }

    //@Path("/{uuid}/name/{name}")
    public static void devSetName(String uuid, String name) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.devices.setname", setDeviceNameAdvertisement.set(uuid, name));

        renderText("{ status: \"sent\" }");
    }

    //@Path("/{uuid}/zone/{zone}")
    public static void devSetZone(String uuid, int zone) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.devices.setzone", setDeviceZoneAdvertisement.set(uuid, zone));

        renderText("{ status: \"sent\" }");
    }

    private static String sendLevelMessage(String uuid, String label, String value) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        try {
            messaging.broadcast("event.devices.setvalue", setDeviceLevelAdvertisement.set(uuid, label, value));
            return "sent";
        } catch (final Throwable t) {
            return "Something goes wrong: " + t.toString();
        }
    }

    //////////////////////////////////////////
    /// Scheduler
    //////////////////////////////////////////

    public static void schedulerGetAll() throws IOException, SQLException {

    }

    public static void schedulerGet(int id) throws IOException {

    }

	//////////////////////////////////////////
	/// ZWave Controller
	//////////////////////////////////////////

	public static void zwaveNodeAdd()
	{
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

		try {
			messaging.broadcast("event.devices.zwave.node.add", setNodeAdd);
			renderText("{ status: \"sent\" }");
		} catch (final Throwable t) {
			render("{ \"error\": \"" + t.toString() + "\" }");
		}
	}

	public static void zwaveNodeRemove(short id) {
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

		setNodeRemove.setNode(id);

		try {
			messaging.broadcast("event.devices.zwave.node.remove", setNodeRemove);
			renderText("{ status: \"sent\" }");
		} catch (final Throwable t) {
			render("{ \"error\": \"" + t.toString() + "\" }");
		}

	}

	public static void zwaveCancel() {
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

		try {
			messaging.broadcast("event.devices.zwave.cancel", setCancel);
			renderText("{ status: \"sent\" }");
		} catch (final Throwable t) {
			render("{ \"error\": \"" + t.toString() + "\" }");
		}

	}
}
