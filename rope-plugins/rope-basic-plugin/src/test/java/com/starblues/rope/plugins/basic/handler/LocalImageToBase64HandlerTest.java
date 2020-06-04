package com.starblues.rope.plugins.basic.handler;

import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.Record;
import org.junit.Test;

/**
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-06-03
 */
public class LocalImageToBase64HandlerTest {

    @Test
    public void handle() throws Exception {
        LocalImageToBase64Handler.Param param = new LocalImageToBase64Handler.Param();
        param.setBase64ImageFieldKey("base64");
        param.setLocalImagePathRule("${relativePath}\\AAIYPNXEISVHPCQ.jpg");

        LocalImageToBase64Handler localImageToBase64Handler = new LocalImageToBase64Handler();
        localImageToBase64Handler.setParam(param);

        Record record = new DefaultRecord();
        record.putColumn(Column.auto("relativePath", "D:\\image"));
        Record handle = localImageToBase64Handler.handle(record);
        System.out.println(handle.getColumn("base64").getMetadata());

    }

}
