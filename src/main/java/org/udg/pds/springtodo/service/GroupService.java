package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.repository.GroupRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserService userService;

    public GroupRepository crud() {
        return groupRepository;
    }

    @Transactional
    public IdObject addGroup(String name, String description, Long ownerId) {
        try {
            User owner = userService.getUser(ownerId);

            Group group = new Group(name, description);

            group.setOwner(owner);

            owner.addGroup(group); // add this group to the list of groups which this user is the owner

            owner.addGroupMember(group); // add this group to the list of groups which this user is member

            group.addUser(owner);

            groupRepository.save(group);
            return new IdObject(group.getId());
        } catch (Exception ex) {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an ServiceException
            // We catch the normal exception and then transform it in a ServiceException
            throw new ServiceException(ex.getMessage());
        }
    }

    public Group getGroup(Long ownerId, Long id) {

        Optional<Group> g = groupRepository.findById(id);

        if (!g.isPresent()) throw new ServiceException("Group does not exist");
        if (g.get().getOwner().getId() != ownerId)
            throw new ServiceException("User does not own this group");

        return g.get();

    }

    @Transactional
    public void addUserToGroup(Long ownerId, Long newMemberId, Long groupId) {

        Group g = this.getGroup(ownerId, groupId);

        if (g.getOwner().getId() != ownerId)
            throw new ServiceException("You are not the owner of the group");

        User newMember = userService.getUser(newMemberId);
        // First check if the user we want to add to the group is already a member, if not, we add him to the group
        if (g.isMember(newMember.getId()))
            throw new ServiceException("The user you want to add to this group is already added");

        try {
            g.addUser(newMember); // add the user to the group
            newMember.addGroupMember(g); // add the group to the list of groups which the user is member

        } catch (Exception ex) {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an ServiceException
            // We catch the normal exception and then transform it in a ServiceException
            throw new ServiceException(ex.getMessage());
        }
    }




    public Collection<Group> getGroups(Long id) {
        return userService.getUser(id).getGroups();
    }



}
