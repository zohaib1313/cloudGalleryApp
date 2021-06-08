package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Likes type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Likes")
public final class Likes implements Model {
  public static final QueryField ID = field("Likes", "id");
  public static final QueryField POST_ID = field("Likes", "postId");
  public static final QueryField WHO_LIKED_USER = field("Likes", "likesWhoLikedUserId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String postId;
  private final @ModelField(targetType="UserCloudGallery", isRequired = true) @BelongsTo(targetName = "likesWhoLikedUserId", type = UserCloudGallery.class) UserCloudGallery whoLikedUser;
  public String getId() {
      return id;
  }
  
  public String getPostId() {
      return postId;
  }
  
  public UserCloudGallery getWhoLikedUser() {
      return whoLikedUser;
  }
  
  private Likes(String id, String postId, UserCloudGallery whoLikedUser) {
    this.id = id;
    this.postId = postId;
    this.whoLikedUser = whoLikedUser;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Likes likes = (Likes) obj;
      return ObjectsCompat.equals(getId(), likes.getId()) &&
              ObjectsCompat.equals(getPostId(), likes.getPostId()) &&
              ObjectsCompat.equals(getWhoLikedUser(), likes.getWhoLikedUser());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getPostId())
      .append(getWhoLikedUser())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Likes {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("postId=" + String.valueOf(getPostId()) + ", ")
      .append("whoLikedUser=" + String.valueOf(getWhoLikedUser()))
      .append("}")
      .toString();
  }
  
  public static PostIdStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static Likes justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Likes(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      postId,
      whoLikedUser);
  }
  public interface PostIdStep {
    WhoLikedUserStep postId(String postId);
  }
  

  public interface WhoLikedUserStep {
    BuildStep whoLikedUser(UserCloudGallery whoLikedUser);
  }
  

  public interface BuildStep {
    Likes build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements PostIdStep, WhoLikedUserStep, BuildStep {
    private String id;
    private String postId;
    private UserCloudGallery whoLikedUser;
    @Override
     public Likes build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Likes(
          id,
          postId,
          whoLikedUser);
    }
    
    @Override
     public WhoLikedUserStep postId(String postId) {
        Objects.requireNonNull(postId);
        this.postId = postId;
        return this;
    }
    
    @Override
     public BuildStep whoLikedUser(UserCloudGallery whoLikedUser) {
        Objects.requireNonNull(whoLikedUser);
        this.whoLikedUser = whoLikedUser;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String postId, UserCloudGallery whoLikedUser) {
      super.id(id);
      super.postId(postId)
        .whoLikedUser(whoLikedUser);
    }
    
    @Override
     public CopyOfBuilder postId(String postId) {
      return (CopyOfBuilder) super.postId(postId);
    }
    
    @Override
     public CopyOfBuilder whoLikedUser(UserCloudGallery whoLikedUser) {
      return (CopyOfBuilder) super.whoLikedUser(whoLikedUser);
    }
  }
  
}
