package ai.resola.chatapp.model;

import com.google.android.gms.maps.model.LatLng;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import org.jsoup.nodes.Element;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.resola.chatapp.CONST;

/**
 * Created by dotuan on 2017/03/24.
 */

public class Message implements IMessage {

    public static class GeoPoint {
        public double lon;
        public double lat;
    }

    public static class Media {
        public String url;
        public String imageUrl;
        public String videoUrl;
        public GeoPoint geo;

        public LatLng getLocation() {
            return new LatLng(geo.lat, geo.lon);
        }

        public String getYoutubeVideoId() {
            String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(videoUrl); //url is youtube url for which you want to extract the id.
            if (matcher.find()) {
                return matcher.group();
            }
            return null;
        }
    }

    public enum Type {
        Text,
        Link,
        Image,
        Video,
        Location
    }

    private  long id;
    private String text;
    private Date createdAt;
    private IUser user;

    private Type type;
    private Media media;

    public Message(String text, Type type, Media media, Date createdAt, IUser user) {
        this.text = text;
        this.type = type;
        this.media = media;
        this.createdAt = createdAt;
        this.user = user;
        this.id = UUID.randomUUID().getLeastSignificantBits();
    }

    public static Message create(Element element, Date createdAt, IUser user) {
        String tagName = element.tagName();
        String text = element.text().replace(CONST.MARK_LINE_BREAK, CONST.LINE_BREAK);
        text = text.trim();
        Media media = null;
        Type type = Type.Text;
        switch (tagName) {
            case "text":
                break;
            case "a":
                type = Type.Link;
                media = new Message.Media();
                media.url = element.attr("href");
                text = media.url;
                break;
            case "img":
                type = Type.Image;
                media = new Message.Media();
                media.imageUrl = element.attr("src");
                break;
            case "video":
                type = Type.Video;
                media = new Message.Media();
                media.videoUrl = element.attr("src");
                text = media.videoUrl;
                break;
            case "map":
                type = Type.Location;
                media = new Message.Media();
                Message.GeoPoint geo = new Message.GeoPoint();
                geo.lat = Double.parseDouble(element.attr("lat"));
                geo.lon = Double.parseDouble(element.attr("lon"));
                media.geo = geo;
                break;

            default: break;
        }

        return new Message(text, type, media, createdAt, user);
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    public Type getType() {
        return type;
    }

    public Media getMedia() {
        return media;
    }

    public boolean isMedia() {
        return (type != Type.Text);
    }
}

