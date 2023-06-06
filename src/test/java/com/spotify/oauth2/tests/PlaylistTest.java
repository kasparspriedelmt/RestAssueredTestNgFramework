package com.spotify.oauth2.tests;

import com.spotify.oauth2.api.StatusCode;
import com.spotify.oauth2.api.applicationAPI.PlaylistApi;
import com.spotify.oauth2.pojo.Error;
import com.spotify.oauth2.pojo.Playlist;

import com.spotify.oauth2.utils.DataLoader;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.Link;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PlaylistTest extends BaseTest {

    @Test
    public void shouldBeAbleToCreatePlaylist(){
        Playlist requestPlaylist = playlistBuilder("New RestAssured Playlist", "sample RestAssured playlist", false);
        Response response = PlaylistApi.post(requestPlaylist);
        assertStatusCode(response.statusCode(), StatusCode.CODE_201.getCode());
        assertPlaylistEqual(response.as(Playlist.class), requestPlaylist);
    }

    @Link("http://gerda.lmt.lv/")
    @TmsLink("VML-000")
    @Link(name = "URLNAME", type = "myLink")
    @Issue("This is Issue VML-007")
    @Description("This is alluere report description")
    @Test
    public void shouldBeAbleToGetPlaylist(){
        Response response = PlaylistApi.get(DataLoader.getInstance().getPlaylistId());
        assertStatusCode(response.statusCode(), StatusCode.CODE_200.getCode());
    }

    @Test
    public void shouldBeAbleToUpdatePlaylist(){
        Playlist requestPlaylist = playlistBuilder("New RestAssured UPDATE Playlist", "sample RestAssured UPDATE playlist", false);
        Response response = PlaylistApi.update(DataLoader.getInstance().getUpdatePlaylistId(), requestPlaylist);
        assertStatusCode(response.getStatusCode(), StatusCode.CODE_200.getCode());
    }

    @Test
    public void shouldNotBeAbleToCreatePlaylistWithoutName(){
        Playlist requestPlaylist = playlistBuilder("","sample RestAssured empty playlist", false);
        Response response = PlaylistApi.post(requestPlaylist);
        assertStatusCode(response.getStatusCode(), StatusCode.CODE_400.getCode());
        assertError(response.as(Error.class),StatusCode.CODE_400.getCode(), StatusCode.CODE_400.getMsg());
    }

    @Test
    public void shouldNotBeAbleToCreatePlaylistWithExpiredToken(){
        String invalid_token = "12345";

        Playlist requestPlaylist = playlistBuilder("NewPlaylistRESTASSURED","sample RestAssured playlist",false);
        Response response = PlaylistApi.post(invalid_token,requestPlaylist);
        assertStatusCode(response.getStatusCode(), StatusCode.CODE_401.getCode());
        assertError(response.as(Error.class),StatusCode.CODE_401.getCode(), StatusCode.CODE_401.getMsg());
    }
    public Playlist playlistBuilder(String name, String description, boolean _public){
        return Playlist.builder().
                name(name).
                description(description).
                _public(_public).
                build();
    }

    public void assertPlaylistEqual(Playlist responsePlaylist, Playlist requestPlaylist){
        assertThat(responsePlaylist.getName(), equalTo(requestPlaylist.getName()));
        assertThat(responsePlaylist.get_public(), equalTo(requestPlaylist.get_public()));
    }

    public void assertStatusCode(int actualStatusCode, int expectedStatusCode){
        assertThat(actualStatusCode, equalTo(expectedStatusCode));
    }

    public void assertError(Error responseErr, int expectedStatusCode, String expectedMsg) {
        assertThat(responseErr.getError().getStatus(), equalTo(expectedStatusCode));
        assertThat(responseErr.getError().getMessage(), equalTo(expectedMsg));
    }
}
