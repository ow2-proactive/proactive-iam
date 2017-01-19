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
package org.ow2.proactive.iam.rest.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.shiro.SecurityUtils;
import org.ow2.proactive.iam.rest.model.User;
import org.springframework.stereotype.Service;


@Service("userService")
public class UserService {

    private Map<String, User> users = new HashMap<>();

    public Collection<User> findAllUsers() {
        return users.values();
    }

    public Optional<User> findByName(String name) {
        return Optional.ofNullable(users.get(name));
    }

    public void saveUser(User user) {
        users.put(user.getName(), user);
    }

    public void updateUser(User user) {
        users.put(user.getName(), user);
    }

    public void deleteUserByName(String name) {
        users.remove(name);
    }

    public void deleteAllUsers() {
        users.clear();
    }

    public User getCurrentUser() {
        final String currentUserName = (String) SecurityUtils.getSubject().getPrincipal();
        if (currentUserName != null) {
            return findByName(currentUserName).orElse(null);
        }
        return null;
    }
}
