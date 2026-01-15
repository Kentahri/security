package vt.security.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vt.security.entity.Post;
import vt.security.repository.PostRepository;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Lấy tất cả posts
     */
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    /**
     * Lấy post theo ID
     */
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    /**
     * Lấy posts của 1 user
     */
    public List<Post> findByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    /**
     * Tạo post mới
     */
    @Transactional
    public Post save(Post post) {
        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        return postRepository.save(post);
    }

    /**
     * Update post
     */
    @Transactional
    public Post update(Long id, Post postUpdate) {
        Post existingPost = findById(id);

        if (postUpdate.getTitle() != null) {
            existingPost.setTitle(postUpdate.getTitle());
        }
        if (postUpdate.getContent() != null) {
            existingPost.setContent(postUpdate.getContent());
        }

        return postRepository.save(existingPost);
    }

    /**
     * Xóa post
     */
    @Transactional
    public void delete(Long id) {
        Post post = findById(id);
        postRepository.delete(post);
    }

    /**
     * Search posts theo title
     */
    public List<Post> searchByTitle(String keyword) {
        return postRepository.findByTitleContainingIgnoreCase(keyword);
    }
}