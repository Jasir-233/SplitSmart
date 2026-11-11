
package com.splitsmart.controller;

import com.splitsmart.dto.AddGroupMemberRequest;
import com.splitsmart.dto.CreateGroupRequest;
import com.splitsmart.dto.GroupMemberResponse;
import com.splitsmart.dto.GroupResponse;
import com.splitsmart.entity.ExpenseGroup;
import com.splitsmart.service.GroupService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody CreateGroupRequest request) {

        ExpenseGroup group =
                groupService.createGroup(request.name());

        GroupResponse response = new GroupResponse(
                group.getId(),
                group.getName(),
                group.getCreatedBy().getId(),
                group.getCreatedAt()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<Void> addMember(
            @PathVariable Long groupId,
            @Valid @RequestBody AddGroupMemberRequest request) {

        groupService.addMember(groupId, request.email());

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getMyGroups() {

        return ResponseEntity.ok(groupService.getMyGroups());
    }

    // GET ALL MEMBERS OF A GROUP
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberResponse>> getGroupMembers(
            @PathVariable Long groupId) {

        return ResponseEntity.ok(
                groupService.getGroupMembers(groupId)
        );
    }
}
