package controllers;

import models.*;
import other.common.messaging.JsonMessaging;
import play.data.Upload;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import ru.iris.common.messaging.model.devices.GenericAdvertisement;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    public static void addNooDevice(Integer channel)
    {
        Device noolite = new Device();
        noolite.node = (short) (1000 + channel);
        noolite.manufname = "Nootechnika";
        noolite.productname = "Generic Switch";
        noolite.internaltype = "switch";
        noolite.internalname = "noolite/channel/" + channel;
        noolite.status = "listening";
        noolite.uuid = UUID.randomUUID().toString();
        noolite.source = "noolite";
        noolite.name = "not set";
        noolite.type = "Noolite Device";

        noolite = noolite.save();

        DeviceValues value = new DeviceValues();
        value.device = noolite;
        value.label = "type";
        value.value = "switch";

        DeviceValues value2 = new DeviceValues();
        value2.device = noolite;
        value2.label = "channel";
        value2.value = channel.toString();

        DeviceValues value3 = new DeviceValues();
        value3.device = noolite;
        value3.label = "Level";
        value3.value = "0";

        noolite.values.add(value);
        noolite.values.add(value2);
        noolite.values.add(value3);

        noolite.save();

        index();
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

        // check if we need to create MapDevice
        MapDevice mapdevice = MapDevice.find("byDevice", device).first();

        if(mapdevice == null)
        {
            mapdevice = new MapDevice();
            mapdevice.device = device;
            mapdevice = mapdevice.merge();
            mapdevice.save();
        }

        render(device, logs, tempdata, humidata, switchdata, mapdevice, zones);
    }

    public static void associationIndex()
    {
        render();
    }

    public static void associationNoolite(String nooaction, byte channel)
    {
        JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());

        //event.devices.noolite.tx.bindchannel
        //event.devices.noolite.tx.unbindchannel
        //event.devices.noolite.rx.bindchannel
        //event.devices.noolite.rx.unbindchannel

        switch (nooaction) {
            case "pc-assoc": {
                messaging.broadcast("event.devices.noolite.tx.bindchannel", new GenericAdvertisement("BindTXChannel", channel));
                break;
            }
            case "pc-deassoc": {
                messaging.broadcast("event.devices.noolite.tx.unbindchannel", new GenericAdvertisement("UnbindTXChannel", channel));
                break;
            }
            case "rx-assoc": {
                messaging.broadcast("event.devices.noolite.rx.bindchannel", new GenericAdvertisement("BindRXChannel", channel));
                break;
            }
            case "rx-deassoc": {
                messaging.broadcast("event.devices.noolite.rx.unbindchannel", new GenericAdvertisement("UnbindRXChannel", channel));
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
                messaging.broadcast("event.devices.zwave.node.add", new GenericAdvertisement("ZWaveAddNodeRequest", channel));
                break;
            case "deassoc":
                messaging.broadcast("event.devices.zwave.node.remove", new GenericAdvertisement("ZWaveRemoveNodeRequest", channel));
                break;
            case "cancel":
                messaging.broadcast("event.devices.zwave.node.cancel", new GenericAdvertisement("ZWaveCancelCommand", channel));
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

    public static void setFName(Long id, String fname)
    {
        Device device = Device.findById(id);
        device.friendlyname = fname;
        device = device.merge();
        device.save();

        device(id);
    }

    public static void setIcons(Long id, Upload on, Upload off)
    {
        MapDevice device = MapDevice.find("byDevice", Device.findById(id)).first();

        device.iconon = on.asBytes();

        if(off != null)
            device.iconoff = off.asBytes();

        device = device.merge();
        device.save();

        device(id);
    }

}