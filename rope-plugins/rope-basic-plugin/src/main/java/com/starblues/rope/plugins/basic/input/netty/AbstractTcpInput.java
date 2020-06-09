package com.starblues.rope.plugins.basic.input.netty;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.BooleanField;
import com.starblues.rope.core.common.param.fields.DropdownField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.converter.ConverterFactory;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import com.starblues.rope.core.input.support.accept.AbstractAcceptConverterInput;
import com.starblues.rope.plugins.basic.input.netty.confg.NettyInputConfig;
import com.starblues.rope.plugins.basic.utils.KeyUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * 抽象的netty输入
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractTcpInput<Source> extends AbstractAcceptConverterInput<Source> {


    protected final TcpConfig tcpConfig;

    protected Logger log;

    private InetSocketAddress socketAddress;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;


    public AbstractTcpInput(TcpConfig tcpConfig, ConverterFactory converterFactory) {
        super(converterFactory);
        this.tcpConfig = Objects.requireNonNull(tcpConfig,"tcpConfig can't null");
    }


    @Override
    public void init() throws Exception {
        this.socketAddress = new InetSocketAddress(
                tcpConfig.getBindAddress(),
                tcpConfig.getPort()
        );
        Logger logger = getLogger();
        if(logger == null){
            log = LoggerFactory.getLogger(AbstractTcpInput.class);
        } else {
            log = logger;
        }
    }

    @Override
    protected void toStart(Consumer consumer) throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);

        bootstrap.option(ChannelOption.SO_KEEPALIVE, tcpConfig.isTcpKeepalive());
        bootstrap.option(ChannelOption.SO_RCVBUF, tcpConfig.getReceiveBufferSize());

        final LinkedHashMap<String, Callable<? extends ChannelHandler>> allHandlerList =
                Maps.newLinkedHashMap();
        LinkedHashMap<String, Callable<? extends ChannelHandler>> baseHandlers = getBaseChannelHandlers(consumer);
        if(baseHandlers != null && !baseHandlers.isEmpty()){
            allHandlerList.putAll(baseHandlers);
        }
        LinkedHashMap<String, Callable<? extends ChannelHandler>> finalHandlers = getFinalChannelHandlers(consumer);
        if(finalHandlers != null && !finalHandlers.isEmpty()){
            allHandlerList.putAll(finalHandlers);
        }


        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                final ChannelPipeline p = channel.pipeline();
                for(Map.Entry<String, Callable<? extends ChannelHandler>> entry : allHandlerList.entrySet()) {
                    p.addLast(entry.getKey(), entry.getValue().call());
                }
            }
        });
        channelFuture = bootstrap.bind(socketAddress);
        log.info("Bind [{}:{}] success", socketAddress.getHostString(), socketAddress.getPort());
    }




    @Override
    protected void toStop() throws Exception {
        if (channelFuture != null) {
            channelFuture.channel().closeFuture();
            channelFuture = null;
        }
        if(bossGroup != null){
            bossGroup.shutdownGracefully();
            bossGroup = null;
        }
        if(workerGroup != null){
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }
    }

    protected LinkedHashMap<String, Callable<? extends ChannelHandler>> getBaseChannelHandlers(Consumer consumer) throws Exception{
        LinkedHashMap<String, Callable<? extends ChannelHandler>> handlerList = Maps.newLinkedHashMap();

        if (!tcpConfig.isTslEnable()) {
            return handlerList;
        }

        File tlsCertFile = new File(tcpConfig.getTlsCertFile());
        File tlsKeyFile = new File(tcpConfig.getKeyFile());

        if (!tlsCertFile.exists() || !tlsKeyFile.exists()) {
            log.warn("TLS key file or certificate file does not exist, " +
                    "creating a self-signed certificate for process '{}'.", processId());

            final String tmpDir = System.getProperty("java.io.tmpdir");
            Objects.requireNonNull(tmpDir, "The temporary 'java.io.tmpdir' directory must not be null!");
            final Path tmpPath = Paths.get(tmpDir);
            if(!Files.isDirectory(tmpPath) || !Files.isWritable(tmpPath)) {
                throw new IllegalStateException("Couldn't write to temporary directory: "
                        + tmpPath.toAbsolutePath());
            }

            try {
                final SelfSignedCertificate ssc = new SelfSignedCertificate(
                        tcpConfig.getBindAddress() + ":" + tcpConfig.getPort());
                tlsCertFile = ssc.certificate();
                tlsKeyFile = ssc.privateKey();
            } catch (CertificateException e) {
                log.error("Problem creating a self-signed certificate " +
                                "for process '{}'." ,
                        processId(), e);
                return null;
            }


            if (tlsCertFile.exists() && tlsKeyFile.exists()) {
                handlerList.put("tls", buildSslHandlerCallable(tlsCertFile, tlsKeyFile));
            }
        }


        return handlerList;
    }


    private Callable<? extends ChannelHandler> buildSslHandlerCallable(File tlsCertFile, File tlsKeyFile) {
        return new Callable<ChannelHandler>() {

            @Override
            public ChannelHandler call() throws Exception {
                try {
                    return new SslHandler(getSSLEngine());
                } catch (Exception e) {
                    log.error("Error creating SSL context. Make sure the certificate and key are in the correct format: cert=X.509 key=PKCS#8");
                    throw e;
                }
            }

            private SSLEngine getSSLEngine() throws Exception {
                final SSLContext instance = SSLContext.getInstance("TLS");
                TrustManager[] initTrustStore = new TrustManager[0];
                String tlsClientAuth = tcpConfig.getTlsClientAuth();
                if(StringUtils.isEmpty(tlsClientAuth)){
                    tlsClientAuth = TcpConfig.TLS_CLIENT_AUTH_DISABLED;
                }

                if (TcpConfig.TLS_CLIENT_AUTH_OPTIONAL.equals(tlsClientAuth)
                        || TcpConfig.TLS_CLIENT_AUTH_REQUIRED.equals(tlsClientAuth)) {
                    File tlsClientAuthCertFile = new File(tcpConfig.getClientAuthTrustedCertFile());
                    if (tlsClientAuthCertFile.exists()) {
                        initTrustStore = KeyUtil.initTrustStore(tlsClientAuthCertFile);
                    } else {
                        log.warn("client auth configured, but no authorized certificates / certificate authorities configured");
                    }
                }

                instance.init(KeyUtil.initKeyStore(tlsKeyFile, tlsCertFile,
                        tcpConfig.getKeyPassword()), initTrustStore, new SecureRandom());
                final SSLEngine engine = instance.createSSLEngine();

                engine.setUseClientMode(false);


                switch (tlsClientAuth) {
                    case TcpConfig.TLS_CLIENT_AUTH_DISABLED:
                        log.debug("Not using TLS client authentication");
                        break;
                    case TcpConfig.TLS_CLIENT_AUTH_OPTIONAL:
                        log.debug("Using optional TLS client authentication");
                        engine.setWantClientAuth(true);
                        break;
                    case TcpConfig.TLS_CLIENT_AUTH_REQUIRED:
                        log.debug("Using mandatory TLS client authentication");
                        engine.setNeedClientAuth(true);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown TLS client authentication mode: " + tlsClientAuth);
                }

                return engine;
            }

        };
    }


    protected LinkedHashMap<String, Callable<? extends ChannelHandler>> getFinalChannelHandlers(Consumer consumer) {
        return Maps.newLinkedHashMap();
    }


    /**
     * 子类返回日志打印对象
     * @return Logger
     */
    protected abstract Logger getLogger();



    /**
     * 子类配置 NettyInputConfig 的继承类
     * @return NettyInputConfig 的继承类
     */
    @Override
    public NettyInputConfig configParameter(){
        return tcpConfig;
    }

    @Getter
    @ToString
    protected static class TcpConfig extends NettyInputConfig{

        public static final String USE_NULL_DELIMITER = "useNullDelimiter";
        private static final String MAX_MESSAGE_SIZE = "maxMessageSize";
        private static final String NGINX_PROXY = "isNginxProxy";


        private static final String TCP_KEEPALIVE = "tcpKeepalive";

        private static final String TLS_ENABLE = "tlsEnable";
        private static final String TLS_CERT_FILE = "tlsCertFile";
        private static final String TLS_KEY_FILE = "tlsKeyFile";
        private static final String TLS_KEY_PASSWORD = "tlsKeyPassword";

        private static final String TLS_CLIENT_AUTH = "tlsClientAuth";
        private static final String TLS_CLIENT_AUTH_TRUSTED_CERT_FILE = "tlsClientAuthTrustedCertFile";


        public static final String TLS_CLIENT_AUTH_DISABLED = "disabled";
        public static final String TLS_CLIENT_AUTH_OPTIONAL = "optional";
        public static final String TLS_CLIENT_AUTH_REQUIRED = "required";
        private static final Map<String, String> TLS_CLIENT_AUTH_OPTIONS = ImmutableMap.of(
                TLS_CLIENT_AUTH_DISABLED, TLS_CLIENT_AUTH_DISABLED,
                TLS_CLIENT_AUTH_OPTIONAL, TLS_CLIENT_AUTH_OPTIONAL,
                TLS_CLIENT_AUTH_REQUIRED, TLS_CLIENT_AUTH_REQUIRED);

        private boolean tcpKeepalive = false;

        private boolean tslEnable = false;
        private String tlsCertFile;
        private String keyFile;
        private String keyPassword;

        private String tlsClientAuth;
        private String clientAuthTrustedCertFile;

        @Override
        protected void childParsing(ConfigParamInfo paramInfo) throws Exception {
            super.childParsing(paramInfo);
            this.tcpKeepalive = paramInfo.getBoolean(TCP_KEEPALIVE, false);

            this.tslEnable = paramInfo.getBoolean(TLS_ENABLE, false);
            this.tlsCertFile = paramInfo.getString(TLS_CERT_FILE);
            this.keyFile = paramInfo.getString(TLS_KEY_FILE);
            this.keyPassword = paramInfo.getString(TLS_KEY_PASSWORD);

            this.tlsClientAuth = paramInfo.getString(TLS_CLIENT_AUTH, TLS_CLIENT_AUTH_DISABLED);
            this.clientAuthTrustedCertFile = paramInfo.getString(TLS_CLIENT_AUTH_TRUSTED_CERT_FILE);
        }

        @Override
        protected void configParam(ConfigParam configParam) {
            super.configParam(configParam);

            configParam.addField(
                    BooleanField.toBuilder(TCP_KEEPALIVE, "TCP keepalive", false)
                            .required(true)
                            .description("是否启用TCP keepalive")
                            .build()
            );

            configParam.addField(
                    BooleanField.toBuilder(TLS_ENABLE, "启用TLS", false)
                            .required(true)
                            .description("接受TLS连接")
                            .build()
            );
            configParam.addField(
                    TextField.toBuilder(TLS_CERT_FILE, "TLS认证文件", "")
                            .required(false)
                            .description("TLS认证文件的路径")
                            .build()
            );
            configParam.addField(
                    TextField.toBuilder(TLS_KEY_FILE, "TLS私钥文件", "")
                            .required(false)
                            .description("TLS私钥文件路径")
                            .build()
            );
            configParam.addField(
                    TextField.toBuilder(TLS_KEY_PASSWORD, "TLS密钥密码", "")
                            .required(false)
                            .description("加密文件的密码")
                            .build()
            );

            configParam.addField(
                    DropdownField.toBuilder(
                            TLS_CLIENT_AUTH, "TLS客户端认证",
                            TLS_CLIENT_AUTH_DISABLED,
                            TLS_CLIENT_AUTH_OPTIONS)
                            .required(false)
                            .description("TLS是否需要客户端认证")
                            .build()
            );
            configParam.addField(
                    TextField.toBuilder(TLS_CLIENT_AUTH_TRUSTED_CERT_FILE,
                            "TLS客户端认证证书", "")
                            .required(false)
                            .description("TLS客户端认证证书路径")
                            .build()
            );


        }
    }

}
