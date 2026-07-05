package com.folio.domain.publicapi;

import com.folio.domain.board.BoardPost;
import com.folio.domain.board.BoardRepository;
import com.folio.domain.board.BoardType;
import com.folio.domain.profile.Profile;
import com.folio.domain.profile.ProfileRepository;
import com.folio.domain.user.User;
import com.folio.domain.user.UserRepository;
import com.folio.domain.visit.VisitCount;
import com.folio.domain.visit.VisitService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PublicService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final BoardRepository boardRepository;
    private final VisitService visitService;

    @Transactional(readOnly = true)
    public Map<String, Object> getHomepage(String username) {
        Profile profile = getPublicProfile(username);
        List<BoardPost> posts = boardRepository.findByUser_UsernameAndIsPublicTrueOrderBySortOrderAsc(username);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("profile", profile);
        result.put("boardPosts", posts);
        return result;
    }

    @Transactional(readOnly = true)
    public List<BoardPost> getBoardPosts(String username, BoardType type) {
        getPublicProfile(username);
        if (type != null) {
            return boardRepository.findByUser_UsernameAndBoardTypeAndIsPublicTrueOrderBySortOrderAsc(username, type);
        }
        return boardRepository.findByUser_UsernameAndIsPublicTrueOrderBySortOrderAsc(username);
    }

    @Transactional(readOnly = true)
    public BoardPost getBoardPost(String username, Long postId) {
        BoardPost post = boardRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        if (!post.getUser().getUsername().equals(username) || !Boolean.TRUE.equals(post.getIsPublic())) {
            throw new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId);
        }
        return post;
    }

    @Transactional
    public void recordVisit(String username, String visitorIp) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + username));
        visitService.recordVisit(owner.getId(), visitorIp);
    }

    @Transactional(readOnly = true)
    public VisitCount getVisitCount(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + username));
        return visitService.getCount(owner.getId());
    }

    private Profile getPublicProfile(String username) {
        Profile profile = profileRepository.findByUser_Username(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + username));
        if (!Boolean.TRUE.equals(profile.getIsPublic())) {
            throw new EntityNotFoundException("비공개 홈피입니다: " + username);
        }
        return profile;
    }
}
