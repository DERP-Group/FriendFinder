package com.derpgroup.friendfinder.manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.derpwizard.manager.AbstractManager;
import com.derpgroup.derpwizard.voice.exception.DerpwizardException;
import com.derpgroup.derpwizard.voice.model.SsmlDocumentBuilder;
import com.derpgroup.derpwizard.voice.model.VoiceInput;
import com.derpgroup.derpwizard.voice.util.ConversationHistoryUtils;
import com.derpgroup.friendfinder.MixInModule;
import com.derpgroup.friendfinder.model.SteamClientWrapper;
import com.derpgroup.friendfinder.model.UserData;
import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.friendslist.Friend;
import com.lukaspradel.steamapi.data.json.friendslist.GetFriendList;
import com.lukaspradel.steamapi.data.json.playersummaries.GetPlayerSummaries;
import com.lukaspradel.steamapi.data.json.playersummaries.Player;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetFriendListRequest;
import com.lukaspradel.steamapi.webapi.request.GetPlayerSummariesRequest;
import com.lukaspradel.steamapi.webapi.request.builders.SteamWebApiRequestFactory;

public class FriendFinderManager extends AbstractManager {
  private final Logger LOG = LoggerFactory.getLogger(FriendFinderManager.class);
  
  private SteamWebApiClient steamClient;
  private static SteamClientWrapper steamClientWrapper;
  
  static{
    steamClientWrapper = SteamClientWrapper.getInstance();
  }
  
  static{
    ConversationHistoryUtils.getMapper().registerModule(new MixInModule());
  }
  
  public FriendFinderManager(){
    super();
  }

  @Override
  protected void doHelpRequest(VoiceInput voiceInput,
      SsmlDocumentBuilder builder) throws DerpwizardException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void doHelloRequest(VoiceInput voiceInput,
      SsmlDocumentBuilder builder) throws DerpwizardException {
    builder.text("Yo.  Let me find your friends.");
  }

  @Override
  protected void doGoodbyeRequest(VoiceInput voiceInput,
      SsmlDocumentBuilder builder) throws DerpwizardException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void doCancelRequest(VoiceInput voiceInput,
      SsmlDocumentBuilder builder) throws DerpwizardException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void doStopRequest(VoiceInput voiceInput,
      SsmlDocumentBuilder builder) throws DerpwizardException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void doConversationRequest(VoiceInput voiceInput,
      SsmlDocumentBuilder builder) throws DerpwizardException {
    steamClient = steamClientWrapper.getClient();

    if(voiceInput.getMessageSubject().equals("STEAM_FRIENDS")){
      if(steamClient == null){
        String message = "Couldn't build Steam client.";
        LOG.warn(message);
        throw new DerpwizardException(new SsmlDocumentBuilder().text(message).build().getSsml(), message, "No Steam client.");
      }else{
        UserData data = getUserData("");
        GetFriendListRequest friendListRequest = SteamWebApiRequestFactory.createGetFriendListRequest(data.getSteamId());
        List<String> friends = new LinkedList<String>();
        try {
          GetFriendList friendList = steamClient.<GetFriendList> processRequest(friendListRequest);
          for(Friend friend : friendList.getFriendslist().getFriends()){
            friends.add(friend.getSteamid());
          }
        } catch (SteamApiException e) {
          String message = "Unknown Steam exception '" + e.getMessage() + "'.";
          LOG.warn(message);
          throw new DerpwizardException(new SsmlDocumentBuilder().text(message).build().getSsml(), message, "Unknown Steam exception.");
        }

        GetPlayerSummariesRequest playerSummariesRequest = SteamWebApiRequestFactory.createGetPlayerSummariesRequest(friends);
        //List<String> onlineFriends = new LinkedList<String>();
        Map<Integer, String> stateValues = new HashMap<Integer, String>();
        stateValues.put(1, " is online.  ");
        stateValues.put(2, " is online, but busy.  ");
        stateValues.put(3, " is online, but away.  ");
        stateValues.put(4, " is online, but snoozing.  ");
        stateValues.put(5, " is online.  ");
        stateValues.put(6, " is online and looking to play!  ");
        try {
          GetPlayerSummaries playerSummaries = steamClient.<GetPlayerSummaries> processRequest(playerSummariesRequest);
          for(Player player : playerSummaries.getResponse().getPlayers()){
            Integer state = player.getPersonastate();
            if(state == null || state <= 0 || state >= 7){
              continue;
            }
            String username = player.getPersonaname();
            builder.text(username + stateValues.get(state)).endSentence();
          }
        } catch (SteamApiException e) {
          String message = "Unknown Steam exception '" + e.getMessage() + "'.";
          LOG.warn(message);
          throw new DerpwizardException(new SsmlDocumentBuilder().text(message).build().getSsml(), message, "Unknown Steam exception.");
        }
      }
    }else{
      String message = "Unknown request type '" + voiceInput.getMessageSubject() + "'.";
      LOG.warn(message);
      throw new DerpwizardException(new SsmlDocumentBuilder().text(message).build().getSsml(), message, "Unknown request.");
    }
    
  }

  private UserData getUserData(String string) {
    UserData userData = new UserData();
    userData.setSteamId("76561198019030536"); //Hardcoded to Eric's ID for now
    return userData;
  }

}
