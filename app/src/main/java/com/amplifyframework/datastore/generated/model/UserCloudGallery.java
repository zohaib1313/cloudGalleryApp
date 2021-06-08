package com.amplifyframework.datastore.generated.model;


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

/** This is an auto generated class representing the UserCloudGallery type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "UserCloudGalleries")
public final class UserCloudGallery implements Model {
  public static final QueryField ID = field("UserCloudGallery", "id");
  public static final QueryField NAME = field("UserCloudGallery", "name");
  public static final QueryField PHONE = field("UserCloudGallery", "phone");
  public static final QueryField IMAGE = field("UserCloudGallery", "image");
  public static final QueryField DEVICE_TOKEN = field("UserCloudGallery", "device_token");
  public static final QueryField CREATED_TIME = field("UserCloudGallery", "createdTime");
  public static final QueryField IS_PUBLIC = field("UserCloudGallery", "isPublic");
  public static final QueryField ABOUT = field("UserCloudGallery", "about");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="String", isRequired = true) String phone;
  private final @ModelField(targetType="String") String image;
  private final @ModelField(targetType="String", isRequired = true) String device_token;
  private final @ModelField(targetType="String", isRequired = true) String createdTime;
  private final @ModelField(targetType="Boolean", isRequired = true) Boolean isPublic;
  private final @ModelField(targetType="String") String about;
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public String getPhone() {
      return phone;
  }
  
  public String getImage() {
      return image;
  }
  
  public String getDeviceToken() {
      return device_token;
  }
  
  public String getCreatedTime() {
      return createdTime;
  }
  
  public Boolean getIsPublic() {
      return isPublic;
  }
  
  public String getAbout() {
      return about;
  }
  
  private UserCloudGallery(String id, String name, String phone, String image, String device_token, String createdTime, Boolean isPublic, String about) {
    this.id = id;
    this.name = name;
    this.phone = phone;
    this.image = image;
    this.device_token = device_token;
    this.createdTime = createdTime;
    this.isPublic = isPublic;
    this.about = about;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      UserCloudGallery userCloudGallery = (UserCloudGallery) obj;
      return ObjectsCompat.equals(getId(), userCloudGallery.getId()) &&
              ObjectsCompat.equals(getName(), userCloudGallery.getName()) &&
              ObjectsCompat.equals(getPhone(), userCloudGallery.getPhone()) &&
              ObjectsCompat.equals(getImage(), userCloudGallery.getImage()) &&
              ObjectsCompat.equals(getDeviceToken(), userCloudGallery.getDeviceToken()) &&
              ObjectsCompat.equals(getCreatedTime(), userCloudGallery.getCreatedTime()) &&
              ObjectsCompat.equals(getIsPublic(), userCloudGallery.getIsPublic()) &&
              ObjectsCompat.equals(getAbout(), userCloudGallery.getAbout());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getPhone())
      .append(getImage())
      .append(getDeviceToken())
      .append(getCreatedTime())
      .append(getIsPublic())
      .append(getAbout())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("UserCloudGallery {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("phone=" + String.valueOf(getPhone()) + ", ")
      .append("image=" + String.valueOf(getImage()) + ", ")
      .append("device_token=" + String.valueOf(getDeviceToken()) + ", ")
      .append("createdTime=" + String.valueOf(getCreatedTime()) + ", ")
      .append("isPublic=" + String.valueOf(getIsPublic()) + ", ")
      .append("about=" + String.valueOf(getAbout()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
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
  public static UserCloudGallery justId(String id) {
//    try {
//      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
//    } catch (Exception exception) {
//      throw new IllegalArgumentException(
//              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
//              "of an existing object with only its ID field for sending as a mutation parameter. When " +
//              "creating a new object, use the standard builder method and leave the ID field blank."
//      );
//    }
    return new UserCloudGallery(
      id,
      null,
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
      name,
      phone,
      image,
      device_token,
      createdTime,
      isPublic,
      about);
  }
  public interface NameStep {
    PhoneStep name(String name);
  }
  

  public interface PhoneStep {
    DeviceTokenStep phone(String phone);
  }
  

  public interface DeviceTokenStep {
    CreatedTimeStep deviceToken(String deviceToken);
  }
  

  public interface CreatedTimeStep {
    IsPublicStep createdTime(String createdTime);
  }
  

  public interface IsPublicStep {
    BuildStep isPublic(Boolean isPublic);
  }
  

  public interface BuildStep {
    UserCloudGallery build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep image(String image);
    BuildStep about(String about);
  }
  

  public static class Builder implements NameStep, PhoneStep, DeviceTokenStep, CreatedTimeStep, IsPublicStep, BuildStep {
    private String id;
    private String name;
    private String phone;
    private String device_token;
    private String createdTime;
    private Boolean isPublic;
    private String image;
    private String about;
    @Override
     public UserCloudGallery build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new UserCloudGallery(
          id,
          name,
          phone,
          image,
          device_token,
          createdTime,
          isPublic,
          about);
    }
    
    @Override
     public PhoneStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public DeviceTokenStep phone(String phone) {
        Objects.requireNonNull(phone);
        this.phone = phone;
        return this;
    }
    
    @Override
     public CreatedTimeStep deviceToken(String deviceToken) {
        Objects.requireNonNull(deviceToken);
        this.device_token = deviceToken;
        return this;
    }
    
    @Override
     public IsPublicStep createdTime(String createdTime) {
        Objects.requireNonNull(createdTime);
        this.createdTime = createdTime;
        return this;
    }
    
    @Override
     public BuildStep isPublic(Boolean isPublic) {
        Objects.requireNonNull(isPublic);
        this.isPublic = isPublic;
        return this;
    }
    
    @Override
     public BuildStep image(String image) {
        this.image = image;
        return this;
    }
    
    @Override
     public BuildStep about(String about) {
        this.about = about;
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
        
//        try {
//            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
//        } catch (Exception exception) {
//          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
//                    exception);
//        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String name, String phone, String image, String deviceToken, String createdTime, Boolean isPublic, String about) {
      super.id(id);
      super.name(name)
        .phone(phone)
        .deviceToken(deviceToken)
        .createdTime(createdTime)
        .isPublic(isPublic)
        .image(image)
        .about(about);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder phone(String phone) {
      return (CopyOfBuilder) super.phone(phone);
    }
    
    @Override
     public CopyOfBuilder deviceToken(String deviceToken) {
      return (CopyOfBuilder) super.deviceToken(deviceToken);
    }
    
    @Override
     public CopyOfBuilder createdTime(String createdTime) {
      return (CopyOfBuilder) super.createdTime(createdTime);
    }
    
    @Override
     public CopyOfBuilder isPublic(Boolean isPublic) {
      return (CopyOfBuilder) super.isPublic(isPublic);
    }
    
    @Override
     public CopyOfBuilder image(String image) {
      return (CopyOfBuilder) super.image(image);
    }
    
    @Override
     public CopyOfBuilder about(String about) {
      return (CopyOfBuilder) super.about(about);
    }
  }
  
}
