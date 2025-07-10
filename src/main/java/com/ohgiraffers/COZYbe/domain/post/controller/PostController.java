package com.ohgiraffers.COZYbe.domain.post.controller;

import com.ohgiraffers.COZYbe.domain.post.dto.PostDto;
import com.ohgiraffers.COZYbe.domain.post.entity.Post;
import com.ohgiraffers.COZYbe.domain.post.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 등록
    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody PostDto postDto) {
        System.out.println("postDto : " + postDto);
        return ResponseEntity.ok(postService.createPost(postDto));
    }

    // 게시글 정보를 볼수있다. 게시판의 내용을 클릭하여 볼때 한번 더 보낸다.
    @GetMapping("/list")
    public ResponseEntity<List<Post>> getPostList() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // 게시글 안에서 유저의 정보를 불러와서 관련된 내용을 수정합니다.
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody PostDto postDto) {
        Post updatedPost = postService.updatePost(id, postDto);
        return ResponseEntity.ok(updatedPost);
    }

    // 게시글 안에서 유저의 정보를 불러와서 관련된 내용을 삭제합니다.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // 게시글의 대한 내용을 상세하게 보여주기 위함
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

}
