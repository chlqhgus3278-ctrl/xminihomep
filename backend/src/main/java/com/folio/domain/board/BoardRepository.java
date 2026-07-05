package com.folio.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardPost, Long> {

    Optional<BoardPost> findByIdAndUser_Id(Long id, Long userId);

    List<BoardPost> findByUser_IdOrderBySortOrderAsc(Long userId);

    List<BoardPost> findByUser_IdAndBoardTypeOrderBySortOrderAsc(Long userId, BoardType boardType);

    List<BoardPost> findByUser_UsernameAndIsPublicTrueOrderBySortOrderAsc(String username);

    List<BoardPost> findByUser_UsernameAndBoardTypeAndIsPublicTrueOrderBySortOrderAsc(String username, BoardType boardType);
}
