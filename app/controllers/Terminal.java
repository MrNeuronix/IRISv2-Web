package controllers;

import models.*;
import play.mvc.Controller;

import java.util.List;

public class Terminal extends Controller {

    public static void index() {

        render();
    }

    public static void list() {

        List<Map> maps = Map.findAll();
        render(maps);
    }

    public static void indexMap(Long id) {

        Map map = Map.findById(id);
        List<MapDevice> devices = MapDevice.find("byMapid", id).fetch();

        render(map, devices);
    }

}