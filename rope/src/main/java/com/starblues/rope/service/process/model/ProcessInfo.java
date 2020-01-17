package com.starblues.rope.service.process.model;

import com.starblues.rope.core.common.State;
import lombok.*;

/**
 * 流程模型bean
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC )
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ProcessInfo {


    private String processId;
    private State state;


}
