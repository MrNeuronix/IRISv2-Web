package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import models.Event;

import java.util.List;

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
        render(events);
	}

	public static void add(String key, String script, String enabled) {

		Event event = new Event();
		event.subject = key;
		event.script = script;
		event.isEnabled = enabled.equals("on");
		event.save();

		index();
	}

	public static void delete(Long id) {

		Event event = Event.findById(id);
		event.delete();

		index();
	}

}