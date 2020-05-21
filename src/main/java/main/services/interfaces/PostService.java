package main.services.interfaces;

import main.api.responses.ApiResponseBody;
import main.api.responses.PostResponseBody;
import main.api.responses.PostWallResponseBody;
import main.model.Post;
import main.services.bodies.ErrorsBody;
import main.services.bodies.UserBody;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public interface PostService
{
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    PostWallResponseBody getAllPosts(int offset, int limit, String mode);
    PostWallResponseBody searchPosts(int offset, int limit, String query);
    PostWallResponseBody getPostsByDate(int offset, int limit, String date);
    PostResponseBody getPostById(int id);
    PostWallResponseBody getPostsByTag(int offset, int limit, String tag);
    ResponseEntity<PostWallResponseBody> getPostsForModeration(int offset, int limit, String status);
    ResponseEntity<PostWallResponseBody> getMyPosts(int offset, int limit, String status);
    ApiResponseBody postLike(int id);
    ApiResponseBody postDislike (int id);
    ApiResponseBody addPost (PostResponseBody post);
    ApiResponseBody editPost (int id, PostResponseBody postResponseBody);

    /** default methods*/

    default List<PostResponseBody> getListPostBodies(List<Post> posts)
    {
        List<PostResponseBody> postBodies = new ArrayList<>();

        for (int i = 0; i < posts.size(); i++)
        {
            Post post = posts.get(i);

            postBodies.add(PostResponseBody.builder().id(post.getId())
                    .time(post.getTime().format(formatter))
                    .user(UserBody.builder().id(post.getUser().getId()).name(post.getUser().getName()).build())
                    .title(post.getTitle())
                    .announce(getAnnounce(post))
                    .likeCount(post.getVotesCount("likes"))
                    .dislikeCount(post.getVotesCount("dislikes"))
                    .commentCount(post.getCommentsCount())
                    .viewCount(post.getViewCount())
                    .build());
        }
        return postBodies;
    }

    default String getAnnounce(Post post)
    {
        String announce = "";

        if (post.getText().length() > 500) {
            announce = post.getText().substring(0, 499) + "...";
        } else announce = post.getText();

        return announce;
    }

    default boolean isTitleAndTextCorrect(PostResponseBody postResponseBody)
    {
        return postResponseBody.getTitle() == null || postResponseBody.getText() == null
            || postResponseBody.getTitle().length() < 3 || postResponseBody.getText().length() < 10;
    }

    default LocalDateTime setTime (String time)
    {
        LocalDateTime newTime = LocalDateTime.parse(time, formatter);
        return (newTime.isBefore(LocalDateTime.now())) ? LocalDateTime.now() : newTime;
    }

    default ApiResponseBody errorResponse ()
    {
        ErrorsBody error = ErrorsBody.builder()
                .title("Заголовок не установлен или короткий")
                .text("Текст публикации не установлен или слишком короткий")
                .build();

        return ApiResponseBody.builder().result(false).errors(error).build();
    }
}
