package controllers;

import models.Device;
import models.Log;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import models.SensorData;

import java.util.*;
import java.util.Calendar;

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

        String tempdata = null;
        String humidata = null;
        Device device = Device.findById(id);
        List<Log> logs = Log.find("uuid = ? order by logdate desc", device.uuid).fetch(20);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -24);

        List<SensorData> temp = SensorData.find("uuid = ? and sensor = ? and logdate >= ? and logdate <= ?",
                device.uuid,
                "Temperature",
                cal.getTime(),
                new Date()
        ).fetch();

        for (SensorData sensorData : temp)
        {
            Date date = sensorData.logdate;
            cal.setTime(date);

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);

            if(tempdata == null)
                tempdata = "[Date.UTC(" + year + ", " + month + ", " + day + ", " + hour + ", " + min + "), " + sensorData.value + "]";
            else
                tempdata += ", [Date.UTC(" + year + ", " + month + ", " + day + ", " + hour + ", " + min + "), " + sensorData.value + "]";
        }

        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, -24);

        List<SensorData> humi = SensorData.find("uuid = ? and sensor = ? and logdate >= ? and logdate <= ?",
                device.uuid,
                "Humidity",
                cal.getTime(),
                new Date()
        ).fetch();

        for (SensorData sensorData : humi)
        {
            Date date = sensorData.logdate;
            cal.setTime(date);

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);

            if(humidata == null)
                humidata = "[Date.UTC(" + year + ", " + month + ", " + day + ", " + hour + ", " + min + "), " + sensorData.value + "]";
            else
                humidata += ", [Date.UTC(" + year + ", " + month + ", " + day + ", " + hour + ", " + min + "), " + sensorData.value + "]";
        }

        render(device, logs, tempdata, humidata);
    }

}