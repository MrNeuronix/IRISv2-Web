package jobs;

import models.Device;
import models.MapDevice;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import java.util.List;

@OnApplicationStart(async = true)
public class CheckAndCreateMapDevices extends Job
{
	public void doJob()
	{
		List<Device> devices = Device.findAll();

		for(Device device : devices) {

			// check if we need to create MapDevice
			MapDevice mapdevice = MapDevice.find("byDevice", device).first();

			if (mapdevice == null) {

				Logger.info("Create new MapDevice for device " + device.internalname);

				mapdevice = new MapDevice();
				mapdevice.device = device;
				mapdevice.save();
			}
		}
	}
}
