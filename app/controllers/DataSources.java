package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Command;
import models.DataSource;
import models.User;
import other.common.messaging.JsonMessaging;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import ru.iris.common.datasource.model.GoogleCalendarModel;
import ru.iris.common.datasource.model.VKModel;
import ru.iris.common.messaging.model.events.EventChangesAdvertisement;
import ru.iris.common.messaging.model.tasks.TaskSourcesChangesAdvertisement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@With(Secure.class)
public class DataSources extends Controller {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.name);
        }
    }

    public static void index() {

		List<DataSource> sources = DataSource.findAll();
        render(sources);
    }

    public static void add(String title, String type, String enabled, HashMap<String, String> data) {

        Map<String, String> newdata = new HashMap<>();
        int size = data.size()/2;

        for(int i = 0; i < size; ++i)
        {
            String key = data.get("key-"+i);
            String value = data.get("value-"+i);

            newdata.put(key, value);
        }

        String obj = "";

        if(type.equals("vk"))
        {
            VKModel model = new VKModel();
            model.setAccesstoken(newdata.get("accesstoken"));
            model.setClientid(Integer.valueOf(newdata.get("clientid")));
            model.setPassword(newdata.get("password"));
            model.setUsername(newdata.get("username"));
            model.setSecretkey(newdata.get("secretkey"));

            obj = gson.toJson(model);
        }
        else if (type.equals("gcal"))
        {
            GoogleCalendarModel model = new GoogleCalendarModel();
            model.setUsername(newdata.get("username"));
            model.setPassword(newdata.get("password"));
            model.setFeedUrl(newdata.get("feedurl"));

            obj = gson.toJson(model);
        }

        DataSource dataSource = new DataSource();
        dataSource.title = title;
        dataSource.enabled = enabled.equals("on");
        dataSource.type = type;
        dataSource.obj = obj;

        dataSource.save();

        // notify events module to reload sources
        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.scheduler.reload.sources", new TaskSourcesChangesAdvertisement());

        index();
    }

    public static void delete(Long id) {

        DataSource dataSource = DataSource.findById(id);
        dataSource.delete();

        // notify events module to reload sources
        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
        messaging.broadcast("event.scheduler.reload.sources", new TaskSourcesChangesAdvertisement());

        index();
    }

}