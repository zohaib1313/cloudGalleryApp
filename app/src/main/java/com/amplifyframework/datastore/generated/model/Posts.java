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

/** This is an auto generated class representing the Posts type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Posts")
public final class Posts implements Model {
  public static final QueryField ID = field("Posts", "id");
  public static final QueryField CREATED_TIME = field("Posts", "createdTime");
  public static final QueryField IMAGE = field("Posts", "image");
  public static final QueryField IS_PUBLIC = field("Posts", "isPublic");
  public static final QueryField TITLE = field("Posts", "title");
  public static final QueryField DESCRIPTION = field("Posts", "description");
  public static final QueryField WHO_POSTED_USER = field("Posts", "postsWhoPostedUserId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String createdTime;
  private final @ModelField(targetType="String", isRequired = true) String image;
  private final @ModelField(targetType="Boolean", isRequired = true) Boolean isPublic;
  private final @ModelField(targetType="String", isRequired = true) String title;
  private final @ModelField(targetType="String", isRequired = true) String description;
  private final @ModelField(targetType="UserCloudGallery", isRequired = true) @BelongsTo(targetName = "postsWhoPostedUserId", type = UserCloudGallery.class) UserCloudGallery whoPostedUser;
  public String getId() {
      return id;
  }
  
  public String getCreatedTime() {
      return createdTime;
  }
  
  public String getImage() {
      return image;
  }
  
  public Boolean getIsPublic() {
      return isPublic;
  }
  
  public String getTitle() {
      return title;
  }
  
  public String getDescription() {
      return description;
  }
  
  public UserCloudGallery getWhoPostedUser() {
      return whoPostedUser;
  }
  
  private Posts(String id, String createdTime, String image, Boolean isPublic, String title, String description, UserCloudGallery whoPostedUser) {
    this.id = id;
    this.createdTime = createdTime;
    this.image = image;
    this.isPublic = isPublic;
    this.title = title;
    this.description = description;
    this.whoPostedUser = whoPostedUser;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Posts posts = (Posts) obj;
      return ObjectsCompat.equals(getId(), posts.getId()) &&
              ObjectsCompat.equals(getCreatedTime(), posts.getCreatedTime()) &&
              ObjectsCompat.equals(getImage(), posts.getImage()) &&
              ObjectsCompat.equals(getIsPublic(), posts.getIsPublic()) &&
              ObjectsCompat.equals(getTitle(), posts.getTitle()) &&
              ObjectsCompat.equals(getDescription(), posts.getDescription()) &&
              ObjectsCompat.equals(getWhoPostedUser(), posts.getWhoPostedUser());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getCreatedTime())
      .append(getImage())
      .append(getIsPublic())
      .append(getTitle())
      .append(getDescription())
      .append(getWhoPostedUser())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Posts {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("createdTime=" + String.valueOf(getCreatedTime()) + ", ")
      .append("image=" + String.valueOf(getImage()) + ", ")
      .append("isPublic=" + String.valueOf(getIsPublic()) + ", ")
      .append("title=" + String.valueOf(getTitle()) + ", ")
      .append("description=" + String.valueOf(getDescription()) + ", ")
      .append("whoPostedUser=" + String.valueOf(getWhoPostedUser()))
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
  public static Posts justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Posts(
      id,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      createdTime,
      image,
      isPublic,
      title,
      description,
      whoPostedUser);
  }
  public interface CreatedTimeStep {
    ImageStep createdTime(String createdTime);
  }
  

  public interface ImageStep {
    IsPublicStep image(String image);
  }
  

  public interface IsPublicStep {
    TitleStep isPublic(Boolean isPublic);
  }
  

  public interface TitleStep {
    DescriptionStep title(String title);
  }
  

  public interface DescriptionStep {
    WhoPostedUserStep description(String description);
  }
  

  public interface WhoPostedUserStep {
    BuildStep whoPostedUser(UserCloudGallery whoPostedUser);
  }
  

  public interface BuildStep {
    Posts build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements CreatedTimeStep, ImageStep, IsPublicStep, TitleStep, DescriptionStep, WhoPostedUserStep, BuildStep {
    private String id;
    private String createdTime;
    private String image;
    private Boolean isPublic;
    private String title;
    private String description;
    private UserCloudGallery whoPostedUser;
    @Override
     public Posts build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Posts(
          id,
          createdTime,
          image,
          isPublic,
          title,
          description,
          whoPostedUser);
    }
    
    @Override
     public ImageStep createdTime(String createdTime) {
        Objects.requireNonNull(createdTime);
        this.createdTime = createdTime;
        return this;
    }
    
    @Override
     public IsPublicStep image(String image) {
        Objects.requireNonNull(image);
        this.image = image;
        return this;
    }
    
    @Override
     public TitleStep isPublic(Boolean isPublic) {
        Objects.requireNonNull(isPublic);
        this.isPublic = isPublic;
        return this;
    }
    
    @Override
     public DescriptionStep title(String title) {
        Objects.requireNonNull(title);
        this.title = title;
        return this;
    }
    
    @Override
     public WhoPostedUserStep description(String description) {
        Objects.requireNonNull(description);
        this.description = description;
        return this;
    }
    
    @Override
     public BuildStep whoPostedUser(UserCloudGallery whoPostedUser) {
        Objects.requireNonNull(whoPostedUser);
        this.whoPostedUser = whoPostedUser;
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
    private CopyOfBuilder(String id, String createdTime, String image, Boolean isPublic, String title, String description, UserCloudGallery whoPostedUser) {
      super.id(id);
      super.createdTime(createdTime)
        .image(image)
        .isPublic(isPublic)
        .title(title)
        .description(description)
        .whoPostedUser(whoPostedUser);
    }
    
    @Override
     public CopyOfBuilder createdTime(String createdTime) {
      return (CopyOfBuilder) super.createdTime(createdTime);
    }
    
    @Override
     public CopyOfBuilder image(String image) {
      return (CopyOfBuilder) super.image(image);
    }
    
    @Override
     public CopyOfBuilder isPublic(Boolean isPublic) {
      return (CopyOfBuilder) super.isPublic(isPublic);
    }
    
    @Override
     public CopyOfBuilder title(String title) {
      return (CopyOfBuilder) super.title(title);
    }
    
    @Override
     public CopyOfBuilder description(String description) {
      return (CopyOfBuilder) super.description(description);
    }
    
    @Override
     public CopyOfBuilder whoPostedUser(UserCloudGallery whoPostedUser) {
      return (CopyOfBuilder) super.whoPostedUser(whoPostedUser);
    }
  }
  
}
