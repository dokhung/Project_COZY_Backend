package com.ohgiraffers.COZYbe.domain.post.service;

import com.ohgiraffers.COZYbe.domain.post.dto.PostDto;
import com.ohgiraffers.COZYbe.domain.post.entity.Post;
import com.ohgiraffers.COZYbe.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 게시판글의 대한 내용을 프론트엔드에 보내기 위함
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // 게시판 디비에 등록함
    public Post createPost(PostDto postDto) {
        Post post = Post.builder()
                .title(postDto.getTitle())
                .nickname(postDto.getNickName())
                .status(postDto.getStatus())
                .postText(postDto.getPostText())
                .createdAt(LocalDateTime.now())
                .build();
        return postRepository.save(post);
    }


    // 삭제
    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(()->
                new RuntimeException("게시글이 없어요"));
        postRepository.delete(post);
    }

    // 수정
    public Post updatePost(Long id, PostDto postDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 없어요."));
        post.setTitle(postDto.getTitle());
        post.setPostText(postDto.getPostText());
        post.setStatus(postDto.getStatus());
        return postRepository.save(post);
    }

    // 내용상세
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(()-> new RuntimeException("게시글이 없다."));
    }
}
