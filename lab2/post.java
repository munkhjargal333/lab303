
//Үндсэн асуудлууд:
//DRY admin эрхийн шалгалт 2р хийгдсэн
//SRP 2р үйлдэл хийж байна.
//OCR role өөрчлөгдөхөд кодон дээр өөрчлөлт хийх шаардлагатай болно.???


// SRP: зөвхөн post-той холбоотой үйлдэл
public class PostService {
    private final AuthorizationService authService;

    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId);
        User user = userRepository.findById(userId);

        if (!authService.canDeletePost(user, post)) {
            throw new UnauthorizedException("Cannot delete this post");
        }
        postRepository.delete(post); // үндсэн үйлдэл
    }
}

// SRP: зөвхөн comment-той холбоотой үйлдэл
public class CommentService {
    private final AuthorizationService authService;

    public void editComment(Long commentId, Long userId, String newText) {
        Comment comment = commentRepository.findById(commentId);
        User user = userRepository.findById(userId);

        if (!authService.canEditComment(user, comment)) {
            throw new UnauthorizedException("Cannot edit this comment");
        }

        comment.setText(newText);
        commentRepository.save(comment); // үндсэн үйлдэл
    }
}

// DRY: admin шалгалт нэг газарт
// SRP: зөвхөн эрх шалгах үүрэгтэй
public class AuthorizationService {
    private ADMIN = "ADMIN";
    public boolean isAdmin(User user) {
        return user.getRole().equals(ADMIN);
    }

    public boolean isOwnerOfPost(User user, Post post) {
        return post.getAuthorId().equals(user.getId());
    }

    public boolean isOwnerOfComment(User user, Comment comment) {
        return comment.getUserId().equals(user.getId());
    }

    public boolean canDeletePost(User user, Post post) {
        return isAdmin(user) || isOwnerOfPost(user, post);
    }

    public boolean canEditComment(User user, Comment comment) {
        return isAdmin(user) || isOwnerOfComment(user, comment);
    }
}

