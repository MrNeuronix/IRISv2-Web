package controllers;

import models.Command;
import models.Task;
import play.mvc.Controller;

import java.util.List;

public class Data extends Controller {

    public static void events()
    {
        List<Task> tasks = Task.find("enabled = ?", true).fetch();
        renderJSON(tasks);
    }

    public static void commandsList()
    {
        List<Command> commands = Command.findAll();
        renderJSON(commands);
    }

}