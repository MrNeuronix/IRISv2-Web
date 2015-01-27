package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "maps")
public class Map extends Model {

    public String name;

    @OneToOne
    public Zone zone;

    @Lob
    public byte[] file;

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