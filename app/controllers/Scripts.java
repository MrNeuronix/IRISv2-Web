package controllers;

import models.User;
import org.apache.commons.io.FilenameUtils;
import other.common.messaging.JsonEnvelope;
import other.common.messaging.JsonMessaging;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import models.Event;
import ru.iris.common.messaging.model.events.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@With(Secure.class)
public class Scripts extends Controller {

    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.name);
        }
    }

    public static void index() {

		List<Event> events = Event.findAll();
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
		List<File> scriptsFile = null;

		EventListScriptsAdvertisement advertisement = new EventListScriptsAdvertisement();

		try {
			JsonEnvelope envelope = messaging.request("event.script.list", advertisement);
			EventResponseListScriptsAdvertisement responseAdv = envelope.getObject();
			scriptsFile = responseAdv.getScripts();
		} catch (final Throwable t) {
			renderText("{ \"error\": \"" + t.getMessage() + "\" }");
		}

		List<String> scripts = new ArrayList<>();

		for(File script: scriptsFile)
		{
			scripts.add(FilenameUtils.getName(script.getName()));
		}

        render(events, scripts);
	}

	public static void add(String key, String script, String enabled) {

		Event event = new Event();
		event.subject = key;
		event.script = script;
		event.isEnabled = enabled.equals("on");
		event.save();

		// notify events module to reload events
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
		messaging.broadcast("events.changed", new EventChangesAdvertisement());

		index();
	}

	public static void delete(Long id) {

		Event event = Event.findById(id);
		event.delete();

		// notify events module to reload events
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
		messaging.broadcast("events.changed", new EventChangesAdvertisement());

		index();
	}

	public static void edit(String name) {

		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

		EventGetScriptAdvertisement advertisment = new EventGetScriptAdvertisement();
		advertisment.setName(name);
		String body = null;

		try {
			JsonEnvelope envelope = messaging.request("event.script.get", advertisment);
			EventResponseGetScriptAdvertisement advertisement = envelope.getObject();
			body = advertisement.getBody();

		} catch (final Throwable t) {
			renderText("{ \"error\": \"" + t.getMessage() + "\" }");
		}

		render(name, body);
	}

	public static void editSave(String name, String jscode) {

		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

		EventResponseSaveScriptAdvertisement advertisment = new EventResponseSaveScriptAdvertisement(jscode);
		advertisment.setName(name);

		try {
			messaging.broadcast("event.script.save", advertisment);
		} catch (final Throwable t) {
			renderText("{ \"error\": \"" + t.getMessage() + "\" }");
		}

		index();
	}

	public static void saveNewScriptForm()
	{
		render();
	}

}