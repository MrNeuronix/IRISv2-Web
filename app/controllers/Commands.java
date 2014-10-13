package controllers;

import models.Command;
import models.User;
import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        render(commands);
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

}