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


@Entity
@Table(name="user")
public class User extends Model {

    public String email;
    public String name;
    public String password;

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public static User connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }
}