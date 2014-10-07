package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Speaks;
import play.mvc.Controller;
import other.common.messaging.JsonEnvelope;
import other.common.messaging.JsonMessaging;
import ru.iris.common.messaging.model.command.CommandAdvertisement;
import ru.iris.common.messaging.model.devices.*;
import ru.iris.common.messaging.model.devices.noolite.ResponseNooliteDeviceInventoryAdvertisement;
import ru.iris.common.messaging.model.devices.zwave.ResponseZWaveDeviceInventoryAdvertisement;
import ru.iris.common.messaging.model.speak.SpeakAdvertisement;
import ru.iris.common.messaging.model.speak.SpeakRecognizedAdvertisement;

import java.io.IOException;
import java.sql.SQLException;
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
    private static GetInventoryAdvertisement getInventoryAdvertisement = new GetInventoryAdvertisement();
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
        messaging.broadcast("event.speak", new SpeakAdvertisement().set(text, 100, device));
        messaging.close();

        renderText("{ status: \"sent\" }");
    }

    // Recognized text from terminal
    public static void recognized(String device, String text)  {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.speak.recognized", new SpeakRecognizedAdvertisement().set(text, 100, device));
        messaging.close();

        renderText("{ status: \"sent\" }");
    }

    // Command
    public static void cmd(String text) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.command", new CommandAdvertisement().set(text));
        messaging.close();

        renderText("{ status: \"sent\" }");
    }

    //////////////////////////////////////////
    /// Devices
    //////////////////////////////////////////

    public static void device(String uuid) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        try {

            messaging.subscribe("event.devices.responseinventory");
            messaging.start();

            messaging.broadcast("event.devices.getinventory", getInventoryAdvertisement.set(uuid));

            final JsonEnvelope envelope = messaging.receive(5000);
            if (envelope != null)
			{
                if (envelope.getObject() instanceof ResponseDeviceInventoryAdvertisement) {
                    ResponseDeviceInventoryAdvertisement advertisement = envelope.getObject();
                    renderText(gson.toJson(advertisement.getDevices()));
                } else if (envelope.getObject() instanceof ResponseZWaveDeviceInventoryAdvertisement) {
                    ResponseZWaveDeviceInventoryAdvertisement advertisement = envelope.getObject();
                    renderText(gson.toJson(advertisement.getDevice()));
                } else if (envelope.getObject() instanceof ResponseNooliteDeviceInventoryAdvertisement) {
                    ResponseNooliteDeviceInventoryAdvertisement advertisement = envelope.getObject();
                    renderText(gson.toJson(advertisement.getDevice()));
                } else {
                    renderText("{ \"error\": \"Unknown response! Class: " + envelope.getObject().getClass() + " Response: " + envelope.getObject() + "\" }");
                }

				messaging.close();
            }
        } catch (final Throwable t) {
            messaging.close();
            renderText("{ \"error\": \"" + t.toString() + "\" }");
        }

        messaging.close();
        renderText("{ \"error\": \"no answer\" }");
    }

    //@Path("/{uuid}/{label}/{level}")
    public static void devSetLevel(String uuid, String label, String level) {
        renderText("{ status: " + sendLevelMessage(uuid, label, level) + " }");
    }

    //@Path("/{uuid}/name/{name}")
    public static void devSetName(String uuid, String name) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.devices.setname", setDeviceNameAdvertisement.set(uuid, name));
        messaging.close();

        renderText("{ status: \"sent\" }");
    }

    //@Path("/{uuid}/zone/{zone}")
    public static void devSetZone(String uuid, int zone) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.devices.setzone", setDeviceZoneAdvertisement.set(uuid, zone));
        messaging.close();

        renderText("{ status: \"sent\" }");
    }

    private static String sendLevelMessage(String uuid, String label, String value) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        try {
            messaging.broadcast("event.devices.setvalue", setDeviceLevelAdvertisement.set(uuid, label, value));
            messaging.close();

            return "sent";

        } catch (final Throwable t) {
            messaging.close();
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
    /// Status
    //////////////////////////////////////////

    public static void statusAll() {
    }

    public static void statusByName(String name){

    }
}
