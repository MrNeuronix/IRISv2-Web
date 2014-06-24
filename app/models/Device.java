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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;
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

    @Transient
    private transient List<DeviceValues> values;

    // Default
    public Device() {
    }

    public List<DeviceValues> getValues()
    {
		values = DeviceValues.find("byUuid", uuid).fetch();
        return values;
    }

	public DeviceValues getValue(String value) {

		values = DeviceValues.find("byUuid", uuid).fetch();

		for (DeviceValues zvalue : values) {
			if (zvalue.getLabel().equals(value)) {
				return zvalue;
			}
		}
		return null;
	}

}