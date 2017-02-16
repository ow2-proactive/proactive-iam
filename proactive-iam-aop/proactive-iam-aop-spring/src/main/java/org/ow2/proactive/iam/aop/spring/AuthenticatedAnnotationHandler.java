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
package org.ow2.proactive.iam.aop.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.ow2.proactive.iam.annotation.RequiresAuthentication;
import org.ow2.proactive.iam.core.AuthorizationContext;
import org.ow2.proactive.iam.core.AuthorizationInfo;
import org.ow2.proactive.iam.core.exception.IamAuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;

import lombok.extern.log4j.Log4j2;


/**
 * @author ActiveEon Team
 * @since 02/02/17
 */
@Component
@Aspect
@Log4j2
public class AuthenticatedAnnotationHandler extends AnnotationHandler {

    private final static String SESSION_ID = "sessionId";

    private final static String JSESSION_ID = "JSESSIONID";

    @Value("${pa.iam.server.url}")
    private String iamServerUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @Around("anyPublicMethod() && @annotation(authenticationAnnotation) && args(request,..)")
    public Object assertAuthentication(ProceedingJoinPoint joinPoint, RequiresAuthentication authenticationAnnotation,
            HttpServletRequest request) throws Throwable {

        if (Strings.isNullOrEmpty(iamServerUrl)) {
            throw new IllegalStateException("IAM server URL is not initialized");
        }

        AuthorizationInfo info;

        try {
            String sessionId = request.getHeader(SESSION_ID) == null ? request.getParameter(JSESSION_ID)
                                                                     : request.getHeader(SESSION_ID);

            if (StringUtils.isEmpty(sessionId)) {
                log.error("session id is not available in the request");
                throw new IamAuthenticationException("Not authorized");
            }

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("sessionId", sessionId);
            headers.add("Content-Type", "application/json");

            HttpEntity<Map<String, String>> requestHeaders = new HttpEntity<>(headers);

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            info = restTemplate.exchange(iamServerUrl, HttpMethod.GET, requestHeaders, AuthorizationInfo.class)
                               .getBody();

        } catch (Exception e) {
            throw new IamAuthenticationException("Exception", e);
        }

        AuthorizationContext.set(info);

        Object object = joinPoint.proceed();

        AuthorizationContext.remove();

        return object;

    }
}
