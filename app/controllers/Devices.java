package controllers;

import models.*;
import other.common.messaging.JsonMessaging;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import ru.iris.common.messaging.model.devices.noolite.BindRXChannelAdvertisment;
import ru.iris.common.messaging.model.devices.noolite.BindTXChannelAdvertisment;
import ru.iris.common.messaging.model.devices.noolite.UnbindRXChannelAdvertisment;
import ru.iris.common.messaging.model.devices.noolite.UnbindTXChannelAdvertisment;
import ru.iris.common.messaging.model.devices.zwave.ZWaveAddNodeRequest;
import ru.iris.common.messaging.model.devices.zwave.ZWaveCancelCommand;
import ru.iris.common.messaging.model.devices.zwave.ZWaveRemoveNodeRequest;

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

        List<Zone> zones = Zone.findAll();

        render(device, logs, tempdata, humidata, switchdata, zones);
    }

    public static void associationIndex()
    {
        render();
    }

    public static void associationNoolite(String nooaction, int channel)
    {
        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        //event.devices.noolite.tx.bindchannel
        //event.devices.noolite.tx.unbindchannel
        //event.devices.noolite.rx.bindchannel
        //event.devices.noolite.rx.unbindchannel

        switch (nooaction) {
            case "pc-assoc": {
                BindTXChannelAdvertisment advertisment = new BindTXChannelAdvertisment();
                advertisment.setChannel(channel);
                messaging.broadcast("event.devices.noolite.tx.bindchannel", advertisment);
                break;
            }
            case "pc-deassoc": {
                UnbindTXChannelAdvertisment advertisment = new UnbindTXChannelAdvertisment();
                advertisment.setChannel(channel);
                messaging.broadcast("event.devices.noolite.tx.unbindchannel", advertisment);
                break;
            }
            case "rx-assoc": {
                BindRXChannelAdvertisment advertisment = new BindRXChannelAdvertisment();
                advertisment.setChannel(channel);
                messaging.broadcast("event.devices.noolite.rx.bindchannel", advertisment);
                break;
            }
            case "rx-deassoc": {
                UnbindRXChannelAdvertisment advertisment = new UnbindRXChannelAdvertisment();
                advertisment.setChannel(channel);
                messaging.broadcast("event.devices.noolite.rx.unbindchannel", advertisment);
                break;
            }
            default:
                renderText("Unknown command type: " + nooaction);
                break;
        }

        index();
    }

    public static void associationZWave(String zwaction, int channel)
    {
        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        //event.devices.zwave.node.add
        //event.devices.zwave.node.remove
        //event.devices.zwave.node.cancel

        switch (zwaction) {
            case "assoc":
                messaging.broadcast("event.devices.zwave.node.add", new ZWaveAddNodeRequest());
                break;
            case "deassoc":
                messaging.broadcast("event.devices.zwave.node.remove", new ZWaveRemoveNodeRequest());
                break;
            case "cancel":
                messaging.broadcast("event.devices.zwave.node.cancel", new ZWaveCancelCommand());
                break;
            default:
                renderText("Unknown command type: " + zwaction);
                break;
        }

        index();
    }

    public static void setZone(Long id, int zone)
    {
        Device device = Device.findById(id);
        device.zone = zone;
        device = device.merge();
        device.save();

        device(id);
    }

    public static void setName(Long id, String name)
    {
        Device device = Device.findById(id);
        device.name = name;
        device = device.merge();
        device.save();

        device(id);
    }

}