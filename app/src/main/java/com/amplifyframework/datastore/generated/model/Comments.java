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

/** This is an auto generated class representing the Comments type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Comments")
public final class Comments implements Model {
  public static final QueryField ID = field("Comments", "id");
  public static final QueryField CREATED_TIME = field("Comments", "createdTime");
  public static final QueryField CONTENT = field("Comments", "content");
  public static final QueryField POST = field("Comments", "commentsPostId");
  public static final QueryField WHO_COMMENTED_USER = field("Comments", "commentsWhoCommentedUserId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String createdTime;
  private final @ModelField(targetType="String", isRequired = true) String content;
  private final @ModelField(targetType="Posts", isRequired = true) @BelongsTo(targetName = "commentsPostId", type = Posts.class) Posts post;
  private final @ModelField(targetType="UserCloudGallery", isRequired = true) @BelongsTo(targetName = "commentsWhoCommentedUserId", type = UserCloudGallery.class) UserCloudGallery whoCommentedUser;
  public String getId() {
      return id;
  }
  
  public String getCreatedTime() {
      return createdTime;
  }
  
  public String getContent() {
      return content;
  }
  
  public Posts getPost() {
      return post;
  }
  
  public UserCloudGallery getWhoCommentedUser() {
      return whoCommentedUser;
  }
  
  private Comments(String id, String createdTime, String content, Posts post, UserCloudGallery whoCommentedUser) {
    this.id = id;
    this.createdTime = createdTime;
    this.content = content;
    this.post = post;
    this.whoCommentedUser = whoCommentedUser;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Comments comments = (Comments) obj;
      return ObjectsCompat.equals(getId(), comments.getId()) &&
              ObjectsCompat.equals(getCreatedTime(), comments.getCreatedTime()) &&
              ObjectsCompat.equals(getContent(), comments.getContent()) &&
              ObjectsCompat.equals(getPost(), comments.getPost()) &&
              ObjectsCompat.equals(getWhoCommentedUser(), comments.getWhoCommentedUser());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getCreatedTime())
      .append(getContent())
      .append(getPost())
      .append(getWhoCommentedUser())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Comments {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("createdTime=" + String.valueOf(getCreatedTime()) + ", ")
      .append("content=" + String.valueOf(getContent()) + ", ")
      .append("post=" + String.valueOf(getPost()) + ", ")
      .append("whoCommentedUser=" + String.valueOf(getWhoCommentedUser()))
      .append("}")
      .toString();
  }
  
  public static CreatedTimeStep builder() {
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
  public static Comments justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Comments(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      createdTime,
      content,
      post,
      whoCommentedUser);
  }
  public interface CreatedTimeStep {
    ContentStep createdTime(String createdTime);
  }
  

  public interface ContentStep {
    PostStep content(String content);
  }
  

  public interface PostStep {
    WhoCommentedUserStep post(Posts post);
  }
  

  public interface WhoCommentedUserStep {
    BuildStep whoCommentedUser(UserCloudGallery whoCommentedUser);
  }
  

  public interface BuildStep {
    Comments build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements CreatedTimeStep, ContentStep, PostStep, WhoCommentedUserStep, BuildStep {
    private String id;
    private String createdTime;
    private String content;
    private Posts post;
    private UserCloudGallery whoCommentedUser;
    @Override
     public Comments build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Comments(
          id,
          createdTime,
          content,
          post,
          whoCommentedUser);
    }
    
    @Override
     public ContentStep createdTime(String createdTime) {
        Objects.requireNonNull(createdTime);
        this.createdTime = createdTime;
        return this;
    }
    
    @Override
     public PostStep content(String content) {
        Objects.requireNonNull(content);
        this.content = content;
        return this;
    }
    
    @Override
     public WhoCommentedUserStep post(Posts post) {
        Objects.requireNonNull(post);
        this.post = post;
        return this;
    }
    
    @Override
     public BuildStep whoCommentedUser(UserCloudGallery whoCommentedUser) {
        Objects.requireNonNull(whoCommentedUser);
        this.whoCommentedUser = whoCommentedUser;
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
    private CopyOfBuilder(String id, String createdTime, String content, Posts post, UserCloudGallery whoCommentedUser) {
      super.id(id);
      super.createdTime(createdTime)
        .content(content)
        .post(post)
        .whoCommentedUser(whoCommentedUser);
    }
    
    @Override
     public CopyOfBuilder createdTime(String createdTime) {
      return (CopyOfBuilder) super.createdTime(createdTime);
    }
    
    @Override
     public CopyOfBuilder content(String content) {
      return (CopyOfBuilder) super.content(content);
    }
    
    @Override
     public CopyOfBuilder post(Posts post) {
      return (CopyOfBuilder) super.post(post);
    }
    
    @Override
     public CopyOfBuilder whoCommentedUser(UserCloudGallery whoCommentedUser) {
      return (CopyOfBuilder) super.whoCommentedUser(whoCommentedUser);
    }
  }
  
}
