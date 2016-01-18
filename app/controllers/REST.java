package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Device;
import models.Speaks;
import play.Logger;
import play.mvc.Controller;
import other.common.messaging.JsonMessaging;
import ru.iris.common.messaging.model.command.CommandAdvertisement;
import ru.iris.common.messaging.model.devices.*;
import ru.iris.common.messaging.model.speak.SpeakAdvertisement;
import ru.iris.common.messaging.model.speak.SpeakRecognizedAdvertisement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	private static Map<String, Object> params = new HashMap<>();
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

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
        messaging.broadcast("event.speak", new SpeakAdvertisement(text, 100, device));

        renderText("{ status: \"sent\" }");
    }

    // Recognized text from terminal
    public static void recognized(String device, String text)  {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.speak.recognized", new SpeakRecognizedAdvertisement(text, 100, device));

        renderText("{ status: \"sent\" }");
    }

    // Command
    public static void cmd(String script) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.command", new CommandAdvertisement(script, null));

        renderText("{ status: \"sent\" }");
    }

    //////////////////////////////////////////
    /// Devices
    //////////////////////////////////////////

    public static void device(String uuid) {

		if(!uuid.equals("all"))
		{
			Device device = Device.find("uuid = ?", uuid).first();

            if(device == null)
                renderText("{ \"error\": \"device not not found\" }");

			renderText(gson.toJson(device));
		}
		else
		{
			List<Device> devices = Device.findAll();
			renderText(gson.toJson(devices));
		}

    }

    //@Path("/{uuid}/level/{level}")
    public static void devSetLevel(String uuid, Byte level) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        try {

            params.put("uuid", uuid);
            params.put("label", "level");
            params.put("data", level);

            messaging.broadcast("event.devices.setvalue", new GenericAdvertisement("DeviceSetLevel", params));

        } catch (final Throwable t) {
            renderText("Something goes wrong: " + t.toString());
        }


        renderText("{ status: \"sent\" }");
    }

    //@Path("/{uuid}/level/on")
    public static void devSetOn(String uuid) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        try {
            params.put("uuid", uuid);
            messaging.broadcast("event.devices.setvalue", new GenericAdvertisement("DeviceOn", params));
        } catch (final Throwable t) {
            renderText("Something goes wrong: " + t.toString());
        }


        renderText("{ status: \"sent\" }");
    }
    //@Path("/{uuid}/level/off")
    public static void devSetOff(String uuid) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        try {
            params.put("uuid", uuid);
            messaging.broadcast("event.devices.setvalue", new GenericAdvertisement("DeviceOff", params));
        } catch (final Throwable t) {
            renderText("Something goes wrong: " + t.toString());
        }


        renderText("{ status: \"sent\" }");
    }

    //@Path("/{uuid}/name/{name}")
    public static void devSetName(String uuid, String name) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        params.put("uuid", uuid);
        params.put("name", name);
        messaging.broadcast("event.devices.setname", new GenericAdvertisement("DeviceSetName", params));

        renderText("{ status: \"sent\" }");
    }

    //@Path("/{uuid}/zone/{zone}")
    public static void devSetZone(String uuid, int zone) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        params.put("uuid", uuid);
        params.put("zone", zone);
        messaging.broadcast("event.devices.setzone", new GenericAdvertisement("DeviceSetZone", params));

        renderText("{ status: \"sent\" }");
    }

    //////////////////////////////////////////
    /// Scheduler
    //////////////////////////////////////////

    public static void schedulerGetAll() {

    }

    public static void schedulerGet(int id) {

    }

	//////////////////////////////////////////
	/// ZWave Controller
	//////////////////////////////////////////

	public static void zwaveNodeAdd()
	{
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

		try {
			messaging.broadcast("event.devices.zwave.node.add", new GenericAdvertisement("ZWaveAddNode"));
			renderText("{ status: \"sent\" }");
		} catch (final Throwable t) {
			render("{ \"error\": \"" + t.toString() + "\" }");
		}
	}

	public static void zwaveNodeRemove(short id)
    {
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

		try {
			messaging.broadcast("event.devices.zwave.node.remove", new GenericAdvertisement("ZWaveRemoveNode", id));
			renderText("{ status: \"sent\" }");
		} catch (final Throwable t) {
			render("{ \"error\": \"" + t.toString() + "\" }");
		}

	}

	public static void zwaveCancel() {
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

		try {
			messaging.broadcast("event.devices.zwave.cancel", new GenericAdvertisement("ZWaveCancelCommand"));
			renderText("{ status: \"sent\" }");
		} catch (final Throwable t) {
			render("{ \"error\": \"" + t.toString() + "\" }");
		}

	}

    //////////////////////////////////////////
    /// Noolite PC Controller
    //////////////////////////////////////////

    public static void noolitePcNodeAdd(short channel)
    {
        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        try {
            messaging.broadcast("event.devices.noolite.tx.bindchannel", new GenericAdvertisement("BindTXChannelAdvertisment"));
            renderText("{ status: \"sent\" }");
        } catch (final Throwable t) {
            render("{ \"error\": \"" + t.toString() + "\" }");
        }
    }

    public static void noolitePcNodeRemove(short channel) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        try {
            messaging.broadcast("event.devices.noolite.tx.unbindchannel", new GenericAdvertisement("UnbindTXChannelAdvertisment", channel));
            renderText("{ status: \"sent\" }");
        } catch (final Throwable t) {
            render("{ \"error\": \"" + t.toString() + "\" }");
        }
    }

    //////////////////////////////////////////
    /// Noolite RX Controller
    //////////////////////////////////////////

    public static void nooliteRxNodeAdd(short channel)
    {
        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        try {
            messaging.broadcast("event.devices.noolite.rx.bindchannel", new GenericAdvertisement("BindRXChannelAdvertisment"));
            renderText("{ status: \"sent\" }");
        } catch (final Throwable t) {
            render("{ \"error\": \"" + t.toString() + "\" }");
        }
    }

    public static void nooliteRxNodeRemove(short channel) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        try {
            messaging.broadcast("event.devices.noolite.rx.unbindchannel", new GenericAdvertisement("UnbindRXChannelAdvertisment", channel));
            renderText("{ status: \"sent\" }");
        } catch (final Throwable t) {
            render("{ \"error\": \"" + t.toString() + "\" }");
        }
    }

    public static void test() {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        GenericAdvertisement advertisment = new GenericAdvertisement();
        advertisment.setLabel("StartPlugin");
        advertisment.setValue("iris-devices-noolite");

        try {
            messaging.broadcast("event.plugin", advertisment);
        } catch (final Throwable t) {
            renderText("{ \"error\": \"" + t.getMessage() + "\" }");
        }
    }
}
