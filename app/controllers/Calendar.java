package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Device;
import models.Log;
import models.Task;
import models.User;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import other.common.messaging.JsonEnvelope;
import other.common.messaging.JsonMessaging;
import other.json.Event;
import play.data.binding.As;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import ru.iris.common.messaging.model.command.CommandAdvertisement;
import ru.iris.common.messaging.model.events.EventListScriptsAdvertisement;
import ru.iris.common.messaging.model.events.EventResponseListScriptsAdvertisement;
import ru.iris.common.messaging.model.tasks.TaskChangesAdvertisement;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@With(Secure.class)
public class Calendar extends Controller {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

    public static void indexCron() {

        List<Task> events = Task.find("enabled = ? and showInCalendar = ?", true, false).fetch();

        render(events);
    }

    public static void addCronForm() {

        List<File> scriptsFile = null;

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
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

        render(scripts);
    }

    public static void addCronEvent(String name, String desc, String script, String enabled, String period) throws ParseException
    {
        period = "0 " + period;
        String numbers[] = period.split(" ");

        if(numbers[4].equals("*") && numbers[5].equals("*"))
            numbers[5] = "?";

        Task task = new Task();

        CommandAdvertisement adv = new CommandAdvertisement();
        adv.setScript(script);

        task.title = name;
        task.text = desc;
        task.source = "user";
        task.obj = gson.toJson(adv);
        task.subject = "event.command";
        task.period = StringUtils.join(numbers, " ");
        task.enabled = enabled.equals("on");
        task.showInCalendar = false;
        task.startdate = new Timestamp(new Date().getTime());
        task.clazz = "ru.iris.scheduler.jobs.SendCommandAdvertisementJob";
        task.script = script;

        task.save();

        // notify scheduler to reload tasks
        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.scheduler.reload.tasks", new TaskChangesAdvertisement());

        indexCron();
    }

    public static void deleteCronEvent(long id)
    {
        Task task = Task.findById(id);
        task.delete();

        // notify scheduler to reload tasks
        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.scheduler.reload.tasks", new TaskChangesAdvertisement());

        indexCron();
    }

    public static void events(String start, String end) throws ParseException
    {
        DateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");

        List<Task> tasks = Task.find("enabled = ? and showInCalendar = ? and startdate between ? and ?", true, true, dfs.parse(start), dfs.parse(end)).fetch();
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

        List<File> scriptsFile = null;

        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
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

        if(task != null)
        {
            render(task, scripts);
        }
        else
        {
            renderText("Event not found!");
        }
    }

    public static void addForm(String name, long start, long end)
    {
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

        render(name, start, end, scripts);
    }

    public static void addEvent(String name, String desc,
                                @As("dd.MM.yyyy HH:mm:ss") Date start, @As("dd.MM.yyyy HH:mm:ss") Date end,
                                String script, String enabled, String period
    ) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = sdf.parse(period);
        long interval = date.getTime();

        Task task = new Task();
        task.startdate = new Timestamp(start.getTime());
        task.enddate = new Timestamp(end.getTime());

        CommandAdvertisement adv = new CommandAdvertisement();
        adv.setScript(script);

        task.title = name;
        task.text = desc;
        task.source = "user";
        task.obj = gson.toJson(adv);
        task.subject = "event.command";
        task.period = String.valueOf(interval);
        task.enabled = enabled.equals("on");
        task.showInCalendar = false;
        task.script = script;
        task.clazz = "ru.iris.scheduler.jobs.SendCommandAdvertisementJob";

        task.save();

        // notify scheduler to reload tasks
        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.scheduler.reload.tasks", new TaskChangesAdvertisement());

        renderHtml("<html><script>window.close()</script></html>");
    }

    public static void deleteEvent(long id)
    {
        Task task = Task.findById(id);
        task.delete();

        // notify scheduler to reload tasks
        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.scheduler.reload.tasks", new TaskChangesAdvertisement());

        renderHtml("<html><script>window.close()</script></html>");
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

            // notify scheduler to reload tasks
            JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
            messaging.broadcast("event.scheduler.reload.tasks", new TaskChangesAdvertisement());

            renderText("ok");
        }

        renderText("not found");
    }
}