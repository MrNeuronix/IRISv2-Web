package controllers;

import models.Speaks;
import org.junit.Before;
import play.mvc.Controller;
import ru.iris.common.messaging.JsonMessaging;
import ru.iris.common.messaging.model.CommandAdvertisement;
import ru.iris.common.messaging.model.SpeakAdvertisement;
import ru.iris.common.messaging.model.SpeakRecognizedAdvertisement;

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

    public static void queue(String device)  {
        Speaks queue = Speaks.find("device=? and isActive=true", device).first();
        render(queue);
    }

    // Speak text on terminal
    public static void say(String device, String text) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.speak", new SpeakAdvertisement().set(text, 100, device));
        messaging.close();

        render("{ status: \"sent\" }");
    }

    // Recognized text from terminal
    public static void recognized(String device, String text)  {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.speak.recognized", new SpeakRecognizedAdvertisement().set(text, 100, device));
        messaging.close();

        render("{ status: \"sent\" }");
    }

    // Command
    public static void cmd(String text) {

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.command", new CommandAdvertisement().set(text));
        messaging.close();

        render("{ status: \"sent\" }");
    }
}
