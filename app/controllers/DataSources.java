package controllers;

import models.DataSource;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

@With(Secure.class)
public class DataSources extends Controller {

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

}