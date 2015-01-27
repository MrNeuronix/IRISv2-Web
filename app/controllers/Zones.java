package controllers;

import models.User;
import models.Zone;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

@With(Secure.class)
public class Zones extends Controller {

    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.name);
        }
    }

    public static void index() {

        List<Zone> zones = Zone.findAll();
        render(zones);
    }

    public static void add(int num, String name)
    {
        Zone zone = new Zone();
        zone.num = num;
        zone.name = name;
        zone.save();

        index();
    }

    public static void editForm(Long id) {
        Zone zone = Zone.findById(id);
        render(zone);
    }

    public static void edit(Long id, int num, String name) {

        Zone zone = Zone.findById(id);
        zone.num = num;
        zone.name = name;

        zone = zone.merge();
        zone.save();

        index();
    }

    public static void delete(Long id) {

        Zone zone = Zone.findById(id);
        zone.delete();

        index();
    }

}