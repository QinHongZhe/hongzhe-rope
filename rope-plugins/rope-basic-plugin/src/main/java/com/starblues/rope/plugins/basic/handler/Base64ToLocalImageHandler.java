package com.starblues.rope.plugins.basic.handler;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.param.fields.BooleanField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.handler.DateHandler;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.utils.IDUtils;
import com.starblues.rope.utils.PlaceholderResolver;
import com.starblues.rope.utils.PluginLogger;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.util.Map;
import java.util.Objects;

/**
 * base64字符转换成本地图片文件
 *
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-05-19
 */
@Component
public class Base64ToLocalImageHandler implements DateHandler {

    private Logger logger;

    private static final String ID = "base64-to-local-image";

    private final PlaceholderResolver placeholderResolver;
    private Param param = new Param();

    public Base64ToLocalImageHandler() {
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
        return true;
    }

    @Override
    public Record handle(Record record) throws Exception {
        Column column = record.getColumn(param.getImageFieldKey());
        if(column == null){
            logger.warn("Not found base64 image string of key {}", param.getAccessPathFiledKey());
            return record;
        }
        String base64Image = column.getMetadata(String.class);
        if(base64Image == null){
            logger.warn("Not found base64 image string of key {}", param.getAccessPathFiledKey());
            return record;
        }
        // 得到图片的全路径
        String imageSavePath = param.getImageSavePath();
        File file = new File(imageSavePath);
        if (!file.exists()) {
            // 该路径不存在, 则新建
            file.mkdirs();
        }
        Map<String, Object> recordMap = record.toMap();
        String imageName = getImageName(recordMap);
        String imageFilePath = file.getPath() + File.separator + imageName;
        // 保存图片
        saveImage(base64Image, imageFilePath);
        // 处理图片访问路径
        processImageAccessPath(imageName, record, recordMap);
        // 处理完成后根据配置删除base64`图片
        if(param.isDeleteBase64ImageString() && !Objects.equals(param.getImageFieldKey(), param.getAccessPathFiledKey())){
            // 如果图片字段key和访问图片的key不相同, 则进行移除原来存储base64字段的key
            record.removeColumn(param.getImageFieldKey());
        }
        return record;
    }

    /**
     * 得到图片文件名称
     * @param recordMap 记录的Map集合
     * @return 图片文件名称
     */
    private String getImageName(Map<String, Object> recordMap){
        // 得到图片文件名称
        String imageNameRule = param.getImageNameRule();
        if(StringUtils.isEmpty(imageNameRule)){
            return IDUtils.uuid() + ".jpg";
        } else {
            return placeholderResolver.resolveByMap(imageNameRule, recordMap);
        }
    }

    /**
     * 保存图片
     * @param base64Image base64图片字符串
     * @param imageFilePath 保存的图片路径
     */
    private void saveImage(String base64Image, String imageFilePath){
        BASE64Decoder decoder = new BASE64Decoder();
        OutputStream out = null;
        try {
            out = new FileOutputStream(imageFilePath);
            // Base64解码
            byte[] b = decoder.decodeBuffer(base64Image);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    // 调整异常数据
                    b[i] += 256;
                }
            }
            out.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理图片访问路径
     * @param imageName 图片名称
     * @param record 当前记录
     * @param recordMap 记录的Map
     */
    private void processImageAccessPath(String imageName, Record record, Map<String, Object> recordMap){
        if(!param.isGenAccessPath()){
            return;
        }
        String accessPathFiledKey = param.getAccessPathFiledKey();
        if(StringUtils.isEmpty(accessPathFiledKey)){
            accessPathFiledKey = "accessPath";
        }
        String accessPathRule = param.getAccessPathRule();
        recordMap.put(Param.IMAGE_NAME, imageName);
        String accessPath = placeholderResolver.resolveByMap(accessPathRule, recordMap);
        record.putColumn(Column.auto(accessPathFiledKey, accessPath));
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
        return "base64转图片";
    }

    @Override
    public String describe() {
        return "将base64图片信息解析并保持到本地文件系统中";
    }

    @Getter
    @Setter
    public static class Param implements ConfigParameter{

        public static final String IMAGE_NAME = "imageName";

        private static final String P_IMAGE_FIELD_KEY = "imageFieldKey";
        private static final String P_IMAGE_NAME_RULE = "imageNameRule";
        private static final String P_IMAGE_SAVE_PATH = "imageSavePath";
        private static final String P_GEN_ACCESS_PATH = "genAccessPath";
        private static final String P_ACCESS_PATH_FILED_KEY = "accessPathFiledKey";
        private static final String P_ACCESS_PATH_RULE = "accessPathRule";
        private static final String P_DELETE_BASE64_IMAGE_STRING = "deleteBase64ImageString";


        /**
         * 图片字段的key
         */
        private String imageFieldKey;

        /**
         * 图片名称规则。如果需要其他字段的值填充, 则使用${value}格式预填充
         */
        private String imageNameRule;

        /**
         * 本地图片保存的路径
         */
        private String imageSavePath;

        /**
         * 是否成功图片的访问路径
         */
        private boolean genAccessPath;


        /**
         * 访问路径的字段key
         */
        private String accessPathFiledKey;

        /**
         * 图片访问路径规则。使用 ${imageName} 占位图片的名称, 如果需要其他字段的值填充, 则使用${value}格式预填充
         */
        private String accessPathRule;

        /**
         * 处理完成后是否删除base64图片字符串。默认删除
         */
        private boolean deleteBase64ImageString = true;


        @Override
        public void parsing(ConfigParamInfo paramInfo) throws Exception {
            imageFieldKey = paramInfo.getString(P_IMAGE_FIELD_KEY);
            imageNameRule = paramInfo.getString(P_IMAGE_NAME_RULE);
            imageSavePath = paramInfo.getString(P_IMAGE_SAVE_PATH);
            genAccessPath = paramInfo.getBoolean(P_GEN_ACCESS_PATH, false);
            accessPathFiledKey = paramInfo.getString(P_ACCESS_PATH_FILED_KEY);
            accessPathRule = paramInfo.getString(P_ACCESS_PATH_RULE);
            deleteBase64ImageString = paramInfo.getBoolean(P_DELETE_BASE64_IMAGE_STRING, deleteBase64ImageString);
        }

        @Override
        public ConfigParam configParam() {
            ConfigParam configParam = new ConfigParam();

            configParam.addField(
                    TextField.toBuilder(
                            P_IMAGE_FIELD_KEY, "图片字段key", "")
                            .description("要解析的图片字段key")
                            .required(true)
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(
                            P_IMAGE_NAME_RULE, "图片文件名称规则", "")
                            .description("要解析的图片字段key, 如果需要其他字段值填充, 则使用${key}格式预填充, key为字段key。" +
                                    "如果未填写则默认以UUID生成图片名")
                            .required(false)
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(
                            P_IMAGE_SAVE_PATH, "图片存储路径", "")
                            .description("解析后的图片所存储的文件路径")
                            .required(true)
                            .build()
            );

            configParam.addField(
                    BooleanField.toBuilder(
                            P_GEN_ACCESS_PATH, "是否生成图片访问路径", false)
                            .description("根据配置的文件访问规则生产图片访问路径")
                            .required(true)
                            .build()
            );


            configParam.addField(
                    TextField.toBuilder(
                            P_ACCESS_PATH_FILED_KEY, "访问路径字段key", "")
                            .description("图片访问路径生成规则值的字段key")
                            .required(true)
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(
                            P_ACCESS_PATH_RULE, "图片访问路径", "")
                            .description("图片访问路径生成规则. " +
                                    "使用 ${" + IMAGE_NAME + "} 占位图片的名称, 如果需要其他字段的值填充, 则使用${value}格式预填充")
                            .required(true)
                            .build()
            );


            configParam.addField(
                    BooleanField.toBuilder(
                            P_DELETE_BASE64_IMAGE_STRING, "是否删除Base64图片字符", deleteBase64ImageString)
                            .description("处理完成后, 是否删除Base64图片字符")
                            .required(true)
                            .build()
            );


            return configParam;
        }
    }

}
