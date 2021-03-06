package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.controller.exceptions.ControllerException;
import org.udg.pds.springtodo.entity.Group;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Task;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.serializer.JsonDateDeserializer;
import org.udg.pds.springtodo.service.GroupService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

// This class is used to process all the authentication related URLs
@RequestMapping(path="/groups")
@RestController
public class GroupController extends BaseController{

    @Autowired
    GroupService groupService;

    @PostMapping(consumes = "application/json")
    public IdObject addGroup(HttpSession session, @Valid @RequestBody GroupController.R_Group group) {

        Long ownerId = getLoggedUser(session);

        return groupService.addGroup(group.name, group.description, ownerId);
    }

    @PostMapping(path="/{id}/members")
    public String addUser(@RequestBody Long newMemberId, HttpSession session,
                          @PathVariable("id") Long groupId) {

        Long ownerId = getLoggedUser(session);

        groupService.addUserToGroup(ownerId, newMemberId, groupId);

        return BaseController.OK_MESSAGE;
    }

    @GetMapping
    @JsonView(Views.Private.class)
    public Collection<Group> listAllGroups(HttpSession session,
                                         @RequestParam(value = "from", required = false) Date from) {
        Long ownerId = getLoggedUser(session);

        return groupService.getGroups(ownerId);
    }


    static class R_Group {

        @NotNull
        public String name;

        @NotNull
        public String description;
    }
}
