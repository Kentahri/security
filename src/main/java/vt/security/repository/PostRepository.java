package vt.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vt.security.entity.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Tìm tất cả posts của 1 user
    List<Post> findByUserId(Long userId);

    // Tìm posts theo title (search)
    List<Post> findByTitleContainingIgnoreCase(String title);
}