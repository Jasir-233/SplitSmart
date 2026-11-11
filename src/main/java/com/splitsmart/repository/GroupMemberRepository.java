package com.splitsmart.repository;

import com.splitsmart.entity.GroupMember;
import com.splitsmart.entity.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepository
        extends JpaRepository<GroupMember, GroupMemberId> {

    boolean existsByGroup_IdAndUser_Id(Long groupId, Long userId);

    List<GroupMember> findByUser_Id(Long userId);

    List<GroupMember> findByGroup_Id(Long groupId);
}