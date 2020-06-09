package com.starblues.rope.plugins.basic.handler;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.handler.DateHandler;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.utils.ParamUtils;
import com.starblues.rope.utils.PlaceholderResolver;
import com.starblues.rope.utils.PluginLogger;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 本地图片文件转换成base64字符串
 *
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-05-19
 */
@Component
public class LocalImageToBase64Handler implements DateHandler {

    private Logger logger;

    private static final String ID = "local-image-to-base64";

    private final PlaceholderResolver placeholderResolver;
    private Param param = new Param();

    public LocalImageToBase64Handler() {
        this.placeholderResolver = new PlaceholderResolver();
    }

    /**
     * 用于单元测试set参数
     * @param param 参数信息
     */
    protected void setParam(Param param){
        this.param = param;
    }


    @Override
    public boolean initialize(String processId) throws Exception {
        logger = PluginLogger.getLogger(this, processId);
        String localImagePathRule = param.getLocalImagePathRule();
        ParamUtils.check(processId, "localImagePathRule", localImagePathRule);
        ParamUtils.check(processId, "base64ImageFieldKey", localImagePathRule);
        return true;
    }

    @Override
    public Record handle(Record record) throws Exception {
        Map<String, Object> recordMap = record.toMap();
        String imageFilePath = placeholderResolver.resolveByMap(param.getLocalImagePathRule(), recordMap);
        String base64 = imageToBase64(imageFilePath);
        if(!StringUtils.isEmpty(base64)){
            record.putColumn(Column.auto(param.getBase64ImageFieldKey(), base64));
        }
        return record;
    }



    /**
     * 转换base64
     * @param imageFilePath 图片文件路径
     */
    private String imageToBase64(String imageFilePath){
        byte[] data = null;
        // 读取图片字节数组
        try {
            File imageFile = new File(imageFilePath);
            if(!imageFile.exists()){
                logger.error("Image file does not exist：{}", imageFilePath);
                return null;
            }
            InputStream in = new FileInputStream(imageFilePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
            // 对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            // 返回Base64编码过的字节数组字符串
            return encoder.encode(data);
        } catch (IOException e) {
            logger.error("Image[{}] encode error.", imageFilePath, e);
            return null;
        }
    }



    @Override
    public void destroy() throws Exception {

    }

    @Override
    public ConfigParameter configParameter() {
        return param;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "图片转base64";
    }

    @Override
    public String describe() {
        return "将本地图片文件转换成base64字符串";
    }

    @Getter
    @Setter
    public static class Param implements ConfigParameter{

        public static final String IMAGE_NAME = "imageName";

        private static final String P_LOCAL_IMAGE_PATH_RULE = "localImagePathRule";
        private static final String P_BASE64_IMAGE_FIELD_KEY = "base64ImageFieldKey";


        /**
         * 本地图片路径规则。可使用 ${field} 预填充。field 为其他字段中的值
         */
        private String localImagePathRule;

        /**
         * base64 图片的字段key
         */
        private String base64ImageFieldKey;




        @Override
        public void parsing(ConfigParamInfo paramInfo) throws Exception {
            localImagePathRule = paramInfo.getString(P_LOCAL_IMAGE_PATH_RULE);
            base64ImageFieldKey = paramInfo.getString(P_BASE64_IMAGE_FIELD_KEY);
        }

        @Override
        public ConfigParam configParam() {
            ConfigParam configParam = new ConfigParam();

            configParam.addField(
                    TextField.toBuilder(
                            P_LOCAL_IMAGE_PATH_RULE, "图片路径规则", "")
                            .description("可使用 ${field} 预填充。${field} 为其他字段中的值")
                            .required(true)
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(
                            P_BASE64_IMAGE_FIELD_KEY, "base64图片字段key", "")
                            .description("图片转换成base64后, 所存储的字段key")
                            .required(true)
                            .build()
            );


            return configParam;
        }
    }

}
