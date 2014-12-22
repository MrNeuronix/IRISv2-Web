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
import java.sql.Timestamp;

@Entity
@Table(name="calendar")
public class Task extends Model {

	// Время начала
	@Column(columnDefinition = "timestamp")
	public Timestamp startdate;

	// Время конца
	@Column(columnDefinition = "timestamp")
	public Timestamp enddate;

	// Заголовок задачи
	public String title;

	// Текст задачи
	public String text;

	// Адрес, куда слать (например, event.command)
	public String subject;

	// Тут хранится сериализованный в JSON advertisement
	public String obj;

	// Интервал, с которой будет запускаться задача
	public String period;

	// Источник данных
	public String source;

	// Активна ли?
	public boolean enabled;
}