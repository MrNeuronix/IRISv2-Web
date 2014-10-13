package models;

/**
 * IRISv2 Project
 * Author: Nikolay A. Viguro
 * WWW: iris.ph-systems.ru
 * E-Mail: nv@ph-systems.ru
 * Date: 09.10.14
 * Time: 15:05
 */

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name="scriptlock")
public class ScriptLock extends Model {

	// Время начала
	@Column(columnDefinition = "timestamp")
	private Date startlock;

	// Время конца
	@Column(columnDefinition = "timestamp")
	private Date endlock;

	// Заголовок
	private String title;

}