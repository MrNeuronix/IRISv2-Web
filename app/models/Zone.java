package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Table;
@Entity
@Table(name = "zone")
public class Zone extends Model {

    public String name;
    public int num;

    // Default
    public Zone() {
    }

    public static Zone getZoneByNum(int num)
    {
        return Zone.find("byNum", num).first();
    }
}