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
import java.sql.Timestamp;

@Entity
@Table(name="devicesvalues")
public class DeviceValues extends Model {

    public String label;
    public String value;
    public String type;
    public String units;

	@Column(name = "is_readonly")
    public boolean isReadonly;

    // Default
    public DeviceValues() {
    }
}