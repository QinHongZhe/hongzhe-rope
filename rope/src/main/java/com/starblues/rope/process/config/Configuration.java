package com.starblues.rope.process.config;

import com.starblues.rope.core.common.config.CommonConfig;
import com.starblues.rope.core.input.InputManagerConfig;
import com.starblues.rope.core.output.OutputFactory;
import com.starblues.rope.core.output.OutputManagerConfig;
import lombok.*;


/**
 * 流程配置bean
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC )
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Configuration {

    private String name;
    private CommonConfig inputTransport;
    private CommonConfig outputTransport;
    private InputManagerConfig inputConfig;
    private OutputManagerConfig outputConfig;
    private OutputFactory.OutputPollConfig outputPollConfig;

}
