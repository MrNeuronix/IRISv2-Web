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
@Table(name="messages")
public class Message extends Model {

    @Column(columnDefinition = "timestamp")
    public Timestamp time;

    public String subject;
    public String sender;
    @Column(name="class")
    public String eclass;

    @Column(columnDefinition = "TEXT")
    public String json;

    // Default
    public Message() {
    }
}