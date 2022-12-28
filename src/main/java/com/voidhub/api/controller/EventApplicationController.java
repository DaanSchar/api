package com.voidhub.api.controller;

import com.voidhub.api.form.EventApplicationForm;
import com.voidhub.api.service.EventApplicationService;
import com.voidhub.api.util.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/events/")
public class EventApplicationController {

    private @Autowired EventApplicationService eventApplicationService;

    @PostMapping("{event_id}/apply")
    public ResponseEntity<Message> apply(@PathVariable(name = "event_id") UUID eventId, Principal principal) {
        return eventApplicationService.apply(eventId, principal.getName());
    }

    @PostMapping("{event_id}/apply_without_account")
    public ResponseEntity<Message> applyWithoutAuthentication(
            @PathVariable(name = "event_id") UUID eventId,
            @Valid @RequestBody EventApplicationForm form
    ) {
        return eventApplicationService.apply(eventId, form);
    }


}
