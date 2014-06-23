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

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;


@Entity
@Table(name="speaks")
public class Speaks extends Model {

    @Column(columnDefinition = "timestamp")
    public Timestamp date;

    @Column(columnDefinition = "TEXT")
    public String text;

    public Double confidence;
    public String device;

	@Column(name = "is_active")
    public boolean isActive = false;

    // Default
    public Speaks() {
    }
}