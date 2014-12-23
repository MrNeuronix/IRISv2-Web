package controllers;

import models.Command;
import models.Event;
import models.User;
import org.apache.commons.io.FilenameUtils;
import other.common.messaging.JsonEnvelope;
import other.common.messaging.JsonMessaging;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import ru.iris.common.messaging.model.events.*;

import java.io.File;
import java.util.*;

@With(Secure.class)
public class Commands extends Controller {

    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.name);
        }
    }

    public static void index() {

		List<Command> commands = Command.findAll();
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
		List<File> scriptsFile = null;

		EventListScriptsAdvertisement advertisement = new EventListScriptsAdvertisement();
		advertisement.setCommand(true);

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

		render(commands, scripts);
	}

	public static void add(String script, String title, HashMap<String, String> data) {

		Map<String, String> newdata = new HashMap<>();
		int size = data.size()/2;

		for(int i = 0; i < size; ++i)
		{
			String key = data.get("key-"+i);
			String value = data.get("value-"+i);

			newdata.put(key, value);
		}

		Command command = new Command();

		command.script = script;
		command.title = title;
		command.data = newdata;

		command.save();

		index();
	}

	public static void delete(Long id) {

		Command command = Command.findById(id);
		command.delete();

		index();
	}

	public static void list()
	{
		List<Command> commands = Command.findAll();
		renderJSON(commands);
	}

	public static void deleteFile(Long id) {

		Event event = Event.findById(id);
		String file = event.script;

		EventRemoveScriptAdvertisement advertisement = new EventRemoveScriptAdvertisement();
		advertisement.setName(file);
		advertisement.setCommand(true);

		// notify events module to delete script and reload events
		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
		messaging.broadcast("event.script.delete", advertisement);

		index();
	}

	public static void edit(String name) {

		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

		EventGetScriptAdvertisement advertisement = new EventGetScriptAdvertisement();
		advertisement.setName(name);
		advertisement.setCommand(true);
		String body = null;

		try {
			JsonEnvelope envelope = messaging.request("event.script.get", advertisement);
			EventResponseGetScriptAdvertisement advertisement2 = envelope.getObject();
			body = advertisement2.getBody();

		} catch (final Throwable t) {
			renderText("{ \"error\": \"" + t.getMessage() + "\" }");
		}

		render(name, body);
	}

	public static void editSave(String name, String jscode) {

		JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

		EventResponseSaveScriptAdvertisement advertisement = new EventResponseSaveScriptAdvertisement(jscode);
		advertisement.setName(name);
		advertisement.setCommand(true);

		try {
			messaging.broadcast("event.script.save", advertisement);
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