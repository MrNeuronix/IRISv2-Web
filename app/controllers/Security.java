package controllers;

/**
 * IRISv2 Project
 * Author: Nikolay A. Viguro
 * WWW: iris.ph-systems.ru
 * E-Mail: nv@ph-systems.ru
 * Date: 04.01.14
 * Time: 18:35
 * License: GPL v3
 */

import models.*;

public class Security extends Secure.Security {

    static boolean authenticate(String username, String password) {
        return User.connect(username, password) != null;
    }

}
