package controllers;

import models.Device;
import models.Log;
import models.Task;
import models.User;
import other.json.Event;
import play.data.binding.As;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@With(Secure.class)
public class Calendar extends Controller {

    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.name);
        }
    }

    public static void index() {
        render();
    }

    public static void events(String start, String end) throws ParseException
    {
        DateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");

        List<Task> tasks = Task.find("enabled = ? and startdate between ? and ?", true, dfs.parse(start), dfs.parse(end)).fetch();
        List<Event> events = new ArrayList<>();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        for(Task task : tasks)
        {
            Event event = new Event();
            event.setId(task.id);
            event.setTitle(task.title);
            event.setStart(df.format(task.startdate));
            event.setEnd(df.format(task.enddate));
            event.setUrl("/calendar/event/"+task.id);

            events.add(event);
        }

        renderJSON(events);
    }

    public static void showEvent(Long id)
    {
        Task task = Task.findById(id);

        if(task != null)
        {
            render(task);
        }
        else
        {
            renderText("Event not found!");
        }
    }

    public static void saveEvent(Long id)
    {
        //TODO
    }

    public static void moveEvent(long id, @As("yyyy-MM-dd'T'HH:mm:ss") Date start, @As("yyyy-MM-dd'T'HH:mm:ss") Date end)
    {
        Task task = Task.findById(id);

        if(task != null)
        {
            task.startdate = new Timestamp(start.getTime());
            task.enddate = new Timestamp(end.getTime());

            task = task.merge();
            task.save();

            renderText("ok");
        }

        renderText("not found");
    }
}