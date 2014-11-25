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
        String switchdata = null;
        Device device = Device.findById(id);
        List<Log> logs = Log.find("uuid = ? order by logdate desc", device.uuid).fetch(20);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -24);

        List<SensorData> temp = SensorData.find("uuid = ? and logdate >= ? and logdate <= ?",
                device.uuid,
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

            // temp sensor
            if(device.getValue("sensorname") != null
                    && (device.getValue("sensorname").value.equals("PT111") || device.getValue("sensorname").value.equals("PT112"))
                    && sensorData.sensor.equals("Temperature"))
            {
                if(tempdata == null)
                    tempdata = "[Date.UTC(" + year + ", " + month + ", " + day + ", " + hour + ", " + min + "), " + sensorData.value + "]";
                else
                    tempdata += ", [Date.UTC(" + year + ", " + month + ", " + day + ", " + hour + ", " + min + "), " + sensorData.value + "]";
            }

            // humi sensor
            if(device.getValue("sensorname") != null && device.getValue("sensorname").value.equals("PT111") && sensorData.sensor.equals("Humidity"))
            {
                if(humidata == null)
                    humidata = "[Date.UTC(" + year + ", " + month + ", " + day + ", " + hour + ", " + min + "), " + sensorData.value + "]";
                else
                    humidata += ", [Date.UTC(" + year + ", " + month + ", " + day + ", " + hour + ", " + min + "), " + sensorData.value + "]";
            }

            // switch
            if(device.internaltype.equals("switch") && sensorData.sensor.equals("Switch"))
            {
                byte i = 0;

                if(sensorData.value.equals("ON"))
                    i = 1;
                else
                    i = 0;

                if(switchdata == null)
                    switchdata = "[Date.UTC(" + year + ", " + month + ", " + day + ", " + hour + ", " + min + "), " + i + "]";
                else
                    switchdata += ", [Date.UTC(" + year + ", " + month + ", " + day + ", " + hour + ", " + min + "), " + i + "]";
            }
        }

        render(device, logs, tempdata, humidata, switchdata);
    }

}