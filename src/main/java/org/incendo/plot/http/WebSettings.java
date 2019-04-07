package org.incendo.plot.http;

import com.boydti.fawe.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class WebSettings extends Config {

    @Final
    public String VERSION = null;

    @Comment("The port the server is running on")
    public int PORT = 8080;
    @Comment("The public web url")
    public String WEB_IP = "http://empcraft.com";
    @Comment("Log file")
    public String LOG_FILE = "plothttp.log";

    @Config.Create
    public WHITELIST WHITELIST;
    @Create
    public CONTENT CONTENT;
    @Create
    public API API;

    public static class WHITELIST {
        @Comment("Whitelist specific IP addresses")
        public boolean ENABLED = true;
        public List<String> ALLOWED = new ArrayList<>(Collections.singletonList("127.0.0.1"));
    }

    public static class CONTENT {
        @Comment("Serve content to whitelisted IPs")
        public boolean SERVE = true;
        @Comment("The max file upload size in bytes")
        public int MAX_UPLOAD = 200000000;
        @Comment("The file download format")
        public String FILENAME = "%world%-%id%-%player%";
        @Comment("The navigation markup for the web interface")
        public List<String> LINKS = new ArrayList<>(Arrays.asList("<a class=navlink href='https://www.spigotmc.org/resources/1177/'>Home</a>", "<a class=navlink href='https://github.com/IntellectualCrafters/PlotSquared/wiki'>Wiki</a>", "<a class=navlink href='https://github.com/IntellectualCrafters/PlotSquared/issues'>Report Issue</a>", "<a class=navlink href='https://discord.gg/ngZCzbU'>Support/Chat</a>"));
    }

    public static class API {
        @Comment("Serve the web API to whitelisted IPs")
        public boolean SERVE = true;
    }
}
