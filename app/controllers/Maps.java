package controllers;

import models.*;
import play.data.Upload;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
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

        Map check = Map.find("byZone", Zone.getZoneByNum(zone)).first();

        if(check != null)
            index();

        Map newmap = new Map();
        newmap.name = name;
        newmap.zone = Zone.getZoneByNum(zone);
        newmap.file = map.asBytes();
        newmap.save();

        //reload to obtain id
        newmap = newmap.merge();

        // Attach devices to map by zone
        List<Device> devices = Device.find("byZone", zone).fetch();

        for(Device device : devices)
        {
            MapDevice mapdevice = MapDevice.find("byDevice", device).first();
            mapdevice.mapid = newmap.id;
            mapdevice.save();
        }

        index();
    }

    public static void editForm(Long id)
    {
        Map map = Map.findById(id);
        List<MapDevice> devices = MapDevice.find("byMapid", map.id).fetch();
        List<Zone> zones = Zone.findAll();

        render(map, devices, zones);
    }

    public static void edit(Long id, String name, int zone, Upload map, HashMap<Long, Integer> X, HashMap<Long, Integer> Y)
    {
        Map newmap = Map.findById(id);
        List<MapDevice> mapdevices = MapDevice.find("byMapid", newmap.id).fetch();

        if(newmap.zone.num != zone)
        {
            for(MapDevice device : mapdevices)
            {
                device.mapid = 0;
                device.save();
            }

            // Attach devices to map by zone
            List<Device> devices = Device.find("byZone", zone).fetch();

            for(Device device : devices)
            {
                MapDevice mapdevice = MapDevice.find("byDevice", device).first();
                mapdevice.mapid = newmap.id;
                mapdevice.save();
            }

            newmap.zone = Zone.getZoneByNum(zone);
        }
        if(map != null)
            newmap.file = map.asBytes();

        newmap.name = name;

        // save coordinates
        for(MapDevice device : mapdevices)
        {
            if(X.get(device.id) != 0 || Y.get(device.id) != 0) {
                device.x = X.get(device.id);
                device.y = Y.get(device.id);
                device = device.merge();
                device.save();
            }
        }

        newmap = newmap.merge();
        newmap.save();

        editForm(id);
    }

    public static void delete(Long id)
    {
        Map map = Map.findById(id);

        // Find old mapdevices
        List<MapDevice> oldmapdevices = MapDevice.find("byMapid", map.id).fetch();

        for(MapDevice olddevice : oldmapdevices)
        {
            olddevice.mapid = 0;
            olddevice.save();
        }

        map.delete();

        index();
    }

    public static void render(Long id)
    {
        Map map = Map.findById(id);
        renderBinary(new ByteArrayInputStream(map.file), map.file.length);
    }

    public static void renderOn(Long id)
    {
        MapDevice device = MapDevice.findById(id);
        renderBinary(new ByteArrayInputStream(device.iconon), device.iconon.length);
    }

    public static void renderOff(Long id)
    {
        MapDevice device = MapDevice.findById(id);
        renderBinary(new ByteArrayInputStream(device.iconoff), device.iconoff.length);
    }

}