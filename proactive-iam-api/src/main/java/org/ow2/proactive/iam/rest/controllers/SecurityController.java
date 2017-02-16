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

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;
import org.ow2.proactive.iam.core.AuthorizationInfo;
import org.ow2.proactive.iam.core.exception.IamAuthenticationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.log4j.Log4j2;


/**
 * @author ActiveEon Team
 * @since 18/01/17
 */
@Controller
@Log4j2
public class SecurityController {

    @RequestMapping("/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:/login.html";
    }

    @RequestMapping({ "/", "/login.html" })
    public String root() {
        return "login";
    }

    @RequestMapping("/login")
    public String login(String username, char[] password) {
        Subject currentUser = SecurityUtils.getSubject();
        if (StringUtils.hasText(username) && password != null) {
            try {
                currentUser.login(new UsernamePasswordToken(username, password));
                if (currentUser.isAuthenticated()) {
                    Session session = currentUser.getSession();
                    // TODO need a service class to transform shiro permissions into AuthorizationInfo
                    session.setAttribute("authorization",
                                         new AuthorizationInfo("admin", "liu", session.getId().toString()));
                }
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                return "login";
            }
            return "redirect:home";
        } else {
            return "login";
        }
    }

    @RequestMapping(value = "/authentication", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AuthorizationInfo authenticate(HttpServletRequest request) {
        String requestedSessionId = request.getHeader("sessionId") == null ? request.getRequestedSessionId()
                                                                           : request.getHeader("sessionId");

        Subject subject = new Subject.Builder().sessionId(requestedSessionId).buildSubject();

        if (subject != null && subject.isAuthenticated()) {
            return (AuthorizationInfo) subject.getSession().getAttribute("authorization");
        }
        throw new IamAuthenticationException("Not authenticated!");
    }

    @RequestMapping("/home")
    @RequiresAuthentication
    public String home(HttpServletRequest request, Model model) {

        String name = "World";

        String hostname = request.getRemoteHost();
        Subject subject = new Subject.Builder().sessionId(request.getRequestedSessionId()).buildSubject();
        Session session = subject.getSession(false);

        if (subject != null && session.getHost().equals(hostname) && subject.isAuthenticated()) {
            PrincipalCollection principalCollection = subject.getPrincipals();

            if (principalCollection != null && !principalCollection.isEmpty()) {
                Collection<Map> principalMaps = subject.getPrincipals().byType(Map.class);
                if (CollectionUtils.isEmpty(principalMaps)) {
                    name = subject.getPrincipal().toString();
                } else {
                    name = (String) principalMaps.iterator().next().get("username");
                }
            }

            model.addAttribute("name", name);
            model.addAttribute("subject", subject);

            return "hello";
        } else {
            return "login";
        }

    }

    @RequestMapping("/account-info")
    @RequiresAuthentication
    @RequiresRoles("admin")
    public String account(HttpServletRequest request, Model model) {

        String name = "World";

        String hostname = request.getRemoteHost();
        Subject subject = new Subject.Builder().sessionId(request.getRequestedSessionId()).buildSubject();
        Session session = subject.getSession(false);

        if (subject != null && session.getHost().equals(hostname) && subject.isAuthenticated()) {
            PrincipalCollection principalCollection = subject.getPrincipals();

            if (principalCollection != null && !principalCollection.isEmpty()) {
                name = principalCollection.getPrimaryPrincipal().toString();
            }

            model.addAttribute("name", name);

            return "account-info";
        }

        return "login";
    }

}
