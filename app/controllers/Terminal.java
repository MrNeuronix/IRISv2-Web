package controllers;

import models.Map;
import models.MapDevice;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

@With(Secure.class)
public class Terminal extends Controller {

    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.name);
        }
    }

    public static void indexMap() {

        render();
    }

    public static void indexMap(Long id) {

        Map map = Map.findById(id);
        List<MapDevice> devices = MapDevice.find("byMapid", id).fetch();

        render(map, devices);
    }

}