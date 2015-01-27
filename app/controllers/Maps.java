package controllers;

import models.Map;
import models.User;
import models.Zone;
import play.data.Upload;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@With(Secure.class)
public class Maps extends Controller {

    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.name);
        }
    }

    public static void index() {

        List<Zone> zones = Zone.findAll();
        List<Map> maps = Map.findAll();

        render(zones, maps);
    }

    public static void add(String name, int zone, Upload map) throws FileNotFoundException {

        Map newmap = new Map();
        newmap.name = name;
        newmap.zone = Zone.getZoneByNum(zone);
        newmap.file = map.asBytes();
        newmap.save();

        index();
    }

    public static void editForm(Long id)
    {
        Map map = Map.findById(id);

        render(map);
    }

    public static void edit(Long id, String name, int zone, Upload map)
    {
        Map newmap = Map.findById(id);

        newmap.name = name;
        newmap.zone = Zone.getZoneByNum(zone);

        if(map != null)
            newmap.file = map.asBytes();

        newmap = newmap.merge();
        newmap.save();

        editForm(id);
    }

    public static void delete(Long id)
    {
        Map map = Map.findById(id);
        map.delete();

        index();
    }

    public static void render(Long id)
    {
        Map map = Map.findById(id);
        renderBinary(new ByteArrayInputStream(map.file), map.file.length);
    }
}