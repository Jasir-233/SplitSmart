package com.splitsmart.service;

import com.splitsmart.entity.User;
import com.splitsmart.repository.GroupMemberRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GroupAuthorizationService {

    private final GroupMemberRepository groupMemberRepository;
    private final CurrentUserService currentUserService;

    public GroupAuthorizationService(
            GroupMemberRepository groupMemberRepository,
            CurrentUserService currentUserService) {

        this.groupMemberRepository = groupMemberRepository;
        this.currentUserService = currentUserService;
    }

//    public void checkMembership(Long groupId) {
//
//        // 1. Retrieve the currently logged-in user.
//        User currentUser = currentUserService.getCurrentUser();
//
//        // 2. Get their database ID.
//        Long userId = currentUser.getId();
//
//        // 3. Check whether they belong to this group.
//        boolean isMember =
//                groupMemberRepository.existsByGroup_IdAndUser_Id(
//                        groupId, userId
//                );
//
//        // 4. Reject access if they are not a member.
//        if (!isMember) {
//            throw new AccessDeniedException(
//                    "You are not a member of this group"
//            );
//        }
//    }

    public void checkMembership(Long groupId) {

        User currentUser = currentUserService.getCurrentUser();

        Long userId = currentUser.getId();

        boolean isMember =
                groupMemberRepository.existsByGroup_IdAndUser_Id(
                        groupId, userId
                );

        if (!isMember) {
            throw new AccessDeniedException(
                    "You are not a member of this group"
            );
        }
    }
}