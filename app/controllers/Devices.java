package controllers;

import models.Device;
import models.Log;
import models.ModuleStatus;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

@With(Secure.class)
public class Devices extends Controller {

    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.name);
        }
    }

    public static void index() {
        List<Device> devices = Device.findAll();
        render(devices);
    }

    public static void device(long id) {
        Device device = Device.findById(id);
        List<Log> logs = Log.find("uuid = ? order by logdate desc", device.uuid).fetch(20);
        render(device, logs);
    }

}