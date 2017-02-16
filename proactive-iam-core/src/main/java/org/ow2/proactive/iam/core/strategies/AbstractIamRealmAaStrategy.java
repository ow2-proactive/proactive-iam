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
package org.ow2.proactive.iam.core.strategies;

import java.util.Map;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.AuthorizingRealmWrapper;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.util.StringUtils;


/**
 * @author ActiveEon Team
 * @since 09/02/17
 */
public abstract class AbstractIamRealmAaStrategy {

    private String realmName;

    public AbstractIamRealmAaStrategy() {

    }

    public AbstractIamRealmAaStrategy(String realmName) {
        this.realmName = realmName;
    }

    protected String getRealmName() {
        return this.realmName;
    }

    protected boolean isRealmSpecified() {
        return StringUtils.isEmpty(realmName);
    }

    protected AuthenticationInfo getAuthenticationInfoFromGivenRealm(Map<String, Realm> realms,
            AuthenticationToken token) {
        if (isRealmSpecified()) {
            return realms.get(getRealmName()).getAuthenticationInfo(token);
        }
        return null;
    }

    protected AuthorizationInfo getAuthorizationInfoFromGivenRealm(Map<String, Realm> realms,
            PrincipalCollection principals) {
        if (isRealmSpecified()) {
            return new AuthorizingRealmWrapper((AuthorizingRealm) realms.get(getRealmName())).doGetAuthorizationInfo(principals);
        }
        return null;
    }

}
