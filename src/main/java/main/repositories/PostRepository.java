package main.repositories;

import main.model.Post;
import main.model.User;
import main.model.enums.ModerationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>
{
    String PART_ALL_POST_QUERY = "WHERE p.isActive = ?1 AND p.moderationStatus = ?2 AND p.time <= ?3";

    //==== get total count ====//

    @Query("SELECT COUNT(*) FROM Post p " + PART_ALL_POST_QUERY)
    int getPostsCountByActiveAndModStatusAndTime(byte isActive, ModerationStatus status, LocalDateTime time);

    @Query("SELECT COUNT(*) FROM Post p " + PART_ALL_POST_QUERY + " AND to_char(p.time,'YYYY-MM-DD') LIKE %?4%")
    int getTotalPostCountByDate(byte isActive, ModerationStatus status, LocalDateTime time, String date);

    @Query("SELECT COUNT(*) FROM Post p " + PART_ALL_POST_QUERY + " AND p.text LIKE %?4%")
    int getTotalPostCountByQuery(byte isActive, ModerationStatus status,LocalDateTime time, String query);

    @Query("SELECT COUNT(*) FROM Post p LEFT JOIN TagToPost tp ON tp.post.id = p.id "
            + PART_ALL_POST_QUERY + " AND tp.tag.id = ?4")
    int getTotalPostByTag(byte isActive, ModerationStatus status, LocalDateTime time, int tagId);

    @Query("SELECT COUNT(*) FROM Post p WHERE p.isActive = ?1 AND p.moderationStatus = ?2")
    int getPostsCountByActiveAndModStatus(byte isActive, ModerationStatus status);

    @Query("SELECT COUNT(*) FROM Post p WHERE p.isActive = ?1 AND p.moderationStatus = ?2 AND p.moderatorId = ?3")
    int getTotalPostsByModerator(byte isActive, ModerationStatus status, int userId);

    @Query("SELECT COUNT(*) FROM Post p WHERE p.isActive = 0 AND p.user = ?1")
    int getTotalInactivePostsByUser(User user);

    @Query("SELECT COUNT(*) FROM Post p WHERE p.isActive = ?1 AND p.moderationStatus = ?2 AND p.user = ?3")
    int getTotalPostsCountByUser(byte isActive, ModerationStatus status, User user);

    @Query("SELECT COUNT(*), to_char(p.time,'YYYY-MM-DD') AS date FROM Post p " + PART_ALL_POST_QUERY +
            " AND YEAR(p.time) = ?4 GROUP BY date")
    List<Object[]> getPostCountInYearGroupByDate(byte isActive, ModerationStatus status, LocalDateTime time, int year);

    //=== main page ===//

    @Query("SELECT p, COUNT (pc) AS commentsCount FROM Post p LEFT JOIN PostComment pc " +
            "ON pc.post.id = p.id " + PART_ALL_POST_QUERY + " GROUP BY p.id")
    List<Post> findAllPostSortedByComments(byte isActive, ModerationStatus status, LocalDateTime time, Pageable pageable);

    @Query("SELECT p, (SELECT COUNT(*) FROM PostVote pv WHERE pv.post.id = p.id AND pv.value = 1) FROM Post p " +
            "LEFT JOIN PostVote pv ON pv.post.id = p.id " + PART_ALL_POST_QUERY + " GROUP BY p.id ORDER BY 2 DESC")
    List<Post> findAllPostSortedByLikes(byte isActive, ModerationStatus status, LocalDateTime time, Pageable pageable);

    List<Post> findPostByIsActiveAndModerationStatusAndTimeBefore(byte isActive, ModerationStatus status,
                                                                  LocalDateTime time, Pageable pageable);

    //=== other query ===//

    Post findById (int id);

    List<Post> findByModerationStatusAndIsActive(ModerationStatus status, byte isActive, Pageable pageable);
    List<Post> findByModerationStatusAndIsActiveAndModeratorId(ModerationStatus status,
                                                               byte isActive, int moderatorId, Pageable pageable);

    @Query("SELECT p FROM Post p " + PART_ALL_POST_QUERY + " AND p.text LIKE %?4%")
    List<Post> findPostByQuery(byte isActive, ModerationStatus status, LocalDateTime time, String query, Pageable pageable);

    @Query("SELECT p FROM Post p " + PART_ALL_POST_QUERY + " AND to_char(p.time,'YYYY-MM-DD') LIKE %?4%")
    List<Post> findPostByDate(byte isActive, ModerationStatus status, LocalDateTime time, String date, Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN TagToPost tp ON tp.post.id = p.id " + PART_ALL_POST_QUERY + " AND tp.tag.id = ?4")
    List<Post> findAllPostByTag(byte isActive, ModerationStatus status, LocalDateTime time, int tagId, Pageable pageable);

    List<Post> findByIsActiveAndUser(byte isActive, User user, Pageable pageable);
    List<Post> findByIsActiveAndModerationStatusAndUser(byte isActive, ModerationStatus status, User user, Pageable pageable);

    @Query("SELECT YEAR(time) AS years FROM Post p " + PART_ALL_POST_QUERY + " GROUP BY years")
    List<Integer> getYears(byte isActive, ModerationStatus status, LocalDateTime time);

    @Query("SELECT p FROM Post p " + PART_ALL_POST_QUERY + " AND p.user = ?4")
    List<Post> findPostsByUser(byte isActive, ModerationStatus status, LocalDateTime time, User user, Sort sort);

    @Query("SELECT p FROM Post p " + PART_ALL_POST_QUERY)
    List<Post> findSortPosts(byte isActive, ModerationStatus status, LocalDateTime time, Sort sort);
}
