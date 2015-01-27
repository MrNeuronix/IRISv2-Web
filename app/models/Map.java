package models;

import play.db.jpa.Blob;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "maps")
public class Map extends Model {

    public String name;
    public int zone;
    public Blob file;

    @OneToMany(fetch = FetchType.EAGER)
    public List<MapDevice> devices;

    // Default
    public Map() {
    }

    public List<MapDevice> getDevices()
    {
        return MapDevice.find("byMapid", id).fetch();
    }
}