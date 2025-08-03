package com.ohgiraffers.COZYbe.domain.community.service;

import com.ohgiraffers.COZYbe.domain.community.dto.CommunityDto;
import com.ohgiraffers.COZYbe.domain.community.entity.Community;
import com.ohgiraffers.COZYbe.domain.community.repository.CommunityRepository;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import com.ohgiraffers.COZYbe.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommunityService(CommunityRepository communityRepository, UserRepository userRepository) {
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
    }

    // コミュニティーのリストを全て取ってくる
    public List<Community> getAllCommunityList(){
        return communityRepository.findAll();
    }

    // 新しいコミュニティーの内容を登録する。
    public Community createCommunity(CommunityDto communityDto){
        Community community = Community.builder()
                .title(communityDto.getTitle())
                .nickname(communityDto.getNickName())
                .communityText(communityDto.getCommunityText())
                .build();
        return communityRepository.save(community);
    }

    //　登録されてるコミュニティーの内容を修正する
    public Community updateCommunity(Long id, CommunityDto communityDto) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        if (communityDto.getTitle() != null && !communityDto.getTitle().isEmpty()) {
            community.setTitle(communityDto.getTitle());
        }

        if (communityDto.getCommunityText() != null && !communityDto.getCommunityText().isEmpty()) {
            community.setCommunityText(communityDto.getCommunityText());
        }

        return communityRepository.save(community);
    }



    //　登録されてるコミュニティーの内容を削除する。
    public void deleteCommunity(Long id){
        Community community = communityRepository.findById(id).orElseThrow(()->
                new RuntimeException("掲示板がございません"));
        communityRepository.delete(community);
    }

    public Community getCommunityById(Long id){
        return communityRepository.findById(id).orElseThrow(()-> new  RuntimeException(
                "ありません"
        ));
    }
    //　ニックネームでコミュニティーを調べる。
    public List<Community> getCommunityByNickname(String nickname){
        return communityRepository.findAllByNickname(nickname);
    }

    public List<Community> getCommunityByUserId(UUID userId){
        User user  = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("使用者を探せません"));
        return communityRepository.findAllByNickname(user.getNickname());
    }
}
