package models;

/**
 * IRISv2 Project
 * Author: Nikolay A. Viguro
 * WWW: iris.ph-systems.ru
 * E-Mail: nv@ph-systems.ru
 * Date: 03.12.13
 * Time: 13:57
 * License: GPL v3
 */

import play.db.jpa.Model;
import ru.iris.common.database.model.devices.DeviceValue;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="devices")
public class Device extends Model {

    public String source;
    public String uuid;
    public String type;
    public String manufname;
    public int node;
    public String status;
    public String name;
    public int zone;
    public String internaltype;
    public String productname;
    public String internalname;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "device")
    public List<DeviceValues> values = new ArrayList<>();

    // Default
    public Device() {
    }

	public DeviceValues getValue(String value) {

		for (DeviceValues zvalue : values) {
			if (zvalue.getLabel().equals(value)) {
				return zvalue;
			}
		}
		return null;
	}

    public String getZoneName()
    {
        Zone zone = Zone.find("byNum", this.zone).first();

        if(zone == null)
            return "not set";
        else
            return zone.name;
    }

}