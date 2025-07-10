package com.ohgiraffers.COZYbe.domain.post.repository;

import com.ohgiraffers.COZYbe.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    //TODO : 검색
//    List<Post> findByTitleContaining(String keyword);
//    List<Post> findBydNickName(String nickName);
}
