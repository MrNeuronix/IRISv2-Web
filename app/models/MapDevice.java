package models;

import play.db.jpa.Model;

import javax.persistence.*;

@Entity
@Table(name = "map_devices")
public class MapDevice extends Model {

    public long mapid;

    @OneToOne
    public Device device;

    public int x;
    public int y;
    public String label;

    @Lob
    public byte[] iconon;

    @Lob
    public byte[] iconoff;

    // Default
    public MapDevice() {
    }
}