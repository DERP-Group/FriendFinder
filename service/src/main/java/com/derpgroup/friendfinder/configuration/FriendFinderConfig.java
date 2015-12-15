package com.derpgroup.friendfinder.configuration;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FriendFinderConfig {
  
  @NotNull
  private String apiKey;

  @JsonProperty
  public String getApiKey() {
    return apiKey;
  }

  @JsonProperty
  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }
}
