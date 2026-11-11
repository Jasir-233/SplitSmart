
package com.splitsmart.service;

import com.splitsmart.dto.GroupMemberResponse;
import com.splitsmart.dto.GroupResponse;
import com.splitsmart.entity.ExpenseGroup;
import com.splitsmart.entity.GroupMember;
import com.splitsmart.entity.User;

import com.splitsmart.repository.ExpenseGroupRepository;
import com.splitsmart.repository.GroupMemberRepository;

import com.splitsmart.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class GroupService {

    private final ExpenseGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final CurrentUserService currentUserService;

    private final UserRepository userRepository;
    private final GroupAuthorizationService groupAuthorizationService;

    public GroupService(
            ExpenseGroupRepository groupRepository,
            GroupMemberRepository memberRepository,
            CurrentUserService currentUserService,
            UserRepository userRepository,
            GroupAuthorizationService groupAuthorizationService) {

        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.groupAuthorizationService = groupAuthorizationService;
    }

    @Transactional
    public ExpenseGroup createGroup(String name) {

        // 1. Retrieve the currently logged-in user.
        User user = currentUserService.getCurrentUser();

        // 2. Create a new ExpenseGroup object.
        ExpenseGroup group = new ExpenseGroup();

        // 3. Set the group name and creator.
        group.setName(name.trim());
        group.setCreatedBy(user);

        // 4. Save the group in the database.
        ExpenseGroup savedGroup = groupRepository.save(group);

        // 5. Create a membership object.
        GroupMember member = new GroupMember();

        // 6. Connect the membership to the group and user.
        member.setGroup(savedGroup);
        member.setUser(user);

        // 7. Save the membership.
        memberRepository.save(member);

        // 8. Return the saved group.
        return savedGroup;
    }

    @Transactional
    public void addMember(long groupId, String email){

        // 1. Find the group. expense group:
        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Group not found"
                ));

        // 2. Check whether the current user can access this group.
        groupAuthorizationService.checkMembership(groupId);

        // 3. Find the user who should be added.
        User user = userRepository.findByEmail(
                email.trim().toLowerCase()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User not found"
        ));

        // 4. Prevent duplicate membership.
        boolean alreadyMember =
                memberRepository.existsByGroup_IdAndUser_Id(
                        groupId, user.getId()
                );
        if (alreadyMember) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User is already a member of this group"
            );
        }

        // 5. Create the membership.
        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);

        // 6. Save it.
        memberRepository.save(member);
    }


    @Transactional(readOnly = true)
    public List<GroupResponse> getMyGroups() {

        // 1. Identify the currently logged-in user.
        User user = currentUserService.getCurrentUser();

        // 2. Find all groups this user belongs to.
        List<GroupMember> memberships =
                memberRepository.findByUser_Id(user.getId());

        // 3. Convert each membership into a GroupResponse.
        return memberships.stream()
                .map(member -> {

                    ExpenseGroup group = member.getGroup();

                    return new GroupResponse(
                            group.getId(),
                            group.getName(),
                            group.getCreatedBy().getId(),
                            group.getCreatedAt()
                    );
                })
                .toList();
    }


    @Transactional(readOnly = true)
    public List<GroupMemberResponse> getGroupMembers(Long groupId) {

        // Check whether the group exists
        groupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Group not found"
                ));

        // Check whether the logged-in user belongs to this group
        groupAuthorizationService.checkMembership(groupId);

        // Retrieve all group memberships
        List<GroupMember> members =
                memberRepository.findByGroup_Id(groupId);

        // Convert memberships into response DTOs
        return members.stream()
                .map(member -> {
                    User user = member.getUser();

                    return new GroupMemberResponse(
                            user.getId(),
                            user.getName(),
                            user.getEmail()
                    );
                })
                .toList();
    }
}
