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

import com.google.gson.annotations.Expose;
import play.db.jpa.Model;
import ru.iris.common.database.model.devices.DeviceValue;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="devices")
public class Device extends Model {

    @Expose
    public String source;
    @Expose
    public String uuid;
    @Expose
    public String type;
    @Expose
    public String manufname;
    @Expose
    public int node;
    @Expose
    public String status;
    @Expose
    public String name;
    @Expose
    public String friendlyname;
    @Expose
    public int zone;
    @Expose
    public String internaltype;
    @Expose
    public String productname;
    @Expose
    public String internalname;

    @Expose
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
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