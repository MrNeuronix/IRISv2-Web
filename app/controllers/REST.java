package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Speaks;
import play.mvc.Controller;
import ru.iris.common.Config;
import ru.iris.common.SQL;
import ru.iris.common.messaging.JsonEnvelope;
import ru.iris.common.messaging.JsonMessaging;
import ru.iris.common.messaging.model.command.CommandAdvertisement;
import ru.iris.common.messaging.model.devices.*;
import ru.iris.common.messaging.model.devices.noolite.ResponseNooliteDeviceInventoryAdvertisement;
import ru.iris.common.messaging.model.devices.zwave.ResponseZWaveDeviceInventoryAdvertisement;
import ru.iris.common.messaging.model.speak.SpeakAdvertisement;
import ru.iris.common.messaging.model.speak.SpeakRecognizedAdvertisement;
import ru.iris.scheduler.Task;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
            if (envelope != null) {
                if (envelope.getObject() instanceof ResponseDeviceInventoryAdvertisement) {
                    messaging.close();
                    ResponseDeviceInventoryAdvertisement advertisement = envelope.getObject();
                    renderText(gson.toJson(advertisement.getDevices()));
                } else if (envelope.getObject() instanceof ResponseZWaveDeviceInventoryAdvertisement) {
                    messaging.close();
                    ResponseZWaveDeviceInventoryAdvertisement advertisement = envelope.getObject();
                    renderText(gson.toJson(advertisement.getDevice()));
                } else if (envelope.getObject() instanceof ResponseNooliteDeviceInventoryAdvertisement) {
                    messaging.close();
                    ResponseNooliteDeviceInventoryAdvertisement advertisement = envelope.getObject();
                    renderText(gson.toJson(advertisement.getDevice()));
                } else {
                    messaging.close();
                    renderText("{ \"error\": \"Unknown response! Class: " + envelope.getObject().getClass() + " Response: " + envelope.getObject() + "\" }");
                }
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

        ArrayList<Task> allTasks = new ArrayList<Task>();
        Config config = new Config();
        SQL sql = new SQL();

        try {
            ResultSet rs = sql.select("SELECT id FROM scheduler WHERE enabled='1' AND language='" + config.getConfig().get("language") + "' ORDER BY ID ASC");

            while (rs.next()) {
                allTasks.add(new Task(rs.getInt("id")));
            }

            rs.close();

        } catch (Exception e) {
            renderText(e);
        }

        renderText(gson.toJson(allTasks));
    }

    public static void schedulerGet(int id) throws IOException {

        Task task = null;

        try {
            task = new Task(id);
        } catch (SQLException e) {
            renderText("{ \"error\": \"task " + id + " not found\" }");
        }

        renderText(gson.toJson(task));
    }

    //////////////////////////////////////////
    /// Status
    //////////////////////////////////////////

    public static void statusAll() {

        SQL sql = new SQL();
        ResultSet rs = sql.select("SELECT * FROM modulestatus");

        HashMap<String, Object> obj = new HashMap<String, Object>();
        ArrayList result = new ArrayList();

        try {
            while (rs.next()) {
                obj.put("name", rs.getString("name"));
                obj.put("state", rs.getString("state"));
                obj.put("lastseen", rs.getString("lastseen"));

                result.add(obj.clone());
                obj.clear();
            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        renderText(gson.toJson(result));
    }

    public static void statusByName(String name){

        SQL sql = new SQL();
        ResultSet rs = sql.select("SELECT * FROM modulestatus WHERE name='" + name + "'");

        HashMap<String, Object> result = new HashMap<String, Object>();

        try {
            while (rs.next()) {
                result.put("id", rs.getInt("id"));
                result.put("name", rs.getString("name"));
                result.put("state", rs.getString("state"));
                result.put("lastseen", rs.getString("lastseen"));
            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        renderText(gson.toJson(result));
    }
}
