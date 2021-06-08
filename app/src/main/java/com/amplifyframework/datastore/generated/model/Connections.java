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

/** This is an auto generated class representing the Connections type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Connections")
public final class Connections implements Model {
  public static final QueryField ID = field("Connections", "id");
  public static final QueryField STATUS = field("Connections", "status");
  public static final QueryField FOLLOW_BY = field("Connections", "connectionsFollowById");
  public static final QueryField FOLLOW_TO = field("Connections", "connectionsFollowToId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String status;
  private final @ModelField(targetType="UserCloudGallery", isRequired = true) @BelongsTo(targetName = "connectionsFollowById", type = UserCloudGallery.class) UserCloudGallery followBy;
  private final @ModelField(targetType="UserCloudGallery", isRequired = true) @BelongsTo(targetName = "connectionsFollowToId", type = UserCloudGallery.class) UserCloudGallery followTo;
  public String getId() {
      return id;
  }
  
  public String getStatus() {
      return status;
  }
  
  public UserCloudGallery getFollowBy() {
      return followBy;
  }
  
  public UserCloudGallery getFollowTo() {
      return followTo;
  }
  
  private Connections(String id, String status, UserCloudGallery followBy, UserCloudGallery followTo) {
    this.id = id;
    this.status = status;
    this.followBy = followBy;
    this.followTo = followTo;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Connections connections = (Connections) obj;
      return ObjectsCompat.equals(getId(), connections.getId()) &&
              ObjectsCompat.equals(getStatus(), connections.getStatus()) &&
              ObjectsCompat.equals(getFollowBy(), connections.getFollowBy()) &&
              ObjectsCompat.equals(getFollowTo(), connections.getFollowTo());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getStatus())
      .append(getFollowBy())
      .append(getFollowTo())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Connections {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("status=" + String.valueOf(getStatus()) + ", ")
      .append("followBy=" + String.valueOf(getFollowBy()) + ", ")
      .append("followTo=" + String.valueOf(getFollowTo()))
      .append("}")
      .toString();
  }
  
  public static StatusStep builder() {
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
  public static Connections justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Connections(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      status,
      followBy,
      followTo);
  }
  public interface StatusStep {
    FollowByStep status(String status);
  }
  

  public interface FollowByStep {
    FollowToStep followBy(UserCloudGallery followBy);
  }
  

  public interface FollowToStep {
    BuildStep followTo(UserCloudGallery followTo);
  }
  

  public interface BuildStep {
    Connections build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements StatusStep, FollowByStep, FollowToStep, BuildStep {
    private String id;
    private String status;
    private UserCloudGallery followBy;
    private UserCloudGallery followTo;
    @Override
     public Connections build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Connections(
          id,
          status,
          followBy,
          followTo);
    }
    
    @Override
     public FollowByStep status(String status) {
        Objects.requireNonNull(status);
        this.status = status;
        return this;
    }
    
    @Override
     public FollowToStep followBy(UserCloudGallery followBy) {
        Objects.requireNonNull(followBy);
        this.followBy = followBy;
        return this;
    }
    
    @Override
     public BuildStep followTo(UserCloudGallery followTo) {
        Objects.requireNonNull(followTo);
        this.followTo = followTo;
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
    private CopyOfBuilder(String id, String status, UserCloudGallery followBy, UserCloudGallery followTo) {
      super.id(id);
      super.status(status)
        .followBy(followBy)
        .followTo(followTo);
    }
    
    @Override
     public CopyOfBuilder status(String status) {
      return (CopyOfBuilder) super.status(status);
    }
    
    @Override
     public CopyOfBuilder followBy(UserCloudGallery followBy) {
      return (CopyOfBuilder) super.followBy(followBy);
    }
    
    @Override
     public CopyOfBuilder followTo(UserCloudGallery followTo) {
      return (CopyOfBuilder) super.followTo(followTo);
    }
  }
  
}
