package org.incendo.plot.http;

import com.boydti.fawe.util.MainUtil;
import org.incendo.plot.http.resource.PlotResource;
import xyz.kvantum.server.api.config.CoreConfig;
import xyz.kvantum.server.api.core.Kvantum;
import xyz.kvantum.server.api.core.ServerImplementation;
import xyz.kvantum.server.api.logging.LogWrapper;
import xyz.kvantum.server.api.util.MapBuilder;
import xyz.kvantum.server.api.util.RequestManager;
import xyz.kvantum.server.api.views.HTMLView;
import xyz.kvantum.server.implementation.ServerContext;
import xyz.kvantum.server.implementation.SimpleServer;

import java.io.File;
import java.net.URL;
import java.util.Optional;

public class PlotHttp {

    private final PlotResourceManager resourceManager = new PlotResourceManager();
    private final File parentFolder;
    private final File downloadsFolder;
    private final LogWrapper logWrapper;
    private final File jarFile;
    private WebSettings webSettings;

    private boolean commands = false;

    /**
     * Construct a new plot http instance. Does not start the server.
     *
     * @see #start() to start the server
     * @param parentFolder Parent folder, cannot be null
     * @param logWrapper Log wrapper, cannot be null
     */
    public PlotHttp(final File parentFolder, final LogWrapper logWrapper) throws Exception {
        if (parentFolder == null) {
            throw new IllegalArgumentException("Parent folder cannot be null");
        }
        if  (logWrapper == null) {
            throw new IllegalArgumentException("Log wrapper cannot be null");
        }
        this.parentFolder = parentFolder;
        this.downloadsFolder = new File(this.parentFolder, "downloads");
        final URL url = PlotHttp.class.getProtectionDomain().getCodeSource().getLocation();
        this.jarFile = new File(new URL(url.toURI().toString().split("!")[0].replaceAll("jar:file", "file")).toURI().getPath());
        this.logWrapper = logWrapper;
    }

    public void start() {
        // Clear downloads on load
        deleteFolder(this.downloadsFolder);
        // Setup configuration
        this.setupConfig();
        // Setup resources
        this.setupResources();
        // Setup web interface
        this.setupWeb();
        // Setup commands
        this.setupCommands();
        // Handle Kvantum settings
        CoreConfig.setPreConfigured(true);
        CoreConfig.enableInputThread = false;
        CoreConfig.enableSecurityManager = false;
        CoreConfig.autoDetectViews = false;
        CoreConfig.port = this.webSettings.PORT;
        CoreConfig.webAddress = this.webSettings.WEB_IP;
        CoreConfig.exitOnStop = false;
        CoreConfig.loadWebJars = false;
        CoreConfig.Templates.engine = CoreConfig.TemplatingEngine.NONE.name();

        /*
        TODO
            RequestManager manager = server.getRequestManager();
            if (config().WHITELIST.ENABLED) {
                for (final String ip : config().WHITELIST.ALLOWED) {
                    final HashMap<String, String> params = new HashMap<>();
                    params.put("*", "*");
                    manager.addToken(new Request(ip, "*", "*", params));
                }
            } else {
                final HashMap<String, String> params = new HashMap<>();
                params.put("*", "*");
                manager.addToken(new Request("*", "*", "*", params));
            }
         */


        final ServerContext serverContext = ServerContext
            .builder().coreFolder(this.parentFolder).logWrapper(this.logWrapper).router(
                RequestManager.builder().build()).standalone(false).serverSupplier(SimpleServer::new)
            .build();
        final Optional<Kvantum> serverOptional = serverContext.create();
        if (serverOptional.isPresent()) {
            final Kvantum kvantum = serverOptional.get();
            kvantum.getRouter().clear(); // Remove all automatically created views, if any
            kvantum.getRouter().scanAndAdd(new PlotRequestHandler(this));
            kvantum.getRouter().add(new HTMLView("[file=index].html", MapBuilder.<String, Object>newHashMap()
                .put("cache-applicable", true).put("filePattern", "${file}.html").put("folder", "./views")
                .get())); // TODO: Make sure these settings are correct
            kvantum.start();
        } else {
            throw new IllegalStateException("Failed to create Kvantum instance");
        }
    }

    public void stop() {
        if (ServerImplementation.getImplementation() != null) {
            ServerImplementation.getImplementation().stopServer();
        }
    }

    public PlotResourceManager getResourceManager() {
        return this.resourceManager;
    }

    private void setupConfig() {
        File config = new File(this.parentFolder, "settings.yml");
        webSettings = new WebSettings();
        webSettings.load(config);
        webSettings.VERSION = "development";
        webSettings.save(config);
    }

    private void deleteFolder(final File folder) {
        final File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (final File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    private void setupResources() {
        // Adding the plot resource
        // TODO: this.resourceManager.addResource(new ClusterResource());
        // TODO this.resourceManager.addResource(new CommentResource());
        // TODO this.resourceManager.addResource(new SchematicResource());
        // TODO this.resourceManager.addResource(new UUIDResource());
        // TODO this.resourceManager.addResource(new WebResource());
        // TODO this.resourceManager.addResource(new WorldResource());
        this.resourceManager.addResource(new PlotResource());
    }

    private void setupWeb() {
        final File dir = new File(this.parentFolder, "views");
        MainUtil.copyFile(jarFile, "views/index.html", dir);
        MainUtil.copyFile(jarFile, "views/upload.html", dir);
        MainUtil.copyFile(jarFile, "views/uploadworld.html", dir);
        MainUtil.copyFile(jarFile, "views/download.html", dir);
        MainUtil.copyFile(jarFile, "level.dat", parentFolder);
    }

    private void setupCommands() {
        if (this.commands) {
            return;
        }
        this.commands = true;
        // TODO MainCommand.getInstance().register(new Web());
        // TODO MainCommand.getInstance().register(new Upload());
    }

}
