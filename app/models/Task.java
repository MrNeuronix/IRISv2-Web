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
	private Timestamp startdate;

	// Время конца
	@Column(columnDefinition = "timestamp")
	private Timestamp enddate;

	// Заголовок задачи
	private String title;

	// Текст задачи
	private String text;

	// Тип таска:
	// 1 - Однократный запуск
	// 2 - Многократный запуск от и до с интервалом
	private String type;

	// Адрес, куда слать (например, event.command)
	private String subject;

	// Тут хранится сериализованный в JSON advertisement
	private String obj;

	// Интервал, с которой будет запускаться задача
	private String period;

	// Источник данных
	private String source;

	// Показывать ли в календаре?
	private boolean showInCalendar;

	// Активна ли?
	private boolean enabled;
}