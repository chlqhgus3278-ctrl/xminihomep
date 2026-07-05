package com.folio.domain.board;

import com.folio.domain.user.User;
import com.folio.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public List<BoardPost> getMyPosts(Long userId, BoardType type) {
        if (type != null) {
            return boardRepository.findByUser_IdAndBoardTypeOrderBySortOrderAsc(userId, type);
        }
        return boardRepository.findByUser_IdOrderBySortOrderAsc(userId);
    }

    public BoardPost getOwnedPost(Long userId, Long postId) {
        return getOwned(userId, postId);
    }

    @Transactional
    public BoardPost create(Long userId, BoardPost input) {
        User user = userRepository.getReferenceById(userId);
        BoardPost post = BoardPost.builder()
                .user(user)
                .boardType(input.getBoardType())
                .title(input.getTitle())
                .content(input.getContent())
                .isPublic(input.getIsPublic() == null ? true : input.getIsPublic())
                .sortOrder(input.getSortOrder() == null ? 0 : input.getSortOrder())
                .build();
        return boardRepository.save(post);
    }

    @Transactional
    public BoardPost update(Long userId, Long postId, BoardPost input) {
        BoardPost post = getOwned(userId, postId);
        post.setTitle(input.getTitle());
        post.setContent(input.getContent());
        if (input.getBoardType() != null) {
            post.setBoardType(input.getBoardType());
        }
        if (input.getIsPublic() != null) {
            post.setIsPublic(input.getIsPublic());
        }
        if (input.getSortOrder() != null) {
            post.setSortOrder(input.getSortOrder());
        }
        return post;
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        boardRepository.delete(getOwned(userId, postId));
    }

    @Transactional
    public BoardPost toggleVisibility(Long userId, Long postId) {
        BoardPost post = getOwned(userId, postId);
        post.setIsPublic(!Boolean.TRUE.equals(post.getIsPublic()));
        return post;
    }

    private BoardPost getOwned(Long userId, Long postId) {
        return boardRepository.findByIdAndUser_Id(postId, userId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));
    }
}
