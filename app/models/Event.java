package models;

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "events")
public class Event extends Model
{
	public String subject;
	public String script;

	@Column(name = "is_enabled")
	public boolean isEnabled;

	public Event()
	{}
}