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

/** This is an auto generated class representing the BlockedUsers type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "BlockedUsers")
public final class BlockedUsers implements Model {
  public static final QueryField ID = field("BlockedUsers", "id");
  public static final QueryField BLOCK_BY = field("BlockedUsers", "blockedUsersBlockById");
  public static final QueryField BLOCK_TO = field("BlockedUsers", "blockedUsersBlockToId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="UserCloudGallery", isRequired = true) @BelongsTo(targetName = "blockedUsersBlockById", type = UserCloudGallery.class) UserCloudGallery blockBy;
  private final @ModelField(targetType="UserCloudGallery", isRequired = true) @BelongsTo(targetName = "blockedUsersBlockToId", type = UserCloudGallery.class) UserCloudGallery blockTo;
  public String getId() {
      return id;
  }
  
  public UserCloudGallery getBlockBy() {
      return blockBy;
  }
  
  public UserCloudGallery getBlockTo() {
      return blockTo;
  }
  
  private BlockedUsers(String id, UserCloudGallery blockBy, UserCloudGallery blockTo) {
    this.id = id;
    this.blockBy = blockBy;
    this.blockTo = blockTo;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      BlockedUsers blockedUsers = (BlockedUsers) obj;
      return ObjectsCompat.equals(getId(), blockedUsers.getId()) &&
              ObjectsCompat.equals(getBlockBy(), blockedUsers.getBlockBy()) &&
              ObjectsCompat.equals(getBlockTo(), blockedUsers.getBlockTo());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getBlockBy())
      .append(getBlockTo())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("BlockedUsers {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("blockBy=" + String.valueOf(getBlockBy()) + ", ")
      .append("blockTo=" + String.valueOf(getBlockTo()))
      .append("}")
      .toString();
  }
  
  public static BlockByStep builder() {
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
  public static BlockedUsers justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new BlockedUsers(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      blockBy,
      blockTo);
  }
  public interface BlockByStep {
    BlockToStep blockBy(UserCloudGallery blockBy);
  }
  

  public interface BlockToStep {
    BuildStep blockTo(UserCloudGallery blockTo);
  }
  

  public interface BuildStep {
    BlockedUsers build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements BlockByStep, BlockToStep, BuildStep {
    private String id;
    private UserCloudGallery blockBy;
    private UserCloudGallery blockTo;
    @Override
     public BlockedUsers build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new BlockedUsers(
          id,
          blockBy,
          blockTo);
    }
    
    @Override
     public BlockToStep blockBy(UserCloudGallery blockBy) {
        Objects.requireNonNull(blockBy);
        this.blockBy = blockBy;
        return this;
    }
    
    @Override
     public BuildStep blockTo(UserCloudGallery blockTo) {
        Objects.requireNonNull(blockTo);
        this.blockTo = blockTo;
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
    private CopyOfBuilder(String id, UserCloudGallery blockBy, UserCloudGallery blockTo) {
      super.id(id);
      super.blockBy(blockBy)
        .blockTo(blockTo);
    }
    
    @Override
     public CopyOfBuilder blockBy(UserCloudGallery blockBy) {
      return (CopyOfBuilder) super.blockBy(blockBy);
    }
    
    @Override
     public CopyOfBuilder blockTo(UserCloudGallery blockTo) {
      return (CopyOfBuilder) super.blockTo(blockTo);
    }
  }
  
}
