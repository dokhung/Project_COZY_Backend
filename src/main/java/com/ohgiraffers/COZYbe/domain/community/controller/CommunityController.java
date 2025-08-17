package com.ohgiraffers.COZYbe.domain.community.controller;

import com.ohgiraffers.COZYbe.domain.community.dto.CommunityDto;
import com.ohgiraffers.COZYbe.domain.community.entity.Community;
import com.ohgiraffers.COZYbe.domain.community.service.CommunityService;
import com.ohgiraffers.COZYbe.domain.plan.entity.Plan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    //　新しいコミュニティー内容を登録する。
    @PostMapping("/create")
    public ResponseEntity<Community> createCommunity(@RequestBody CommunityDto dto){
        return ResponseEntity.ok(communityService.createCommunity(dto));
    }
    //　コミュニティーデータの中身を見る。
    @GetMapping("/list")
    public ResponseEntity<List<Community>> getAllCommunity(){
        return ResponseEntity.ok(communityService.getAllCommunityList());
    }
    //　リストから特定のユーザーの情報を呼び修正する。
    @PutMapping("/{id}")
    public ResponseEntity<Community> updateCommunity(@PathVariable Long id, @RequestBody CommunityDto dto){
        Community community = communityService.updateCommunity(id,dto);
        return ResponseEntity.ok(community);
    }
    //  特定のコミュニティーの掲示板を削除する。
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable Long id){
        communityService.deleteCommunity(id);
        return ResponseEntity.noContent().build();
    }
    //　アカウントで調べる。
    @GetMapping("/{id}")
    public ResponseEntity<Community> getPlanById(@PathVariable Long id) {
        Community community = communityService.getCommunityById(id);
        return ResponseEntity.ok(community);
    }

    @DeleteMapping("/project/{projectId}")
    public ResponseEntity<Void> deleteByProjectId(@PathVariable Long projectId) {
        communityService.deleteAllByProjectId(projectId);
        return ResponseEntity.noContent().build();
    }


}
