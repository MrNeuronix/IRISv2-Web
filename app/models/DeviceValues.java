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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;

@Entity
@Table(name="devicesvalues")
public class DeviceValues extends Model {

	@Transient
    public transient String uuid;

	@Transient
	@Column(name = "device_id")
	public transient int node;

    public String label;
    public String value;
    public String type;
    public String units;

	@Column(name = "is_readonly")
    public boolean isReadonly;

	@Column(name = "value_id")
	public String valueId;

	public String source;

    // Default
    public DeviceValues() {
    }

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}