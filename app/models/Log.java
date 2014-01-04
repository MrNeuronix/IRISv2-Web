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

import javax.persistence.*;
import play.db.jpa.Model;
import java.sql.Timestamp;

@Entity
@Table(name="log")
public class Log extends Model {

    @Column(columnDefinition = "timestamp")
    public Timestamp date;

    public String level;
    public String message;

    // Default
    public Log() {
    }
}