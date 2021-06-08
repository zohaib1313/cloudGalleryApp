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

/** This is an auto generated class representing the PostStatus type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "PostStatuses")
public final class PostStatus implements Model {
  public static final QueryField ID = field("PostStatus", "id");
  public static final QueryField POST = field("PostStatus", "postStatusPostId");
  public static final QueryField ALLOWED_USER = field("PostStatus", "postStatusAllowedUserId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Posts", isRequired = true) @BelongsTo(targetName = "postStatusPostId", type = Posts.class) Posts post;
  private final @ModelField(targetType="UserCloudGallery", isRequired = true) @BelongsTo(targetName = "postStatusAllowedUserId", type = UserCloudGallery.class) UserCloudGallery allowedUser;
  public String getId() {
      return id;
  }
  
  public Posts getPost() {
      return post;
  }
  
  public UserCloudGallery getAllowedUser() {
      return allowedUser;
  }
  
  private PostStatus(String id, Posts post, UserCloudGallery allowedUser) {
    this.id = id;
    this.post = post;
    this.allowedUser = allowedUser;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      PostStatus postStatus = (PostStatus) obj;
      return ObjectsCompat.equals(getId(), postStatus.getId()) &&
              ObjectsCompat.equals(getPost(), postStatus.getPost()) &&
              ObjectsCompat.equals(getAllowedUser(), postStatus.getAllowedUser());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getPost())
      .append(getAllowedUser())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("PostStatus {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("post=" + String.valueOf(getPost()) + ", ")
      .append("allowedUser=" + String.valueOf(getAllowedUser()))
      .append("}")
      .toString();
  }
  
  public static PostStep builder() {
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
  public static PostStatus justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new PostStatus(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      post,
      allowedUser);
  }
  public interface PostStep {
    AllowedUserStep post(Posts post);
  }
  

  public interface AllowedUserStep {
    BuildStep allowedUser(UserCloudGallery allowedUser);
  }
  

  public interface BuildStep {
    PostStatus build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements PostStep, AllowedUserStep, BuildStep {
    private String id;
    private Posts post;
    private UserCloudGallery allowedUser;
    @Override
     public PostStatus build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new PostStatus(
          id,
          post,
          allowedUser);
    }
    
    @Override
     public AllowedUserStep post(Posts post) {
        Objects.requireNonNull(post);
        this.post = post;
        return this;
    }
    
    @Override
     public BuildStep allowedUser(UserCloudGallery allowedUser) {
        Objects.requireNonNull(allowedUser);
        this.allowedUser = allowedUser;
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
    private CopyOfBuilder(String id, Posts post, UserCloudGallery allowedUser) {
      super.id(id);
      super.post(post)
        .allowedUser(allowedUser);
    }
    
    @Override
     public CopyOfBuilder post(Posts post) {
      return (CopyOfBuilder) super.post(post);
    }
    
    @Override
     public CopyOfBuilder allowedUser(UserCloudGallery allowedUser) {
      return (CopyOfBuilder) super.allowedUser(allowedUser);
    }
  }
  
}
