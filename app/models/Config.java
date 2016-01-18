package models;

/**
 * Created by nikolay.viguro on 22.10.2015.
 */
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "config")
public class Config extends Model
{
    public String param;
    public String value;
}