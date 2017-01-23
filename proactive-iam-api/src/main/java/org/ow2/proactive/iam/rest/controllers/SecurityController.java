/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.iam.rest.controllers;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.ow2.proactive.iam.rest.commands.LoginCommand;
import org.ow2.proactive.iam.rest.validators.LoginValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * @author ActiveEon Team
 * @since 18/01/17
 */
@Controller
@RequestMapping("/")
public class SecurityController {

    private final LoginValidator loginValidator;

    @Autowired
    public SecurityController(LoginValidator loginValidator) {
        this.loginValidator = loginValidator;
    }

    @RequestMapping(value = "/")
    public String showMainPage(Model model, @ModelAttribute LoginCommand command) {
        return "redirect:/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLoginForm(Model model, @ModelAttribute LoginCommand command) {
        return "login";
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String showUserHome(Model model, @ModelAttribute LoginCommand command) {
        return "home";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(Model model, @ModelAttribute LoginCommand command, BindingResult errors) {
        loginValidator.validate(command, errors);

        if (errors.hasErrors()) {
            return showLoginForm(model, command);
        }

        UsernamePasswordToken token = new UsernamePasswordToken(command.getUsername(),
                                                                command.getPassword(),
                                                                command.isRememberMe());
        try {
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            errors.reject("error.login.generic", "Invalid username or password.  Please try again.");
        }

        if (errors.hasErrors()) {
            return showLoginForm(model, command);
        } else {
            //return new ModelAndView("home");
            return "redirect:/home";
        }
    }

    @RequestMapping("/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }
}
