package models;

/**
 * IRISv2 Project
 * Author: Nikolay A. Viguro
 * WWW: iris.ph-systems.ru
 * E-Mail: nv@ph-systems.ru
 * Date: 12.10.14
 * Time: 20:28
 */

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name="commands")
public class Command extends Model {

    public String title;
    public String script;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "data_key", nullable = false)
    public Map<String, String> data;

    // Default
    public Command() {
    }
}