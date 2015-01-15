package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Device;
import models.Speaks;
import other.common.messaging.JsonEnvelope;
import play.mvc.Controller;
import other.common.messaging.JsonMessaging;
import ru.iris.common.messaging.model.command.CommandAdvertisement;
import ru.iris.common.messaging.model.devices.*;
import ru.iris.common.messaging.model.devices.noolite.BindRXChannelAdvertisment;
import ru.iris.common.messaging.model.devices.noolite.BindTXChannelAdvertisment;
import ru.iris.common.messaging.model.devices.noolite.UnbindRXChannelAdvertisment;
import ru.iris.common.messaging.model.devices.noolite.UnbindTXChannelAdvertisment;
import ru.iris.common.messaging.model.devices.zwave.ZWaveAddNodeRequest;
import ru.iris.common.messaging.model.devices.zwave.ZWaveCancelCommand;
import ru.iris.common.messaging.model.devices.zwave.ZWaveRemoveNodeRequest;
import ru.iris.common.messaging.model.events.EventGetScriptAdvertisement;
import ru.iris.common.messaging.model.speak.SpeakAdvertisement;
import ru.iris.common.messaging.model.speak.SpeakRecognizedAdvertisement;

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
        messaging.broadcast("event.devices.setname", new SetDeviceNameAdvertisement(uuid, name));

        renderText("{ status: \"sent\" }");
    }

    //@Path("/{uuid}/zone/{zone}")
    public static void devSetZone(String uuid, int zone) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.devices.setzone", new SetDeviceZoneAdvertisement(uuid, zone));

        renderText("{ status: \"sent\" }");
    }

    private static String sendLevelMessage(String uuid, String label, String value) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        try {
            messaging.broadcast("event.devices.setvalue", new SetDeviceLevelAdvertisement(uuid, label, value));
            return "sent to "+uuid;
        } catch (final Throwable t) {
            return "Something goes wrong: " + t.toString();
        }
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
			messaging.broadcast("event.devices.zwave.node.add", new ZWaveAddNodeRequest());
			renderText("{ status: \"sent\" }");
		} catch (final Throwable t) {
			render("{ \"error\": \"" + t.toString() + "\" }");
		}
	}

	public static void zwaveNodeRemove(short id)
    {
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        ZWaveRemoveNodeRequest adv = new ZWaveRemoveNodeRequest();
        adv.setNode(id);

		try {
			messaging.broadcast("event.devices.zwave.node.remove", adv);
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

    //////////////////////////////////////////
    /// Noolite PC Controller
    //////////////////////////////////////////

    public static void noolitePcNodeAdd(short channel)
    {
        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        BindTXChannelAdvertisment advertisment = new BindTXChannelAdvertisment();
        advertisment.setChannel(channel);

        try {
            messaging.broadcast("event.devices.noolite.tx.bindchannel", advertisment);
            renderText("{ status: \"sent\" }");
        } catch (final Throwable t) {
            render("{ \"error\": \"" + t.toString() + "\" }");
        }
    }

    public static void noolitePcNodeRemove(short channel) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        UnbindTXChannelAdvertisment advertisment = new UnbindTXChannelAdvertisment();
        advertisment.setChannel(channel);

        try {
            messaging.broadcast("event.devices.noolite.tx.unbindchannel", advertisment);
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

        BindRXChannelAdvertisment advertisment = new BindRXChannelAdvertisment();
        advertisment.setChannel(channel);

        try {
            messaging.broadcast("event.devices.noolite.rx.bindchannel", advertisment);
            renderText("{ status: \"sent\" }");
        } catch (final Throwable t) {
            render("{ \"error\": \"" + t.toString() + "\" }");
        }
    }

    public static void nooliteRxNodeRemove(short channel) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        UnbindRXChannelAdvertisment advertisment = new UnbindRXChannelAdvertisment();
        advertisment.setChannel(channel);

        try {
            messaging.broadcast("event.devices.noolite.rx.unbindchannel", advertisment);
            renderText("{ status: \"sent\" }");
        } catch (final Throwable t) {
            render("{ \"error\": \"" + t.toString() + "\" }");
        }
    }

    public static void test() {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        EventGetScriptAdvertisement advertisment = new EventGetScriptAdvertisement();
        advertisment.setName("hour-say.js");

        try {
            JsonEnvelope envelope = messaging.request("event.script.get", advertisment);

            if(envelope != null)
                renderText(envelope.toString());
            else
                renderText("error");
        } catch (final Throwable t) {
            renderText("{ \"error\": \"" + t.getMessage() + "\" }");
        }
    }
}
