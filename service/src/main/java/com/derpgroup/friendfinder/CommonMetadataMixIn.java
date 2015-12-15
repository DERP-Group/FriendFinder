package com.derpgroup.friendfinder;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property="type", defaultImpl = FriendFinderMetadata.class)
@JsonSubTypes({
  @Type(value = FriendFinderMetadata.class)
})
public abstract class CommonMetadataMixIn {

}
