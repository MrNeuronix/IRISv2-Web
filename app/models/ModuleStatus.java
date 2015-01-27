package models;

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "modulestatus")
public class ModuleStatus extends Model {
    public String internalName;
    public String name;
    public String description;
    public String status;

    @Column(columnDefinition = "timestamp")
    public Timestamp lastseen;

    // Default
    public ModuleStatus() {
    }
}