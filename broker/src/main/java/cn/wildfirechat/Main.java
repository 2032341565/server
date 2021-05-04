package cn.wildfirechat;

import cn.wildfirechat.http.LoServer;
import cn.wildfirechat.http.action.admin.AdminAction;
import cn.wildfirechat.im.BrokerConstants;
import cn.wildfirechat.im.server.Server;
import cn.wildfirechat.im.server.config.IConfig;
import cn.wildfirechat.push.PushServer;
import cn.wildfirechat.util.Utility;
import io.netty.util.ResourceLeakDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static cn.wildfirechat.im.BrokerConstants.HTTP_SERVER_API_NO_CHECK_TIME;
import static cn.wildfirechat.im.BrokerConstants.HTTP_SERVER_SECRET_KEY;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        //启动im服务器
        Server instance = new Server();
        final IConfig config = Server.defaultConfig();
        System.setProperty("hazelcast.logging.type", "none" );
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        instance.startServer(config);

        //启动http服务器
        int httpLocalPort = Integer.parseInt(config.getProperty(BrokerConstants.HTTP_LOCAL_PORT));
        int httpAdminPort = Integer.parseInt(config.getProperty(BrokerConstants.HTTP_ADMIN_PORT));

        AdminAction.setSecretKey(config.getProperty(HTTP_SERVER_SECRET_KEY));
        AdminAction.setNoCheckTime(config.getProperty(HTTP_SERVER_API_NO_CHECK_TIME));
        final LoServer httpServer = new LoServer(httpLocalPort, httpAdminPort,
            instance.getProcessor().getMessagesStore(), instance.getStore().sessionsStore());
        try {
            httpServer.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Utility.printExecption(LOG, e);
        }

        //启动push服务器
        final PushServer pushServer = PushServer.getServer();
        pushServer.init(config, instance.getStore().sessionsStore());

        //Bind  a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(instance::stopServer));
        Runtime.getRuntime().addShutdownHook(new Thread(httpServer::shutdown));

        System.out.println("Wildfire IM server start success...");
    }
}
