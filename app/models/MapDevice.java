package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "map_devices")
public class MapDevice extends Model {

    @ManyToOne
    public Map map;

    @OneToOne
    public Device device;

    public int x;
    public int y;

    // Default
    public MapDevice() {
    }
}