package controllers;

import models.Command;
import models.Task;
import other.json.Event;
import play.Logger;
import play.mvc.Controller;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class Data extends Controller {

    public static void events(String start, String end) throws ParseException
	{
		DateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");

        List<Task> tasks = Task.find("enabled = ? and startdate between ? and ?", true, dfs.parse(start), dfs.parse(end)).fetch();
		List<Event> events = new ArrayList<>();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

		for(Task task : tasks)
		{
			Event event = new Event();
			event.setTitle(task.title);
			event.setStart(df.format(task.startdate));
			event.setEnd(df.format(task.enddate));

			events.add(event);
		}

        renderJSON(events);
    }

    public static void commandsList()
    {
        List<Command> commands = Command.findAll();
        renderJSON(commands);
    }

}