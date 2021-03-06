/*
 * Copyright 2017 Banco Bilbao Vizcaya Argentaria, S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bbva.arq.devops.ae.mirrorgate.api;

import com.bbva.arq.devops.ae.mirrorgate.dto.DashboardDTO;
import com.bbva.arq.devops.ae.mirrorgate.dto.SlackDTO;
import com.bbva.arq.devops.ae.mirrorgate.service.DashboardService;
import com.bbva.arq.devops.ae.mirrorgate.service.SlackService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/slack")
public class SlackController {

    private final DashboardService dashboardService;
    private final SlackService slackService;

    @Autowired
    public SlackController(DashboardService dashboardService, SlackService slackService) {
        this.dashboardService = dashboardService;
        this.slackService = slackService;
    }

    @RequestMapping(value = "/code-capturer",
            method = GET,
            produces = TEXT_HTML_VALUE)
    public String getSlackCode(@RequestParam("code") String code) {
        return "<html><head><script>opener.postMessage('"+code+"',document.location.origin);window.close();</script></head></html>";
    }

    @RequestMapping(value = "/channels",
            method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String>> getDashboardChannels(
            @RequestParam("dashboard") String dashboardId,
            @RequestParam("token") String token
    ) {

        if(StringUtils.isBlank(token)) {
            return ResponseEntity.badRequest().build();
        }

        DashboardDTO dashboard = dashboardService.getDashboard(dashboardId);

        if (dashboard == null) {
            return ResponseEntity.badRequest().build();
        }

        if (dashboard.getSlackToken() == null) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }

        return ResponseEntity.ok(slackService.getChannelList(dashboard.getSlackToken()));

    }

    @RequestMapping(value = "/token-generator",
            method = GET,
            produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<?> getSlackToken(
            @RequestParam("code") String code,
            @RequestParam("clientId") String clientId,
            @RequestParam("team") String team,
            @RequestParam("clientSecret") String clientSecret
    ) {

        SlackDTO notification = slackService.getToken(team, clientId, clientSecret, code);

        return notification.isOk() ?
            ResponseEntity.ok(notification.getAccess_token()) :
            ResponseEntity.status(HttpStatus.CONFLICT).body(notification.getError());
    }

}
