package vt.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vt.security.config.UserPrincipal;
import vt.security.entity.Post;
import vt.security.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * GET /api/posts - Lấy tất cả posts
     * Permission: READ_POST
     */
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }

    /**
     * GET /api/posts/{id} - Lấy 1 post theo ID
     * Permission: READ_POST
     */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.findById(id);
        return ResponseEntity.ok(post);
    }

    /**
     * POST /api/posts - Tạo post mới
     * Permission: CREATE_POST
     */
    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestBody Post post,
            Authentication authentication) {

        // Set userId từ current user
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        post.setUserId(principal.getId());

        Post createdPost = postService.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    /**
     * PUT /api/posts/{id} - Update post
     * Permission: UPDATE_OWN_POST (nếu là owner) hoặc UPDATE_ANY_POST (nếu là admin)
     * Authorization được check trong DynamicAuthorizationFilter
     */
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestBody Post postUpdate) {

        Post updatedPost = postService.update(id, postUpdate);
        return ResponseEntity.ok(updatedPost);
    }

    /**
     * DELETE /api/posts/{id} - Xóa post
     * Permission: DELETE_OWN_POST (nếu là owner) hoặc DELETE_ANY_POST (nếu là admin)
     * Authorization được check trong DynamicAuthorizationFilter
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/posts/my - Lấy posts của current user
     * Permission: READ_POST
     */
    @GetMapping("/my")
    public ResponseEntity<List<Post>> getMyPosts(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        List<Post> myPosts = postService.findByUserId(principal.getId());
        return ResponseEntity.ok(myPosts);
    }
}