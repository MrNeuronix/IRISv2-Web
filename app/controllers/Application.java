package controllers;

import play.mvc.*;

import models.*;

import java.util.List;

@With(Secure.class)
public class Application extends Controller {

    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.name);
        }
    }

    public static void index() {
        List<ModuleStatus> modules = ModuleStatus.findAll();
        render(modules);
    }

}